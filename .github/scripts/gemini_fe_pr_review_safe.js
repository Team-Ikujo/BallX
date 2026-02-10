/**
 * .github/scripts/gemini_fe_pr_review_safe.js
 *
 * Fix: 모델 하드코딩(candidates) 제거
 *  - v1beta /models 리스트를 먼저 호출(ListModels)
 *  - supportedGenerationMethods에 generateContent가 있는 모델을 골라 호출
 *  - models/gemini-1.5-xxx not found 404를 방지
 */

const axios = require("axios");

const { GEMINI_API_KEY, GITHUB_TOKEN, PR_TITLE, PR_NUMBER, REPO } = process.env;

if (!GEMINI_API_KEY) throw new Error("Missing GEMINI_API_KEY");
if (!GITHUB_TOKEN) throw new Error("Missing GITHUB_TOKEN");
if (!PR_TITLE || !PR_NUMBER || !REPO) throw new Error("Missing PR env");

const [owner, repo] = REPO.split("/");

/**
 * 안전장치 #1: 스팸 방지(코멘트 누적 방지)
 * - 매 실행마다 새 코멘트가 쌓이지 않게
 * - "Gemini FE Automated Review" 코멘트가 있으면 업데이트, 없으면 생성
 */
const COMMENT_MARKER = "<!-- gemini-fe-review -->";

/**
 * 안전장치 #2: 민감정보 마스킹
 * - diff에서 흔한 키/토큰/프라이빗키 패턴을 최대한 제거
 * - 완벽하진 않지만 “그대로 유출”을 크게 줄임
 */
function maskSecrets(text) {
    if (!text) return text;

    let t = text;

    const patterns = [
        // Private key blocks
        [
            /-----BEGIN [A-Z ]*PRIVATE KEY-----[\s\S]*?-----END [A-Z ]*PRIVATE KEY-----/g,
            "[REDACTED_PRIVATE_KEY]",
        ],
        // AWS Access Key ID (AKIA...)
        [/\bAKIA[0-9A-Z]{16}\b/g, "[REDACTED_AWS_ACCESS_KEY]"],
        // Generic "api_key", "token", "secret" assignments (very common)
        [/(api[_-]?key\s*[:=]\s*)(['"]?)[^'"\s]+(\2)/gi, "$1[REDACTED]$3"],
        [/(access[_-]?token\s*[:=]\s*)(['"]?)[^'"\s]+(\2)/gi, "$1[REDACTED]$3"],
        [/(refresh[_-]?token\s*[:=]\s*)(['"]?)[^'"\s]+(\2)/gi, "$1[REDACTED]$3"],
        [/(secret\s*[:=]\s*)(['"]?)[^'"\s]+(\2)/gi, "$1[REDACTED]$3"],
        // Bearer tokens in headers
        [/Authorization:\s*Bearer\s+[A-Za-z0-9\-\._~\+\/]+=*/gi, "Authorization: Bearer [REDACTED]"],
    ];

    for (const [re, repl] of patterns) t = t.replace(re, repl);

    return t;
}

/**
 * 안전장치 #3: 대형 PR 처리
 * - PR 전체를 한 번에 보내지 않고 "chunk"로 나눠서 Gemini 호출
 * - 결과를 합쳐 최종 코멘트 작성
 */
function chunkString(str, chunkSize) {
    const chunks = [];
    for (let i = 0; i < str.length; i += chunkSize) {
        chunks.push(str.slice(i, i + chunkSize));
    }
    return chunks;
}

async function getPRFiles() {
    const url = `https://api.github.com/repos/${owner}/${repo}/pulls/${PR_NUMBER}/files?per_page=100`;
    const res = await axios.get(url, {
        headers: {
            Authorization: `Bearer ${GITHUB_TOKEN}`,
            Accept: "application/vnd.github+json",
        },
    });
    return res.data || [];
}

async function buildFrontendDiff(files) {
    const frontendFiles = files.filter((f) => f.filename.startsWith("frontend/"));

    // patch가 null인 파일은 제외(큰 파일/바이너리 등)
    const patches = frontendFiles
        .map((f) => (f.patch ? `FILE: ${f.filename}\n---\n${f.patch}\n` : null))
        .filter(Boolean);

    return patches.join("\n");
}

function buildPrompt() {
    return `
You are a senior frontend reviewer for the BallX project.

You are an expert in:
- TypeScript, React 19, Rsbuild
- Tailwind CSS v4, shadcn/ui, Ant Design
- React Query, Zustand, React Hook Form, Zod, Axios
- FSD-oriented architecture

Project rules:

Folder structure (FSD):
- app: app bootstrapping/composition
- pages: route-level screens
- widgets: large UI blocks
- features: domain-specific logic + UI
- entities: domain model management
- shared: cross-cutting utilities/components

Development principles:
- Write maintainable and performant code.
- Consider accessibility (a11y) by default.
- Prefer simple state management.
- Respect existing structure and conventions.
- Minimize unnecessary changes.
- Split UI into reusable components.
- Prefer switch over chained if/else for complex branching.
- Prefer:
  - React Query for data fetching
  - React Hook Form + Zod for forms
  - Zustand for global state
  - Tailwind + shadcn/ui for UI
- Follow design system in src/styles/globals.css.

When reviewing:
- Focus ONLY on frontend code.
- Evaluate structure, readability, performance, and maintainability.
- Point out violations of FSD structure.
- Check consistency with existing patterns.
- Identify potential bugs and edge cases.
- Suggest concrete improvements.

Important:
- Write the entire review in Korean.
- Be constructive and practical.
- Do not repeat the diff.

Output format (in Korean):
1. 요약 (핵심 변경 사항 요약)
2. 주요 개선 포인트 (중요도 높은 문제 위주)
3. 구조/아키텍처 관점 피드백
4. 코드 품질 및 성능 관련 제안
5. 개선 제안 예시 (필요 시 코드 스니펫)
6. 체크리스트 (머지 전 확인 사항)
`;
}

/**
 * ✅ NEW: v1beta ListModels 호출
 * - 사용 가능한 모델과 지원 메서드(예: generateContent)를 확인
 */
async function listGeminiModels() {
    const url = `https://generativelanguage.googleapis.com/v1beta/models?key=${GEMINI_API_KEY}`;
    const res = await axios.get(url, { headers: { Accept: "application/json" } });
    return res.data?.models ?? [];
}

/**
 * ✅ NEW: generateContent 지원 모델 선택
 * - 최신 계열을 선호하되, 실제로 지원하는 것만 고름
 */
function pickModelForGenerateContent(models) {
    const usable = (models || []).filter(
        (m) =>
            Array.isArray(m.supportedGenerationMethods) &&
            m.supportedGenerationMethods.includes("generateContent") &&
            typeof m.name === "string"
    );

    if (usable.length === 0) return null;

    // 선호 baseModelId (환경에 따라 다를 수 있어도, 지원하는 것만 pick됨)
    const preferredBaseIds = ["gemini-2.5-pro", "gemini-2.5-flash", "gemini-2.0-flash", "gemini-1.5-pro", "gemini-1.5-flash"];

    for (const base of preferredBaseIds) {
        const found = usable.find((m) => m.baseModelId === base);
        if (found) return found.name; // ex) "models/gemini-2.0-flash"
    }

    // 그래도 없으면 첫 번째 사용 가능 모델
    return usable[0].name;
}

async function callGemini(prompt, diffText) {
    const body = {
        contents: [
            {
                role: "user",
                parts: [{ text: `PR Title: ${PR_TITLE}\n\n${prompt}\n\nPR diff (frontend only):\n${diffText}` }],
            },
        ],
        generationConfig: { temperature: 0.2, topP: 0.9, maxOutputTokens: 6000 },
    };

    // ✅ 여기서 모델을 “실제 사용 가능한 것”으로 선택
    const models = await listGeminiModels();
    const modelName = pickModelForGenerateContent(models);

    if (!modelName) {
        const debug = JSON.stringify(models?.slice?.(0, 3) ?? [], null, 2);
        throw new Error(`No Gemini models support generateContent. models(sample)=${debug}`);
    }

    const url = `https://generativelanguage.googleapis.com/v1beta/${modelName}:generateContent?key=${GEMINI_API_KEY}`;

    try {
        const res = await axios.post(url, body, { headers: { "Content-Type": "application/json" } });

        const text =
            res.data?.candidates?.[0]?.content?.parts?.map((p) => p.text).join("") ||
            "No response from Gemini.";

        return text;
    } catch (err) {
        const status = err?.response?.status;
        const data = err?.response?.data;
        console.log(`Gemini call failed for ${modelName}:generateContent status=${status}`);
        if (data) console.log("Gemini error body:", JSON.stringify(data).slice(0, 2000));
        throw err;
    }
}

async function listIssueComments() {
    const url = `https://api.github.com/repos/${owner}/${repo}/issues/${PR_NUMBER}/comments?per_page=100`;
    const res = await axios.get(url, {
        headers: {
            Authorization: `Bearer ${GITHUB_TOKEN}`,
            Accept: "application/vnd.github+json",
        },
    });
    return res.data || [];
}

async function createIssueComment(body) {
    const url = `https://api.github.com/repos/${owner}/${repo}/issues/${PR_NUMBER}/comments`;
    await axios.post(
        url,
        { body },
        {
            headers: {
                Authorization: `Bearer ${GITHUB_TOKEN}`,
                Accept: "application/vnd.github+json",
            },
        }
    );
}

async function updateIssueComment(commentId, body) {
    const url = `https://api.github.com/repos/${owner}/${repo}/issues/comments/${commentId}`;
    await axios.patch(
        url,
        { body },
        {
            headers: {
                Authorization: `Bearer ${GITHUB_TOKEN}`,
                Accept: "application/vnd.github+json",
            },
        }
    );
}

function buildCommentBody(reviewMarkdown, meta) {
    const { chunksUsed, truncated } = meta;

    const lines = [
        COMMENT_MARKER,
        "## 🤖 Gemini FE Automated Review",
        `**PR Title:** ${PR_TITLE}`,
        "",
        reviewMarkdown,
        "",
        "---",
        `**Notes:** chunks=${chunksUsed}${truncated ? ", truncated=true" : ""}`,
        "<sub>Generated by GitHub Actions + Gemini (frontend-only)</sub>",
    ];

    return lines.join("\n");
}

(async () => {
    // 1) PR files -> frontend diff
    const files = await getPRFiles();
    let diff = await buildFrontendDiff(files);

    if (!diff.trim()) {
        console.log("No frontend patches found. Skip.");
        return;
    }

    // 2) Mask secrets
    diff = maskSecrets(diff);

    // 3) Chunking for large PR
    const prompt = buildPrompt();

    // 경험적으로 너무 크게 보내면 실패/품질저하가 나서, 적당히 분할
    const MAX_CHARS_PER_CHUNK = 45000;
    const chunks = chunkString(diff, MAX_CHARS_PER_CHUNK);

    // 너무 많은 chunk면 비용/시간이 커지니 상한선(필요시 조정)
    const MAX_CHUNKS = 4;
    const usedChunks = chunks.slice(0, MAX_CHUNKS);
    const truncated = chunks.length > MAX_CHUNKS;

    const results = [];
    for (let i = 0; i < usedChunks.length; i++) {
        const partHeader = usedChunks.length > 1 ? `\n\n[Chunk ${i + 1}/${usedChunks.length}]\n` : "\n";
        const review = await callGemini(prompt, partHeader + usedChunks[i]);
        results.push(review);
    }

    // 여러 chunk 결과를 합치기
    const combinedReview =
        results.length === 1
            ? results[0]
            : results.map((r, idx) => `### Part ${idx + 1}\n\n${r}`).join("\n\n");

    const body = buildCommentBody(combinedReview, {
        chunksUsed: usedChunks.length,
        truncated,
    });

    // 4) Upsert comment (no spam)
    const comments = await listIssueComments();
    const existing = comments.find((c) => typeof c.body === "string" && c.body.includes(COMMENT_MARKER));

    if (existing) {
        await updateIssueComment(existing.id, body);
        console.log("✅ Updated existing Gemini FE review comment.");
    } else {
        await createIssueComment(body);
        console.log("✅ Created Gemini FE review comment.");
    }
})().catch((e) => {
    console.error("❌ Gemini FE review action failed:", e?.message ?? e);
    process.exit(1);
});
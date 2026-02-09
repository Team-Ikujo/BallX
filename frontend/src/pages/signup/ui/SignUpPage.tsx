import { useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link } from 'react-router-dom';
import { z } from 'zod';
import clsx from 'clsx';

const carriers = [
   'SKT',
   'KT',
   'LG U+',
   'SKT 알뜰폰',
   'KT 알뜰폰',
   'LG U+ 알뜰폰',
] as const;

type Carrier = (typeof carriers)[number];

type AgreementKey = 'privacy' | 'identity' | 'service' | 'carrier' | 'age';

type Agreements = Record<AgreementKey, boolean>;

const signUpSchema = z.object({
   name: z
      .string()
      .trim()
      .min(1, '이름을 입력해주세요.')
      .max(30, '이름은 30자 이내로 입력해주세요.'),
   nationality: z.enum(['domestic', 'foreign']),
   birthDate: z
      .string()
      .trim()
      .regex(/^\d{8}$/, '생년월일은 8자리 숫자로 입력해주세요.'),
   gender: z.enum(['male', 'female']).optional(),
   carrier: z.enum(carriers).optional(),
   phone: z
      .string()
      .trim()
      .regex(/^\d{10,11}$/, "휴대폰 번호는 '-' 없이 10~11자리 숫자만 입력해주세요."),
   agreements: z.object({
      privacy: z.boolean().refine(Boolean, { message: '개인정보 취급 동의가 필요합니다.' }),
      identity: z.boolean().refine(Boolean, { message: '고유식별 정보처리 동의가 필요합니다.' }),
      service: z.boolean().refine(Boolean, { message: '본인 확인 서비스 이용약관 동의가 필요합니다.' }),
      carrier: z.boolean().refine(Boolean, { message: '통신사 이용약관 동의가 필요합니다.' }),
      age: z.boolean().refine(Boolean, { message: '만 14세 이상 동의가 필요합니다.' }),
   }),
});

type SignUpFormValues = z.infer<typeof signUpSchema>;

const SignUpPage = () => {
   const {
      register,
      handleSubmit,
      setValue,
      watch,
      formState: { errors },
   } = useForm<SignUpFormValues>({
      resolver: zodResolver(signUpSchema),
      defaultValues: {
         name: '',
         nationality: 'domestic',
         birthDate: '',
         gender: undefined,
         carrier: undefined,
         phone: '',
         agreements: {
            privacy: false,
            identity: false,
            service: false,
            carrier: false,
            age: false,
         },
      },
   });

   const nationality = watch('nationality');
   const gender = watch('gender');
   const carrier = watch('carrier');
   const agreements = watch('agreements');

   const allAgreed = useMemo(() => {
      if (!agreements) return false;
      return Object.values(agreements).every(Boolean);
   }, [agreements]);

   const setAllAgreements = (checked: boolean) => {
      const next: Agreements = {
         privacy: checked,
         identity: checked,
         service: checked,
         carrier: checked,
         age: checked,
      };

      setValue('agreements', next, { shouldDirty: true, shouldValidate: true });
   };

   const onSubmit = (values: SignUpFormValues) => {
      console.log('signup submit', values);
   };

   return (
      <div
         className="min-h-screen bg-(--color-surface) text-(--color-foreground)
      flex items-center justify-center"
      >
         <section className="w-full max-w-md p-8">
            <div className="text-center">
               <h2 className="text-2xl font-semibold">본인 확인을 위해 </h2>
               <h2 className="text-2xl font-semibold">인증을 진행해주세요</h2>
            </div>
            <form className="mt-8 space-y-5" onSubmit={handleSubmit(onSubmit)}>
               <label className="block text-sm text-(--color-muted-foreground)">
                  이름*
                  <input
                     type="text"
                     placeholder="30자 이내 입력"
                     className="mt-2 w-full rounded-xl border border-slate-800 bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400"
                     {...register('name')}
                  />
                  {errors.name?.message && (
                     <p className="mt-1 text-xs text-rose-400">{errors.name.message}</p>
                  )}
               </label>
               <label className="block text-sm text-(--color-muted-foreground)">
                  국적
                  <div className="flex gap-2">
                     <button
                        type="button"
                        className={clsx(
                           'mt-2 w-full rounded-xl border bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400',
                           nationality === 'domestic'
                              ? 'border-emerald-400 text-emerald-200'
                              : 'border-slate-800',
                        )}
                        aria-pressed={nationality === 'domestic'}
                        onClick={() =>
                           setValue('nationality', 'domestic', { shouldDirty: true, shouldValidate: true })
                        }
                     >
                        내국인
                     </button>
                     <button
                        type="button"
                        className={clsx(
                           'mt-2 w-full rounded-xl border bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400',
                           nationality === 'foreign'
                              ? 'border-emerald-400 text-emerald-200'
                              : 'border-slate-800',
                        )}
                        aria-pressed={nationality === 'foreign'}
                        onClick={() =>
                           setValue('nationality', 'foreign', { shouldDirty: true, shouldValidate: true })
                        }
                     >
                        외국인
                     </button>
                  </div>
               </label>
               <label className="block text-sm text-(--color-muted-foreground)">
                  생년월일*
                  <input
                     type="text"
                     placeholder="예: 19990101 (8자리)"
                     className="mt-2 w-full rounded-xl border border-slate-800 bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400"
                     {...register('birthDate')}
                  />
                  {errors.birthDate?.message && (
                     <p className="mt-1 text-xs text-rose-400">{errors.birthDate.message}</p>
                  )}
               </label>
               <label className="block text-sm text-(--color-muted-foreground)">
                  성별
                  <div className="flex gap-2">
                     <button
                        type="button"
                        className={clsx(
                           'mt-2 w-full rounded-xl border bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400',
                           gender === 'male' ? 'border-emerald-400 text-emerald-200' : 'border-slate-800',
                        )}
                        aria-pressed={gender === 'male'}
                        onClick={() => setValue('gender', 'male', { shouldDirty: true })}
                     >
                        남성
                     </button>
                     <button
                        type="button"
                        className={clsx(
                           'mt-2 w-full rounded-xl border bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400',
                           gender === 'female' ? 'border-emerald-400 text-emerald-200' : 'border-slate-800',
                        )}
                        aria-pressed={gender === 'female'}
                        onClick={() => setValue('gender', 'female', { shouldDirty: true })}
                     >
                        여성
                     </button>
                  </div>
               </label>
               <label className="block text-sm text-(--color-muted-foreground)">
                  통신사
                  <div className="grid grid-cols-3 gap-2 mt-2">
                     {carriers.map(item => (
                        <button
                           key={item}
                           type="button"
                           className={clsx(
                              'w-full rounded-xl border bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400',
                              carrier === item ? 'border-emerald-400 text-emerald-200' : 'border-slate-800',
                           )}
                           aria-pressed={carrier === item}
                           onClick={() =>
                              setValue('carrier', item, { shouldDirty: true, shouldValidate: true })
                           }
                        >
                           {item}
                        </button>
                     ))}
                  </div>
               </label>
               <label className="block text-sm text-(--color-muted-foreground)">
                  휴대폰 번호*
                  <input
                     type="text"
                     placeholder="'-'를 제외한 숫자만 입력"
                     className="mt-2 w-full rounded-xl border border-slate-800 bg-slate-950 px-4 py-3 text-sm text-slate-100 outline-none transition focus:border-emerald-400"
                     {...register('phone')}
                  />
                  {errors.phone?.message && (
                     <p className="mt-1 text-xs text-rose-400">{errors.phone.message}</p>
                  )}
               </label>
               <div className="gap-2">
                  <div className="flex items-center gap-2 bg-(--color-background) border-(--color-border) px-4 py-3 rounded-lg">
                     <input
                        id="agree-all"
                        type="checkbox"
                        checked={allAgreed}
                        onChange={event => setAllAgreements(event.target.checked)}
                     />
                     <label
                        htmlFor="agree-all"
                        className="text-(text-display-1-bold) text-(--color-foreground) cursor-pointer"
                     >
                        전체 동의
                     </label>
                  </div>
                  <div className="mt-2">
                     <div className="flex items-center gap-2 bg-(--color-background) border-(--color-border) px-4 py-3 rounded-lg">
                        <input id="agree-privacy" type="checkbox" {...register('agreements.privacy')} />
                        <label
                           htmlFor="agree-privacy"
                           className="text-(text-display-1-bold) text-(--color-foreground) cursor-pointer"
                        >
                           (필수) 개인정보 취급 동의
                        </label>
                     </div>
                     {errors.agreements?.privacy?.message && (
                        <p className="mt-1 text-xs text-rose-400">{errors.agreements.privacy.message}</p>
                     )}
                     <div className="flex items-center gap-2 bg-(--color-background) border-(--color-border) px-4 py-3 rounded-lg">
                        <input id="agree-identity" type="checkbox" {...register('agreements.identity')} />
                        <label
                           htmlFor="agree-identity"
                           className="text-(text-display-1-bold) text-(--color-foreground) cursor-pointer"
                        >
                           (필수) 고유식별 정보처리 동의
                        </label>
                     </div>
                     {errors.agreements?.identity?.message && (
                        <p className="mt-1 text-xs text-rose-400">{errors.agreements.identity.message}</p>
                     )}
                     <div className="flex items-center gap-2 bg-(--color-background) border-(--color-border) px-4 py-3 rounded-lg">
                        <input id="agree-service" type="checkbox" {...register('agreements.service')} />
                        <label
                           htmlFor="agree-service"
                           className="text-(text-display-1-bold) text-(--color-foreground) cursor-pointer"
                        >
                           (필수) 본인 확인 서비스 이용약관 동의
                        </label>
                     </div>
                     {errors.agreements?.service?.message && (
                        <p className="mt-1 text-xs text-rose-400">{errors.agreements.service.message}</p>
                     )}
                     <div className="flex items-center gap-2 bg-(--color-background) border-(--color-border) px-4 py-3 rounded-lg">
                        <input id="agree-carrier" type="checkbox" {...register('agreements.carrier')} />
                        <label
                           htmlFor="agree-carrier"
                           className="text-(text-display-1-bold) text-(--color-foreground) cursor-pointer"
                        >
                           (필수) 통신사 이용약관 동의
                        </label>
                     </div>
                     {errors.agreements?.carrier?.message && (
                        <p className="mt-1 text-xs text-rose-400">{errors.agreements.carrier.message}</p>
                     )}
                     <div className="flex items-center gap-2 bg-(--color-background) border-(--color-border) px-4 py-3 rounded-lg">
                        <input id="agree-age" type="checkbox" {...register('agreements.age')} />
                        <label
                           htmlFor="agree-age"
                           className="text-(text-display-1-bold) text-(--color-foreground) cursor-pointer"
                        >
                           (필수) 만 14세 이상입니다.
                        </label>
                     </div>
                     {errors.agreements?.age?.message && (
                        <p className="mt-1 text-xs text-rose-400">{errors.agreements.age.message}</p>
                     )}
                  </div>
               </div>
               <button
                  type="submit"
                  className="w-full rounded-xl bg-emerald-400 px-4 py-3 text-sm font-semibold text-slate-950 transition hover:bg-emerald-300"
               >
                  인증번호 전송
               </button>
            </form>
            <div className="mt-8 text-xs text-slate-500">
               <Link className="text-emerald-300 hover:text-emerald-200" to="/">
                  홈으로 돌아가기
               </Link>
            </div>
         </section>
      </div>
   );
};

export default SignUpPage;

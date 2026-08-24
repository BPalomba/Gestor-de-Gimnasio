import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'

const schema = z.object({
    firstName: z.string().min(2, 'Mínimo 2 caracteres'),
    lastName: z.string().min(2, 'Mínimo 2 caracteres'),
    dni: z.string().optional(),
    email: z.string().email('Email inválido').optional().or(z.literal('')),
    phone: z.string().optional(),
})

function Field({ label, error, children }) {
    return (
        <div>
            <label className="text-neutral-400 text-xs mb-1 block">{label}</label>
            {children}
            {error && <p className="text-red-400 text-xs mt-1">{error}</p>}
        </div>
    )
}

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"

export default function MemberForm({ defaultValues, onSubmit, loading }) {
    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(schema),
        defaultValues,
    })

    return (
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
                <Field label="Nombre *" error={errors.firstName?.message}>
                    <input {...register('firstName')} placeholder="Juan" className={inputClass} />
                </Field>
                <Field label="Apellido *" error={errors.lastName?.message}>
                    <input {...register('lastName')} placeholder="Pérez" className={inputClass} />
                </Field>
            </div>
            <div className="grid grid-cols-2 gap-4">
                <Field label="DNI" error={errors.dni?.message}>
                    <input {...register('dni')} placeholder="35000000" className={inputClass} />
                </Field>
                <Field label="Teléfono" error={errors.phone?.message}>
                    <input {...register('phone')} placeholder="11 5566 7788" className={inputClass} />
                </Field>
            </div>
            <Field label="Email" error={errors.email?.message}>
                <input {...register('email')} type="email" placeholder="juan@email.com" className={inputClass} />
            </Field>
            <div className="flex justify-end gap-3 pt-2">
                <button
                    type="submit"
                    disabled={loading}
                    className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
                >
                    {loading ? 'Guardando...' : 'Guardar socio'}
                </button>
            </div>
        </form>
    )
}
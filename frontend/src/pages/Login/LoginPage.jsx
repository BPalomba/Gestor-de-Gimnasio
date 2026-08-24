import { useForm } from 'react-hook-form'
import { z } from 'zod'
import { zodResolver } from '@hookform/resolvers/zod'
import { useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { login } from '@/api/auth'
import useAuthStore from '@/store/authStore'

const schema = z.object({
    email: z.string().email('Email inválido'),
    password: z.string().min(1, 'La contraseña es obligatoria'),
})

export default function LoginPage() {
    const navigate = useNavigate()
    const setAuth = useAuthStore((s) => s.setAuth)
    const [error, setError] = useState(null)
    const [loading, setLoading] = useState(false)

    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(schema),
    })

    const onSubmit = async (data) => {
        setLoading(true)
        setError(null)
        try {
            const res = await login(data.email, data.password)
            localStorage.setItem('token', res.accessToken)
            setAuth(res.accessToken, { email: res.email, role: res.role }, res.permissions ?? [])
            navigate('/dashboard')
        } catch (err) {
            setError(err.response?.data?.message ?? 'Credenciales incorrectas')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-h-screen bg-neutral-950 flex items-center justify-center px-4">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-8 w-full max-w-sm">

                <div className="flex flex-col items-center mb-8">
                    <div className="w-10 h-10 bg-violet-600 rounded-lg mb-3" />
                    <h1 className="text-white text-xl font-bold">GymSaaS</h1>
                    <p className="text-neutral-400 text-sm mt-1">Ingresá a tu panel</p>
                </div>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Email</label>
                        <input
                            {...register('email')}
                            type="email"
                            placeholder="admin@gimnasio.com"
                            className="w-full bg-neutral-800 border border-neutral-700 rounded-md px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"
                        />
                        {errors.email && (
                            <p className="text-red-400 text-xs mt-1">{errors.email.message}</p>
                        )}
                    </div>

                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Contraseña</label>
                        <input
                            {...register('password')}
                            type="password"
                            placeholder="••••••••"
                            className="w-full bg-neutral-800 border border-neutral-700 rounded-md px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"
                        />
                        {errors.password && (
                            <p className="text-red-400 text-xs mt-1">{errors.password.message}</p>
                        )}
                    </div>

                    {error && (
                        <div className="bg-red-950 border border-red-800 rounded-md px-3 py-2">
                            <p className="text-red-400 text-sm">{error}</p>
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white font-medium py-2 rounded-md text-sm transition-colors"
                    >
                        {loading ? 'Ingresando...' : 'Ingresar'}
                    </button>
                </form>
            </div>
        </div>
    )
}
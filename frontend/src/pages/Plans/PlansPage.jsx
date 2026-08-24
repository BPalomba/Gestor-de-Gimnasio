import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getPlans, createPlan, updatePlan, deactivatePlan } from '@/api/plans'
import StatusBadge from '@/components/shared/StatusBadge'
import ConfirmModal from '@/components/shared/ConfirmModal'

function formatMoney(amount) {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', maximumFractionDigits: 0 }).format(amount ?? 0)
}

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"

function PlanModal({ plan, onClose, onSave, loading, error }) {
    const [form, setForm] = useState({
        name: plan?.name ?? '',
        description: plan?.description ?? '',
        price: plan?.price ?? '',
        currency: plan?.currency ?? 'ARS',
        durationDays: plan?.durationDays ?? '',
        sessionsPerWeek: plan?.sessionsPerWeek ?? '',
        publicPlan: plan?.publicPlan ?? true,
    })

    const handleSubmit = (e) => {
        e.preventDefault()
        onSave({
            ...form,
            price: parseFloat(form.price),
            durationDays: parseInt(form.durationDays),
            sessionsPerWeek: form.sessionsPerWeek ? parseInt(form.sessionsPerWeek) : null,
        })
    }

    return (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-md">
                <div className="flex items-center justify-between mb-5">
                    <h2 className="text-white font-semibold">{plan ? 'Editar plan' : 'Nuevo plan'}</h2>
                    <button onClick={onClose} className="text-neutral-400 hover:text-white">✕</button>
                </div>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Nombre *</label>
                        <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} placeholder="Plan Mensual" className={inputClass} required />
                    </div>
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Descripción</label>
                        <input value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} placeholder="Descripción opcional" className={inputClass} />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Precio *</label>
                            <input type="number" value={form.price} onChange={e => setForm(f => ({ ...f, price: e.target.value }))} placeholder="5000" className={inputClass} required />
                        </div>
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Moneda</label>
                            <select value={form.currency} onChange={e => setForm(f => ({ ...f, currency: e.target.value }))} className={inputClass}>
                                <option value="ARS">ARS</option>
                                <option value="USD">USD</option>
                            </select>
                        </div>
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Duración (días) *</label>
                            <input type="number" value={form.durationDays} onChange={e => setForm(f => ({ ...f, durationDays: e.target.value }))} placeholder="30" className={inputClass} required />
                        </div>
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Sesiones/semana</label>
                            <input type="number" value={form.sessionsPerWeek} onChange={e => setForm(f => ({ ...f, sessionsPerWeek: e.target.value }))} placeholder="Ilimitado" className={inputClass} />
                        </div>
                    </div>
                    <label className="flex items-center gap-3 cursor-pointer">
                        <input type="checkbox" checked={form.publicPlan} onChange={e => setForm(f => ({ ...f, publicPlan: e.target.checked }))} className="w-4 h-4 accent-violet-500" />
                        <span className="text-neutral-300 text-sm">Visible en la landing pública</span>
                    </label>
                    {error && <p className="text-red-400 text-xs">{error}</p>}
                    <div className="flex justify-end gap-3 pt-2">
                        <button type="button" onClick={onClose} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                        <button type="submit" disabled={loading} className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors">
                            {loading ? 'Guardando...' : 'Guardar plan'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default function PlansPage() {
    const queryClient = useQueryClient()
    const [showModal, setShowModal] = useState(false)
    const [editingPlan, setEditingPlan] = useState(null)
    const [confirmDeactivate, setConfirmDeactivate] = useState(null)

    const { data: plans = [], isLoading } = useQuery({
        queryKey: ['plans'],
        queryFn: getPlans,
    })

    const invalidate = () => queryClient.invalidateQueries({ queryKey: ['plans'] })

    const createMut = useMutation({
        mutationFn: createPlan,
        onSuccess: () => { invalidate(); setShowModal(false) },
    })

    const updateMut = useMutation({
        mutationFn: ({ id, data }) => updatePlan(id, data),
        onSuccess: () => { invalidate(); setEditingPlan(null) },
    })

    const deactivateMut = useMutation({
        mutationFn: (id) => deactivatePlan(id),
        onSuccess: () => { invalidate(); setConfirmDeactivate(null) },
    })

    return (
        <div className="space-y-5">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Planes</h1>
                    <p className="text-neutral-400 text-sm mt-1">{plans.length} planes activos</p>
                </div>
                <button onClick={() => setShowModal(true)} className="bg-violet-600 hover:bg-violet-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors">
                    + Nuevo plan
                </button>
            </div>

            {/* Tabla */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-neutral-800">
                            {['Nombre', 'Precio', 'Duración', 'Sesiones/sem', 'Visibilidad', 'Acciones'].map(h => (
                                <th key={h} className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">{h}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={6} className="text-center text-neutral-400 py-12 text-sm">Cargando...</td></tr>
                        ) : plans.length === 0 ? (
                            <tr><td colSpan={6} className="text-center text-neutral-400 py-12 text-sm">No hay planes</td></tr>
                        ) : plans.map(plan => (
                            <tr key={plan.id} className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/40 transition-colors">
                                <td className="px-4 py-3">
                                    <div className="text-white font-medium">{plan.name}</div>
                                    {plan.description && <div className="text-neutral-500 text-xs mt-0.5">{plan.description}</div>}
                                </td>
                                <td className="px-4 py-3 text-neutral-300">{formatMoney(plan.price)} <span className="text-neutral-500 text-xs">{plan.currency}</span></td>
                                <td className="px-4 py-3 text-neutral-400">{plan.durationDays} días</td>
                                <td className="px-4 py-3 text-neutral-400">{plan.sessionsPerWeek ?? 'Ilimitado'}</td>
                                <td className="px-4 py-3">
                                    <span className={`text-xs font-medium px-2 py-0.5 rounded-md ${plan.publicPlan ? 'bg-blue-950 text-blue-400 border border-blue-800' : 'bg-neutral-800 text-neutral-400'}`}>
                                        {plan.publicPlan ? 'Público' : 'Privado'}
                                    </span>
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex gap-3">
                                        <button onClick={() => setEditingPlan(plan)} className="text-violet-400 hover:text-violet-300 text-xs transition-colors">
                                            Editar
                                        </button>
                                        <button onClick={() => setConfirmDeactivate(plan)} className="text-red-400 hover:text-red-300 text-xs transition-colors">
                                            Desactivar
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Modal crear */}
            {showModal && (
                <PlanModal
                    onClose={() => setShowModal(false)}
                    onSave={(data) => createMut.mutate(data)}
                    loading={createMut.isPending}
                    error={createMut.error?.response?.data?.message}
                />
            )}

            {/* Modal editar */}
            {editingPlan && (
                <PlanModal
                    plan={editingPlan}
                    onClose={() => setEditingPlan(null)}
                    onSave={(data) => updateMut.mutate({ id: editingPlan.id, data })}
                    loading={updateMut.isPending}
                    error={updateMut.error?.response?.data?.message}
                />
            )}

            {/* Confirmar desactivar */}
            <ConfirmModal
                open={!!confirmDeactivate}
                title="Desactivar plan"
                description={`¿Desactivar "${confirmDeactivate?.name}"? No estará disponible para nuevas membresías. Las existentes no se ven afectadas.`}
                confirmLabel="Desactivar"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={deactivateMut.isPending}
                onConfirm={() => deactivateMut.mutate(confirmDeactivate.id)}
                onCancel={() => setConfirmDeactivate(null)}
            />
        </div>
    )
}
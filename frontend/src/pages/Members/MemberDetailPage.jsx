import { useParams, useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { getMemberById, suspendMember, reactivateMember, cancelMember } from '@/api/members'
import { getMembershipsByMember, createMembership, freezeMembership, unfreezeMembership, cancelMembership } from '@/api/memberships'
import { getPaymentsByMember, createPayment } from '@/api/payments'
import { getPlans } from '@/api/plans'
import StatusBadge from '@/components/shared/StatusBadge'
import ConfirmModal from '@/components/shared/ConfirmModal'

function formatMoney(amount) {
    return new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS', maximumFractionDigits: 0 }).format(amount ?? 0)
}

function formatDate(dateStr) {
    if (!dateStr) return '—'
    return new Date(dateStr).toLocaleDateString('es-AR')
}

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"

export default function MemberDetailPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const [confirmAction, setConfirmAction] = useState(null)
    const [showNewMembership, setShowNewMembership] = useState(false)
    const [showPayment, setShowPayment] = useState(false)
    const [showFreeze, setShowFreeze] = useState(false)
    const [selectedPlanId, setSelectedPlanId] = useState('')
    const [freezeDate, setFreezeDate] = useState('')
    const [paymentData, setPaymentData] = useState({ amount: '', paymentMethod: 'CASH' })

    const { data: member, isLoading } = useQuery({
        queryKey: ['member', id],
        queryFn: () => getMemberById(id),
    })

    const { data: memberships = [] } = useQuery({
        queryKey: ['memberships', id],
        queryFn: () => getMembershipsByMember(id),
    })

    const { data: plans = [] } = useQuery({
        queryKey: ['plans'],
        queryFn: getPlans,
        enabled: showNewMembership,
    })

    const { data: payments = [] } = useQuery({
        queryKey: ['payments', 'member', id],
        queryFn: () => getPaymentsByMember(id),
    })

    const invalidate = () => {
        queryClient.invalidateQueries({ queryKey: ['member', id] })
        queryClient.invalidateQueries({ queryKey: ['memberships', id] })
        queryClient.invalidateQueries({ queryKey: ['payments', 'member', id] })
        queryClient.invalidateQueries({ queryKey: ['members'] })
    }

    const suspendMut = useMutation({ mutationFn: () => suspendMember(id), onSuccess: () => { invalidate(); setConfirmAction(null) } })
    const reactivateMut = useMutation({ mutationFn: () => reactivateMember(id), onSuccess: () => { invalidate(); setConfirmAction(null) } })
    const cancelMut = useMutation({ mutationFn: () => cancelMember(id), onSuccess: () => { invalidate(); setConfirmAction(null) } })

    const createMsMut = useMutation({
        mutationFn: () => createMembership({ memberId: id, planId: selectedPlanId }),
        onSuccess: () => { invalidate(); setShowNewMembership(false); setSelectedPlanId('') },
    })

    const freezeMut = useMutation({
        mutationFn: (msId) => freezeMembership(msId, { frozenSince: freezeDate }),
        onSuccess: () => { invalidate(); setShowFreeze(false); setFreezeDate('') },
    })

    const unfreezeMut = useMutation({
        mutationFn: (msId) => unfreezeMembership(msId),
        onSuccess: () => { invalidate() },
    })

    const cancelMsMut = useMutation({
        mutationFn: (msId) => cancelMembership(msId),
        onSuccess: () => { invalidate(); setConfirmAction(null) },
    })

    const paymentMut = useMutation({
        mutationFn: (msId) => createPayment({
            memberId: id,
            membershipId: msId,
            amount: parseFloat(paymentData.amount),
            paymentMethod: paymentData.paymentMethod,
        }),
        onSuccess: () => { invalidate(); setShowPayment(false); setPaymentData({ amount: '', paymentMethod: 'CASH' }) },
    })

    const activeMembership = memberships.find(ms => ms.status === 'ACTIVE' || ms.status === 'FROZEN')

    if (isLoading) return <div className="text-neutral-400 text-sm">Cargando...</div>
    if (!member) return <div className="text-red-400 text-sm">Socio no encontrado</div>

    return (
        <div className="space-y-6">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <button onClick={() => navigate('/members')} className="text-neutral-400 hover:text-white text-sm transition-colors">
                        ← Socios
                    </button>
                    <div className="flex items-center gap-3">
                        <div className="w-10 h-10 rounded-full bg-violet-600/20 flex items-center justify-center text-violet-400 font-semibold">
                            {member.firstName[0]}{member.lastName[0]}
                        </div>
                        <div>
                            <h1 className="text-white text-xl font-bold">{member.firstName} {member.lastName}</h1>
                            <div className="flex items-center gap-2 mt-0.5">
                                <StatusBadge status={member.statusCode} />
                                {member.branchName && <span className="text-neutral-500 text-xs">{member.branchName}</span>}
                            </div>
                        </div>
                    </div>
                </div>
                <div className="flex gap-2">
                    {member.statusCode === 'ACTIVE' && (
                        <button onClick={() => setConfirmAction({ type: 'suspend' })} className="px-3 py-1.5 text-xs rounded-lg bg-yellow-950 border border-yellow-800 text-yellow-400 hover:bg-yellow-900 transition-colors">
                            Suspender
                        </button>
                    )}
                    {member.statusCode === 'SUSPENDED' && (
                        <button onClick={() => setConfirmAction({ type: 'reactivate' })} className="px-3 py-1.5 text-xs rounded-lg bg-emerald-950 border border-emerald-800 text-emerald-400 hover:bg-emerald-900 transition-colors">
                            Reactivar
                        </button>
                    )}
                    {member.statusCode !== 'CANCELLED' && (
                        <button onClick={() => setConfirmAction({ type: 'cancel' })} className="px-3 py-1.5 text-xs rounded-lg bg-red-950 border border-red-800 text-red-400 hover:bg-red-900 transition-colors">
                            Cancelar socio
                        </button>
                    )}
                </div>
            </div>

            <div className="grid grid-cols-3 gap-6">

                {/* Datos personales */}
                <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5">
                    <h2 className="text-white font-semibold text-sm mb-4">Datos personales</h2>
                    <div className="space-y-3">
                        {[
                            { label: 'DNI', value: member.dni },
                            { label: 'Email', value: member.email },
                            { label: 'Teléfono', value: member.phone },
                            { label: 'Nacimiento', value: formatDate(member.birthDate) },
                            { label: 'Alta', value: formatDate(member.createdAt) },
                        ].map(({ label, value }) => (
                            <div key={label}>
                                <div className="text-neutral-500 text-xs">{label}</div>
                                <div className="text-neutral-200 text-sm mt-0.5">{value ?? '—'}</div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Membresía activa */}
                <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5">
                    <div className="flex items-center justify-between mb-4">
                        <h2 className="text-white font-semibold text-sm">Membresía activa</h2>
                        {!activeMembership && (
                            <button onClick={() => setShowNewMembership(true)} className="text-xs bg-violet-600 hover:bg-violet-700 text-white px-3 py-1 rounded-lg transition-colors">
                                + Nueva
                            </button>
                        )}
                    </div>

                    {activeMembership ? (
                        <div className="space-y-3">
                            <div className="flex items-center justify-between">
                                <span className="text-white font-medium text-sm">{activeMembership.planName}</span>
                                <StatusBadge status={activeMembership.status} />
                            </div>
                            {[
                                { label: 'Inicio', value: formatDate(activeMembership.startDate) },
                                { label: 'Vencimiento', value: formatDate(activeMembership.endDate) },
                                { label: 'Días restantes', value: `${activeMembership.daysUntilExpiration} días` },
                                { label: 'Precio pagado', value: formatMoney(activeMembership.pricePaid) },
                                { label: 'Días congelados', value: activeMembership.frozenDays },
                            ].map(({ label, value }) => (
                                <div key={label} className="flex justify-between text-xs">
                                    <span className="text-neutral-500">{label}</span>
                                    <span className="text-neutral-200">{value}</span>
                                </div>
                            ))}

                            {/* Barra de progreso */}
                            <div className="pt-1">
                                <div className="h-1.5 bg-neutral-800 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-violet-500 rounded-full transition-all"
                                        style={{
                                            width: `${Math.min(100, 100 - (activeMembership.daysUntilExpiration / 30) * 100)}%`
                                        }}
                                    />
                                </div>
                            </div>

                            <div className="flex gap-2 pt-1">
                                {activeMembership.status === 'ACTIVE' && (
                                    <>
                                        <button onClick={() => setShowFreeze(true)} className="flex-1 text-xs py-1.5 rounded-lg bg-neutral-800 text-neutral-300 hover:bg-neutral-700 transition-colors">
                                            Congelar
                                        </button>
                                        <button onClick={() => setShowPayment(true)} className="flex-1 text-xs py-1.5 rounded-lg bg-violet-600 hover:bg-violet-700 text-white transition-colors">
                                            Registrar pago
                                        </button>
                                    </>
                                )}
                                {activeMembership.status === 'FROZEN' && (
                                    <button onClick={() => unfreezeMut.mutate(activeMembership.id)} disabled={unfreezeMut.isPending} className="flex-1 text-xs py-1.5 rounded-lg bg-emerald-950 border border-emerald-800 text-emerald-400 hover:bg-emerald-900 transition-colors">
                                        {unfreezeMut.isPending ? 'Descongelando...' : 'Descongelar'}
                                    </button>
                                )}
                                <button onClick={() => setConfirmAction({ type: 'cancelMs', id: activeMembership.id })} className="text-xs py-1.5 px-3 rounded-lg bg-red-950 border border-red-800 text-red-400 hover:bg-red-900 transition-colors">
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className="flex flex-col items-center justify-center h-32 text-center">
                            <p className="text-neutral-500 text-sm">Sin membresía activa</p>
                        </div>
                    )}
                </div>

                {/* Historial de pagos */}
                <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-5">
                    <h2 className="text-white font-semibold text-sm mb-4">Historial de pagos</h2>
                    {payments.length === 0 ? (
                        <p className="text-neutral-500 text-sm">Sin pagos registrados</p>
                    ) : (
                        <div className="space-y-3">
                            {payments.slice(0, 5).map(payment => (
                                <div key={payment.id} className="flex items-center justify-between">
                                    <div>
                                        <div className="text-white text-sm font-medium">{formatMoney(payment.amount)}</div>
                                        <div className="text-neutral-500 text-xs">{payment.paymentMethod} · {formatDate(payment.createdAt)}</div>
                                    </div>
                                    <StatusBadge status={payment.status} />
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Historial completo de membresías */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                <div className="px-5 py-4 border-b border-neutral-800">
                    <h2 className="text-white font-semibold text-sm">Historial de membresías</h2>
                </div>
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-neutral-800">
                            {['Plan', 'Inicio', 'Vencimiento', 'Estado', 'Precio'].map(h => (
                                <th key={h} className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">{h}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {memberships.map(ms => (
                            <tr key={ms.id} className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/40 transition-colors">
                                <td className="px-4 py-3 text-white font-medium">{ms.planName}</td>
                                <td className="px-4 py-3 text-neutral-400">{formatDate(ms.startDate)}</td>
                                <td className="px-4 py-3 text-neutral-400">{formatDate(ms.endDate)}</td>
                                <td className="px-4 py-3"><StatusBadge status={ms.status} /></td>
                                <td className="px-4 py-3 text-neutral-400">{formatMoney(ms.pricePaid)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {/* Modal nueva membresía */}
            {showNewMembership && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
                    <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-md">
                        <div className="flex items-center justify-between mb-5">
                            <h2 className="text-white font-semibold">Nueva membresía</h2>
                            <button onClick={() => setShowNewMembership(false)} className="text-neutral-400 hover:text-white">✕</button>
                        </div>
                        <div className="space-y-4">
                            <div>
                                <label className="text-neutral-400 text-xs mb-2 block">Seleccionar plan</label>
                                <div className="space-y-2">
                                    {plans.map(plan => (
                                        <button
                                            key={plan.id}
                                            onClick={() => setSelectedPlanId(plan.id)}
                                            className={`w-full text-left px-4 py-3 rounded-lg border transition-colors ${selectedPlanId === plan.id
                                                ? 'border-violet-500 bg-violet-600/10'
                                                : 'border-neutral-700 hover:border-neutral-600'
                                                }`}
                                        >
                                            <div className="flex justify-between items-center">
                                                <span className="text-white text-sm font-medium">{plan.name}</span>
                                                <span className="text-violet-400 text-sm font-semibold">{formatMoney(plan.price)}</span>
                                            </div>
                                            <div className="text-neutral-500 text-xs mt-1">{plan.durationDays} días</div>
                                        </button>
                                    ))}
                                </div>
                            </div>
                            {createMsMut.isError && (
                                <p className="text-red-400 text-xs">{createMsMut.error?.response?.data?.message ?? 'Error'}</p>
                            )}
                            <div className="flex justify-end gap-3 pt-2">
                                <button onClick={() => setShowNewMembership(false)} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                                <button
                                    onClick={() => createMsMut.mutate()}
                                    disabled={!selectedPlanId || createMsMut.isPending}
                                    className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
                                >
                                    {createMsMut.isPending ? 'Creando...' : 'Crear membresía'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal congelar */}
            {showFreeze && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
                    <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-sm">
                        <div className="flex items-center justify-between mb-5">
                            <h2 className="text-white font-semibold">Congelar membresía</h2>
                            <button onClick={() => setShowFreeze(false)} className="text-neutral-400 hover:text-white">✕</button>
                        </div>
                        <div className="space-y-4">
                            <div>
                                <label className="text-neutral-400 text-xs mb-1 block">Fecha de inicio del congelamiento</label>
                                <input type="date" value={freezeDate} onChange={e => setFreezeDate(e.target.value)} className={inputClass} />
                            </div>
                            <p className="text-neutral-500 text-xs">El vencimiento se extenderá automáticamente por los días congelados.</p>
                            <div className="flex justify-end gap-3">
                                <button onClick={() => setShowFreeze(false)} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                                <button
                                    onClick={() => freezeMut.mutate(activeMembership.id)}
                                    disabled={!freezeDate || freezeMut.isPending}
                                    className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
                                >
                                    {freezeMut.isPending ? 'Congelando...' : 'Confirmar'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modal registrar pago */}
            {showPayment && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
                    <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-sm">
                        <div className="flex items-center justify-between mb-5">
                            <h2 className="text-white font-semibold">Registrar pago</h2>
                            <button onClick={() => setShowPayment(false)} className="text-neutral-400 hover:text-white">✕</button>
                        </div>
                        <div className="space-y-4">
                            <div>
                                <label className="text-neutral-400 text-xs mb-1 block">Monto</label>
                                <input
                                    type="number"
                                    value={paymentData.amount}
                                    onChange={e => setPaymentData(p => ({ ...p, amount: e.target.value }))}
                                    placeholder="5000"
                                    className={inputClass}
                                />
                            </div>
                            <div>
                                <label className="text-neutral-400 text-xs mb-1 block">Método de pago</label>
                                <select
                                    value={paymentData.paymentMethod}
                                    onChange={e => setPaymentData(p => ({ ...p, paymentMethod: e.target.value }))}
                                    className={inputClass}
                                >
                                    <option value="CASH">Efectivo</option>
                                    <option value="TRANSFER">Transferencia</option>
                                    <option value="CARD">Tarjeta</option>
                                    <option value="MP">Mercado Pago</option>
                                </select>
                            </div>
                            {paymentData.amount && (
                                <div className="bg-neutral-800 rounded-lg px-4 py-3 flex justify-between text-xs">
                                    <span className="text-neutral-400">Revenue share (10%)</span>
                                    <span className="text-violet-400 font-medium">{formatMoney(paymentData.amount * 0.1)}</span>
                                </div>
                            )}
                            {paymentMut.isError && (
                                <p className="text-red-400 text-xs">{paymentMut.error?.response?.data?.message ?? 'Error'}</p>
                            )}
                            <div className="flex justify-end gap-3">
                                <button onClick={() => setShowPayment(false)} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                                <button
                                    onClick={() => paymentMut.mutate(activeMembership.id)}
                                    disabled={!paymentData.amount || paymentMut.isPending}
                                    className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
                                >
                                    {paymentMut.isPending ? 'Registrando...' : 'Confirmar pago'}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Modales de confirmación */}
            <ConfirmModal
                open={confirmAction?.type === 'suspend'}
                title="Suspender socio"
                description="El socio no podrá ingresar al gimnasio mientras esté suspendido."
                confirmLabel="Suspender"
                confirmClass="bg-yellow-600 hover:bg-yellow-700"
                loading={suspendMut.isPending}
                onConfirm={() => suspendMut.mutate()}
                onCancel={() => setConfirmAction(null)}
            />
            <ConfirmModal
                open={confirmAction?.type === 'reactivate'}
                title="Reactivar socio"
                description="El socio podrá ingresar al gimnasio nuevamente."
                confirmLabel="Reactivar"
                confirmClass="bg-emerald-600 hover:bg-emerald-700"
                loading={reactivateMut.isPending}
                onConfirm={() => reactivateMut.mutate()}
                onCancel={() => setConfirmAction(null)}
            />
            <ConfirmModal
                open={confirmAction?.type === 'cancel'}
                title="Cancelar socio"
                description="Esta acción es definitiva y no se puede revertir."
                confirmLabel="Cancelar socio"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={cancelMut.isPending}
                onConfirm={() => cancelMut.mutate()}
                onCancel={() => setConfirmAction(null)}
            />
            <ConfirmModal
                open={confirmAction?.type === 'cancelMs'}
                title="Cancelar membresía"
                description="La membresía se cancelará y el socio pasará a estado Suspendido."
                confirmLabel="Cancelar membresía"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={cancelMsMut.isPending}
                onConfirm={() => cancelMsMut.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />

        </div>
    )
}
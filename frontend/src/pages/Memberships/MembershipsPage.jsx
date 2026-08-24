import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import {
    getMemberships, freezeMembership, unfreezeMembership, cancelMembership
} from '@/api/memberships'
import StatusBadge from '@/components/shared/StatusBadge'
import ConfirmModal from '@/components/shared/ConfirmModal'

function formatDate(dateStr) {
    if (!dateStr) return '—'
    return new Date(dateStr).toLocaleDateString('es-AR')
}

function formatMoney(amount) {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency', currency: 'ARS', maximumFractionDigits: 0
    }).format(amount ?? 0)
}

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-violet-500"

const STATUS_OPTIONS = [
    { value: 'ACTIVE', label: 'Activas' },
    { value: 'FROZEN', label: 'Congeladas' },
    { value: 'EXPIRED', label: 'Vencidas' },
    { value: 'CANCELLED', label: 'Canceladas' },
]

export default function MembershipsPage() {
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const [status, setStatus] = useState('ACTIVE')
    const [page, setPage] = useState(0)
    const [showFreeze, setShowFreeze] = useState(false)
    const [freezeTarget, setFreezeTarget] = useState(null)
    const [freezeDate, setFreezeDate] = useState('')
    const [confirmAction, setConfirmAction] = useState(null)

    const { data, isLoading } = useQuery({
        queryKey: ['memberships', status, page],
        queryFn: () => getMemberships({ status, page }),
    })

    const invalidate = () => queryClient.invalidateQueries({ queryKey: ['memberships'] })

    const freezeMut = useMutation({
        mutationFn: ({ id, date }) => freezeMembership(id, { frozenSince: date }),
        onSuccess: () => { invalidate(); setShowFreeze(false); setFreezeDate('') },
    })

    const unfreezeMut = useMutation({
        mutationFn: (id) => unfreezeMembership(id),
        onSuccess: () => { invalidate(); setConfirmAction(null) },
    })

    const cancelMut = useMutation({
        mutationFn: (id) => cancelMembership(id),
        onSuccess: () => { invalidate(); setConfirmAction(null) },
    })

    const memberships = data?.content ?? []
    const totalPages = data?.totalPages ?? 0

    return (
        <div className="space-y-5">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Membresías</h1>
                    <p className="text-neutral-400 text-sm mt-1">{data?.totalElements ?? 0} membresías</p>
                </div>
            </div>

            {/* Filtros de estado */}
            <div className="flex gap-2">
                {STATUS_OPTIONS.map(opt => (
                    <button
                        key={opt.value}
                        onClick={() => { setStatus(opt.value); setPage(0) }}
                        className={`px-3 py-2 text-xs rounded-lg font-medium transition-colors ${status === opt.value
                            ? 'bg-violet-600 text-white'
                            : 'bg-neutral-900 border border-neutral-700 text-neutral-400 hover:text-white'
                            }`}
                    >
                        {opt.label}
                    </button>
                ))}
            </div>

            {/* Tabla */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-neutral-800">
                            {['Socio', 'Plan', 'Inicio', 'Vencimiento', 'Días restantes', 'Precio', 'Estado', 'Acciones'].map(h => (
                                <th key={h} className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">{h}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={8} className="text-center text-neutral-400 py-12 text-sm">Cargando...</td></tr>
                        ) : memberships.length === 0 ? (
                            <tr><td colSpan={8} className="text-center text-neutral-400 py-12 text-sm">No hay membresías</td></tr>
                        ) : memberships.map(ms => (
                            <tr key={ms.id} className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/40 transition-colors">
                                <td className="px-4 py-3">
                                    <button
                                        onClick={() => navigate(`/members/${ms.memberId}`)}
                                        className="text-violet-400 hover:text-violet-300 font-medium transition-colors text-left"
                                    >
                                        {ms.memberFullName}
                                    </button>
                                </td>
                                <td className="px-4 py-3 text-neutral-300">{ms.planName}</td>
                                <td className="px-4 py-3 text-neutral-400">{formatDate(ms.startDate)}</td>
                                <td className="px-4 py-3">
                                    <span className={ms.daysUntilExpiration <= 7 && ms.status === 'ACTIVE' ? 'text-yellow-400 font-medium' : 'text-neutral-400'}>
                                        {formatDate(ms.endDate)}
                                    </span>
                                </td>
                                <td className="px-4 py-3">
                                    {ms.status === 'ACTIVE' ? (
                                        <span className={`text-sm font-medium ${ms.daysUntilExpiration <= 7 ? 'text-yellow-400' : 'text-neutral-300'}`}>
                                            {ms.daysUntilExpiration} días
                                        </span>
                                    ) : '—'}
                                </td>
                                <td className="px-4 py-3 text-neutral-400">{formatMoney(ms.pricePaid)}</td>
                                <td className="px-4 py-3"><StatusBadge status={ms.status} /></td>
                                <td className="px-4 py-3">
                                    <div className="flex gap-3">
                                        {ms.status === 'ACTIVE' && (
                                            <>
                                                <button
                                                    onClick={() => { setFreezeTarget(ms); setShowFreeze(true) }}
                                                    className="text-violet-400 hover:text-violet-300 text-xs transition-colors"
                                                >
                                                    Congelar
                                                </button>
                                                <button
                                                    onClick={() => setConfirmAction({ type: 'cancel', id: ms.id, name: ms.memberFullName })}
                                                    className="text-red-400 hover:text-red-300 text-xs transition-colors"
                                                >
                                                    Cancelar
                                                </button>
                                            </>
                                        )}
                                        {ms.status === 'FROZEN' && (
                                            <button
                                                onClick={() => setConfirmAction({ type: 'unfreeze', id: ms.id, name: ms.memberFullName })}
                                                className="text-emerald-400 hover:text-emerald-300 text-xs transition-colors"
                                            >
                                                Descongelar
                                            </button>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {totalPages > 1 && (
                    <div className="flex items-center justify-between px-4 py-3 border-t border-neutral-800">
                        <span className="text-neutral-400 text-xs">Página {page + 1} de {totalPages}</span>
                        <div className="flex gap-2">
                            <button onClick={() => setPage(p => p - 1)} disabled={page === 0}
                                className="px-3 py-1 text-xs rounded-lg bg-neutral-800 text-neutral-400 hover:text-white disabled:opacity-40 transition-colors">
                                Anterior
                            </button>
                            <button onClick={() => setPage(p => p + 1)} disabled={page >= totalPages - 1}
                                className="px-3 py-1 text-xs rounded-lg bg-neutral-800 text-neutral-400 hover:text-white disabled:opacity-40 transition-colors">
                                Siguiente
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* Modal congelar */}
            {showFreeze && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
                    <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-sm">
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-white font-semibold">Congelar membresía</h2>
                            <button onClick={() => setShowFreeze(false)} className="text-neutral-400 hover:text-white">✕</button>
                        </div>
                        <p className="text-neutral-400 text-sm mb-4">{freezeTarget?.memberFullName} · {freezeTarget?.planName}</p>
                        <div className="mb-4">
                            <label className="text-neutral-400 text-xs mb-1 block">Fecha de inicio del congelamiento</label>
                            <input type="date" value={freezeDate} onChange={e => setFreezeDate(e.target.value)} className={inputClass} />
                        </div>
                        <p className="text-neutral-500 text-xs mb-5">El vencimiento se extenderá automáticamente al descongelar.</p>
                        <div className="flex justify-end gap-3">
                            <button onClick={() => setShowFreeze(false)} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                            <button
                                onClick={() => freezeMut.mutate({ id: freezeTarget.id, date: freezeDate })}
                                disabled={!freezeDate || freezeMut.isPending}
                                className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
                            >
                                {freezeMut.isPending ? 'Congelando...' : 'Confirmar'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <ConfirmModal
                open={confirmAction?.type === 'unfreeze'}
                title="Descongelar membresía"
                description={`¿Descongelar la membresía de ${confirmAction?.name}? El vencimiento se extenderá automáticamente.`}
                confirmLabel="Descongelar"
                confirmClass="bg-emerald-600 hover:bg-emerald-700"
                loading={unfreezeMut.isPending}
                onConfirm={() => unfreezeMut.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />

            <ConfirmModal
                open={confirmAction?.type === 'cancel'}
                title="Cancelar membresía"
                description={`¿Cancelar la membresía de ${confirmAction?.name}? El socio pasará a estado Suspendido.`}
                confirmLabel="Cancelar membresía"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={cancelMut.isPending}
                onConfirm={() => cancelMut.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />
        </div>
    )
}
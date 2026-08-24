import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getPayments, getPaymentSummary, refundPayment } from '@/api/payments'
import StatusBadge from '@/components/shared/StatusBadge'
import ConfirmModal from '@/components/shared/ConfirmModal'

function formatMoney(amount) {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency', currency: 'ARS', maximumFractionDigits: 0
    }).format(amount ?? 0)
}

function formatDate(dateStr) {
    if (!dateStr) return '—'
    return new Date(dateStr).toLocaleDateString('es-AR')
}

const METHOD_LABELS = {
    CASH: 'Efectivo',
    TRANSFER: 'Transferencia',
    CARD: 'Tarjeta',
    MP: 'Mercado Pago',
    OTHER: 'Otro',
}

function getFirstDayOfMonth() {
    const d = new Date()
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-01`
}

function getToday() {
    const d = new Date()
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

export default function PaymentsPage() {
    const queryClient = useQueryClient()
    const [from, setFrom] = useState(getFirstDayOfMonth())
    const [to, setTo] = useState(getToday())
    const [page, setPage] = useState(0)
    const [confirmRefund, setConfirmRefund] = useState(null)

    const params = { from, to, page }

    const { data: paymentsData, isLoading } = useQuery({
        queryKey: ['payments', params],
        queryFn: () => getPayments(params),
    })

    const { data: summary } = useQuery({
        queryKey: ['payments', 'summary', from, to],
        queryFn: () => getPaymentSummary({ from, to }),
    })

    const refundMut = useMutation({
        mutationFn: (id) => refundPayment(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['payments'] })
            setConfirmRefund(null)
        },
    })

    const payments = paymentsData?.content ?? []
    const totalPages = paymentsData?.totalPages ?? 0

    return (
        <div className="space-y-5">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Pagos</h1>
                    <p className="text-neutral-400 text-sm mt-1">{paymentsData?.totalElements ?? 0} pagos en el período</p>
                </div>
                <div className="flex gap-2 items-center">
                    <input type="date" value={from} onChange={e => { setFrom(e.target.value); setPage(0) }}
                        className="bg-neutral-900 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-violet-500"
                    />
                    <span className="text-neutral-500 text-sm">→</span>
                    <input type="date" value={to} onChange={e => { setTo(e.target.value); setPage(0) }}
                        className="bg-neutral-900 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm focus:outline-none focus:border-violet-500"
                    />
                </div>
            </div>

            {/* Resumen financiero */}
            {summary && (
                <div className="grid grid-cols-4 gap-4">
                    {[
                        { label: 'Ingresos del período', value: formatMoney(summary.totalAmount) },
                        { label: 'Revenue share', value: formatMoney(summary.totalRevenueShare), color: 'text-violet-400' },
                        { label: 'Cantidad de pagos', value: summary.totalPayments },
                        { label: 'Promedio por pago', value: formatMoney(summary.averagePayment) },
                    ].map(({ label, value, color }) => (
                        <div key={label} className="bg-neutral-900 border border-neutral-800 rounded-xl p-4">
                            <div className="text-neutral-400 text-xs mb-2">{label}</div>
                            <div className={`text-xl font-semibold ${color ?? 'text-white'}`}>{value}</div>
                        </div>
                    ))}
                </div>
            )}

            {/* Tabla */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-neutral-800">
                            {['Socio', 'Monto', 'Método', 'Revenue share', 'Registrado por', 'Fecha', 'Estado', 'Acciones'].map(h => (
                                <th key={h} className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">{h}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={8} className="text-center text-neutral-400 py-12 text-sm">Cargando...</td></tr>
                        ) : payments.length === 0 ? (
                            <tr><td colSpan={8} className="text-center text-neutral-400 py-12 text-sm">No hay pagos en el período</td></tr>
                        ) : payments.map(payment => (
                            <tr key={payment.id} className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/40 transition-colors">
                                <td className="px-4 py-3 text-white font-medium">{payment.memberFullName}</td>
                                <td className="px-4 py-3 text-neutral-300 font-medium">{formatMoney(payment.amount)}</td>
                                <td className="px-4 py-3 text-neutral-400">{METHOD_LABELS[payment.paymentMethod] ?? payment.paymentMethod}</td>
                                <td className="px-4 py-3 text-violet-400">{formatMoney(payment.revenueShareAmount)}</td>
                                <td className="px-4 py-3 text-neutral-400">{payment.registeredByName ?? 'Sistema'}</td>
                                <td className="px-4 py-3 text-neutral-400">{formatDate(payment.createdAt)}</td>
                                <td className="px-4 py-3"><StatusBadge status={payment.status} /></td>
                                <td className="px-4 py-3">
                                    {payment.status === 'COMPLETED' && !payment.revenueSharePaid && (
                                        <button
                                            onClick={() => setConfirmRefund(payment)}
                                            className="text-yellow-400 hover:text-yellow-300 text-xs transition-colors"
                                        >
                                            Reembolsar
                                        </button>
                                    )}
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

            <ConfirmModal
                open={!!confirmRefund}
                title="Reembolsar pago"
                description={`¿Reembolsar el pago de ${formatMoney(confirmRefund?.amount)} de ${confirmRefund?.memberFullName}? Esta acción no se puede deshacer.`}
                confirmLabel="Reembolsar"
                confirmClass="bg-yellow-600 hover:bg-yellow-700"
                loading={refundMut.isPending}
                onConfirm={() => refundMut.mutate(confirmRefund.id)}
                onCancel={() => setConfirmRefund(null)}
            />
        </div>
    )
}
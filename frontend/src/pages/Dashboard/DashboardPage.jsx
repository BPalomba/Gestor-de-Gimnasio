import { useQuery } from '@tanstack/react-query'
import { getDashboard } from '@/api/dashboard'
import MetricCard from '@/components/shared/MetricCard'

function formatMoney(amount) {
    return new Intl.NumberFormat('es-AR', {
        style: 'currency',
        currency: 'ARS',
        maximumFractionDigits: 0,
    }).format(amount ?? 0)
}

function RevenueComparison({ current, previous }) {
    if (!previous || previous === 0) return null
    const diff = ((current - previous) / previous) * 100
    const isPositive = diff >= 0
    return (
        <span className={isPositive ? 'text-emerald-400' : 'text-red-400'}>
            {isPositive ? '+' : ''}{diff.toFixed(1)}% vs mes anterior
        </span>
    )
}

export default function DashboardPage() {
    const { data, isLoading, isError } = useQuery({
        queryKey: ['dashboard'],
        queryFn: getDashboard,
        refetchInterval: 60000, // refresca cada 1 minuto
    })

    if (isLoading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="text-neutral-400 text-sm">Cargando métricas...</div>
            </div>
        )
    }

    if (isError) {
        return (
            <div className="bg-red-950 border border-red-800 rounded-xl p-4">
                <p className="text-red-400 text-sm">Error al cargar el dashboard</p>
            </div>
        )
    }

    return (
        <div className="space-y-6">

            {/* Header */}
            <div>
                <h1 className="text-white text-2xl font-bold">Dashboard</h1>
                <p className="text-neutral-400 text-sm mt-1">Métricas en tiempo real del gimnasio</p>
            </div>

            {/* Métricas de socios */}
            <div>
                <h2 className="text-neutral-400 text-xs font-semibold uppercase tracking-widest mb-3">
                    Socios
                </h2>
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                    <MetricCard
                        label="Socios activos"
                        value={data.totalMembersActive}
                        sub={`+${data.newMembersThisMonth} este mes`}
                        subColor="text-emerald-400"
                    />
                    <MetricCard
                        label="Suspendidos"
                        value={data.totalMembersSuspended}
                        subColor="text-yellow-400"
                    />
                    <MetricCard
                        label="Cancelados"
                        value={data.totalMembersCancelled}
                        subColor="text-red-400"
                    />
                    <MetricCard
                        label="Membresías activas"
                        value={data.totalMembershipsActive}
                        sub={`${data.totalMembershipsFrozen} congeladas`}
                        subColor="text-violet-400"
                    />
                </div>
            </div>

            {/* Alertas de vencimientos */}
            {data.membershipsExpiringIn7Days > 0 && (
                <div className="bg-yellow-950 border border-yellow-800 rounded-xl px-4 py-3 flex items-center gap-3">
                    <div className="w-2 h-2 rounded-full bg-yellow-400 shrink-0" />
                    <p className="text-yellow-300 text-sm">
                        <span className="font-semibold">{data.membershipsExpiringIn7Days} membresías</span>
                        {' '}vencen en los próximos 7 días
                        {data.membershipsExpiringIn30Days > 0 && (
                            <span className="text-yellow-500">
                                {' '}· {data.membershipsExpiringIn30Days} en los próximos 30 días
                            </span>
                        )}
                    </p>
                </div>
            )}

            {/* Métricas financieras */}
            <div>
                <h2 className="text-neutral-400 text-xs font-semibold uppercase tracking-widest mb-3">
                    Financiero — mes actual
                </h2>
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                    <MetricCard
                        label="Ingresos del mes"
                        value={formatMoney(data.revenueThisMonth)}
                        sub={<RevenueComparison current={data.revenueThisMonth} previous={data.revenuePreviousMonth} />}
                    />
                    <MetricCard
                        label="Mes anterior"
                        value={formatMoney(data.revenuePreviousMonth)}
                    />
                    <MetricCard
                        label="Revenue share pendiente"
                        value={formatMoney(data.revenueSharePending)}
                        subColor="text-yellow-400"
                    />
                    <MetricCard
                        label="Pagos del mes"
                        value={data.totalPaymentsThisMonth}
                        sub="transacciones completadas"
                    />
                </div>
            </div>

            {/* Top planes */}
            {data.topPlans?.length > 0 && (
                <div>
                    <h2 className="text-neutral-400 text-xs font-semibold uppercase tracking-widest mb-3">
                        Planes más populares
                    </h2>
                    <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                        <table className="w-full text-sm">
                            <thead>
                                <tr className="border-b border-neutral-800">
                                    <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">Plan</th>
                                    <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">Membresías activas</th>
                                    <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">% del total</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data.topPlans.map((plan) => (
                                    <tr
                                        key={plan.planName}
                                        className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/50 transition-colors"
                                    >
                                        <td className="px-4 py-3 text-white font-medium">{plan.planName}</td>
                                        <td className="px-4 py-3">
                                            <div className="flex items-center gap-3">
                                                <div className="flex-1 bg-neutral-800 rounded-full h-1.5 max-w-24">
                                                    <div
                                                        className="h-1.5 rounded-full bg-violet-500"
                                                        style={{ width: plan.percentage }}
                                                    />
                                                </div>
                                                <span className="text-neutral-300">{plan.activeMemberships}</span>
                                            </div>
                                        </td>
                                        <td className="px-4 py-3 text-neutral-400">{plan.percentage}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

        </div>
    )
}
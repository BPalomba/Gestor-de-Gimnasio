const config = {
    ACTIVE: { label: 'Activo', className: 'bg-emerald-950 text-emerald-400 border border-emerald-800' },
    SUSPENDED: { label: 'Suspendido', className: 'bg-yellow-950 text-yellow-400 border border-yellow-800' },
    CANCELLED: { label: 'Cancelado', className: 'bg-red-950 text-red-400 border border-red-800' },
    FROZEN: { label: 'Congelado', className: 'bg-violet-950 text-violet-400 border border-violet-800' },
    EXPIRED: { label: 'Vencida', className: 'bg-red-950 text-red-400 border border-red-800' },
    COMPLETED: { label: 'Completado', className: 'bg-emerald-950 text-emerald-400 border border-emerald-800' },
    PENDING: { label: 'Pendiente', className: 'bg-blue-950 text-blue-400 border border-blue-800' },
    REFUNDED: { label: 'Reembolsado', className: 'bg-yellow-950 text-yellow-400 border border-yellow-800' },
}

export default function StatusBadge({ status }) {
    const { label, className } = config[status] ?? { label: status, className: 'bg-neutral-800 text-neutral-400' }
    return (
        <span className={`text-xs font-medium px-2 py-0.5 rounded-md ${className}`}>
            {label}
        </span>
    )
}
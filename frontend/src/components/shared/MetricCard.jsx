export default function MetricCard({ label, value, sub, subColor = 'text-neutral-400' }) {
    return (
        <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-4">
            <div className="text-neutral-400 text-xs mb-2">{label}</div>
            <div className="text-white text-2xl font-semibold">{value}</div>
            {sub && <div className={`text-xs mt-1 ${subColor}`}>{sub}</div>}
        </div>
    )
}
export default function ConfirmModal({ open, title, description, confirmLabel, confirmClass, onConfirm, onCancel, loading }) {
    if (!open) return null
    return (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-sm">
                <h3 className="text-white font-semibold text-base mb-2">{title}</h3>
                <p className="text-neutral-400 text-sm mb-6">{description}</p>
                <div className="flex justify-end gap-3">
                    <button
                        onClick={onCancel}
                        disabled={loading}
                        className="px-4 py-2 text-sm text-neutral-400 hover:text-white transition-colors"
                    >
                        Cancelar
                    </button>
                    <button
                        onClick={onConfirm}
                        disabled={loading}
                        className={`px-4 py-2 text-sm font-medium rounded-lg text-white transition-colors disabled:opacity-50 ${confirmClass}`}
                    >
                        {loading ? 'Procesando...' : confirmLabel}
                    </button>
                </div>
            </div>
        </div>
    )
}
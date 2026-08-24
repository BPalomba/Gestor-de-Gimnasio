import { useNavigate } from 'react-router-dom'

export default function NotFoundPage() {
    const navigate = useNavigate()

    return (
        <div className="min-h-screen bg-neutral-950 flex items-center justify-center px-4">
            <div className="text-center">
                <div className="text-violet-500 text-8xl font-black mb-4 select-none">404</div>
                <h1 className="text-white text-2xl font-bold mb-2">Página no encontrada</h1>
                <p className="text-neutral-400 text-sm mb-8">
                    La página que buscás no existe o fue movida.
                </p>
                <div className="flex gap-3 justify-center">
                    <button
                        onClick={() => navigate(-1)}
                        className="px-4 py-2 text-sm text-neutral-400 hover:text-white border border-neutral-700 hover:border-neutral-500 rounded-lg transition-colors"
                    >
                        ← Volver
                    </button>
                    <button
                        onClick={() => navigate('/dashboard')}
                        className="px-4 py-2 text-sm bg-violet-600 hover:bg-violet-700 text-white rounded-lg transition-colors"
                    >
                        Ir al dashboard
                    </button>
                </div>
            </div>
        </div>
    )
}
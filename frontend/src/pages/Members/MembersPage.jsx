import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate } from 'react-router-dom'
import { getMembers, searchMembers, createMember, suspendMember, cancelMember } from '@/api/members'
import StatusBadge from '@/components/shared/StatusBadge'
import ConfirmModal from '@/components/shared/ConfirmModal'
import MemberForm from './MemberForm'

const STATUS_OPTIONS = ['ACTIVE', 'SUSPENDED', 'CANCELLED']

export default function MembersPage() {
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const [statusCode, setStatusCode] = useState('ACTIVE')
    const [search, setSearch] = useState('')
    const [page, setPage] = useState(0)
    const [showForm, setShowForm] = useState(false)
    const [confirmAction, setConfirmAction] = useState(null)

    const queryKey = search
        ? ['members', 'search', search, page]
        : ['members', statusCode, page]

    const { data, isLoading } = useQuery({
        queryKey,
        queryFn: () => search
            ? searchMembers({ q: search, page })
            : getMembers({ statusCode, page }),
    })

    const createMutation = useMutation({
        mutationFn: createMember,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['members'] })
            setShowForm(false)
        },
    })

    const suspendMutation = useMutation({
        mutationFn: (id) => suspendMember(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['members'] })
            setConfirmAction(null)
        },
    })

    const cancelMutation = useMutation({
        mutationFn: (id) => cancelMember(id),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['members'] })
            setConfirmAction(null)
        },
    })

    const members = data?.content ?? []
    const totalPages = data?.totalPages ?? 0

    return (
        <div className="space-y-5">

            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Socios</h1>
                    <p className="text-neutral-400 text-sm mt-1">
                        {data?.totalElements ?? 0} socios encontrados
                    </p>
                </div>
                <button
                    onClick={() => setShowForm(true)}
                    className="bg-violet-600 hover:bg-violet-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                >
                    + Nuevo socio
                </button>
            </div>

            {/* Filtros */}
            <div className="flex gap-3 flex-wrap">
                <input
                    type="text"
                    placeholder="Buscar por nombre o DNI..."
                    value={search}
                    onChange={(e) => { setSearch(e.target.value); setPage(0) }}
                    className="bg-neutral-900 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500 w-64"
                />
                {!search && (
                    <div className="flex gap-2">
                        {STATUS_OPTIONS.map(s => (
                            <button
                                key={s}
                                onClick={() => { setStatusCode(s); setPage(0) }}
                                className={`px-3 py-2 text-xs rounded-lg font-medium transition-colors ${statusCode === s
                                    ? 'bg-violet-600 text-white'
                                    : 'bg-neutral-900 border border-neutral-700 text-neutral-400 hover:text-white'
                                    }`}
                            >
                                {s === 'ACTIVE' ? 'Activos' : s === 'SUSPENDED' ? 'Suspendidos' : 'Cancelados'}
                            </button>
                        ))}
                    </div>
                )}
            </div>

            {/* Tabla */}
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-neutral-800">
                            <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">Socio</th>
                            <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">DNI</th>
                            <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">Email</th>
                            <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">Estado</th>
                            <th className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={5} className="text-center text-neutral-400 py-12 text-sm">Cargando...</td></tr>
                        ) : members.length === 0 ? (
                            <tr><td colSpan={5} className="text-center text-neutral-400 py-12 text-sm">No hay socios</td></tr>
                        ) : members.map((member) => (
                            <tr
                                key={member.id}
                                className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/40 transition-colors"
                            >
                                <td className="px-4 py-3">
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 rounded-full bg-violet-600/20 flex items-center justify-center text-violet-400 text-xs font-semibold shrink-0">
                                            {member.firstName[0]}{member.lastName[0]}
                                        </div>
                                        <span className="text-white font-medium">
                                            {member.lastName}, {member.firstName}
                                        </span>
                                    </div>
                                </td>
                                <td className="px-4 py-3 text-neutral-400">{member.dni ?? '—'}</td>
                                <td className="px-4 py-3 text-neutral-400">{member.email ?? '—'}</td>
                                <td className="px-4 py-3">
                                    <StatusBadge status={member.statusCode} />
                                </td>
                                <td className="px-4 py-3">
                                    <div className="flex items-center gap-3">
                                        <button
                                            onClick={() => navigate(`/members/${member.id}`)}
                                            className="text-violet-400 hover:text-violet-300 text-xs transition-colors"
                                        >
                                            Ver
                                        </button>
                                        {member.statusCode === 'ACTIVE' && (
                                            <button
                                                onClick={() => setConfirmAction({ type: 'suspend', id: member.id, name: `${member.firstName} ${member.lastName}` })}
                                                className="text-yellow-400 hover:text-yellow-300 text-xs transition-colors"
                                            >
                                                Suspender
                                            </button>
                                        )}
                                        {member.statusCode === 'SUSPENDED' && (
                                            <button
                                                onClick={() => setConfirmAction({ type: 'cancel', id: member.id, name: `${member.firstName} ${member.lastName}` })}
                                                className="text-red-400 hover:text-red-300 text-xs transition-colors"
                                            >
                                                Cancelar
                                            </button>
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>

                {/* Paginación */}
                {totalPages > 1 && (
                    <div className="flex items-center justify-between px-4 py-3 border-t border-neutral-800">
                        <span className="text-neutral-400 text-xs">
                            Página {page + 1} de {totalPages}
                        </span>
                        <div className="flex gap-2">
                            <button
                                onClick={() => setPage(p => p - 1)}
                                disabled={page === 0}
                                className="px-3 py-1 text-xs rounded-lg bg-neutral-800 text-neutral-400 hover:text-white disabled:opacity-40 transition-colors"
                            >
                                Anterior
                            </button>
                            <button
                                onClick={() => setPage(p => p + 1)}
                                disabled={page >= totalPages - 1}
                                className="px-3 py-1 text-xs rounded-lg bg-neutral-800 text-neutral-400 hover:text-white disabled:opacity-40 transition-colors"
                            >
                                Siguiente
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* Modal nuevo socio */}
            {showForm && (
                <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
                    <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-md">
                        <div className="flex items-center justify-between mb-5">
                            <h2 className="text-white font-semibold text-base">Nuevo socio</h2>
                            <button onClick={() => setShowForm(false)} className="text-neutral-400 hover:text-white">✕</button>
                        </div>
                        <MemberForm
                            onSubmit={(data) => createMutation.mutate(data)}
                            loading={createMutation.isPending}
                        />
                        {createMutation.isError && (
                            <p className="text-red-400 text-xs mt-3">
                                {createMutation.error?.response?.data?.message ?? 'Error al crear el socio'}
                            </p>
                        )}
                    </div>
                </div>
            )}

            {/* Modales de confirmación */}
            <ConfirmModal
                open={confirmAction?.type === 'suspend'}
                title="Suspender socio"
                description={`¿Suspender a ${confirmAction?.name}? No podrá ingresar al gimnasio mientras esté suspendido.`}
                confirmLabel="Suspender"
                confirmClass="bg-yellow-600 hover:bg-yellow-700"
                loading={suspendMutation.isPending}
                onConfirm={() => suspendMutation.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />

            <ConfirmModal
                open={confirmAction?.type === 'cancel'}
                title="Cancelar socio"
                description={`¿Cancelar definitivamente a ${confirmAction?.name}? Esta acción no se puede revertir.`}
                confirmLabel="Cancelar socio"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={cancelMutation.isPending}
                onConfirm={() => cancelMutation.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />

        </div>
    )
}
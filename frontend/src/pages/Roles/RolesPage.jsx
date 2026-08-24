import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getRoles, getPermissions, createRole, updateRole } from '@/api/roles'

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"

function RoleModal({ role, permissions, onClose, onSave, loading, error }) {
    const [name, setName] = useState(role?.name ?? '')
    const [description, setDescription] = useState(role?.description ?? '')
    const [selected, setSelected] = useState(
        new Set(role?.permissions?.map(p => p.id) ?? [])
    )

    const togglePermission = (id) => {
        setSelected(prev => {
            const next = new Set(prev)
            next.has(id) ? next.delete(id) : next.add(id)
            return next
        })
    }

    const toggleModule = (modulePerms) => {
        const ids = modulePerms.map(p => p.id)
        const allSelected = ids.every(id => selected.has(id))
        setSelected(prev => {
            const next = new Set(prev)
            ids.forEach(id => allSelected ? next.delete(id) : next.add(id))
            return next
        })
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        onSave({ name, description, permissionIds: [...selected] })
    }

    // Agrupar permisos por módulo
    const grouped = permissions.reduce((acc, p) => {
        if (!acc[p.module]) acc[p.module] = []
        acc[p.module].push(p)
        return acc
    }, {})

    return (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4 overflow-y-auto py-8">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-lg">
                <div className="flex items-center justify-between mb-5">
                    <h2 className="text-white font-semibold">{role ? 'Editar rol' : 'Nuevo rol'}</h2>
                    <button onClick={onClose} className="text-neutral-400 hover:text-white">✕</button>
                </div>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Nombre *</label>
                            <input value={name} onChange={e => setName(e.target.value)} placeholder="Recepcionista" className={inputClass} required />
                        </div>
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Descripción</label>
                            <input value={description} onChange={e => setDescription(e.target.value)} placeholder="Descripción opcional" className={inputClass} />
                        </div>
                    </div>

                    <div>
                        <div className="flex items-center justify-between mb-2">
                            <label className="text-neutral-400 text-xs">Permisos</label>
                            <span className="text-violet-400 text-xs font-medium">{selected.size} de {permissions.length} seleccionados</span>
                        </div>

                        <div className="space-y-2 max-h-72 overflow-y-auto pr-1">
                            {Object.entries(grouped).map(([module, perms]) => {
                                const allSelected = perms.every(p => selected.has(p.id))
                                return (
                                    <div key={module} className="border border-neutral-700 rounded-lg overflow-hidden">
                                        <div className="flex items-center justify-between px-3 py-2 bg-neutral-800">
                                            <span className="text-neutral-300 text-xs font-medium">{module}</span>
                                            <button
                                                type="button"
                                                onClick={() => toggleModule(perms)}
                                                className="text-violet-400 hover:text-violet-300 text-xs transition-colors"
                                            >
                                                {allSelected ? 'Quitar todos' : 'Seleccionar todos'}
                                            </button>
                                        </div>
                                        <div className="px-3 py-2 grid grid-cols-2 gap-1">
                                            {perms.map(p => (
                                                <label key={p.id} className={`flex items-center gap-2 cursor-pointer px-2 py-1.5 rounded-md transition-colors text-xs ${selected.has(p.id) ? 'bg-violet-600/10 text-violet-300' : 'text-neutral-400 hover:text-neutral-300'}`}>
                                                    <input
                                                        type="checkbox"
                                                        checked={selected.has(p.id)}
                                                        onChange={() => togglePermission(p.id)}
                                                        className="w-3.5 h-3.5 accent-violet-500"
                                                    />
                                                    {p.description}
                                                </label>
                                            ))}
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </div>

                    {error && <p className="text-red-400 text-xs">{error}</p>}

                    <div className="flex justify-end gap-3 pt-2">
                        <button type="button" onClick={onClose} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                        <button
                            type="submit"
                            disabled={loading || selected.size === 0}
                            className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors"
                        >
                            {loading ? 'Guardando...' : 'Guardar rol'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default function RolesPage() {
    const queryClient = useQueryClient()
    const [showModal, setShowModal] = useState(false)
    const [editing, setEditing] = useState(null)

    const { data: roles = [], isLoading } = useQuery({
        queryKey: ['roles'],
        queryFn: getRoles,
    })

    const { data: permissions = [] } = useQuery({
        queryKey: ['permissions'],
        queryFn: getPermissions,
        enabled: showModal || !!editing,
    })

    const invalidate = () => queryClient.invalidateQueries({ queryKey: ['roles'] })

    const createMut = useMutation({
        mutationFn: createRole,
        onSuccess: () => { invalidate(); setShowModal(false) },
    })

    const updateMut = useMutation({
        mutationFn: ({ id, data }) => updateRole(id, data),
        onSuccess: () => { invalidate(); setEditing(null) },
    })

    return (
        <div className="space-y-5">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Roles y permisos</h1>
                    <p className="text-neutral-400 text-sm mt-1">{roles.length} roles definidos</p>
                </div>
                <button onClick={() => setShowModal(true)} className="bg-violet-600 hover:bg-violet-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors">
                    + Nuevo rol
                </button>
            </div>

            <div className="space-y-3">
                {isLoading ? (
                    <p className="text-neutral-400 text-sm">Cargando...</p>
                ) : roles.length === 0 ? (
                    <p className="text-neutral-400 text-sm">No hay roles definidos</p>
                ) : roles.map(role => (
                    <div key={role.id} className="bg-neutral-900 border border-neutral-800 rounded-xl p-5">
                        <div className="flex items-start justify-between mb-3">
                            <div>
                                <div className="text-white font-semibold">{role.name}</div>
                                {role.description && <div className="text-neutral-500 text-xs mt-0.5">{role.description}</div>}
                            </div>
                            <button onClick={() => setEditing(role)} className="text-violet-400 hover:text-violet-300 text-xs transition-colors">
                                Editar
                            </button>
                        </div>
                        <div className="flex flex-wrap gap-1.5">
                            {role.permissions?.map(p => (
                                <span key={p.id} className="text-xs bg-neutral-800 text-neutral-400 px-2 py-0.5 rounded-md">
                                    {p.code}
                                </span>
                            ))}
                        </div>
                    </div>
                ))}
            </div>

            {showModal && (
                <RoleModal
                    permissions={permissions}
                    onClose={() => setShowModal(false)}
                    onSave={(data) => createMut.mutate(data)}
                    loading={createMut.isPending}
                    error={createMut.error?.response?.data?.message}
                />
            )}

            {editing && (
                <RoleModal
                    role={editing}
                    permissions={permissions}
                    onClose={() => setEditing(null)}
                    onSave={(data) => updateMut.mutate({ id: editing.id, data })}
                    loading={updateMut.isPending}
                    error={updateMut.error?.response?.data?.message}
                />
            )}
        </div>
    )
}
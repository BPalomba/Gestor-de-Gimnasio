import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getUsers, createUser, deactivateUser, reactivateUser } from '@/api/users'
import { getRoles } from '@/api/roles'
import ConfirmModal from '@/components/shared/ConfirmModal'

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"

function UserModal({ onClose, onSave, loading, error, roles }) {
    const [form, setForm] = useState({
        firstName: '', lastName: '', email: '', password: '', roleId: ''
    })

    const handleSubmit = (e) => {
        e.preventDefault()
        onSave(form)
    }

    return (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-md">
                <div className="flex items-center justify-between mb-5">
                    <h2 className="text-white font-semibold">Nuevo usuario</h2>
                    <button onClick={onClose} className="text-neutral-400 hover:text-white">✕</button>
                </div>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Nombre *</label>
                            <input value={form.firstName} onChange={e => setForm(f => ({ ...f, firstName: e.target.value }))} placeholder="Juan" className={inputClass} required />
                        </div>
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Apellido *</label>
                            <input value={form.lastName} onChange={e => setForm(f => ({ ...f, lastName: e.target.value }))} placeholder="Pérez" className={inputClass} required />
                        </div>
                    </div>
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Email *</label>
                        <input type="email" value={form.email} onChange={e => setForm(f => ({ ...f, email: e.target.value }))} placeholder="juan@gimnasio.com" className={inputClass} required />
                    </div>
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Contraseña *</label>
                        <input type="password" value={form.password} onChange={e => setForm(f => ({ ...f, password: e.target.value }))} placeholder="Mínimo 8 caracteres" className={inputClass} required />
                    </div>
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Rol *</label>
                        <select value={form.roleId} onChange={e => setForm(f => ({ ...f, roleId: e.target.value }))} className={inputClass} required>
                            <option value="">Seleccionar rol...</option>
                            {roles.map(role => (
                                <option key={role.id} value={role.id}>{role.name}</option>
                            ))}
                        </select>
                    </div>
                    {error && <p className="text-red-400 text-xs">{error}</p>}
                    <div className="flex justify-end gap-3 pt-2">
                        <button type="button" onClick={onClose} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                        <button type="submit" disabled={loading} className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors">
                            {loading ? 'Guardando...' : 'Crear usuario'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default function UsersPage() {
    const queryClient = useQueryClient()
    const [showModal, setShowModal] = useState(false)
    const [confirmAction, setConfirmAction] = useState(null)

    const { data: users = [], isLoading } = useQuery({
        queryKey: ['users'],
        queryFn: getUsers,
    })

    const { data: roles = [] } = useQuery({
        queryKey: ['roles'],
        queryFn: getRoles,
        enabled: showModal,
    })

    const invalidate = () => queryClient.invalidateQueries({ queryKey: ['users'] })

    const createMut = useMutation({
        mutationFn: createUser,
        onSuccess: () => { invalidate(); setShowModal(false) },
    })

    const deactivateMut = useMutation({
        mutationFn: (id) => deactivateUser(id),
        onSuccess: () => { invalidate(); setConfirmAction(null) },
    })

    const reactivateMut = useMutation({
        mutationFn: (id) => reactivateUser(id),
        onSuccess: () => { invalidate(); setConfirmAction(null) },
    })

    return (
        <div className="space-y-5">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Usuarios</h1>
                    <p className="text-neutral-400 text-sm mt-1">{users.length} operadores del sistema</p>
                </div>
                <button onClick={() => setShowModal(true)} className="bg-violet-600 hover:bg-violet-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors">
                    + Nuevo usuario
                </button>
            </div>

            <div className="bg-neutral-900 border border-neutral-800 rounded-xl overflow-hidden">
                <table className="w-full text-sm">
                    <thead>
                        <tr className="border-b border-neutral-800">
                            {['Usuario', 'Email', 'Rol', 'Estado', 'Último login', 'Acciones'].map(h => (
                                <th key={h} className="text-left text-neutral-400 text-xs font-medium uppercase tracking-wide px-4 py-3">{h}</th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {isLoading ? (
                            <tr><td colSpan={6} className="text-center text-neutral-400 py-12 text-sm">Cargando...</td></tr>
                        ) : users.length === 0 ? (
                            <tr><td colSpan={6} className="text-center text-neutral-400 py-12 text-sm">No hay usuarios</td></tr>
                        ) : users.map(user => (
                            <tr key={user.id} className="border-b border-neutral-800 last:border-0 hover:bg-neutral-800/40 transition-colors">
                                <td className="px-4 py-3">
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 rounded-full bg-violet-600/20 flex items-center justify-center text-violet-400 text-xs font-semibold">
                                            {user.firstName[0]}{user.lastName[0]}
                                        </div>
                                        <span className="text-white font-medium">{user.firstName} {user.lastName}</span>
                                    </div>
                                </td>
                                <td className="px-4 py-3 text-neutral-400">{user.email}</td>
                                <td className="px-4 py-3">
                                    <span className="text-xs bg-violet-950 text-violet-400 border border-violet-800 px-2 py-0.5 rounded-md">
                                        {user.roleName}
                                    </span>
                                </td>
                                <td className="px-4 py-3">
                                    <span className={`text-xs font-medium px-2 py-0.5 rounded-md ${user.active ? 'bg-emerald-950 text-emerald-400 border border-emerald-800' : 'bg-neutral-800 text-neutral-400'}`}>
                                        {user.active ? 'Activo' : 'Inactivo'}
                                    </span>
                                </td>
                                <td className="px-4 py-3 text-neutral-400 text-xs">
                                    {user.lastLoginAt ? new Date(user.lastLoginAt).toLocaleDateString('es-AR') : 'Nunca'}
                                </td>
                                <td className="px-4 py-3">
                                    {user.active ? (
                                        <button onClick={() => setConfirmAction({ type: 'deactivate', id: user.id, name: `${user.firstName} ${user.lastName}` })}
                                            className="text-red-400 hover:text-red-300 text-xs transition-colors">
                                            Desactivar
                                        </button>
                                    ) : (
                                        <button onClick={() => setConfirmAction({ type: 'reactivate', id: user.id, name: `${user.firstName} ${user.lastName}` })}
                                            className="text-emerald-400 hover:text-emerald-300 text-xs transition-colors">
                                            Reactivar
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {showModal && (
                <UserModal
                    roles={roles}
                    onClose={() => setShowModal(false)}
                    onSave={(data) => createMut.mutate(data)}
                    loading={createMut.isPending}
                    error={createMut.error?.response?.data?.message}
                />
            )}

            <ConfirmModal
                open={confirmAction?.type === 'deactivate'}
                title="Desactivar usuario"
                description={`¿Desactivar a ${confirmAction?.name}? No podrá iniciar sesión hasta ser reactivado.`}
                confirmLabel="Desactivar"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={deactivateMut.isPending}
                onConfirm={() => deactivateMut.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />

            <ConfirmModal
                open={confirmAction?.type === 'reactivate'}
                title="Reactivar usuario"
                description={`¿Reactivar a ${confirmAction?.name}? Podrá volver a iniciar sesión.`}
                confirmLabel="Reactivar"
                confirmClass="bg-emerald-600 hover:bg-emerald-700"
                loading={reactivateMut.isPending}
                onConfirm={() => reactivateMut.mutate(confirmAction.id)}
                onCancel={() => setConfirmAction(null)}
            />
        </div>
    )
}
import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getBranches, createBranch, updateBranch, deactivateBranch } from '@/api/branches'
import ConfirmModal from '@/components/shared/ConfirmModal'

const inputClass = "w-full bg-neutral-800 border border-neutral-700 rounded-lg px-3 py-2 text-white text-sm placeholder:text-neutral-500 focus:outline-none focus:border-violet-500"

function BranchModal({ branch, onClose, onSave, loading, error }) {
    const [form, setForm] = useState({
        name: branch?.name ?? '',
        address: branch?.address ?? '',
        city: branch?.city ?? '',
        province: branch?.province ?? '',
        phone: branch?.phone ?? '',
    })

    const handleSubmit = (e) => {
        e.preventDefault()
        onSave(form)
    }

    return (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 px-4">
            <div className="bg-neutral-900 border border-neutral-800 rounded-xl p-6 w-full max-w-md">
                <div className="flex items-center justify-between mb-5">
                    <h2 className="text-white font-semibold">{branch ? 'Editar sucursal' : 'Nueva sucursal'}</h2>
                    <button onClick={onClose} className="text-neutral-400 hover:text-white">✕</button>
                </div>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Nombre *</label>
                        <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} placeholder="Sede Central" className={inputClass} required />
                    </div>
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Dirección</label>
                        <input value={form.address} onChange={e => setForm(f => ({ ...f, address: e.target.value }))} placeholder="Av. Corrientes 1234" className={inputClass} />
                    </div>
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Ciudad</label>
                            <input value={form.city} onChange={e => setForm(f => ({ ...f, city: e.target.value }))} placeholder="Buenos Aires" className={inputClass} />
                        </div>
                        <div>
                            <label className="text-neutral-400 text-xs mb-1 block">Provincia</label>
                            <input value={form.province} onChange={e => setForm(f => ({ ...f, province: e.target.value }))} placeholder="Buenos Aires" className={inputClass} />
                        </div>
                    </div>
                    <div>
                        <label className="text-neutral-400 text-xs mb-1 block">Teléfono</label>
                        <input value={form.phone} onChange={e => setForm(f => ({ ...f, phone: e.target.value }))} placeholder="11 5566 7788" className={inputClass} />
                    </div>
                    {error && <p className="text-red-400 text-xs">{error}</p>}
                    <div className="flex justify-end gap-3 pt-2">
                        <button type="button" onClick={onClose} className="text-sm text-neutral-400 hover:text-white px-4 py-2">Cancelar</button>
                        <button type="submit" disabled={loading} className="bg-violet-600 hover:bg-violet-700 disabled:opacity-50 text-white text-sm font-medium px-5 py-2 rounded-lg transition-colors">
                            {loading ? 'Guardando...' : 'Guardar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default function BranchesPage() {
    const queryClient = useQueryClient()
    const [showModal, setShowModal] = useState(false)
    const [editing, setEditing] = useState(null)
    const [confirmDeactivate, setConfirmDeactivate] = useState(null)

    const { data: branches = [], isLoading } = useQuery({
        queryKey: ['branches'],
        queryFn: getBranches,
    })

    const invalidate = () => queryClient.invalidateQueries({ queryKey: ['branches'] })

    const createMut = useMutation({
        mutationFn: createBranch,
        onSuccess: () => { invalidate(); setShowModal(false) },
    })

    const updateMut = useMutation({
        mutationFn: ({ id, data }) => updateBranch(id, data),
        onSuccess: () => { invalidate(); setEditing(null) },
    })

    const deactivateMut = useMutation({
        mutationFn: (id) => deactivateBranch(id),
        onSuccess: () => { invalidate(); setConfirmDeactivate(null) },
    })

    return (
        <div className="space-y-5">
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-white text-2xl font-bold">Sucursales</h1>
                    <p className="text-neutral-400 text-sm mt-1">{branches.length} sucursales activas</p>
                </div>
                <button onClick={() => setShowModal(true)} className="bg-violet-600 hover:bg-violet-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors">
                    + Nueva sucursal
                </button>
            </div>

            <div className="grid grid-cols-3 gap-4">
                {isLoading ? (
                    <p className="text-neutral-400 text-sm col-span-3">Cargando...</p>
                ) : branches.length === 0 ? (
                    <p className="text-neutral-400 text-sm col-span-3">No hay sucursales</p>
                ) : branches.map(branch => (
                    <div key={branch.id} className="bg-neutral-900 border border-neutral-800 rounded-xl p-5">
                        <div className="flex items-start justify-between mb-3">
                            <div className="w-9 h-9 rounded-lg bg-violet-600/20 flex items-center justify-center text-violet-400 font-bold text-sm shrink-0">
                                {branch.name[0]}
                            </div>
                            <div className="flex gap-2">
                                <button onClick={() => setEditing(branch)} className="text-violet-400 hover:text-violet-300 text-xs transition-colors">Editar</button>
                                <button onClick={() => setConfirmDeactivate(branch)} className="text-red-400 hover:text-red-300 text-xs transition-colors">Desactivar</button>
                            </div>
                        </div>
                        <div className="text-white font-semibold text-sm mb-1">{branch.name}</div>
                        {branch.address && <div className="text-neutral-500 text-xs">{branch.address}</div>}
                        {branch.city && <div className="text-neutral-500 text-xs">{branch.city}, {branch.province}</div>}
                        {branch.phone && <div className="text-neutral-500 text-xs mt-1">{branch.phone}</div>}
                    </div>
                ))}
            </div>

            {showModal && (
                <BranchModal
                    onClose={() => setShowModal(false)}
                    onSave={(data) => createMut.mutate(data)}
                    loading={createMut.isPending}
                    error={createMut.error?.response?.data?.message}
                />
            )}

            {editing && (
                <BranchModal
                    branch={editing}
                    onClose={() => setEditing(null)}
                    onSave={(data) => updateMut.mutate({ id: editing.id, data })}
                    loading={updateMut.isPending}
                    error={updateMut.error?.response?.data?.message}
                />
            )}

            <ConfirmModal
                open={!!confirmDeactivate}
                title="Desactivar sucursal"
                description={`¿Desactivar "${confirmDeactivate?.name}"? Los socios asignados a esta sucursal no se verán afectados.`}
                confirmLabel="Desactivar"
                confirmClass="bg-red-600 hover:bg-red-700"
                loading={deactivateMut.isPending}
                onConfirm={() => deactivateMut.mutate(confirmDeactivate.id)}
                onCancel={() => setConfirmDeactivate(null)}
            />
        </div>
    )
}
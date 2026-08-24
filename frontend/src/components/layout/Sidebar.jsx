import { NavLink, useNavigate } from 'react-router-dom'
import useAuthStore from '@/store/authStore'

const navItems = [
    { label: 'Dashboard', path: '/dashboard', permission: 'DASHBOARD_VIEW' },
    { label: 'Socios', path: '/members', permission: 'MEMBER_VIEW' },
    { label: 'Membresías', path: '/memberships', permission: 'MEMBERSHIP_VIEW' },
    { label: 'Planes', path: '/plans', permission: 'PLAN_VIEW' },
    { label: 'Pagos', path: '/payments', permission: 'PAYMENT_VIEW' },
    { label: 'Sucursales', path: '/branches', permission: 'BRANCH_VIEW' },
    { label: 'Usuarios', path: '/users', permission: 'USER_VIEW' },
    { label: 'Roles', path: '/roles', permission: 'ROLE_VIEW' },
]

export default function Sidebar() {
    const { user, permissions, logout } = useAuthStore()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    return (
        <aside className="w-52 min-h-screen bg-neutral-900 border-r border-neutral-800 flex flex-col">
            <div className="p-4 border-b border-neutral-800 flex items-center gap-3">
                <div className="w-7 h-7 bg-violet-600 rounded-lg shrink-0" />
                <span className="text-white font-bold text-sm">GymSaaS</span>
            </div>

            <nav className="flex-1 py-4">
                {navItems
                    .filter(item => permissions.includes(item.permission))
                    .map(item => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            className={({ isActive }) =>
                                `block px-4 py-2 text-sm transition-colors border-l-2 ${isActive
                                    ? 'text-white bg-violet-600/10 border-violet-500'
                                    : 'text-neutral-400 hover:text-white border-transparent hover:bg-neutral-800'
                                }`
                            }
                        >
                            {item.label}
                        </NavLink>
                    ))}
            </nav>

            <div className="p-4 border-t border-neutral-800">
                <div className="text-neutral-400 text-xs mb-1">{user?.email}</div>
                <div className="text-neutral-500 text-xs mb-3">{user?.role}</div>
                <button
                    onClick={handleLogout}
                    className="w-full text-left text-xs text-red-400 hover:text-red-300 transition-colors"
                >
                    Cerrar sesión
                </button>
            </div>
        </aside>
    )
}
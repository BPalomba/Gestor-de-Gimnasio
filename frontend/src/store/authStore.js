import { create } from 'zustand'
import { persist } from 'zustand/middleware'

const useAuthStore = create(
    persist(
        (set) => ({
            token: null,
            user: null,
            permissions: [],

            setAuth: (token, user, permissions) =>
                set({ token, user, permissions }),

            logout: () => {
                localStorage.removeItem('token')
                set({ token: null, user: null, permissions: [] })
            },

            hasPermission: (permission) => {
                const { permissions } = useAuthStore.getState()
                return permissions.includes(permission)
            },
        }),
        {
            name: 'auth-storage',
        }
    )
)

export default useAuthStore
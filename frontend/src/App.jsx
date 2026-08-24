import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import LoginPage from '@/pages/Login/LoginPage'
import DashboardPage from '@/pages/Dashboard/DashboardPage'
import MembersPage from '@/pages/Members/MembersPage'
import MemberDetailPage from '@/pages/Members/MemberDetailPage'
import PlansPage from '@/pages/Plans/PlansPage'
import PaymentsPage from '@/pages/Payments/PaymentsPage'
import MembershipsPage from '@/pages/Memberships/MembershipsPage'
import BranchesPage from '@/pages/Branches/BranchesPage'
import UsersPage from '@/pages/Users/UsersPage'
import RolesPage from '@/pages/Roles/RolesPage'
import NotFoundPage from '@/pages/NotFound/NotFoundPage'

import AppLayout from '@/components/layout/AppLayout'
import useAuthStore from '@/store/authStore'

const queryClient = new QueryClient()

function ProtectedRoute({ children }) {
  const token = useAuthStore((s) => s.token)
  if (!token) return <Navigate to="/login" replace />
  return <AppLayout>{children}</AppLayout>
}


export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
          <Route path="/members" element={<ProtectedRoute><MembersPage /></ProtectedRoute>} />
          <Route path="/members/:id" element={<ProtectedRoute><MemberDetailPage /></ProtectedRoute>} />
          <Route path="/memberships" element={<ProtectedRoute><MembershipsPage /></ProtectedRoute>} />
          <Route path="/plans" element={<ProtectedRoute><PlansPage /></ProtectedRoute>} />
          <Route path="/payments" element={<ProtectedRoute><PaymentsPage /></ProtectedRoute>} />
          <Route path="/branches" element={<ProtectedRoute><BranchesPage /></ProtectedRoute>} />
          <Route path="/users" element={<ProtectedRoute><UsersPage /></ProtectedRoute>} />
          <Route path="/roles" element={<ProtectedRoute><RolesPage /></ProtectedRoute>} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  )
}
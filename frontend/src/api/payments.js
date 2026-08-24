import api from './axios'

export const getPayments = async ({ from, to, page = 0, size = 20 }) => {
    const response = await api.get('/api/payments', {
        params: { from, to, page, size }
    })
    return response.data.data
}

export const getPaymentSummary = async ({ from, to }) => {
    const response = await api.get('/api/payments/summary', { params: { from, to } })
    return response.data.data
}

export const getPaymentsByMember = async (memberId) => {
    const response = await api.get(`/api/payments/member/${memberId}`)
    return response.data.data
}

export const createPayment = async (data) => {
    const response = await api.post('/api/payments', data)
    return response.data.data
}

export const refundPayment = async (id) => {
    const response = await api.patch(`/api/payments/${id}/refund`)
    return response.data.data
}
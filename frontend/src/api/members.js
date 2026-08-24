import api from './axios'

export const getMembers = async ({ statusCode = 'ACTIVE', page = 0, size = 20 }) => {
    const response = await api.get('/api/members', {
        params: { statusCode, page, size }
    })
    return response.data.data
}

export const searchMembers = async ({ q, page = 0, size = 20 }) => {
    const response = await api.get('/api/members/search', {
        params: { q, page, size }
    })
    return response.data.data
}

export const getMemberById = async (id) => {
    const response = await api.get(`/api/members/${id}`)
    return response.data.data
}

export const createMember = async (data) => {
    const response = await api.post('/api/members', data)
    return response.data.data
}

export const updateMember = async (id, data) => {
    const response = await api.put(`/api/members/${id}`, data)
    return response.data.data
}

export const suspendMember = async (id) => {
    await api.patch(`/api/members/${id}/suspend`)
}

export const reactivateMember = async (id) => {
    await api.patch(`/api/members/${id}/reactivate`)
}

export const cancelMember = async (id) => {
    await api.delete(`/api/members/${id}`)
}
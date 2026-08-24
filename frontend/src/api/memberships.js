import api from './axios'

export const getMembershipsByMember = async (memberId) => {
    const response = await api.get(`/api/memberships/member/${memberId}`)
    return response.data.data
}

export const getActiveMembership = async (memberId) => {
    const response = await api.get(`/api/memberships/member/${memberId}/active`)
    return response.data.data
}

export const createMembership = async (data) => {
    const response = await api.post('/api/memberships', data)
    return response.data.data
}

export const freezeMembership = async (id, data) => {
    const response = await api.patch(`/api/memberships/${id}/freeze`, data)
    return response.data.data
}

export const unfreezeMembership = async (id) => {
    const response = await api.patch(`/api/memberships/${id}/unfreeze`)
    return response.data.data
}

export const cancelMembership = async (id) => {
    await api.delete(`/api/memberships/${id}`)
}

export const getMemberships = async ({ status, page = 0, size = 20 }) => {
    const response = await api.get('/api/memberships', {
        params: {
            ...(status && { status }),
            page,
            size
        }
    })
    return response.data.data
}
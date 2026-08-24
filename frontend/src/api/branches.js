import api from './axios'

export const getBranches = async () => {
    const response = await api.get('/api/branches')
    return response.data.data
}

export const createBranch = async (data) => {
    const response = await api.post('/api/branches', data)
    return response.data.data
}

export const updateBranch = async (id, data) => {
    const response = await api.put(`/api/branches/${id}`, data)
    return response.data.data
}

export const deactivateBranch = async (id) => {
    await api.delete(`/api/branches/${id}`)
}
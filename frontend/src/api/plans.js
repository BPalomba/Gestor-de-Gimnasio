import api from './axios'

export const getPlans = async () => {
    const response = await api.get('/api/plans')
    return response.data.data
}

export const createPlan = async (data) => {
    const response = await api.post('/api/plans', data)
    return response.data.data
}

export const updatePlan = async (id, data) => {
    const response = await api.put(`/api/plans/${id}`, data)
    return response.data.data
}

export const deactivatePlan = async (id) => {
    await api.delete(`/api/plans/${id}`)
}
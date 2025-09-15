import { apiClient } from './ApiClient'

// Create API functions that accept token as parameter
export const retrieveAllTodosForUsernameApi = (username, token) => {
    return apiClient.get(`/users/${username}/todos`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
}

export const deleteTodoApi = (username, id, token) => {
    return apiClient.delete(`/users/${username}/todos/${id}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
}

export const retrieveTodoApi = (username, id, token) => {
    return apiClient.get(`/users/${username}/todos/${id}`, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
}

export const updateTodoApi = (username, id, todo, token) => {
    return apiClient.put(`/users/${username}/todos/${id}`, todo, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
}

export const createTodoApi = (username, todo, token) => {
    return apiClient.post(`/users/${username}/todos`, todo, {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
}
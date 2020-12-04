import axios from 'axios'

export const AXIOS = axios.create({
    baseURL: `/api`,
    headers: {
        'Access-Control-Allow-Origin': ['http://localhost:8080', 'http://localhost:8081', 'https://it4it.herokuapp.com'],
        'Access-Control-Allow-Methods': 'GET,POST,DELETE,PUT,OPTIONS',
        'Access-Control-Allow-Headers': '*',
        'Access-Control-Allow-Credentials': true
    }
})
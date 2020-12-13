import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

const state = {
    role: localStorage.getItem('user-role') || '',
    username: localStorage.getItem('user-name') || '',
    authorities: localStorage.getItem('authorities') || '',
    projects: localStorage.getItem('projects') || []
};

const getters = {
    isAuthenticated: state => {
        if (state.role != null && state.role != '') {
            return true;
        } else {
            return false;
        }
    },
    isAdmin: state => {
        if (state.role === 'admin') {
            return true;
        } else {
            return false;
        }
    },
    getProjects: state => {
        return state.projects
    },
    getUsername: state => {
        return state.username;
    },
    getAuthorities: state => {
        return state.authorities;
    }
};

const mutations = {
    auth_login: (state, user) => {
        localStorage.setItem('user-name', user.username);
        localStorage.setItem('user-authorities', user.roles);
        state.username = user.username;
        state.authorities = user.roles;
        var isUser = false;
        var isAdmin = false;
        var projects = []
        for (var i = 0; i < user.roles.length; i++) {
            if (user.roles[i].authority === 'ROLE_USER') {
                isUser = true;
            } else if (user.roles[i].authority === 'ROLE_ADMIN') {
                isAdmin = true;
            }
            else{
                projects += user.roles[i].authority
            }
        }
        if (isUser) {
            localStorage.setItem('user-role', 'user');
            state.role = 'user';
        }
        if (isAdmin) {
            localStorage.setItem('user-role', 'admin');
            state.role = 'admin';
        }
        localStorage.setItem('projects', projects)
        state.projects = projects
    },
    auth_logout: () => {
        state.token = '';
        state.role = '';
        state.username = '';
        state.authorities = [];
        localStorage.removeItem('user-role');
        localStorage.removeItem('user-name');
        localStorage.removeItem('user-authorities');
    }
};

const actions = {
    login: (context, user) => {
        context.commit('auth_login', user)
    },
    logout: (context) => {
        context.commit('auth_logout');
    }
};

export const store = new Vuex.Store({
    state,
    getters,
    mutations,
    actions
});
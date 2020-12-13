import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home'
import SignIn from '@/components/SignIn'
import SignUp from '@/components/SignUp'
import UserPage from '@/components/UserPage'
import AdminPage from '@/components/AdminPage'
import Projects from '@/components/Projects'
import NewProject from '@/components/NewProject'
import NewTask from '@/components/NewTask'
import Tasks from "@/components/Tasks"

Vue.use(Router)

export default new Router({
    mode: 'hash',
    routes: [
        {
            path: '/',
            name: 'Home',
            component: Home
        },
        {
            path: '/login',
            name: 'SignIn',
            component: SignIn
        },
        {
            path: '/register',
            name: 'SignUp',
            component: SignUp
        },
        {
            path: '/user',
            name: 'UserPage',
            component: UserPage
        },
        {
            path: '/admin',
            name: 'AdminPage',
            component: AdminPage
        },
        {
            path: '/projects',
            name: 'Projects',
            component: Projects
        },
        {
            path: '/newproject',
            name: 'NewProject',
            component: NewProject
        },
        {
            path: '/newtask',
            name: 'NewTask',
            component: NewTask
        },
        {
            path: '/tasks',
            name: 'Tasks',
            component: Tasks
        }
    ]
})
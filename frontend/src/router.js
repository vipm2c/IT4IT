import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import Home from '@/components/Home'
import SignIn from '@/components/SignIn'
import SignUp from '@/components/SignUp'
import UserPage from '@/components/UserPage'
import AdminPage from '@/components/AdminPage'

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
            path: '/hello-world',
            name: 'HelloWorld',
            component: HelloWorld
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
        }
    ]
})
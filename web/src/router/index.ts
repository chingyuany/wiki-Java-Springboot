import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../views/home.vue'
import About from '../views/about.vue'
import AdminEbook from '../views/admin/admin-ebook.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/about',
    name: 'About',
    component: About
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited. 懶加載就是不會一起打包,只有當訪問該頁面才會加載
    // component: () => import(/* webpackChunkName: "about" */ '../views/about.vue')
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbook',
    component: AdminEbook
  }
]

const router = createRouter({
  //依據不同環境去抓取base url 在main.ts,  如果不是歷史模式 會顯示成localhost/#/about, 歷史模式 localhost/about
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router

import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Home from '../views/home.vue'
import About from '../views/about.vue'
import Doc from '../views/doc.vue'
import AdminUser from '../views/admin/admin-user.vue'
import AdminEbook from '../views/admin/admin-ebook.vue'
import AdminCategory from '../views/admin/admin-category.vue'
import AdminDoc from '../views/admin/admin-doc.vue'
import {Tool} from "@/util/tool";
import store from "@/store";


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
       path: '/admin/user',
       name: 'AdminUser',
       component: AdminUser,
      meta: {
             loginRequire: true
            }
  },
  {
    path: '/admin/ebook',
    name: 'AdminEbook',
    component: AdminEbook,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/category',
    name: 'AdminCategory',
    component: AdminCategory,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/admin/doc',
    name: 'AdminDoc',
    component: AdminDoc,
    meta: {
      loginRequire: true
    }
  },
  {
    path: '/doc',
    name: 'Doc',
    component: Doc
  }
]

const router = createRouter({
  //依據不同環境去抓取base url 在main.ts,  如果不是歷史模式 會顯示成localhost/#/about, 歷史模式 localhost/about
  history: createWebHistory(process.env.BASE_URL),
  routes
})


// 路由登录拦截
//beforeEach每個路由要被執行之前，都會先經過這裡，to 你要去的路由位置。
// from 你從哪一個路由位置進來，如果沒有，預設是 null。
// next() 繼續往下執行的回呼函式，你必須要呼叫他才能繼續執行。
router.beforeEach((to, from, next) => {
  // 要不要对meta.loginRequire属性做监控拦截  item路由的訊息
  if (to.matched.some(function (item) {
    console.log(item, "Need login validate?：", item.meta.loginRequire);
    return item.meta.loginRequire
  }))
  //上面是true就執行下面
  {
    const loginUser = store.state.user;
    if (Tool.isEmpty(loginUser)) {
      console.log("User not login！");
      //回到首頁
      next('/');
    } else {
      //不攔截
      next();
    }
  } else {
    next();
  }
});

export default router

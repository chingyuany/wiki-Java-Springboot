import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from "ant-design-vue";
import "ant-design-vue/dist/antd.css";
//import ant 圖標庫
import * as Icons from '@ant-design/icons-vue';
import axios from "axios";
import {Tool} from "@/util/tool";
import { message } from 'ant-design-vue';

//依據.env.dev and env.prod 去配置base url, home.vue 裡面的 axios.get("/ebook/list")會自己接上base URL
axios.defaults.baseURL = process.env.VUE_APP_SERVER;


/**
 * 前端的 axios拦截器interceptors
 */
//檢查登入token
axios.interceptors.request.use(function (config) {
    console.log('Request parameter：', config);
    const token = store.state.user.token;
    if (Tool.isNotEmpty(token)) {
        config.headers.token = token;
        console.log("Request headers add token:", token);
    }
    return config;
}, error => {
    return Promise.reject(error);
});

axios.interceptors.response.use(function (response) {
    console.log('Return result：', response);
    return response;
}, error => {
    console.log('Return error：', error);
    const response = error.response;
    const status = response.status;
    if (status === 401) {
        // LogInterceptor.java HttpStatus.UNAUTHORIZED.value() == 401
        // 判断状态码是401 跳转到首页或登录页
        console.log("Not login, reroute to home page");
        store.commit("setUser", {});
        message.error("Not login or login over time");
        router.push('/');
    }
    return Promise.reject(error);
});


//#app 是public/js/index.html裡面的 div 區塊
const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');

// 全局使用图标 不用一個一個import
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}
//對應到 .env.dev and .env.prod
console.log('environment：', process.env.NODE_ENV);
console.log('server：', process.env.VUE_APP_SERVER);
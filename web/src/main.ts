import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from "ant-design-vue";
import "ant-design-vue/dist/antd.css";
//import ant 圖標庫
import * as Icons from '@ant-design/icons-vue';
//#app 是index.html裡面的 div 區塊
const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');

// 全局使用图标 不用一個一個import
const icons: any = Icons;
for (const i in icons) {
    app.component(i, icons[i]);
}
//對應到 .env.dev and .env.prod
console.log('环境：', process.env.NODE_ENV);
console.log('服务端：', process.env.VUE_APP_SERVER);
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from "ant-design-vue";
import "ant-design-vue/dist/antd.css";
//#app 是index.html裡面的 div 區塊
createApp(App).use(store).use(router).use(Antd).mount('#app');

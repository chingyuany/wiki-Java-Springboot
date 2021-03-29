<template>
  <a-layout-footer style="text-align: center">
    Ebook Wiki ©2021 Created by Alan Yang.
    <span v-show="user.id">Welcome：{{user.name}} </span>
  </a-layout-footer>
</template>

<script lang="ts">
import { defineComponent,computed,onMounted } from 'vue';
import store from "@/store";
import {Tool} from "@/util/tool";
import { notification } from 'ant-design-vue';

export default defineComponent({
  name: 'the-footer',
  setup() {
    //computed 監聽變量的變化
    const user = computed(() => store.state.user);
    //websocket 點讚通知
    let websocket: any;
    let token: any;
    const onOpen = () => {
      console.log('WebSocket connect success，status code：', websocket.readyState)
    };
    const onMessage = (event: any) => {
      console.log('WebSocket receive msg：', event.data);
      notification['info']({
        message: 'Received Message',
        description: event.data,
      });
    };
    const onError = () => {
      console.log('WebSocket connect error，status code：', websocket.readyState)
    };
    const onClose = () => {
      console.log('WebSocket connection close,status code：', websocket.readyState)
    };
    const initWebSocket = () => {
      //前面的 websocket.onopen 是內部根據當前事件自動觸發調動的   =後面的方法是自己定義的 只是輸出紀錄
      // 连接成功
      websocket.onopen = onOpen;
      // 收到消息的回调
      websocket.onmessage = onMessage;
      // 连接错误
      websocket.onerror = onError;
      // 连接关闭的回调
      websocket.onclose = onClose;
    };

    onMounted(() => {
      // WebSocket
      if ('WebSocket' in window) {
        token = Tool.uuid(10);
        // 连接地址：ws://127.0.0.1:8880/ws/xxx
        //html 5 自帶的方法
        websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
        initWebSocket()

        // 关闭
        // websocket.close();
      } else {
        alert("Does not support current browser")
      }
    });
    return {
      user
    }
  }
});
</script>
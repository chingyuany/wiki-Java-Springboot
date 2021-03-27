<template>
  <a-layout-header class="header">
    <div class="logo" />
    <a-menu
        theme="dark"
        mode="horizontal"
        v-model:selectedKeys="selectedKeys1"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/"><router-link to="/">Home</router-link></a-menu-item>
      <a-menu-item key="/admin/user">
        <router-link to="/admin/user">User Management</router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook"><router-link to="/admin/ebook">Ebook Management</router-link></a-menu-item>
      <a-menu-item key="/admin/category"><router-link to="/admin/category">Category Management</router-link></a-menu-item>
      <a-menu-item key="/about"><router-link to="/about">About Us</router-link></a-menu-item>
      <a class="login-menu" @click="showLoginModal">
        <span>Login</span>
      </a>
    </a-menu>
    <a-modal
        title="Login"
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        @ok="login"
    >
      <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="Login Name">
          <a-input v-model:value="loginUser.loginName" />
        </a-form-item>
        <a-form-item label="Password">
          <a-input v-model:value="loginUser.password" type="password" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import { defineComponent,ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';

declare let hexMd5: any;
declare let KEY: any;

export default defineComponent({
  name: 'the-header',
  setup () {
    const loginUser = ref({
      loginName: "test",
      password: "test"
    });
    const loginModalVisible = ref(false);
    const loginModalLoading = ref(false);
    const showLoginModal = () => {
      loginModalVisible.value = true;
    };

    // login
    const login = () => {
      console.log("Start login");
      loginModalLoading.value = true;
      loginUser.value.password = hexMd5(loginUser.value.password + KEY);
      axios.post('/user/login', loginUser.value).then((response) => {
        loginModalLoading.value = false;
        const data = response.data;
        if (data.success) {
          loginModalVisible.value = false;
          message.success("Login successful！");
        } else {
          message.error(data.message);
        }
      });
    };


    return {
      loginModalVisible,
      loginModalLoading,
      showLoginModal,
      loginUser,
      login
    }
  }
});
</script>
<style>
.login-menu {
  float: right;
  color: white;
}
</style>
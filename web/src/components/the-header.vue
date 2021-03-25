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
      <a-menu-item key="/admin/ebook"><router-link to="/admin/ebook">Ebook Management</router-link></a-menu-item>
      <a-menu-item key="/admin/category"><router-link to="/admin/category">Category Management</router-link></a-menu-item>
      <a-menu-item key="/about"><router-link to="/about">About Us</router-link></a-menu-item>

    </a-menu>
  </a-layout-header>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'the-header',

});
</script>
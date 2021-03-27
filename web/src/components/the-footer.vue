<template>
  <a-layout-footer style="text-align: center">
    Ebook Wiki ©2021 Created by Alan Yang.
    <span v-show="user.id">Welcome：{{user.name}} </span>
  </a-layout-footer>
</template>

<script lang="ts">
import { defineComponent,computed } from 'vue';
import store from "@/store";

export default defineComponent({
  name: 'the-footer',
  setup() {
    //computed 監聽變量的變化
    const user = computed(() => store.state.user);

    return {
      user
    }
  }
});
</script>
<!--slots 自定義渲染, custom render 對值渲染, title 表頭渲染-->
<!--  看6-3  6-6介紹-->
<!--record 是Ebook class-->
<!--loading 是內建的 true的時候會顯示讀取的動畫-->
<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
<!--        套用的樣式 customRender -->
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{ text, record }">
<!--          空格組件-->
          <a-space size="small">
            <a-button type="primary">
              Edit
            </a-button>
            <a-button type="danger">
              Delete
            </a-button>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'AdminEbook',
  setup() {
    const ebooks = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0
    });
    //loading 是內建的 true的時候會顯示讀取的動畫
    const loading = ref(false);

    const columns = [
      {
        title: 'Cover',
        dataIndex: 'cover',
        slots: { customRender: 'cover' }
      },
      {
        title: 'Name',
        dataIndex: 'name'
      },
      {
        title: 'Category1',
        key: 'category1Id',
        dataIndex: 'category1Id'
      },
      {
        title: 'Category2',
        dataIndex: 'category2Id'
      },
      {
        title: 'Document Amount',
        dataIndex: 'docCount'
      },
      {
        title: 'Read',
        dataIndex: 'viewCount'
      },
      {
        title: 'Like',
        dataIndex: 'voteCount'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = (params: any) => {
      loading.value = true;
      //另一種get方式為拼接  "?page="+page+"&" 下面是第一種方法 直接寫params 固定寫法
      axios.get("/ebook/list", {
        params: {
          page: params.page,
          size: params.size
        }
      }).then((response) => {

        loading.value = false;
        //response 是後端傳回來的值
        const data = response.data;
        //響應變量用.value
        ebooks.value = data.content.list;

        // 重置分页按钮  pagination.value.current是內建的函數
        pagination.value.current = params.page;
        pagination.value.total = data.content.total;
      });
    };

    /**
     * 有人點擊頁數按鈕 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };
    //初始化頁面的時候 也是需要先查詢一次 第一頁
    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange
    }
  }
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
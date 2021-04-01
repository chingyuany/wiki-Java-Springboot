<!--slots 自定義渲染, custom render 對值渲染, title 表頭渲染-->
<!--  看6-3  6-6介紹-->
<!--record 是Category class-->
<!--loading 是內建的 true的時候會顯示讀取的動畫-->
<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
<!--        inline 排在同一列-->
        <a-form layout="inline" :model="param">
<!--          <a-form-item>-->
<!--            <a-input v-model:value="param.name" placeholder="Name">-->
<!--            </a-input>-->
<!--          </a-form-item>-->
<!--          <a-form-item>-->
<!--            <a-button type="primary" @click="handleQuery()">-->
<!--              Show All-->
<!--            </a-button>-->
<!--          </a-form-item>-->
          <a-form-item>
          <a-button type="primary" @click="add()" >
            New
          </a-button>
          </a-form-item>
        </a-form>
      </p>
      <p>
        <a-alert
            class="tip"
            message="Reminder: Categories here will show at side bar of home page"
            type="info"
            closable
        />
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          v-if="level1.length > 0"
          :defaultExpandAllRows="true"
          :loading="loading"
          :pagination="false"
      >
<!--        套用的樣式 customRender -->
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
<!--        record 是一列的數據-->
        <template v-slot:action="{ text, record }">
<!--          空格組件-->
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              Edit
            </a-button>
            <a-popconfirm
                title="Delete cannot recover. Are you sure delete this row?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleDelete(record.id)"

            >
              <a-button type="danger">
                Delete
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      title="Category Form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="category" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Name" required>
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="Parent Category" required>
        <a-select
            v-model:value="category.parent"
            ref="select"
        >
          <a-select-option :value="0">
            None
          </a-select-option>
<!--          循環1級分類 把name顯示   id放到 category.parent   如果當前文本框分類的id 是目前選項的id 就變灰色不能選  因為選自己會把自己父分類改成自己本身ID-->
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
<!--            在Html裡面要使用響應式變量就要用{{}}-->
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Sort" required>
        <a-input v-model:value="category.sort" type="number"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminCategory',
  setup() {
    const param = ref();
    param.value = {};
    const categorys = ref();

    //loading 是內建的 true的時候會顯示讀取的動畫
    const loading = ref(false);

    const columns = [
      {
        title: 'Name',
        dataIndex: 'name'
      },
      // {
      //   title: 'Parent Category',
      //   key: 'parent',
      //   dataIndex: 'parent'
      // },
      {
        title: 'sort',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];
    /**
     * 一级分类树，children属性就是二级分类
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */

    const level1 = ref(); // 一级分类树，children属性就是二级分类
    level1.value = [];
    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/category/all").then((response) => {

        loading.value = false;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success){

          //響應變量用.value
          categorys.value = data.content;
          console.log("Original table：", categorys.value);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys.value, 0);
          console.log("Tree data：", level1);


        }else{
          message.error(data.message);
        }

      });
    };



    // -------- Edit 表单 ---------
    const category = ref();
    category.value = []
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      //一定要數字
      var numReg = /^[0-9]*$/
      var numRe = new RegExp(numReg)
      if (!numRe.test(category.value.sort)){
        message.error("sort need to be integer");
      }else {
        modalLoading.value = true
        //post 不需要像get一樣寫param
        axios.post("/category/save", category.value
        ).then((response) => {
          modalLoading.value = false;
          //response 是後端傳回來的值
          const data = response.data;
          if (data.success) {
            modalVisible.value = false;

            //reload form
            handleQuery();
          } else {
            message.error(data.message);
          }
        });

      }
    };

    /**
     * 编辑
     */
    const edit = (record:any) => {
      modalVisible.value = true;
      category.value = Tool.copy(record);
    };

    const add = () => {
      modalVisible.value = true;
      category.value = {}
    };
    //後端long 前端number
    const handleDelete = (id:number) =>{
      axios.delete("/category/delete/"+id
      ).then((response) => {
        //response 是後端傳回來的值, data = common response
        const data = response.data;
        if (data.success){
          //reload form
          handleQuery();
        }
      });
    }
    //初始化頁面的時候 也是需要先查詢一次 第一頁
    onMounted(() => {
      handleQuery();
    });

    return {
      // categorys,
      level1,
      columns,
      loading,

      param,
      edit,
      add,
      handleDelete,

      modalVisible,
      modalLoading,
      handleModalOk,
      category,
      handleQuery
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
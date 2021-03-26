<!--slots 自定義渲染, custom render 對值渲染, title 表頭渲染-->
<!--  看6-3  6-6介紹-->
<!--record 是Doc class-->
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
          <a-form-item>
            <a-button type="primary" @click="handleQuery()">
              Show All
            </a-button>
          </a-form-item>
          <a-form-item>
          <a-button type="primary" @click="add()" >
            New
          </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"

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
      title="Doc Form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="doc" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Name">
        <a-input v-model:value="doc.name" />
      </a-form-item><a-form-item label="Name">
      <!--          : 後面可以放變量, 沒有冒號 後面就是字串, replace fields 是把原本a-tree-select裡面的欄位名稱換成我們自訂的-->
      <a-tree-select
          v-model:value="doc.parent"
          style="width: 100%"
          :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
          :tree-data="treeSelectData"
          placeholder="Please select parent doc"
          tree-default-expand-all

          :replaceFields ="{title:'name',key:'id',value:'id'}"
      >

      </a-tree-select>

      </a-form-item>
      <a-form-item label="Parent Doc">
        <a-select
            v-model:value="doc.parent"
            ref="select"
        >
          <a-select-option :value="0">
            None
          </a-select-option>
<!--          循環1級分類 把name顯示   id放到 doc.parent   如果當前文本框分類的id 是目前選項的id 就變灰色不能選  因為選自己會把自己父分類改成自己本身ID-->
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="doc.id === c.id">
<!--            在Html裡面要使用響應式變量就要用{{}}-->
            {{c.name}}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="Sort">
        <a-input v-model:value="doc.sort" />
      </a-form-item>
      <a-form-item label="Content">
        <div id="content">

        </div>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref,createVNode } from 'vue';
import axios from 'axios';
import { message,Modal } from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
import E from 'wangeditor';
export default defineComponent({
  name: 'AdminDoc',
  setup: function () {
    const route = useRoute()
    // console.log("路由：", route);
    // console.log("route.path：", route.path);
    // console.log("route.query：", route.query);
    //用另一種傳遞方式 可以用params取參數值  admin/doc/docid
    // console.log("route.param：", route.params);
    // console.log("route.fullPath：", route.fullPath);
    // console.log("route.name：", route.name);
    // console.log("route.meta：", route.meta);
    const param = ref();
    param.value = {};
    const docs = ref();

    //loading 是內建的 true的時候會顯示讀取的動畫
    const loading = ref(false);

    const columns = [
      {
        title: 'Name',
        dataIndex: 'name'
      },
      {
        title: 'Parent Doc',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: 'Sort',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
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
    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get("/doc/all").then((response) => {

        loading.value = false;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        level1.value = [];
        //response 是後端傳回來的值
        const data = response.data;
        if (data.success) {

          //響應變量用.value
          docs.value = data.content;
          console.log("Original table：", docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log("Tree data：", level1);


        } else {
          message.error(data.message);
        }

      });
    };


    // -------- Edit 表单 ---------
    // 因为树选择组件的属性状态，会随当前编辑的节点而变化，所以单独声明一个响应式变量
    const treeSelectData = ref();
    treeSelectData.value = [];
    const doc = ref({});
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const editor = new E('#content')

    const handleModalOk = () => {
      modalLoading.value = true
      //post 不需要像get一樣寫param
      axios.post("/doc/save", doc.value
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


    };

    /**
     * 将某节点及其子孙节点全部置为disabled
     */
    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };
    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];
    const getDeleteIds = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          deleteIds.push(node.id);
          deleteNames.push(node.name);
          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    };
    /**
     * 编辑
     */
    const edit = (record: any) => {
      modalVisible.value = true;
      doc.value = Tool.copy(record);
      // 不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择树添加一个"无"  unshift往數組前面添加
      treeSelectData.value.unshift({id: 0, name: 'None'});

      //因為modal create 沒這麼快 所以delay 100ms 在顯示富文本
      setTimeout(function () {
                editor.create();
      }, 100);
    };

    const add = () => {
      modalVisible.value = true;
      doc.value = {ebookId: route.query.ebookId}
      treeSelectData.value = Tool.copy(level1.value);
      // 为选择树添加一个"无"  unshift往數組前面添加
      treeSelectData.value.unshift({id: 0, name: 'None'});
      //因為modal create 沒這麼快 所以delay 100ms 在顯示富文本
      setTimeout(function () {
        editor.create();
      }, 100);
    };
    //後端long 前端number
    const handleDelete = (id: number) => {
      // 清空数组，否则多次删除时，数组会一直增加
      deleteIds.length = 0;
      deleteNames.length = 0;
      getDeleteIds(level1.value,id);
      Modal.confirm({
        title: 'Important Reminder',
        icon: createVNode(ExclamationCircleOutlined),
        content: 'Deleting：【' + deleteNames.join("，") + "】cannot recover，confirm delete？",
        onOk() {
          // console.log(ids)
          axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
            const data = response.data; // data = commonResp
            if (data.success) {
              // 重新加载列表
              handleQuery();
            }
          });
        },
      });
    }
    //初始化頁面的時候 也是需要先查詢一次 第一頁
    onMounted(() => {
      handleQuery();

    });

    return {
      // docs,
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
      doc,
      handleQuery,
      treeSelectData
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
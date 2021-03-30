<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="Login name">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              Search
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()">
              New
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="edit(record)">
              Edit
            </a-button>
            <a-button type="primary" @click="resetPassword(record)">
              ResetPassword
            </a-button>
            <a-popconfirm
                title="Delete cannot recover, confirm delete?"
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
      title="User form"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="Login name" required>
<!--       如果用戶名有值 表示在編輯 就要disable, 假設用戶名沒有值 就是在new 可以顯示-->
<!--        這裡是number 但是 disable 後面是要接布林值   !! 把它變成布林值繞過參數型態檢驗  -->
        <a-input v-model:value="user.loginName" :disabled="!!user.id" />
      </a-form-item>
      <a-form-item label="User name" required>
        <a-input v-model:value="user.name" />
      </a-form-item>
<!--      因為編輯的時候 不能改密碼 不要顯示-->
<!--      v-show是動態顯示  v-if 是直接刪掉元素 適用於初始就判斷顯不顯示 -->
      <a-form-item label="Password"  v-show="!user.id" required>
        <a-input v-model:value="user.password"  type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
      title="Reset password"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk"
  >
    <a-form :model="user" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="New Password" required>
        <a-input v-model:value="user.password"  type="password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";

//雖然index.html已經引用md5.js 這裡可以直接用public變量, 但是typescript需要再宣告一下該變量才能用
declare let hexMd5: any;
declare let KEY: any;
export default defineComponent({
  name: 'AdminUser',
  setup() {
    const param = ref();
    param.value = {};
    const users = ref();
    const pagination = ref({
      current: 1,
      pageSize: 10,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: 'Login name',
        dataIndex: 'loginName'
      },
      {
        title: 'User name',
        dataIndex: 'name'
      },
      {
        title: 'Password',
        dataIndex: 'password'
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
      // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
      users.value = [];
      axios.get("/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.loginName
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          users.value = data.content.list;

          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        } else {
          message.error(data.message);
        }
      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    };

    // -------- 表单 ---------
    const user = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const handleModalOk = () => {
      let regex = new RegExp("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$");
      if (!regex.exec(user.value.password)){
        message.error("Password needs to include letters and numbers, the length is 6-20");
      }
      // if (user.value.password.length < 6 || user.value.password.length > 32){
      //   message.error("password length should between 6 to 32");
      // }
      else {
        modalLoading.value = true;
        //前端先加密 因為只有後端加密 封包可以被攔截
        //key是鹽值 因為一些常見的密碼 如123的md5值是一樣的 很容易被猜出來, 加個key, 就不容易猜出來
        user.value.password = hexMd5(user.value.password + KEY);

        axios.post("/user/save", user.value).then((response) => {
          modalLoading.value = false;
          const data = response.data; // data = commonResp
          if (data.success) {
            modalVisible.value = false;

            // 重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          } else {
            message.error(data.message);
          }
        });
      }
    };

    /**
     * 编辑
     */
    const edit = (record: any) => {
      modalVisible.value = true;
      user.value = Tool.copy(record);
    };

    /**
     * 新增
     */
    const add = () => {
      modalVisible.value = true;
      user.value = {};
    };

    const handleDelete = (id: number) => {
      axios.delete("/user/delete/" + id).then((response) => {
        const data = response.data; // data = commonResp
        if (data.success) {
          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        }
      });
    };
// -------- reset password 表單---------

    const resetModalVisible = ref(false);
    const resetModalLoading = ref(false);
    const handleResetModalOk = () => {
      let regex = new RegExp("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$");
      if (!regex.exec(user.value.password)){
        message.error("Password needs to include letters and numbers, the length is 6-20");
      }
      // if (user.value.password.length < 6 || user.value.password.length > 32){
      //   message.error("password length should between 6 to 32");
      // }
      else{
      resetModalLoading.value = true;
      //前端先加密 因為只有後端加密 封包可以被攔截
      //key是鹽值 因為一些常見的密碼 如123的md5值是一樣的 很容易被猜出來, 加個key, 就不容易猜出來
      user.value.password = hexMd5(user.value.password + KEY);

      axios.post("/user/reset-password", user.value).then((response) => {
        resetModalLoading.value = false;
        const data = response.data; // data = commonResp
        if (data.success) {
          resetModalVisible.value = false;

          // 重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);
        }
      });
      }
    };

    /**
     * 编辑
     */
    const resetPassword = (record: any) => {
      resetModalVisible.value = true;
      user.value = Tool.copy(record);
      //因為要清空password框
      user.value.password = null;
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      param,
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,

      edit,
      add,

      user,
      modalVisible,
      modalLoading,
      handleModalOk,

      handleDelete,
      resetPassword,
      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
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
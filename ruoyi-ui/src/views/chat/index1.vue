<template>
  <div class="ml20" style="padding: 10px; margin-bottom: 50px;">
    <el-row>
      <!-- 聊天室 -->
      <el-col :span="14" style="margin-bottom: 12px;">
        <div style="width: 800px; margin: 0 auto; background-color: white;border-radius: 5px;
            box-shadow: 0 0 10px #ccc">
          <div style="text-align: center; line-height: 50px; font-size: 140%;">
            - 聊天室 -
          </div>
          <div class="chat-messages" ref="messageContainer"
            style="height: 400px; overflow: auto; border-top: 1px solid #ccc;">
            <div v-for="(message, index) in messages" :key="index" class="message">
              <!-- 消息类型为1001，系统提示消息 -->
              <div v-if="message.type === 1001">
                <div class="message-sender system-message">
                  系统消息 - {{ message.sendTime }}
                </div>
                <div>
                  {{ message.content }}
                </div>
              </div>
              <!-- 消息类型为2001，普通消息 -->
              <div v-else-if="message.type === 2001">
                <!-- 自己发送的消息 -->
                <div v-if="message.sender === nickName">
                  <div class="sent-message">
                    {{ message.sender }} - {{ message.sendTime }}
                  </div>
                  <div class="selfMsgContent">
                    {{ message.content }}
                  </div>
                </div>

                <!-- 其他人发送的消息 -->
                <div v-else>
                  <div>
                    <div class="received-message">
                      {{ message.sender }} - {{ message.sendTime }}
                    </div>
                    <div class="orderMsgContent">
                      {{ message.content }}
                    </div>
                  </div>
                </div>
              </div>

              <!-- GPT助手发送消息 -->
              <div v-if="message.type === 3001">
                <div class="received-message">
                  智能小助理 - {{ message.sendTime }}
                </div>
                <!-- 引用发送的消息 -->
                <div class="quoteMsg">
                  {{ message.sendMsg }}
                </div>
                <div>------------------</div>
                <div class="orderMsgContent">
                  {{ message.content }}
                </div>
              </div>
            </div>
          </div>
          <el-switch v-model="iaAssistantSwitch" active-text="智能小助理" style="left: 10px; margin: 1px;">
          </el-switch>
          <div class="chat-input" style="height: 120px; right: 50px;left: 100px;">
            <input v-model="newMessage" placeholder="在此输入信息……" style="height: 120px;
                width: -webkit-fill-available;
                padding: 15px;
                border: none;
                background-color: #f7f7fa;
                border-top: 1px solid #ccc;
                border-bottom: 1px solid #ccc;
                outline: none" @keyup.enter="sendGroupMessage">
            <div style="text-align: right; padding-right: 10px;">
              <el-button type="primary" size="small" @click="sendGroupMessage">发送</el-button>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 在线用户 -->
      <el-col :span="6" :offset="2">
        <el-card>
          <div slot="header" style="text-align: center; font-size: 12px;">
            在线用户
          </div>
          <div style="height: 300px; overflow: auto;">
            <div v-for="(user, index) in onlineUserList" :key="index" class="online-user">
              <span style="color: green;">●</span>{{ user.userName }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getUserProfile } from "@/api/system/user";
import { closeUserConnect } from "@/api/chat/chat.js";
import { getToken } from "@/utils/auth";
import { list } from "@/api/monitor/online";

export default {
  data() {
    return {
      // 遮罩层
      loading: true,
      // 表格数据
      onlineUserList: [],
      messages: [], // 存储接收到的消息
      newMessage: '', // 存储用户输入的消息
      connected: false, // 表示连接状态标志
      socket: null, // WebSocket 实例
      nickName: '',
      msg: '',
      iaAssistantSwitch: false,
    };
  },
  mounted() {
    // 获取在线用户列表
    this.getOnlineUserList();
    getUserProfile().then(response => {
      console.log("response.data --->>", response.data)
      this.nickName = response.data.nickName
    });
    this.initializeWebSocket().then(() => {
      // WebSocket 初始化完成后执行的逻辑
      this.joinGroup();
    });
    // 在 beforeunload 上添加事件监听器
    window.addEventListener('beforeunload', this.closeWebSocket);
  },
  methods: {
    getOnlineUserList() {
      this.loading = true;
      list(this.queryParams).then(response => {
        console.log("在线用户返回 --->>", response)
        this.onlineUserList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    // 加入群聊连接
    joinGroup() {
      const command = {
        msgType: '10002',// 加入群聊事件
        token: getToken(),
      };
      console.log("connection - 建立连接传参--->", JSON.stringify(command))
      this.socket.send(JSON.stringify(command));
    },
    // 发送群组消息
    async sendGroupMessage() {
      if (this.newMessage.trim() !== '') {
        const command = {
          nickName: this.nickName,
          content: this.newMessage,
          msgType: '20002',// 发送群聊消息
          token: getToken(),
        };
        if(this.iaAssistantSwitch){
          command.content = '@小助手, ' + command.content;
        }
        console.log("sendGroupMessage - 发送群聊消息传参--->", JSON.stringify(command))
        this.socket.send(JSON.stringify(command));

        // 如果要向助手提问则执行
        if (this.iaAssistantSwitch) {
          const command = {
            nickName: this.nickName,
            content: this.newMessage,
            msgType: '30001',// 向ai助手发送消息
            token: getToken(),
          };
          console.log("sendGroupMessage - GPT助手传参--->", JSON.stringify(command))
          await this.socket.send(JSON.stringify(command));
          this.iaAssistantSwitch = false;
        }

        this.newMessage = '';// 发送后清空输入框
        // 等待 Vue 更新 DOM 后，滚动到底部
        this.$nextTick(() => {
          const container = this.$refs.messageContainer;
          container.scrollTop = container.scrollHeight;
        });
      }
    },
    initializeWebSocket() {
      return new Promise((resolve, reject) => {

        // WS连接
        this.socket = new WebSocket('ws://118.178.231.233:9044/ws');
        // this.socket = new WebSocket('ws://localhost:9044/ws');

        // 建立消息触发
        this.socket.onopen = () => {
          this.connected = true;
          // 在连接建立时发送鉴权信息
          const authHeader = getToken(); // 替换成实际的鉴权信息
          this.socket.send(JSON.stringify({ type: 'Authorization', token: authHeader }));
          console.log('WebSocket 连接已建立');
          resolve();
        };

        // 接收消息触发
        this.socket.onmessage = (event) => {
          console.log("返回：", event)
          // 接收到消息时触发
          const message = JSON.parse(event.data);
          const formattedMessage = {
            type: message.type,
            sender: message.nickName,
            sendTime: message.sendTime,
            content: message.content,
            sendMsg: message.sendMsg,
          };
          if (message.type == '1002') {
            alert(message.content);
            this.socket.onclose();
          } else {
            // 将返回的消息写入消息列表
            if (formattedMessage.type === 3001) {
              formattedMessage.content = '@'+formattedMessage.sender + ', ' + formattedMessage.content;
            }
            this.messages.push(formattedMessage);
          }

          // 等待 Vue 更新 DOM 后，滚动到底部
          this.$nextTick(() => {
            const container = this.$refs.messageContainer;
            container.scrollTop = container.scrollHeight;
          });
        };

        // 连接关闭时触发
        this.socket.onclose = () => {
          this.connected = false;
          console.log('WebSocket 连接已关闭');
        };

        // 发生错误时触发
        this.socket.onerror = (error) => {
          console.error('WebSocket 错误：', error);
          this.connected = false;
        };
      });
    },
    // 关闭 WebSocket 连接的方法
    closeWebSocket() {
      if (this.socket) {
        // 如果 WebSocket 连接存在，关闭它
        this.socket.close();
        alert("-->", getToken())
        closeUserConnect(getToken()).then((result) => {

        })
      }
    },
  },
  // beforeUnmount() {
  //   // 在组件卸载时确保移除事件监听器
  //   window.removeEventListener('beforeunload', this.closeWebSocket);
  // },
};
</script>

<style>
.message-sender {
  font-size: small;
  /* 设置字体大小为小号 */
}

.message-content {
  font-size: medium;
  /* 设置字体大小为中号 */
  /* 其他样式属性 */
  /* 可以在这里定义其他样式，比如颜色、行高等 */
}

.message-content {
  font-size: medium;
  /* 设置字体大小为中号 */
  /* 其他样式属性 */
  /* 可以在这里定义其他样式，比如颜色、行高等 */
}

.system-message {
  margin-bottom: 8px;
  color: blue;
}

/* 为聊天容器、消息、输入框等添加样式 */
.chat-container {
  width: 400px;
  margin: 20px auto;
  border: 1px solid #ccc;
  padding: 10px;
}

.chat-messages {
  max-height: 400px;
  overflow-y: auto;
}

.message {
  margin-bottom: 5px;
  padding: 5px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.chat-input {
  font-size: large;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  margin: 0 auto;
  /* 居中显示 */
}

.chat-input input {
  flex: 1;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #d20b0b;
  margin-right: 10px;
  margin-left: 5px;
}

.chat-input button {
  padding: 8px 16px;
  border-radius: 4px;
  background-color: #007bff;
  color: #fff;
  border: none;
  cursor: pointer;
}

/* QQ 客户端聊天样式 */
.sent-message {
  color: coral;
}

.received-message {
  color: green;
}

.selfMsgContent {
  color: #fff;
  /* 元素外边距 */
  margin: 3px;
  /* 元素内边距 上右下左 */
  padding: 7px 9px 7px 9px;
  display: inline-block;
  background-color: #5497ef;
  border-radius: 10px;
}

.orderMsgContent {
  color: #000000;
  margin: 3px;
  padding: 7px 9px 7px 9px;
  display: inline-block;
  background-color: #ebebed;
  border-radius: 10px;
}

.quoteMsg {
  color: #aeaeae;
  size: 5px;
}
/* 在线用户样式 */
.online-user {
  margin-bottom: 5px;
  padding: 5px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  font-size: 15px;
}
</style>

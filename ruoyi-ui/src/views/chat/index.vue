<template>
  <div class="ml20">
    <el-row>
      <!-- 聊天室 -->
      <el-col class="chat-container" :span="14" style="margin-bottom: 12px;">
        <div style="width: 800px; margin: 0 auto; background-color: white;border-radius: 5px;
            box-shadow: 0 0 10px #ccc">
          <div style="text-align: center; line-height: 50px; font-size: 140%;">
            - 聊天室 -
          </div>
          <div class="chat-messages" ref="messageContainer"
            style="height: 400px; overflow: auto; border-top: 1px solid #ccc;">
            <!-- 加载提示 -->
            <div v-if="isLoading" class="loading-more">加载中...</div>
            <div v-else class="no-more">没有更多消息了</div>

            <div v-for="(message, index) in messages" :key="index" class="message">
              <!-- 建立群聊连接事件 - 10002 -->
              <div v-if="message.type === 10002" @methods="succeedJoinGroup(message.content)">
                <div class="message-sender system-message">
                  系统消息 - {{ message.sendTime }}
                </div>
                <div v-html="$emo.transform(message.content)">
                </div>
              </div>
              <!-- 群组聊天消息 - 20002 -->
              <div v-else-if="message.type === 20002">
                <!-- 自己发送的消息 -->
                <div v-if="message.fromUid === fromUid">
                  <div class="sent-message">
                    {{ message.nickName }} - {{ message.sendTime }}
                  </div>
                  <div class="selfMsgContent" contenteditable="true" v-html="$emo.transform(message.content)">
                  </div>
                </div>

                <!-- 其他人发送的消息 -->
                <div v-else>
                  <div>
                    <div class="received-message">
                      {{ message.nickName }} - {{ message.sendTime }}
                    </div>
                    <div class="orderMsgContent" contenteditable="true" v-html="$emo.transform(message.content)">
                    </div>
                  </div>
                </div>
              </div>

              <!-- GPT助手发送消息 30001 -->
              <div v-if="message.type === 30001">
                <div class="received-message">
                  智能小助理 - {{ message.sendTime }}
                </div>
                <!-- 引用发送的消息 -->
                <div class="quoteMsg">
                  {{ message.sendMsg }}
                </div>
                <div>------------------</div>
                <div class="orderMsgContent" v-html="$emo.transform(message.content)">
                </div>
              </div>
            </div>
          </div>
          <el-switch v-model="iaAssistantSwitch" active-text="智能小助理" style="left: 10px; margin: 1px;">
          </el-switch>
          <div class="chat-tool-bar">
            <div title="表情" class="el-icon-eleme" ref="emotion" @click.stop="showEmotionBox()">
            </div>
            <!--  -->
            <div title="发送图片" @click="sendImg()">
              <!-- <file-upload :action="'/image/upload'" :maxSize="5 * 1024 * 1024"
                :fileTypes="['image/jpeg', 'image/png', 'image/jpg', 'image/webp', 'image/gif']" @before="onImageBefore"
                @success="onImageSuccess" @fail="onImageFail">
                <i class="el-icon-picture-outline"></i>
              </file-upload> -->
              <i class="el-icon-picture-outline"></i>
            </div>
            <div title="发送文件" @click="sendFileFai()">
              <!-- <file-upload :action="'/file/upload'" :maxSize="10 * 1024 * 1024" @before="onFileBefore"
                @success="onFileSuccess" @fail="onFileFail">
                <i class="el-icon-wallet"></i>
              </file-upload> -->
              <i class="el-icon-wallet"></i>
            </div>
            <div title="发送语音" class="el-icon-microphone" @click="sendVoice()">
            </div>
            <div title="视频聊天" class="el-icon-phone-outline" @click="sendVideo()">
            </div>
            <div title="聊天记录" class="el-icon-chat-dot-round" @click="showChatRecord()"></div>
          </div>
          <!--输入框部份-->
          <div class="chat-input" style="height: 120px; right: 50px;left: 100px;">
            <div class="input-msg" ref="editBox" :content="this.newMessage" contenteditable="true" id="customInput"
              placeholder="在此输入信息……" v-html="newMessage" @input="onEditorInput" @blur="onEditBoxBlur"
              @keydown.down="onKeyDown" @keydown.up="onKeyUp" @keydown.enter.prevent="onKeyEnter"></div>
            <div style="text-align: right; padding-right: 10px;">
              <el-button type="primary" size="small" @click="sendGroupMessage">发送</el-button>
            </div>
          </div>
          <!--输入框部份-->
        </div>
        <Emotion ref="emoBox" @emotion="onEmotion"></Emotion>
      </el-col>

      <!-- 在线用户 -->
      <!-- <el-col :span="6" :offset="2">
        <el-card>
          <div slot="header" style="text-align: center; font-size: 12px;">
            在线用户
          </div>
          <div style="height: 300px; overflow: auto;">
            <div v-for="(user, index) in onlineUserList" :key="index" class="online-user">
              <span style="color: green;">🟢</span>{{ user.userName }} - {{ truncatedContent(user.tokenId) }}
            </div>
          </div>
        </el-card>
      </el-col> -->
    </el-row>
  </div>
</template>

<script>
import { getUserProfile } from "@/api/system/user";
import { closeUserConnect, getMessageNoticeList } from "@/api/chat/chat.js";
import { getToken } from "@/utils/auth";
import { list } from "@/api/monitor/online";
import Emotion from "@/components/Chat/Emotion.vue";

export default {
  components: {
    Emotion,
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 表格数据
      onlineUserList: [],
      messages: [], // 存储接收到的消息
      contents: '',
      newMessage: '', // 存储用户输入的消息
      connected: false, // 表示连接状态标志
      socket: null, // WebSocket 实例
      nickName: '',
      fromUid: '',
      msg: '',
      iaAssistantSwitch: false,
      focusNode: null, // 缓存光标所在节点
      focusOffset: null, // 缓存光标所在节点位置
      zhLock: false, // 解决中文输入法触发英文的情况
      pageNum: 1,
      pageSize: 20,
      total: 0,
      isLoading: false,
      hasMore: true
    };
  },
  created() {
    this.getMsgList();
  },
  mounted() {
    // 获取在线用户列表
    // this.getOnlineUserList();
    getUserProfile().then(response => {
      console.log("getUserProfile - response.data --->>", response.data)
      this.nickName = response.data.nickName
      this.fromUid = response.data.userId
    });
    this.initializeWebSocket().then(() => {
      // WebSocket 初始化完成后执行的逻辑
      this.joinGroup();
    });
    // 在 beforeunload 上添加事件监听器
    window.addEventListener('beforeunload', this.closeWebSocket);
    this.$nextTick(() => {
      const container = this.$refs.messageContainer;
      container.addEventListener('scroll', this.handleScroll);
    });
  },
  beforeUnmount() {
    const container = this.$refs.messageContainer;
    if (container) {
      container.removeEventListener('scroll', this.handleScroll);
    }
  },
  methods: {
    handleScroll() {
      const container = this.$refs.messageContainer;
      console.log("------->handleScroll,isLoading:", this.isLoading + ",hasMore:", this.hasMore);
      // 调整触发条件（增加缓冲区域）
      if (container.scrollTop <= 40 && this.hasMore) {
        console.log("-------------->handleScroll - 调整触发条件（增加缓冲区域）");
        this.loadMoreMessages();
      }
    },
    // 优化后的加载方法
    async loadMoreMessages() {
      console.log("----------------loadMoreMessages")
      const container = this.$refs.messageContainer;
      // 记录当前滚动高度和位置
      const prevScrollHeight = container.scrollHeight;
      const prevScrollTop = container.scrollTop;

      try {
        await this.getMsgList(true);

        // 保持滚动位置
        this.$nextTick(() => {
          const newScrollHeight = container.scrollHeight;
          container.scrollTop = prevScrollTop + (newScrollHeight - prevScrollHeight);
        });
      } catch (error) {
        console.error('加载消息失败:', error);
      }
    },
    truncatedContent(tokenId) {
      // 截取前100个字符
      return tokenId.slice(0, 5);
    },
    /**
     * 获取消息列表
     */
    // 修改后的消息获取方法
    async getMsgList(loadMore = false) {
      if (this.isLoading || !this.hasMore) return;
      this.isLoading = true;
      // 在发送请求前确定当前页码
      const currentPage = loadMore ? this.pageNum + 1 : 1;
      const queryParams = {
        pageNum: currentPage, // 使用计算后的当前页码
        pageSize: this.pageSize
      };

      try {
        const response = await getMessageNoticeList(queryParams);
        if (loadMore) {
          this.messages = [...response.rows, ...this.messages];
          this.pageNum = currentPage; // 请求成功后更新pageNum
        } else {
          this.messages = response.rows;
          this.pageNum = 1; // 初始加载重置为1
          // 初始化时滚动到底部
          this.$nextTick(() => {
            const container = this.$refs.messageContainer;
            container.scrollTop = container.scrollHeight;
          });
        }
        this.total = response.total;
        this.hasMore = this.messages.length < this.total;
      } finally {
        this.isLoading = false;
      }
    },
    succeedJoinGroup(content) {
      this.$notify({
        title: '成功',
        message: "系统消息：" + content,
        type: 'success',
        offset: 80
      });
    },
    getOnlineUserList() {
      this.loading = true;
      // list(this.queryParams).then(response => {
      //   console.log("在线用户返回 --->>", response)
      //   this.onlineUserList = response.rows;
      //   this.total = response.total;
      //   this.loading = false;
      // });
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
      this.newMessage = this.createSendText();

      console.log("【后端入参this.newMessage】 --->>", this.newMessage)
      if (this.newMessage.trim() !== '') {
        const command = {
          nickName: this.nickName,
          content: this.newMessage,
          msgType: '20002',// 发送群聊消息
          token: getToken(),
        };
        if (this.iaAssistantSwitch) {
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
        // 发送后清空输入框
        this.clearInputBox();
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
        this.socket = new WebSocket('ws://localhost:9044/ws');

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
          console.log("event返回：", event)
          // 接收到消息时触发
          const message = JSON.parse(event.data);
          const formattedMessage = {
            type: message.type,
            nickName: message.nickName,
            sendTime: message.sendTime,
            content: message.content,
            sendMsg: message.sendMsg,
            fromUid: message.fromUid
          };
          console.log("接收message返回：", message)
          if (message.type === 90001) { // 断开连接
            alert(message.content);
            this.socket.onclose();
          } else if (message.type === 10000) { // 系统消息
            console.log("--------------->系统消息")
            this.succeedJoinGroup(message.content);
          } else {
            // 将返回的消息写入消息列表
            if (formattedMessage.type === 30001) {
              formattedMessage.content = '@' + formattedMessage.nickName + ', ' + formattedMessage.content;
            }
            this.messages.push(formattedMessage);
          }
          this.contents = formattedMessage.content
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
          this.$notify({
            title: '失败',
            message: "系统消息：WS连接失败",
            type: 'error',
            offset: 80
          });
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
    showEmotionBox() {
      let width = this.$refs.emotion.offsetWidth;
      let left = this.$elm.fixLeft(this.$refs.emotion);
      let top = this.$elm.fixTop(this.$refs.emotion);
      this.$refs.emoBox.open({
        x: left + width / 2,
        y: top
      })

    },
    onEditorInput(event) {

    },
    createSendText() {
      let sendText = "";
      console.log("this.$refs.editBox--->", this.$refs.editBox)
      if (this.$refs.editBox.nodeName === "DIV" && this.$refs.editBox.hasAttribute("contenteditable")) {
        this.$refs.editBox.childNodes.forEach((node) => {
          if (node.nodeName == "#text") {
            sendText += this.html2Escape(node.textContent);
          } else if (node.nodeName == "SPAN") {
            sendText += node.innerText;
            console.log("node.innerText-->", node.innerText)
          } else if (node.nodeName == "IMG") {
            sendText += node.dataset.code;
            console.log("node.dataset.code-->", node.dataset.code)
          }
        })
      } else {
        console.error("--------createSendText error---------")
      }
      return sendText;
    },
    html2Escape(strHtml) {
      return strHtml.replace(/[<>&"]/g, function (c) {
        return {
          '<': '&lt;',
          '>': '&gt;',
          '&': '&amp;',
          '"': '&quot;'
        }[c];
      });
    },
    onEmotion(emoText) {
      // 保持输入框焦点
      this.$refs.editBox.focus();
      let range = window.getSelection().getRangeAt(0);

      // 插入光标所在位置
      range.setStart(this.focusNode, this.focusOffset)
      let element = document.createElement('IMG')
      element.className = "emo"
      element.dataset.code = emoText;
      element.contentEditable = 'true'
      element.setAttribute("src", this.$emo.textToUrl(emoText));
      console.log("element-->", element)
      // 选中元素节点
      range.insertNode(element)

    },
    onEditBoxBlur() {
      let selection = window.getSelection()
      // 记录光标位置(点击emoji时)
      this.focusNode = selection.focusNode;
      this.focusOffset = selection.focusOffset;
      // 输出调试信息
      console.log("Current focusNode:", this.focusNode);
      console.log("Current focusOffset:", this.focusOffset);
    },
    onEditorCompositionStart() {
      this.zhLock = true;
    },
    onEditorCompositionEnd(e) {
      this.zhLock = false;
      this.onEditorInput(e);
    },
    onKeyDown() {
      if (this.$refs.atBox.show) {
        this.$refs.atBox.moveDown()
      }
    },
    onKeyUp() {
      if (this.$refs.atBox.show) {
        this.$refs.atBox.moveUp()
      }
    },
    onKeyEnter() {
      if (this.$refs.atBox.show) {
        // 键盘操作不会自动修正焦点，需要手动修正,原因不详
        this.focusOffset += this.atSearchText.length;
        this.$refs.atBox.select();
      } else {
        this.sendMessage();
      }
    },
    clearInputBox() {
      // 清空输入框的 DOM 操作
      // this.sendImageUrl = "";
      // this.sendImageFile = null;
      this.$nextTick(() => {
        this.$refs.editBox.innerHTML = "";
        this.$refs.editBox.focus();
        this.newMessage = ''; // 同步更新 Vue 实例的数据
      });
    },
    sendImg() {
      alert("发送图片暂未开放，敬请期待……")
    },
    sendFileFai() {
      alert("发送文件暂未开放，敬请期待……")
    },
    sendVoice() {
      alert("发起语音暂未开放，敬请期待……")
    },
    sendVideo() {
      alert("发起视频暂未开放，敬请期待……")
    },
    showChatRecord() {
      alert("查看聊天记录暂未开放，敬请期待……")
    },
  },

  // beforeUnmount() {
  //   // 在组件卸载时确保移除事件监听器
  //   window.removeEventListener('beforeunload', this.closeWebSocket);
  // },
};
</script>

<style lang="scss">
.loading-more,
.no-more {
  text-align: center;
  padding: 10px;
  color: #666;
  font-size: 12px;
}

.el-row {
  display: flex;
}

.el-col:first-child {
  width: 200px;
}

.el-col.chat-container {
  flex: 1;
  opacity: 0.93;
  /* 设置不透明度为0.5 */
}

.ml20 {
  padding: 3px;
  background-image: url("../../assets/images/login-background1.jpg");
  background-size: cover;
}

.chat-tool-bar {

  display: flex;
  position: relative;
  width: 100%;
  height: 40px;
  text-align: left;
  box-sizing: border-box;
  border: #dddddd solid 1px;

  >div {
    margin-left: 10px;
    font-size: 22px;
    cursor: pointer;
    color: #333333;
    line-height: 40px;

    &:hover {
      color: black;
    }
  }
}

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

.chat-messages {
  max-height: 400px;
  overflow-y: auto;
  /* 需要强制指定高度才能触发滚动事件 */
  height: 400px;
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
  .input-content {
    height: 120px;
    width: -webkit-fill-available;
    padding: 15px;
    border: none;
    background-color: #f7f7fa;
    border-top: 1px solid #ccc;
    border-bottom: 1px solid #ccc;
    outline: none
  }
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

.input-msg {
  height: 120px;
  width: -webkit-fill-available;
  padding: 15px;
  border: none;
  background-color: #f7f7fa;
  border-top: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
  outline: noneF
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


.emo {
  width: 30px;
  height: 30px;
  vertical-align: bottom;
}</style>

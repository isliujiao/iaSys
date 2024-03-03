package com.ruoyi.web.controller.chat;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.dto.GroupMessageDTO;
import com.ruoyi.system.domain.vo.GroupMessageVO;
import com.ruoyi.system.service.IGroupMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Api(tags = "群聊消息")
@RestController
@RequestMapping("/message/group")
@RequiredArgsConstructor
public class GroupMessageController {

    private final IGroupMessageService groupMessageService;

    @PostMapping("/send")
    @ApiOperation(value = "发送群聊消息", notes = "发送群聊消息")
    public AjaxResult sendMessage(@Valid @RequestBody GroupMessageDTO vo) {
        return AjaxResult.success(groupMessageService.sendMessage(vo));
    }

//    @DeleteMapping("/recall/{id}")
//    @ApiOperation(value = "撤回消息", notes = "撤回群聊消息")
//    public AjaxResult recallMessage(@NotNull(message = "消息id不能为空") @PathVariable Long id) {
//        groupMessageService.recallMessage(id);
//        return AjaxResult.success();
//    }


//    @GetMapping("/loadMessage")
//    @ApiOperation(value = "拉取消息", notes = "拉取消息,一次最多拉取100条")
//    public AjaxResult loadMessage(@RequestParam Long minId) {
//        return AjaxResult.success(groupMessageService.loadMessage(minId));
//    }


//    @PutMapping("/readed")
//    @ApiOperation(value = "消息已读", notes = "将群聊中的消息状态置为已读")
//    public AjaxResult readedMessage(@RequestParam Long groupId) {
//        groupMessageService.readedMessage(groupId);
//        return AjaxResult.success();
//    }


//    @GetMapping("/history")
//    @ApiOperation(value = "查询聊天记录", notes = "查询聊天记录")
//    public AjaxResult recallMessage(@NotNull(message = "群聊id不能为空") @RequestParam Long groupId,
//                                                      @NotNull(message = "页码不能为空") @RequestParam Long page,
//                                                      @NotNull(message = "size不能为空") @RequestParam Long size) {
//        return AjaxResult.success(groupMessageService.findHistoryMessage(groupId, page, size));
//    }
}


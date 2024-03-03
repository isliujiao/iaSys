package com.ruoyi.common.core.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 群聊消息DTO
 */
@Data
public class GroupMessageDTO {
    /**
     * 群聊id
     */
    @NotNull(message = "群聊id不可为空")
    private Long groupId;

    /**
     * 发送内容
     */
    @Length(max = 1024, message = "发送内容长度不得大于1024")
    @NotEmpty(message = "发送内容不可为空")
    private String content;

    /**
     * 消息类型
     */
    @NotNull(message = "消息类型不可为空")
    private Integer type;

    /**
     * 被@用户列表
     */
    @Size(max = 20, message = "一次最多只能@20个小伙伴哦")
    private List<Long> atUserIds;
}

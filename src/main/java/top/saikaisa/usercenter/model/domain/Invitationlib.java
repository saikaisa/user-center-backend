package top.saikaisa.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName invitationlib
 */
@TableName(value ="invitationlib")
@Data
public class Invitationlib implements Serializable {
    /**
     * 邀请码
     */
    @TableId
    private String invitationCode;

    /**
     * 是否已被使用  0 -未使用
     */
    private Integer isUsed;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
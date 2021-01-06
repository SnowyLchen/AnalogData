package com.cj.demoredis.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.forte.util.mapper.MockBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * mfrs_plctemplate_info
 *
 * @author
 */
@Getter
@Setter
@ToString
@MockBean
public class MfrsPlctemplateInfo implements Serializable {
    /**
     * 自增主键
     */
    private String plctempInfoId;

    /**
     * plc生产信号生成时间
     */
    private Date plcInfoTime;
    /**
     * plc信号ID
     */
    private Integer plctempId;
    /**
     * plc信号类型ID
     */
    private String plcTypeName;

    /**
     * python对应--所在站点id（站点id）
     */
    private Integer siteId;
    /**
     * 站点名称
     */
    private String unitName;

    /**
     * modbus地址
     */
    private Integer modbus;

    /**
     * 寄存器（小写）
     */
    private String registerLowercase;

    /**
     * 对应接收数组key值
     */
    private Integer refeKey;

    /**
     * 值
     */
    private String refeValue;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 厂商id
     */
    private Integer mfrsId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String regex;

    private Integer plcType;
}

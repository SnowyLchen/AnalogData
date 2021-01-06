package com.cj.demoredis.domain;

import com.forte.util.mapper.MockBean;
import com.forte.util.mapper.MockValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * mfrs_plctemplate
 *
 * @author
 */
@Getter
@Setter
@MockBean
@ToString
public class MfrsPlctemplate implements Serializable {
    /**
     * 自增主键
     */
    @MockValue(value = "0",valueType = Integer.class,param = "1-100")
    private Integer plctempId;

    /**
     * 所在站点id
     */
    private Integer siteId;
    /**
     * 站点名称
     */
    private String siteName;

    /**
     * modbus地址  机柜号
     */
    private Integer modbus;

    /**
     * 对应接收数组key值
     */
    private Integer refeKey;


    private String dataScope;

    /**
     * 寄存器（小写）
     */
    private String registerLowercase;

    /**
     * 寄存器含义  plc信号名称
     */
    private String tempName;

    /**
     * 厂商id
     */
    private Integer mfrsId;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
    /**
     * PLC信号编号
     */
    private String plcCode;

    /**
     * 创建者姓名
     */
    private String userName;


    /**
     * PLC信号类型ID
     */
    private Integer plcTypeId;

    private String flag;

    private Integer systemId;

    private String regex;
}
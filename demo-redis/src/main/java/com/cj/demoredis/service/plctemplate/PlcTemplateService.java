package com.cj.demoredis.service.plctemplate;

import com.cj.demoredis.domain.MfrsPlctemplateInfo;

import java.util.List;

public interface PlcTemplateService {
    /**
     * 查询信号模板
     *
     * @return
     */
    List<MfrsPlctemplateInfo> queryPlcTemplateList(Integer siteId);

    /**
     * 查询temp
     *
     * @return
     */
    List<MfrsPlctemplateInfo> queryPlcTemplateTempList();

    /**
     * 查询消耗值的上一次值
     *
     * @return
     */
    List<MfrsPlctemplateInfo> queryRefeValue(String plcIds);

    /**
     * 插入数据info
     *
     * @param mfrsPlctemplateInfo
     * @return
     */
    int insert(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    /**
     * 插入数据temp
     *
     * @param mfrsPlctemplateInfo
     * @return
     */
    int insertTemp(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    /**
     * 插入数据fault
     *
     * @param mfrsPlctemplateInfo
     * @return
     */
    int insertFault(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    /**
     * 截断temp
     *
     * @return
     */
    int deleteTemp(long time);

    /**
     *
     * @param plcTypes
     * @return
     */
    String queryPlctempIds(String plcTypes);
}

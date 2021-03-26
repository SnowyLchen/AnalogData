package com.cj.demoredis.service.plctemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cj.demoredis.domain.MfrsPlctemplateInfo;
import com.cj.demoredis.mapper.PlcTemplateMapper;
import com.cj.demoredis.service.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@Service
public class PlcTemplateServiceImpl implements PlcTemplateService {

    private static final String LIST_PLC_KEY = "list_plc";

    private static final String LIST_CON_KEY = "list_con";

    private static final String LIST_TEMP_KEY = "list_temp";

    @Resource
    private PlcTemplateMapper plcTemplateMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public List<MfrsPlctemplateInfo> queryPlcTemplateList(Integer siteId) {
        List<MfrsPlctemplateInfo> plctemplates = null;
        String plctemplateString = redisService.get(LIST_PLC_KEY);
        if (plctemplateString == null) {
            System.out.println("============从数据库中查询============");
            if (siteId == null) {
                plctemplates = plcTemplateMapper.queryPlcTemplateList();
            } else {
                plctemplates = plcTemplateMapper.queryPlcTemplateList2(siteId);
            }
            //将users转为字符串类型存入
            String toJSONString = JSON.toJSONString(plctemplates);
            //将查出的数据存入redis中
            redisService.set(LIST_PLC_KEY, toJSONString);
            redisService.expire(LIST_PLC_KEY, 1000);
        } else {
            System.out.println("============从Redis中查询============");
            plctemplates = JSONArray.parseArray(plctemplateString, MfrsPlctemplateInfo.class);
        }
        return plctemplates;
    }

    @Override
    public List<MfrsPlctemplateInfo> queryPlcTemplateTempList() {
        return plcTemplateMapper.queryPlcTemplateTempList();
//        List<MfrsPlctemplateInfo> plctemplates = null;
//        String plctemplateString = redisService.get(LIST_TEMP_KEY);
//        if (plctemplateString == null || "[]".equals(plctemplateString)) {
//            System.out.println("============从数据库中查询============");
//            plctemplates = plcTemplateMapper.queryPlcTemplateTempList();
//            //将users转为字符串类型存入
//            String toJSONString = JSON.toJSONString(plctemplates);
//            //将查出的数据存入redis中
//            redisService.set(LIST_TEMP_KEY, toJSONString);
//            redisService.expire(LIST_TEMP_KEY, 1000);
//        } else {
//            System.out.println("============从Redis中查询============");
//            plctemplates = JSONArray.parseArray(plctemplateString, MfrsPlctemplateInfo.class);
//        }
//        return plctemplates;
    }

    @Override
    public List<MfrsPlctemplateInfo> queryRefeValue(String plcIds) {
//        List<MfrsPlctemplateInfo> plctemplates = null;
//        String ps = redisService.get(LIST_CON_KEY);
//        if (ps == null || "[]".equals(ps)) {
//            System.out.println("============从数据库中查询============");
//            plctemplates = plcTemplateMapper.queryRefeValue();
//            //将查出的数据存入redis中
//            redisService.set(LIST_CON_KEY, JSON.toJSONString(plctemplates));
//            redisService.expire(LIST_CON_KEY, 1000);
//        } else {
//            System.out.println("============从Redis中查询============");
//            plctemplates = JSONArray.parseArray(ps, MfrsPlctemplateInfo.class);
//        }
//        return plctemplates;
        return plcTemplateMapper.queryRefeValue(plcIds);
    }

    @Override
    public int insert(MfrsPlctemplateInfo mfrsPlctemplateInfo) {
        return plcTemplateMapper.insert(mfrsPlctemplateInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertTemp(MfrsPlctemplateInfo mfrsPlctemplateInfo) {
        UUID uuid = UUID.randomUUID();
        mfrsPlctemplateInfo.setUuid(uuid.toString());
        int i = plcTemplateMapper.insertTemp(mfrsPlctemplateInfo);
        if (i > 0) {
            plcTemplateMapper.deleteTempById(mfrsPlctemplateInfo.getPlctempId(), mfrsPlctemplateInfo.getUuid());
        }
        return 1;
    }

    @Override
    public int insertFault(MfrsPlctemplateInfo mfrsPlctemplateInfo) {
        return plcTemplateMapper.insertFault(mfrsPlctemplateInfo);
    }

    @Override
    public int deleteTemp(long time) {
        return plcTemplateMapper.deleteTemp(time);
    }

    @Override
    public String queryPlctempIds(String plcTypes) {
        return plcTemplateMapper.queryPlctempIds(plcTypes);
    }
}

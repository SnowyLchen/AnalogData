package com.cj.demoredis.mapper;

import com.cj.demoredis.domain.MfrsPlctemplateInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlcTemplateMapper {

    @Select("SELECT plctemp_id,regex, site_id, modbus, refe_key, register_lowercase, plc_code, temp_name, plc_type, remark, mfrs_id, status, del_flag, create_by, create_time, update_by, update_time\n" +
            "FROM mfrs_plctemplate WHERE del_flag =0")
    List<MfrsPlctemplateInfo> queryPlcTemplateList();

    @Insert("INSERT INTO mfrs_plctemplate_info (plctemp_id,site_id,modbus,register_lowercase,refe_key,refe_value,status,mfrs_id,plc_info_time,del_flag,create_time,update_time)" +
            " VALUES(#{plctempId},#{siteId},#{modbus},#{registerLowercase},#{refeKey},#{refeValue},#{status},#{mfrsId},#{plcInfoTime},#{delFlag},#{createTime},#{updateTime})")
    int insert(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    @Insert("INSERT INTO mfrs_plctemplate_info_fault (plctemp_id,site_id,modbus,register_lowercase,refe_key,refe_value,status,mfrs_id,plc_info_time,del_flag,create_time,update_time)" +
            " VALUES(#{plctempId},#{siteId},#{modbus},#{registerLowercase},#{refeKey},#{refeValue},#{status},#{mfrsId},#{plcInfoTime},#{delFlag},#{createTime},#{updateTime})")
    int insertFault(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    @Select("SELECT mpit.*,mp.plc_type FROM mfrs_plctemplate_info_temp mpit\n" +
            "LEFT JOIN mfrs_plctemplate mp on mp.plctemp_id=mpit.plctemp_id and mp.del_flag=0\n" +
            "WHERE mpit.del_flag = 0 AND FIND_IN_SET( mpit.plctemp_id, '472,474,475,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,958,969,1000,1001,1002,1057,1058,1059,1060,1061,1062,1063,1065,1066')")
    List<MfrsPlctemplateInfo> queryRefeValue();

    @Select("SELECT mpit.*,mp.plc_type FROM mfrs_plctemplate_info_temp mpit\n" +
            "LEFT JOIN mfrs_plctemplate mp on mp.plctemp_id=mpit.plctemp_id and mp.del_flag=0\n" +
            "WHERE mpit.del_flag = 0 AND mp.plc_type in (114,119,120)")
    List<MfrsPlctemplateInfo> queryPlcTemplateTempList();

    @Insert("INSERT INTO mfrs_plctemplate_info_temp (plctemp_id,site_id,modbus,register_lowercase,refe_key,refe_value,status,mfrs_id,plc_info_time,del_flag,create_time,update_time)" +
            " VALUES(#{plctempId},#{siteId},#{modbus},#{registerLowercase},#{refeKey},#{refeValue},#{status},#{mfrsId},#{plcInfoTime},#{delFlag},#{createTime},#{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "plctempInfoId", keyColumn = "plctemp_info_id")
    int insertTemp(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    @Delete("update mfrs_plctemplate_info_temp set refe_value=#{refeValue}, update_time=#{updateTime}, plc_info_time=#{plcInfoTime} where plctemp_id=#{plctempId}")
    int updateTemp(MfrsPlctemplateInfo mfrsPlctemplateInfo);

    @Delete("delete from mfrs_plctemplate_info_temp where plctemp_id=#{plctempId} and plctemp_info_id != #{id}")
    int deleteTempById(@Param("plctempId") int plctempId, @Param("id") String id);

    @Delete("delete from mfrs_plctemplate_info_temp where plc_info_time < FROM_UNIXTIME(#{time}/1000)")
//    @Delete("truncate table mfrs_plctemplate_info_temp")
    int deleteTemp(long time);
}

package com.cj.demoredis.service.data;

import com.cj.demoredis.utils.ApiConfig;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * @author chen
 */
@Service
public class DataServiceImpl implements DataService {
    private Integer[] consumption;
    private Integer[] fault;
    private Integer[] gqs;
    private Integer[] ogqs;
    private Integer[] v1;
    private Integer[] v2;
    private Integer[] v3;
    private Integer[] types;
    private Integer[] scatter;
    private Integer[] electric;
    private Integer[] hvac;

    public DataServiceImpl(@NonNull ApiConfig apiConfig) {
        this.consumption = apiConfig.getConsumption();
        this.fault = apiConfig.getFault();
        this.gqs = apiConfig.getGqs();
        this.ogqs = apiConfig.getOgqs();
        this.v1 = apiConfig.getV1();
        this.v2 = apiConfig.getV2();
        this.v3 = apiConfig.getV3();
        this.types = apiConfig.getTypes();
        this.electric = apiConfig.getElectric();
        this.scatter = apiConfig.getScatter();
        this.hvac = apiConfig.getHvac();
    }

    @Override
    public Integer[] getConsumption() {
        return consumption;
    }

    @Override
    public Integer[] getV1() {
        return v1;
    }

    @Override
    public Integer[] getV2() {
        return v2;
    }

    @Override
    public Integer[] getV3() {
        return v3;
    }

    @Override
    public Integer[] getGqs() {
        return gqs;
    }

    @Override
    public Integer[] getOgqs() {
        return ogqs;
    }

    @Override
    public Integer[] getFault() {
        return fault;
    }

    @Override
    public Integer[] getTypes() {
        return types;
    }

    @Override
    public Integer[] getElectric() {
        return electric;
    }

    @Override
    public Integer[] getScatter() {
        return scatter;
    }

    @Override
    public Integer[] getHVAC() {
        return hvac;
    }
}

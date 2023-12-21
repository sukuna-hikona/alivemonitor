package com.hk_edn.common;

import java.util.HashMap;
import java.util.List;

public class PowerStatusObject {
    
    private Integer id;
    private List<HashMap<String, String>> result;


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<HashMap<String,String>> getResult() {
        return this.result;
    }

    public void setResult(List<HashMap<String,String>> result) {
        this.result = result;
    }
}
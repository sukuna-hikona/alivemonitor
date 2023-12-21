package com.hk_edn.schedule;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;


public class ScheduleObjectList implements Serializable {
    
    @Valid
    private List<ScheduleObject> scheduleDataList;
    

    public List<ScheduleObject> getScheduleDataList() {
        return this.scheduleDataList;
    }

    public void setScheduleDataList(List<ScheduleObject> scheduleDataList) {
        this.scheduleDataList = scheduleDataList;
    }
}

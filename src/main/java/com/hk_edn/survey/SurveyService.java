package com.hk_edn.survey;

import java.util.ArrayList;
import java.util.List;

import com.hk_edn.common.TvObject;
import com.hk_edn.failure.FailureManagementService;
import com.hk_edn.list.ListManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyService {
    
    @Autowired
    private ListManagementService listManagementService;

    @Autowired
    private FailureManagementService failureManagementService;

    public List<TvObject> getCurrentTvList(Integer floor) {

        List<TvObject> currentFloorTvList = new ArrayList<>();
        List<TvObject> allTvList = listManagementService.getTvList();

        for (TvObject bravia : allTvList) {
            if (bravia.getFloorNo().equals(floor)) {
                currentFloorTvList.add(bravia);
            }
        }

        return currentFloorTvList;
    }

    public Integer judgeFailure(List<TvObject> tvList) {

        return failureManagementService.judgeFailure(tvList);
    }

    public Boolean judgeSurveillance() {

        return failureManagementService.onSurveillance();
    }
}

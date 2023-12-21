package com.hk_edn.survey;

import java.util.List;

import com.hk_edn.check.CheckManagementService;
import com.hk_edn.common.TvObject;
import com.hk_edn.list.ListManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private ListManagementService listManagementService;

    @Autowired
    private CheckManagementService checkManagementService;


    private static final String INDEX_URL = "index";


    @GetMapping(value="/")
    public String root(Model model) {
        
        final Integer FLOOR = 1;

        List<TvObject> tvList = listManagementService.getTvList();
        List<TvObject> cTvList = surveyService.getCurrentTvList(FLOOR);

        checkManagementService.isStarted();

        model.addAttribute("tvList", cTvList);
        model.addAttribute("failure", surveyService.judgeFailure(tvList));
        model.addAttribute("surveillance", surveyService.judgeSurveillance());
        model.addAttribute("floor", FLOOR);

        return INDEX_URL;
    }
    
    @GetMapping(value="/Move")
    public String onMoveRequested(@RequestParam(value="floor", required = false) Integer floor,
                                    Model model) {
        
        List<TvObject> tvList = listManagementService.getTvList();
        List<TvObject> cTvList = surveyService.getCurrentTvList(floor);

        checkManagementService.isStarted();
        
        model.addAttribute("tvList", cTvList);
        model.addAttribute("failure", surveyService.judgeFailure(tvList));
        model.addAttribute("surveillance", surveyService.judgeSurveillance());
        model.addAttribute("floor", floor);

        return INDEX_URL;
    }
}

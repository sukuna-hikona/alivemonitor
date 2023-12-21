package com.hk_edn.schedule;

import java.util.Calendar;
import java.util.List;

import com.hk_edn.common.TvObject;
import com.hk_edn.survey.SurveyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ScheduleController {
    
    @Autowired
    private ScheduleService scheduleService;

    @GetMapping(value="/Schedule")
    public String onScheduleChange( @RequestParam(value="month", required = false) Integer month,
                                    Model model) {

        if (month == null) {
            Calendar calendar = Calendar.getInstance();
            month = calendar.get(Calendar.MONTH) + 1;
        }
        if (month < 2) {
            month = 2;
        } else if (11 < month) {
            month = 11;
        }

        ScheduleObjectList scheduleDataList = scheduleService.getSchedule(month);

        model.addAttribute("scheduleDataList", scheduleDataList);
        model.addAttribute("month", month);

        return "schedule/index";
    }

    @Autowired
    private SurveyService surveyService;

    @PostMapping(value="/ScheduleInput")
    public String onScheduleRegistrationRequested(  @Validated @ModelAttribute ScheduleObjectList scheduleDataList,
                                        	        BindingResult bindingResult,
                                        	        Model model) {
        
        if (!bindingResult.hasErrors()) {
            scheduleService.setSchedule(scheduleDataList);
        }

        final Integer FLOOR = 1;

        List<TvObject> tvList = surveyService.getCurrentTvList(FLOOR);
        model.addAttribute("tvList", tvList);
        model.addAttribute("failure", surveyService.judgeFailure(tvList));
        model.addAttribute("floor", FLOOR);

        
        return "redirect:/";
    }
}

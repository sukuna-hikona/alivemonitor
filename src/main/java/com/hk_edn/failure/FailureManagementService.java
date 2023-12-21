package com.hk_edn.failure;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.hk_edn.common.TvObject;
import com.hk_edn.communication.MailSendService;
import com.hk_edn.communication.WebhookService;
import com.hk_edn.logging.LoggingService;
import com.hk_edn.schedule.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FailureManagementService {
    
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private MailSendService mailSendService;

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private WebhookService webhookService;


    public Boolean onSurveillance() {

        List<String> schedule = scheduleService.getCurrentDaySchedule();
        Boolean judgeSurveillance = false;

        if (Integer.parseInt(schedule.get(0)) != 77 && Integer.parseInt(schedule.get(1)) != 77) {
            LocalTime gameStartTime = LocalTime.of(Integer.parseInt(schedule.get(0)), Integer.parseInt(schedule.get(1)));
            LocalTime openGateTime = LocalTime.of(15, 0, 0);

            if (Integer.parseInt(schedule.get(0)) <= 11) {
                openGateTime = gameStartTime.minusMinutes(180);
            } else if (Integer.parseInt(schedule.get(0)) <= 14) {
                openGateTime = LocalTime.of(11, 0, 0);
            }

            LocalTime nowTime = LocalTime.now();

            if (nowTime.isAfter(openGateTime.minusMinutes(25))) {
                judgeSurveillance = true;
            }
        }

        return judgeSurveillance;
    }

    public List<TvObject> setFailure(List<TvObject> tvList) {

        List<String> schedule = scheduleService.getCurrentDaySchedule();
        List<TvObject> cngTvList = new ArrayList<>();

        if (schedule.get(0).equals("77") || schedule.get(1).equals("77")) {

            for (TvObject bravia : tvList) {
                bravia.setFailure(false);

                cngTvList.add(bravia);
            }
        } else {

            LocalTime gameStartTime = LocalTime.of(Integer.parseInt(schedule.get(0)), Integer.parseInt(schedule.get(1)));
            LocalTime gameEndTime = LocalTime.of(Integer.parseInt(schedule.get(0)) + 5, Integer.parseInt(schedule.get(1)));
            LocalTime openGateTime = LocalTime.of(15, 0, 0);

            if (Integer.parseInt(schedule.get(0)) <= 11) {
                openGateTime = gameStartTime.minusMinutes(180);
            } else if (Integer.parseInt(schedule.get(0)) <= 14) {
                openGateTime = LocalTime.of(11, 0, 0);
            }

            for (TvObject bravia : tvList) {

                TvObject newBravia = addFailureStatus(bravia, openGateTime, gameStartTime, gameEndTime);

                cngTvList.add(newBravia);
            }
        }

        return cngTvList;
    }

    private TvObject addFailureStatus(TvObject bravia, LocalTime openGateTime, LocalTime gameStartTime, LocalTime gameEndTime) {

        if (!bravia.isIgnore()) {
            if (bravia.getInstallation() == 1) {
                bravia.setFailure(judgeCarpArea(bravia, openGateTime, gameEndTime));
            } else if (bravia.getInstallation() == 2) {
                bravia.setFailure(judgeEntrance100Inch(bravia, gameStartTime, gameEndTime));
            } else {
                bravia.setFailure(judgeHiroshimaCityArea(bravia, openGateTime, gameStartTime, gameEndTime));
            }

            if (bravia.getFailure() && bravia.getPowerStatus() == 200) {
                bravia.setPowerStatus(502);
            }
        }

        return bravia;
    }

    private Boolean judgeCarpArea(TvObject bravia, LocalTime openGateTime, LocalTime gameEndTime) {

        LocalTime nowTime = LocalTime.now();
        Boolean judgeTerm = false;

        if (nowTime.isAfter(openGateTime.minusMinutes(25)) && nowTime.isBefore(gameEndTime)) {
            judgeTerm = true;
        }

        return judgeTerm && bravia.getPowerStatus() == 404
            || judgeTerm && bravia.getPowerStatus() == 500
            || judgeTerm && !bravia.getChannel().contains("HDMI1") && !bravia.getChannel().contains("HDMI 1");
    }

    private Boolean judgeEntrance100Inch(TvObject bravia, LocalTime gameStartTime, LocalTime gameEndTime) {

        LocalTime nowTime = LocalTime.now();
        Boolean judgeTerm = false;

        if (nowTime.isAfter(gameStartTime.minusMinutes(10)) && nowTime.isBefore(gameEndTime)) {
            judgeTerm = true;
        }

        return judgeTerm && bravia.getPowerStatus() == 404
            || judgeTerm && bravia.getPowerStatus() == 500
            || judgeTerm && !bravia.getChannel().contains("HDMI3") && !bravia.getChannel().contains("HDMI 3");
    }

    private Boolean judgeHiroshimaCityArea(TvObject bravia, LocalTime openGateTime, LocalTime gameStartTime, LocalTime gameEndTime) {

        LocalTime nowTime = LocalTime.now();
        Integer judgeTerm = 0;

        if (nowTime.isAfter(openGateTime.minusMinutes(25)) && nowTime.isBefore(gameStartTime.minusMinutes(20))) {
            judgeTerm = 100;
        } else if (nowTime.isAfter(gameStartTime.minusMinutes(10)) && nowTime.isBefore(gameStartTime.plusMinutes(145))){
            judgeTerm = 500;
        } else if (nowTime.isAfter(gameStartTime.plusMinutes(155)) && nowTime.isBefore(gameEndTime)) {
            judgeTerm = 700;
        }

        return 100 <= judgeTerm && judgeTerm <= 700 && bravia.getPowerStatus() == 404
            || 100 <= judgeTerm && judgeTerm <= 700 && bravia.getPowerStatus() == 500
            || judgeTerm == 100 && !bravia.getChannel().contains("サービス1") && !bravia.getChannel().contains("サービス 1") && !bravia.getChannel().contains("サービス１")
            || judgeTerm == 500 && !bravia.getChannel().contains("HDMI1") && !bravia.getChannel().contains("HDMI 1")
            || judgeTerm == 700 && !bravia.getChannel().contains("サービス1") && !bravia.getChannel().contains("サービス 1") && !bravia.getChannel().contains("サービス１");
    }

    public Integer judgeFailure(List<TvObject> tvList) {

        Integer judgeSend = 200;

        for (TvObject bravia : tvList) {
            if (bravia.getFailure()) {

                judgeSend = bravia.getPowerStatus();
            }
        }

        if (judgeSend.equals(404) || judgeSend.equals(500) || judgeSend.equals(502)) {
            
            mailSendService.sendFailureMail(tvList);
            loggingService.failureLog(tvList);
            try {
                webhookService.sendWebhook(tvList);
            } catch (Exception ex) {
                // TODO
                System.out.println(ex);
            }
        }

        return judgeSend;
    }
}

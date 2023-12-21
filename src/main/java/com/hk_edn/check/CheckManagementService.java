package com.hk_edn.check;

import java.io.IOException;

import com.hk_edn.communication.MailSendService;
import com.hk_edn.failure.FailureManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckManagementService {

    @Autowired
    private MailSendService mailSendService;

    @Autowired
    private FailureManagementService failureManagementService;

    public Boolean isStarted() {

        CheckStarted checkStarted = new CheckStarted();
        Boolean isStarted = checkStarted.isStarted();

        if (Boolean.TRUE.equals(failureManagementService.onSurveillance())) {

            if (Boolean.FALSE.equals(isStarted)) {

                try {

                    checkStarted.changeDate();
                    mailSendService.sendStartedMail();
    
                    isStarted = true;
                } catch (IOException ex) {
                    // TODO エラーページ作成後実装
                }
            }
        } else {

            isStarted = true;
        }

        return isStarted;
    }
}

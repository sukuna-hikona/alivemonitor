package com.hk_edn.communication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hk_edn.common.TvObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailSendService {

    @Autowired
    private MailSender mailSender;
    
    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendFailureMail(List<TvObject> tvList) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("carp-team@hk-edn.com");

        List<String> emailAddresses = new ArrayList<>();
        for (String mailAddress : getMailAddress("FailureMailAddress.txt")) {
            
            emailAddresses.add(mailAddress);
        }
        msg.setTo(emailAddresses.toArray(new String[emailAddresses.size()]));

        msg.setSubject("マツダスタジアムサイネージモニターエラー");               
        msg.setText(createFailureMailBody(tvList));

        try {
            mailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createFailureMailBody(List<TvObject> tvList) {

        Calendar calendar = Calendar.getInstance();
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);
        Integer second = calendar.get(Calendar.SECOND);

        StringBuilder mailBody = new StringBuilder();

        mailBody.append("発生時刻： " + hour + ":" + minute + ":" + second + "\r\n");
        mailBody.append("マツダスタジアムのサイネージモニターにエラーが生じました。\r\n");
        mailBody.append("至急確認してください。\r\n\r\n");

        for (TvObject bravia : tvList) {

            if (bravia.isFailure()) {
                mailBody.append("   ");
                mailBody.append(bravia.getTvNo());
                mailBody.append(":");
                mailBody.append(bravia.getTvName());
                mailBody.append(":");
                mailBody.append(bravia.getPowerStatus());
                mailBody.append(":");
                mailBody.append(bravia.getChannel());
                mailBody.append("\r\n");
            }
        }

        return mailBody.toString();
    }


    public List<String> getMailAddress(String fileName) {

        List<String> mailAddresses = new ArrayList<>();

        try (InputStream is = new ClassPathResource("setting/" + fileName).getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            
            while ((line = br.readLine()) != null) {

                if (!line.equals("")) {
                    mailAddresses.add(line);
                }
            }
        } catch (Exception ex) {
            // TODO エラーページ作成後実装
        }

        if (mailAddresses.isEmpty()) {
            
            mailAddresses.add("");
        }

        return mailAddresses;
    }


    public void sendStartedMail() {

        LocalDateTime now = LocalDateTime.now();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("carp-team@hk-edn.com");

        List<String> emailAddresses = new ArrayList<>();

        for (String mailAddress : getMailAddress("StartedMailAddress.txt")) {

            emailAddresses.add(mailAddress);
        }
        msg.setTo(emailAddresses.toArray(new String[emailAddresses.size()]));

        msg.setSubject("マツダスタジアムサイネージ モニタリング開始");               
        msg.setText(now + " モニタリング開始");

        try {
            mailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

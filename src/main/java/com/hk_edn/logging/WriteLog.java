package com.hk_edn.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.hk_edn.common.TvObject;
import com.hk_edn.communication.MailSendService;

import org.springframework.core.io.ClassPathResource;


public class WriteLog {
    
    public void writeFailureLog(List<TvObject> tvList) {

        Calendar calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer day = calendar.get(Calendar.DATE);
        
        MailSendService mailSendService = new MailSendService();

        final String WRITE_FILE_PATH = "src/main/resources/log/" +year + "_" + month + "_" + day + "_FailureLog.txt";
        final String READ_FILE_PATH = "log/" +year + "_" + month + "_" + day + "_FailureLog.txt";
        // final String READ_FILE_PATH = "log/sample.txt";

        File logFile = new File(WRITE_FILE_PATH);


        try (FileWriter fileWriter = new FileWriter(new ClassPathResource(READ_FILE_PATH).getFile().getAbsolutePath(), true)) {

            StringBuilder logMessage = new StringBuilder();

            logMessage.append("---------------------------------------------------------------------------");
            logMessage.append("\r\n");
            logMessage.append("\r\n");
            logMessage.append(mailSendService.createFailureMailBody(tvList));
            logMessage.append("\r\n");
            logMessage.append("\r\n");
            logMessage.append("---------------------------------------------------------------------------");
            logMessage.append("\r\n");

            fileWriter.write(logMessage.toString());
        } catch (IOException ex) {
            // TODO エラーページ作成後実装
            ex.printStackTrace();
            System.out.println(ex);
        }
    }
}

package com.hk_edn.check;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

public class CheckStarted {

    @Autowired
    ResourceLoader resourceLoader;

    private static final String CHECK_FILE = "check/started.txt";

    Calendar calendar;
    
    public Boolean isStarted() {

        this.calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer day = calendar.get(Calendar.DATE);
        String date = String.valueOf(year) + String.valueOf(month) + String.valueOf(day);

        Boolean isStarted = false;

        try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new ClassPathResource(CHECK_FILE).getInputStream()))) {

            String line;

            while ((line = bufReader.readLine()) != null) {

                if (line.equals(date)) {

                    isStarted = true;
                }
            }
        } catch (Exception ex) {
            // TODO エラーページ作成後実装
            System.out.println(ex);
        }
        return isStarted;
    }


    public void changeDate() throws IOException {
        
        this.calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer day = calendar.get(Calendar.DATE);
        String date = String.valueOf(year) + String.valueOf(month) + String.valueOf(day);

        File checkFile = new ClassPathResource(CHECK_FILE).getFile();


        try (FileWriter fileWriter = new FileWriter(checkFile, false)) {

            fileWriter.write(date);
        }
    }
}

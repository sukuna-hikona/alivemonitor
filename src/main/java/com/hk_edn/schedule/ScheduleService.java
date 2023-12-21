package com.hk_edn.schedule;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private static final String SCHEDULE_TEXT_PATH = "schedule/month";
    private static final String NO_TIME_SETTING = "77";

    
    public ScheduleObjectList getSchedule(Integer month) {

        List<ScheduleObject> scheduleList = new ArrayList<>();

        try (InputStream is = new ClassPathResource(SCHEDULE_TEXT_PATH + month + ".txt").getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            
            Integer count = 1;
            while ((line = br.readLine()) != null) {

                List<String> oneLine;
                if (line.equals(":")) {
                    oneLine = new ArrayList<>(Arrays.asList(NO_TIME_SETTING, NO_TIME_SETTING));
                } else {
                    oneLine = new ArrayList<>(Arrays.asList(line.split(":")));
                }

                ScheduleObject schedule = textToSchedule(month, count, oneLine);

                scheduleList.add(schedule);
                count++;
            }
        } catch (Exception ex) {
            // TODO エラーページ作成後実装
        }


        ScheduleObjectList scheduleDataList = new ScheduleObjectList();
        scheduleDataList.setScheduleDataList(scheduleList);

        return scheduleDataList;
    }


    public List<String> getCurrentDaySchedule() {
        
        Calendar calendar = Calendar.getInstance();
        Integer month = calendar.get(Calendar.MONTH) + 1;
        Integer day = calendar.get(Calendar.DATE);

        List<String> oneLine = new ArrayList<>(Arrays.asList(NO_TIME_SETTING, NO_TIME_SETTING));

        try (InputStream is = new ClassPathResource(SCHEDULE_TEXT_PATH + month + ".txt").getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            
            Integer count = 1;
            while ((line = br.readLine()) != null) {
                if (count.equals(day) && !line.equals(":")) {
                    oneLine = new ArrayList<>(Arrays.asList(line.split(":")));
                }

                count++;
            }
        } catch (Exception ex) {

            oneLine = new ArrayList<>(Arrays.asList(NO_TIME_SETTING, NO_TIME_SETTING));
        }

        return oneLine;
    }


    public void setSchedule(ScheduleObjectList scheduleDataList) {

        try (FileWriter fileWriter = new FileWriter(new ClassPathResource(SCHEDULE_TEXT_PATH + scheduleDataList.getScheduleDataList().get(0).getMonth().toString() + ".txt").getFile())) {
            for (ScheduleObject schedule : scheduleDataList.getScheduleDataList()) {
                List<String> oneLine = scheduleToText(schedule);
                
                fileWriter.write(oneLine.get(0) + ":" + oneLine.get(1) + "\n");
            }
        } catch (IOException ex) {
            // TODO エラーページ作成後実装
        }
    }


    private ScheduleObject textToSchedule(Integer month, Integer day, List<String> oneLine) {

        ScheduleObject schedule = new ScheduleObject();
        schedule.setMonth(month);
        schedule.setDay(day);
        if (oneLine.get(0).equals("")) {
            schedule.setHour(77);
        } else {
            schedule.setHour(Integer.parseInt(oneLine.get(0)));
        }
        if (oneLine.get(1).equals("")) {
            schedule.setMinute(77);
        } else {
            schedule.setMinute(Integer.parseInt(oneLine.get(1)));
        }

        return schedule;
    }

    private List<String> scheduleToText(ScheduleObject schedule) {

        String hour = "";
        String minute = "";

        if (schedule.getHour() != null) {
            hour = schedule.getHour().toString();
        }
        if (schedule.getMinute() != null) {
            minute = schedule.getMinute().toString();
        }

        return new ArrayList<>(Arrays.asList(hour, minute));
    }
}

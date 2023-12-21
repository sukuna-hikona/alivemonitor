package com.hk_edn.list;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hk_edn.common.BraviaSendObject;
import com.hk_edn.common.PowerStatusObject;
import com.hk_edn.common.TvObject;
import com.hk_edn.communication.GetTvStatus;
import com.hk_edn.failure.FailureManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class ListManagementService {

    @Autowired
    private GetTvStatus getTvStatus;

    @Autowired
    private FailureManagementService failureManagementService;
    
    public List<TvObject> getTvList() {

        List<TvObject> tvList = new ArrayList<>();

        try (InputStream is = new ClassPathResource("setting/tvList.txt").getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {

            String line;
            
            while ((line = br.readLine()) != null) {

                List<String> oneLine = new ArrayList<>(Arrays.asList(line.split(":")));

                TvObject bravia = new TvObject();
                bravia.setTvNo(Integer.parseInt(oneLine.get(0)));
                bravia.setFloorNo(Integer.parseInt(oneLine.get(1)));
                bravia.setIpAddress(oneLine.get(2));
                bravia.setTvName(oneLine.get(3));
                if (Integer.parseInt(oneLine.get(4)) == 1) {
                    bravia.setIgnore(true);
                } else {
                    bravia.setIgnore(false);
                }
                bravia.setInstallation(Integer.parseInt(oneLine.get(5)));
                bravia.setPowerStatus(404);
                bravia.setChannel("未取得");

                String[] octed = oneLine.get(2).split(Pattern.quote("."));
                bravia.setLastIP(octed[2] + String.format("%03d", Integer.parseInt(octed[3])));

                tvList.add(bravia);
            }
        } catch (Exception ex) {
            // TODO エラーページ作成後実装
        }

        addTvStatus(tvList);
        failureManagementService.setFailure(tvList);

        return tvList;
    }

    private List<TvObject> addTvStatus(List<TvObject> tvList) {

        List<TvObject> cngTvList = new ArrayList<>();

        // 電源ステータス取得
        for (TvObject bravia : tvList) {
            try {
                String stringUrl = "http://" + bravia.getIpAddress() + "/sony/system";
                BraviaSendObject sendObject = new BraviaSendObject();
                sendObject.setMethod("getPowerStatus");
                sendObject.setId(bravia.getTvNo() * 1000 + 100);
                sendObject.setVersion("1.0");
                sendObject.setParams(new ArrayList<>());

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(sendObject);
                String status = getTvStatus.communication2Tv(stringUrl, json);
                PowerStatusObject powerStatus = mapper.readValue(status, PowerStatusObject.class);

                if (powerStatus.getResult().get(0).get("status").equals("active")) {
                    bravia.setPowerStatus(200);
                } else if (powerStatus.getResult().get(0).get("status").equals("standby")) {
                    bravia.setPowerStatus(500);
                } else {
                    bravia.setPowerStatus(404);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                bravia.setPowerStatus(404);
            }


            if (bravia.getPowerStatus().equals(200)) {
                try {
                    String stringUrl = "http://" + bravia.getIpAddress() + "/sony/avContent";
                    BraviaSendObject sendObject = new BraviaSendObject();
                    sendObject.setMethod("getPlayingContentInfo");
                    sendObject.setId(bravia.getTvNo() * 1000 + 200);
                    sendObject.setVersion("1.0");
                    sendObject.setParams(new ArrayList<>());

                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(sendObject);
                    String status = getTvStatus.communication2Tv(stringUrl, json);
                    PowerStatusObject channelStatus = mapper.readValue(status, PowerStatusObject.class);
                    bravia.setChannel(channelStatus.getResult().get(0).get("title"));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (bravia.getPowerStatus().equals(500)) {
                bravia.setChannel("Display Is Turned off");
            }

            cngTvList.add(bravia);
        }

        return cngTvList;
    }
}

package com.hk_edn.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hk_edn.common.TvObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    @Autowired
    private MailSendService mailSendService;

    
    /** Webhook データ構造 */
    @JsonSerialize
    static class SimpleTeamsIncoming {
        public String title;
        public String text;
    }

    public void sendWebhook(List<TvObject> tvList) throws IOException {

        // 送信データの値をセット
        SimpleTeamsIncoming incoming = new SimpleTeamsIncoming();
        incoming.title = "マツダスタジアムサイネージモニターエラー";
        incoming.text = mailSendService.createFailureMailBody(tvList);

        // 送信データを JSONテキスト化
        final ObjectMapper mapper = new ObjectMapper();
        final String incomingJson = mapper.writeValueAsString(incoming);

        try (InputStream is = new ClassPathResource("setting/WebhookURL.txt").getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            line = br.readLine();

            // タイムアウト設定
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setConnectTimeout(500);
            requestFactory.setReadTimeout(500);

            // API 呼び出し
            RequestEntity<?> req = RequestEntity
                .post(URI.create(line))
                .contentType(MediaType.APPLICATION_JSON)
                .body(incomingJson);
                
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            restTemplate.exchange(req, String.class);
        }
    }
}

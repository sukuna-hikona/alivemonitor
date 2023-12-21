package com.hk_edn.logging;

import java.util.List;

import com.hk_edn.common.TvObject;

import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    
    public void failureLog(List<TvObject> tvList) {

        WriteLog writeLog = new WriteLog();

        writeLog.writeFailureLog(tvList);
    }
}

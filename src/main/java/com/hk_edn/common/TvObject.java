package com.hk_edn.common;

public class TvObject {
    
    private Integer tvNo;
    private Integer floorNo;
    private String ipAddress;
    private String tvName;
    private boolean ignore;
    private boolean failure;
    private Integer installation;
    private Integer powerStatus;
    private String channel;
    private String lastIP;

    
    public Integer getTvNo() {
        return this.tvNo;
    }

    public void setTvNo(Integer tvNo) {
        this.tvNo = tvNo;
    }

    public Integer getFloorNo() {
        return this.floorNo;
    }

    public void setFloorNo(Integer floorNo) {
        this.floorNo = floorNo;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getTvName() {
        return this.tvName;
    }

    public void setTvName(String tvName) {
        this.tvName = tvName;
    }

    public boolean isIgnore() {
        return this.ignore;
    }
    
    public boolean getIgnore() {
        return this.ignore;
    }

    public void setIgnore(boolean isIgnore) {
        this.ignore = isIgnore;
    }

    public boolean isFailure() {
        return this.failure;
    }

    public boolean getFailure() {
        return this.failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public Integer getInstallation() {
        return this.installation;
    }

    public void setInstallation(Integer installation) {
        this.installation = installation;
    }

    public Integer getPowerStatus() {
        return this.powerStatus;
    }

    public void setPowerStatus(Integer powerStatus) {
        this.powerStatus = powerStatus;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLastIP() {
        return this.lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }
}

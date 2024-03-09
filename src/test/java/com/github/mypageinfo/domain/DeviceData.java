package com.github.mypageinfo.domain;

import java.io.Serializable;
import java.util.Date;

public class DeviceData implements Serializable {
    private static final long serialVersionUID = -49357712135829795L;
    private Integer id;
    private String data;
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DeviceData{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}


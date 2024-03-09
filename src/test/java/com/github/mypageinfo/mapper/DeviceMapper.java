package com.github.mypageinfo.mapper;


import com.github.mypageinfo.PageInfo;
import com.github.mypageinfo.domain.DeviceData;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DeviceMapper {
    @Select("select * from device_data")
    List<DeviceData> queryAllByLimit(@Param("pageInfo") PageInfo<DeviceData> pageInfo);
}

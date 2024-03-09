package com.github.mypageinfo;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mypageinfo.domain.DeviceData;
import com.github.mypageinfo.mapper.DeviceMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class MybatisTest {
//    String json = "{\n" +
//            "  \"page\": 0,\n" +
//            "  \"pageSize\": 10,\n" +
//            "  \"totalElementNum\": 100,\n" +
//            "  \"content\": [],\n" +
//            "  \"fieldRule\": {\n" +
//            "    \"ruleItem\": {\n" +
//            "       \"range\": {\n" +
//            "          \"id\": { \"lte\": 20, \"gte\": 3}" +
//            "        }," +
//            "       \"equal\": {\n"+
//            "            \"data\": \"test9\""+
//            "        }"+
//            "     }" +
//            "   }" +
//            "  }";

    String json = "{\n" +
            "  \"page\": 0,\n" +
            "  \"pageSize\": 10,\n" +
            "  \"totalElementNum\": 100,\n" +
            "  \"content\": [],\n" +
            "  \"fieldRule\": {\n" +
            "    \"ruleItems\": [" +
            "       {\n" +
            "         \"range\": {\n" +
            "            \"id\": { \"lte\": 20, \"gte\": 1}" +
            "          }" +
            "        }," +
            "        {" +
            "            \"equal\": {\n"+
            "              \"data\": \"test4\""+
            "              }" +
            "         }"+
            "       ]" +
            "   }" +
            "  }";

    @Test
    public void test() throws IOException {
        ObjectMapper o = new ObjectMapper();
        Reader reader = Resources.getResourceAsReader("mybatis.xml");
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession session = factory.openSession();

        DeviceMapper mapper = session.getMapper(DeviceMapper.class);

        TypeReference<PageInfo<DeviceData>> typeRef = new TypeReference<PageInfo<DeviceData>>() {};
        PageInfo<DeviceData> pageInfo = o.readValue(json, typeRef);
        pageInfo.checkFieldRule(DeviceData.class);

        List<DeviceData> deviceData = mapper.queryAllByLimit(pageInfo);
        deviceData.forEach(System.out::println);
//        Object o = session.selectOne("lrs.user.findById", 1);
//        System.out.println(o);
        session.close();

    }
}

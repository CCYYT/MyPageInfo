## 介绍

可以使用Json语句对数据库进行复杂的条件查询与分页功能



## 使用

### 快速开始

1.将jar包引入到项目

2.在你的mybatis配置文件中 添加

```
<plugins>
        <plugin interceptor="com.github.mypageinfo.MyPageInfoInterceptor"/></plugins>
```

3.在需要分页的查询方法中添加PageInfo参数 并设置@Param("pageInfo")

```
@Select("select * from device_data")
List<DeviceData> queryAllByLimit(@Param("pageInfo") PageInfo<DeviceData> pageInfo);
```

4.测试，你可以选择创建一个PageInfo：

4.1.创建一个不带条件的PageInfo

```
PageInfo<DeviceData> pageInfo = new PageInfo<>(true);
```

4.2.创建一个 需要条件匹配的PageInfo

```
该条件为 data = test4 and id = 4 order by id desc
第一页 当页数据量20条
```

```
String json = {
    "page": 0,
    "pageSize": 20,
    "fieldRule": {
        "ruleItem": {
            "equal": {
                "data": "test4",
                "id": "4"
            }
        },
        "sort": {
            "id": "desc"
        }
    }
};
ObjectMapper o = new ObjectMapper();
TypeReference<PageInfo<DeviceData>> typeRef = new TypeReference<PageInfo<DeviceData>>() {};
PageInfo<DeviceData> pageInfo = o.readValue(json, typeRef);
```

5.在进行查询前，请先执行pageInfo.checkFieldRule()方法

```
pageInfo.checkFieldRule(DeviceData.class);//DeviceData.class 为你需要校验的对象类型
```

6.执行mapper查询

```
List<DeviceData> deviceData = mapper.queryAllByLimit(pageInfo);
```

完整的测试代码见：src/test/java/com/github/mypageinfo/MybatisTest.java

7.返回结果





#### 示例

ruleItem

```
{
    "page": 0,//第几页
    "pageSize": 10,//一页的数据量
    "totalElementNum": 100,//一共有多少条数据
    "content": [],//查询的结果
    "fieldRule": {//条件规则
        "ruleItem": {
            "range": {//范围模式
                "id": { "lte": 20, "gte":1}//字段名:条件
            },
            "equal":{//精确匹配模式
                "data": "test4" //字段名:条件
            },
            "like":{//模糊匹配模式
                "data":"test%"//字段名:条件
            },
            "in":{//包含模式
                "id":[1,2,3,4,5,6,7]//字段名:条件
            }
        },
        "sort": {//排序
          "uuid": "desc",//字段名:排序方式
          "age": "asc"
    	}
    }
}
```



当需要写多个以or进行连接的条件 可以使用 ruleItems 进行查询

```
{
    "page": 0,//第几页
    "pageSize": 10,//一页的数据量
    "totalElementNum": 100,//一共有多少条数据
    "content": [],//查询的结果
    "fieldRule": {//条件规则
        "ruleItems": [
            {
                "range": {
                    "id": { "lte": 4, "gte":1}
                },
                "equal":{
                    "data": "test4"
                }
            },
            {
                "like":{
                    "data":"1%"
                },
                "in":{
                    "id":[1,2,3,4,5,6,7,8]
                }
            }
        ],
        "sort": {
          "uuid": "desc",
          "age": "asc"
    	}
    }
}
```



#### 字段详解

一个标准的查询JSON包括以下内容：

| 字段名            | 参数类型 | 含义                   |
| ----------------- | -------- | ---------------------- |
| "page"            | int      | 第几页                 |
| "pageSize"        | int      | 一页的数据量(默认为10) |
| "totalElementNum" | int      | 一共有多少条数据       |
| "content"         | list []  | 查询的结果             |
| "fieldRule"       | dict {}  | 条件规则               |



##### fieldRule

fieldRule中可以有两种写法：

一、单个条件

| 字段名   | 参数类型 | 含义     |
| -------- | -------- | -------- |
| ruleItem | dict {}  | 条件字段 |
| sort     | dict {}  | 排序条件 |

二、复合条件

| 字段名    | 参数类型 | 含义          |
| --------- | -------- | ------------- |
| ruleItems | list []  | 条件字段 集合 |
| sort      | dict {}  | 排序条件      |



###### ruleItem/ruleItems

ruleItem/ruleItems中包含condition(条件)；区别是ruleItem包含一个condition，ruleItems中包含多个condition; 在rulesitems中多个condition 之间以or进行联系



###### condition

下面以一个condition举例：

```
"ruleItem": {
            "range": {//模式
                "id": { "lte": 20, "gte":1}//字段名:条件内容
            },
            "equal":{//模式
                "data": "test4"//字段名:条件内容
            },
            "like":{//模式
                "data":"test%"//字段名:条件内容
            },
            "in":{//模式
                "id":[1,2,3,4,5,6,7]//字段名:条件内容
            }
        }
```

在此例子中，"ruleItem"中的值就是一个condition。一个condition中可以包含多个模式，多个模式的条件关系为and，以下是目前所支持的模式类型：

```
 "equal":{//精确匹配模式
 	"data": "test4",//字段名:条件内容
 	...
 },
```

```
"range": {//范围匹配模式
	"id": { "lte": 20, "gte":1},//字段名:条件内容 支持的匹配符    eq,neq,ne,lt,lte,gt,gte。多个条件以and连接。
	...
},
```

```
"like":{//模糊匹配模式
	"data":"test%",//字段名:条件内容 支持通配符：%和_
	...
},
```

```
 "in":{//包含模式
 	"id":[1,2,3,4,5,6,7]//字段名:条件内容
 }
```



###### sort

sort字段用于表示字段的排序方式，示例：

```
"sort":{
    "uuid": "desc",
    "age": "asc"
}
```

支持desc(倒序)，asc(正序)，以上的示例表示为：先按"uuid"字段倒序 再按"age"字段正序排序




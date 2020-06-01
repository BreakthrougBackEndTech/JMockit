### 常用招式  
    1. 部分mock 如果在Expectation中没有进行record，则会调用原有代码
    2. 全部mock， 如果没有record的方法返回  null或者原始类型的初始值
    3. Mock私有方法或变量  
    4. 抛出异常  
    5. 按顺序返回一组值
    6. 根据参数返回不同值
    7. 只mock其中某个对象，其它的并不mock
    8. 检查方法调用顺序
    9. mock构造函数
    10. mock时构造参数
    11. 捕获最后一次调用的参数
    12. 捕获所有调用的参数
    13. 捕获新创建的对象
    14. Mock所有实现类
    15. 忽略静态代码块

### org.mockito.Mockito + 反射
```java
    JdbcTemplate jdbcTemplateMock = Mockito.mock(JdbcTemplate.class);
    
    when(jdbcTemplateMock.queryForObject(anyString(),
        new Object[]{planId, any(Timestamp.class), any(Timestamp.class)},
        Integer.class)).thenThrow(QueryTimeoutException.class);
    
    Class<TaskInfoImpl> clazz = TaskInfoImpl.class;
    Field field = clazz.getDeclaredField("jdbcTemplate");
    field.setAccessible(true);
    Object jdbcTemplate = field.get(taskInfoPersist);
    field.set(taskInfoPersist, jdbcTemplateMock);
    
    field.set(taskInfoPersist, jdbcTemplate);
    field.setAccessible(false);
```
      

 
### sonar

<maven.clover.license><![CDATA[RMRQoxHhkDgCUJahonvDlLNEMNUjlaWOihPkJPntDLIOle
 mi2KkuptG>ZXmdlbDUg3ugjm2K9xyCWqRYLYeq8xfLIMgk
 MQPNStmPooqqmPpOopOvuqmRooqMQmmqmqvwTWWtuRVtts
 TUUQSXvxxSvTqVuPPNuxnMRNqnmnmUUnqnvurummmmmmUU
 nqnvurummmmmmUUEFB91WJ95D5EJWE5KNFIBJWFPWWWUUn
 mmmm]]></maven.clover.license>

<plugin>
 <groupId>com.atlassian.maven.plugins</groupId>
 <artifactId>clover-maven-plugin</artifactId>
<version>4.1.1</version>
 </plugin>
 
 
 

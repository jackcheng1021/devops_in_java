package jackcheng1021.devops_in_java.demo.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装常用的函数
 */
@Component //注册为SpringBoot的组件
public class GenerateOperate {

    /**
     * 按格式生成当前日期时间字符串
     * @param format 日期格式
     * @return 日期字符串
     */
    public String genNowDateTimeByFormat(String format){
        DateTimeFormatter dft = DateTimeFormatter.ofPattern(format);
        LocalDateTime date = LocalDateTime.now();
        return dft.format(date);
    }

    /**
     * 封装接口返回的Map
     * @param result
     * @param msg
     * @param data
     * @return
     */
    public Map<String,Object> generateMap(int result,String msg, Object data){
        Map<String,Object> map = new HashMap<>();
        map.put("result",result);
        map.put("msg",msg);
        map.put("data",data);
        return map;
    }

    /**
     * 构建日志的Map
     * @param userId
     * @param type
     * @param moduleName
     * @param param
     * @param print
     * @return
     */
    public Map<String,Object> generateLogMap(String userId,
                                             String type,
                                             String moduleName,
                                             Map<String,Object> param,
                                             String print){
        Map<String,Object> logMap = new HashMap<>();
        logMap.put("userId",userId);
        logMap.put("type",type);
        logMap.put("moduleName",moduleName);
        logMap.put("param",param);
        logMap.put("print",print);
        return logMap;
    }
}

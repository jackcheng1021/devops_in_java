package jackcheng1021.devops_in_java.demo.controller;

import jackcheng1021.devops_in_java.demo.service.impl.CommandServiceImpl;
import jackcheng1021.devops_in_java.demo.util.GenerateOperate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/command")
public class CommandController {
    @Resource
    private CommandServiceImpl commandServiceImpl;

    @Resource
    private GenerateOperate generateOperate;
    /**
     * 检测目标主机到目标网络的连通性
     * @param node 目标主机名
     * @param url 目标网络
     * @return
     */
    @RequestMapping(value = "/checknetwork", method = RequestMethod.GET)
    public Map<String,Object> checkNetwork(@RequestParam String node, @RequestParam String url){
        String cmd = null;
        String result = null;
        Map<String,Object> map = new HashMap<>(); //返回的数据集
        //测试网络的连通性
        cmd = String.format("salt '%s' cmd.run 'ping -c1 %s'",node,url);
        try{
            result = commandServiceImpl.executeCommand(cmd);
            map.put("result",1);
            map.put("msg","");
            map.put("data",result);
        }
        catch (Exception ex){
            map.put("result",0);
            map.put("msg","host can not connect network");
            map.put("data","");
        }
        return map;
    }
}

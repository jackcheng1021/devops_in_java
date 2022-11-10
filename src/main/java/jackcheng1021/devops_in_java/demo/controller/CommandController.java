package jackcheng1021.devops_in_java.demo.controller;

import jackcheng1021.devops_in_java.demo.service.impl.CommandServiceImpl;
import jackcheng1021.devops_in_java.demo.util.GenerateOperate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
    @RequestMapping(value = "/check_network", method = RequestMethod.GET)
    public Map<String,Object> checkNetwork(@RequestParam String node, @RequestParam String url){
        String cmd = null;
        String result = null;
        //测试网络的连通性
        cmd = String.format("salt '%s' cmd.run 'ping -c1 %s'",node,url);
        try{
            result = commandServiceImpl.executeCommand(cmd);
            return generateOperate.generateMap(1,"",result);
        }
        catch (Exception ex){
            return generateOperate.generateMap(0,"host can not connect network", "");
        }
    }

    /**
     * 新增租户
     * @param parameter {"tenant": "租户名称", "user": "用户", "pwd": "密码"}
     * @return
     */
    @RequestMapping(value = "/create_tenant", method = RequestMethod.POST)
    public Map<String, Object> createTenant(@RequestBody Map<String,Object> parameter){
        String cmd = "";
        String result = "";
        String tenant = parameter.get("tenant").toString();
        String user = parameter.get("user").toString();
        String pwd = parameter.get("user").toString();
        cmd = String.format("liberty-tenant-create %s %s %s",tenant,user,pwd);
        try{
            result = commandServiceImpl.executeCommand(cmd);
            return generateOperate.generateMap(1,"", result);
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 新增租户网络
     * @param parameter {"tenant": "租户名", "tenant_net": "租户网络名", "tenant_net_cidr": "网段", "tenant_net_gateway": "网关"}
     * @return
     */
    @RequestMapping(value = "/create_tenant_net", method = RequestMethod.POST)
    public Map<String, Object> createTenantNet(@RequestBody Map<String,Object> parameter){
        String cmd = "";
        String result = "";
        String tenant = parameter.get("tenant").toString();
        String tenant_net = parameter.get("tenant_net").toString();
        String tenant_net_cidr = parameter.get("tenant_net_cidr").toString();
        String tenant_net_gateway = parameter.get("tenant_net_gateway").toString();
        cmd = String.format("liberty-tenant-network-create %s %s %s %s",tenant,tenant_net,tenant_net_cidr,tenant_net_gateway);
        try{
            result = commandServiceImpl.executeCommand(cmd);
            return generateOperate.generateMap(1,"", result);
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 租户创建新实例
     * @param instanceName 实例名
     * @param tenantName 租户名 为空即默认租户
     * @return
     */
    @RequestMapping(value = "/create_tenant_instance")
    public Map<String, Object> createTenantInstance(@RequestParam String instanceName, @RequestParam String tenantName, @RequestParam String usage){
        String cmd = "";
        String result = "";
        if (tenantName.equals("")){
            cmd = String.format("liberty-tenant-instance-create %s %s",instanceName, usage);
        }else {
            cmd = String.format("liberty-tenant-instance-create %s %s %s",tenantName, instanceName, usage);
        }
        try{
            result = commandServiceImpl.executeCommand(cmd);
            return generateOperate.generateMap(1,"", result);
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }
}

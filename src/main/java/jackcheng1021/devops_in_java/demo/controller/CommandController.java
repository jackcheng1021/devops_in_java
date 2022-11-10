package jackcheng1021.devops_in_java.demo.controller;

import jackcheng1021.devops_in_java.demo.service.impl.CommandServiceImpl;
import jackcheng1021.devops_in_java.demo.util.GenerateOperate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    /**
     * 获取可以安装的软件列表
     * @return
     */
    @RequestMapping(value = "/get_apps", method = RequestMethod.GET)
    public Map<String,Object> getAppsByRepo(){
        String cmd = "";
        String result = "";
        cmd = "yum list | awk '{print $1}' | awk -F '.' '{print $1}' | xargs";
        try{
            result = commandServiceImpl.executeCommand(cmd);
            List<String> apps = new ArrayList<>(Arrays.asList(result.split(" ")));
            return generateOperate.generateMap(1,"",apps);

        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 给指定云主机安装软件
     * @param parameter
     *        ip: 云主机地址
     *        tenant: 租户名
     *        instanceName: 云主机实例名
     *        app: 要安装的软件名 前端从软件库中进行选择
     * @return
     */
    @RequestMapping(value = "/install_app", method = RequestMethod.POST)
    public Map<String,Object> installAppInInstance(@RequestBody Map<String,Object> parameter){
        String cmd = "";
        String ip = parameter.get("ip").toString();
        String tenant = parameter.get("tenantName").toString();
        String instanceName = parameter.get("instanceName").toString();
        String app = parameter.get("app").toString();
        cmd = String.format("liberty-tenant-instance-install-app %s %s %s %s",ip,tenant,instanceName,app);
        try{
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 将web项目部署到云主机
     * @param parameter
     *        ip: 云主机ip地址
     *        tenant: 云主机的所属租户名
     *        packageUrl: 项目所在github的地址
     * @return
     */
    @RequestMapping(value = "/deploy_tomcat_package_in_instance", method = RequestMethod.POST)
    public Map<String,Object> deployTomcatPackageInInstance(@RequestBody Map<String,Object> parameter){
        String cmd = "";
        String ip = parameter.get("ip").toString();
        String tenant = parameter.get("tenantName").toString();
        String packageUrl = parameter.get("packageUrl").toString();
        cmd = String.format("liberty-tenant-instance-deploy-tomcat-package %s %s %s",ip,tenant,packageUrl);
        try {
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 将Java项目部署到云主机
     * @param parameter
     *        ip: 云主机ip地址
     *        tenant: 云主机的所属租户名
     *        packageUrl: 项目所在github的地址
     * @return
     */
    @RequestMapping(value = "/deploy_java_package_in_instance", method = RequestMethod.POST)
    public Map<String,Object> deployJavaPackageInInstance(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("ip").toString();
        String tenant = parameter.get("tenantName").toString();
        String packageUrl = parameter.get("packageUrl").toString();
        String packageVersion = parameter.get("packageVersion").toString();
        String cmd = String.format("liberty-tenant-instance-deploy-java-package %s %s %s %s",ip,tenant,packageUrl,packageVersion);
        try {
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 将python项目部署到云主机
     * @param parameter
     *        ip: 云主机ip地址
     *        tenant: 云主机的所属租户名
     *        packageUrl: 项目所在github的地址
     * @return
     */
    @RequestMapping(value = "/deploy_python_package_in_instance", method = RequestMethod.POST)
    public Map<String,Object> deployPythonPackageInInstance(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("ip").toString();
        String tenant = parameter.get("tenantName").toString();
        String packageUrl = parameter.get("packageUrl").toString();
        String packageVersion = parameter.get("packageVersion").toString();
        String cmd = String.format("liberty-tenant-instance-deploy-python-package %s %s %s %s",ip,tenant,packageUrl,packageVersion);
        try {
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }
}

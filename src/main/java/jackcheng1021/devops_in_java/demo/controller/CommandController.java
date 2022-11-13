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
        String pwd = parameter.get("pwd").toString();
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
     * @param parameter {"tenant": "租户名", "tenant_net_cidr": "网段", "tenant_net_gateway": "网关"}
     * @return
     */
    @RequestMapping(value = "/create_tenant_net", method = RequestMethod.POST)
    public Map<String, Object> createTenantNet(@RequestBody Map<String,Object> parameter){
        String cmd = "";
        String result = "";
        String tenant = parameter.get("tenant").toString();
        String tenant_net_cidr = parameter.get("tenant_net_cidr").toString();
        String tenant_net_gateway = parameter.get("tenant_net_gateway").toString();
        cmd = String.format("liberty-tenant-network-create %s %s %s",tenant,tenant_net_cidr,tenant_net_gateway);
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
    public Map<String, Object> createTenantInstance(@RequestParam String tenantName, @RequestParam String instanceName, @RequestParam int instanceType){
        String cmd = "";
        String result = "";
        cmd = String.format("liberty-tenant-instance-create %s %s %d",tenantName, instanceName, instanceType);
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
        String ip = parameter.get("hostIp").toString();
        String tenant = parameter.get("tenantName").toString();
        String instancePass = parameter.get("rootPass").toString();
        String app = parameter.get("app").toString();
        cmd = String.format("liberty-tenant-instance-install-app %s %s %s %s",ip,tenant,instancePass,app);
        try{
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 在云主机上部署git
     * @param parameter
     *        hostIp: 云主机地址
     *        hostRootPass: root密码
     *        tenantName: 租户名
     *        gitUser: git账户
     *        gitPass: git账户密码
     * @return
     */
    @RequestMapping(value = "/deploy_git_in_instance", method = RequestMethod.POST)
    public Map<String,Object> deployGitInInstance(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("hostIp").toString();
        String pass = parameter.get("hostRootPass").toString();
        String tenant = parameter.get("tenantName").toString();
        String gitUser = parameter.get("gitUser").toString();
        String gitPass = parameter.get("gitPass").toString();
        String cmd = String.format("liberty-tenant-instance-git %s %s %s %s %s",ip,pass,tenant,gitUser,gitPass);
        try {
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 在Git上构建项目裸库
     * @param parameter
     *        hostIp: 主机ip
     *        tenantName: 租户名
     *        gitUser: git账户
     *        gitPass: git账户密码
     *        gitRepo: git仓库名
     * @return
     */
    @RequestMapping(value = "/deploy_git_repo_In_Instance", method = RequestMethod.POST)
    public Map<String,Object> deployGitRepoInInstance(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("hostIp").toString();
        String tenant = parameter.get("tenantName").toString();
        String gitUser = parameter.get("gitUser").toString();
        String gitPass = parameter.get("gitPass").toString();
        String gitRepo = parameter.get("gitRepo").toString();
        String cmd = String.format("liberty-tenant-instance-git-repo %s %s %s %s %s",ip,tenant,gitUser,gitPass,gitRepo);
        try{
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));

        }catch (Exception ex){
            return generateOperate.generateMap(0, ex.getMessage(), "");
        }
    }

    /**
     * 在云主机上部署java项目
     * @param parameter
     *        hostIp: 主机ip
     *        tenantName: 租户名
     *        packageUrl: 项目所在的URL路径
     *        packageVersion: 版本号
     *        rootPass: 主机的root密码
     * @return
     */
    @RequestMapping(value = "/deploy_java_package", method = RequestMethod.POST)
    public Map<String,Object> deployJavaPackage(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("hostIp").toString();
        String tenant = parameter.get("tenantName").toString();
        String packageUrl = parameter.get("packageUrl").toString();
        String packageVersion = parameter.get("packageVersion").toString();
        String rootPass = parameter.get("rootPass").toString();
        String cmd = String.format("liberty-tenant-instance-deploy-java-package %s %s %s %s %s",ip,tenant,packageUrl,packageVersion,rootPass);
        try{
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 在云主机上部署python项目
     * @param parameter
     *        hostIp: 主机ip
     *        tenantName: 租户名
     *        packageUrl: 项目所在的URL路径
     *        scriptName: 启动的脚本名
     *        packageVersion: 版本号
     *        rootPass: 主机密码
     * @return
     */
    @RequestMapping(value = "/deploy_python_package", method = RequestMethod.POST)
    public Map<String,Object> deployPythonPackage(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("hostIp").toString();
        String tenant = parameter.get("tenantName").toString();
        String packageUrl = parameter.get("packageUrl").toString();
        String scriptName = parameter.get("scriptName").toString();
        String packageVersion = parameter.get("packageVersion").toString();
        String rootPass = parameter.get("rootPass").toString();
        String cmd = String.format("liberty-tenant-instance-deploy-python-package %s %s %s %s %s %s",ip,tenant,packageUrl,scriptName,packageVersion,rootPass);
        try{
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

    /**
     * 在云主机上部署Web项目
     * @param parameter
     *        hostIp: 主机ip
     *        tenantName: 租户名
     *        packageUrl: 项目所在的URL路径
     *        rootPass: 主机密码
     * @return
     */
    @RequestMapping(value = "/deploy_web_package", method = RequestMethod.POST)
    public Map<String,Object> deployWebPackage(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("hostIp").toString();
        String tenant = parameter.get("tenantName").toString();
        String packageUrl = parameter.get("packageUrl").toString();
        String rootPass = parameter.get("rootPass").toString();
        String cmd = String.format("liberty-tenant-instance-deploy-tomcat-package %s %s %s %s",ip,tenant,packageUrl,rootPass);
        try {
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0, ex.getMessage(), "");
        }
    }

    /**
     * 在云主机上部署开发环境
     * @param parameter
     *        hostIp: 主机ip
     *        rootPass: 主机密码
     * @return
     */
    @RequestMapping(value = "/deploy_dev_env", method = RequestMethod.POST)
    public Map<String,Object> deployDevEnv(@RequestBody Map<String,Object> parameter){
        String ip = parameter.get("hostIp").toString();
        String rootPass = parameter.get("rootPass").toString();
        String cmd = String.format("liberty-tenant-instance-dev %s %s",ip,rootPass);
        try{
            return generateOperate.generateMap(1,"",commandServiceImpl.executeCommand(cmd));
        }catch (Exception ex){
            return generateOperate.generateMap(0,ex.getMessage(),"");
        }
    }

}

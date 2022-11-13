# Devops_In_Java
## 1.介绍
- 该项目基于基础云+SaltStack实现远程运维

- 提供运维API

- 实现可视化效果

## 2.应用
- 第一步、将该项目与liberty_openstack相结合，先实现基础云即代码
- 第二步、将该项目与saltstack结合，实现基础设施即代码
- 第三步、将该项目与GitHub结合，实现软件集成即代码
- 第四步、将该项目与kubernetes结合，实现容器云即代码

## 3.当前状态
- 环境：
  - 三个节点的openstack平台： https://github.com/jackcheng1021/devops 该项目用于部署基于云计算的Devops平台
  - 三个节点部署了saltstack运维工具
- 2022.11.06 
  - 创建项目
  - 开发命令接口: 实现 java 调取 Linux 命令的接口
  - 开发网路测试功能API: 实现服务器集群的网络测试功能
- 2022.11.08
  - 新增接口: 创建普通租户
  - 新增接口: 创建租户自定义网络
  - 新增接口: 创建租户的云主机实例（instance）
- 2022.11.10
  - 新增接口: 获取云主机中可以安装的软件 /get_apps
  - 新增接口: 在云主机中安装指定软件 /install_app
  - 新增接口: 在云主机中部署jar包项目 /deploy_java_package_in_instance
  - 新增接口: 在云主机中部署python项目 /deploy_python_package_in_instance
  - 新增接口: 在云主机中部署web项目 /deploy_tomcat_package_in_instance
- 2022.11.13
  - 新增接口: 在云主机上部署git /deploy_git_in_instance
  - 新增接口: 在Git上构建项目裸库 /deploy_git_repo_In_Instance
  - 修改接口: 在云主机上部署java项目 /deploy_java_package
  - 修改接口: 在云主机上部署python项目 /deploy_python_package
  - 修改接口: 在云主机上部署Web项目 /deploy_web_package
  - 新增接口: 在云主机上部署开发环境 /deploy_dev_env

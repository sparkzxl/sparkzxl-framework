# sparkzxl-component学习教程

<p>
<a href="https://search.maven.org/search?q=sparkzxl"><img src="https://img.shields.io/badge/sparkzxl--component-1.3-blue" alt="maven"></a>
<a href="https://www.apache.org/licenses/"><img src="https://img.shields.io/badge/license-Apache%202.0-blue" alt="Apache 2.0"></a>
<a href="https://github.com/sparkzxl/sparkzxl-component"><img src="https://img.shields.io/badge/组件-sparkzxl--component-orange" alt="sparkzxl-component"></a>
<a href="https://github.com/sparkzxl/sparkzxl-auth"><img src="https://img.shields.io/badge/分布式认证-sparkzxl--auth-success" alt="分布式认证"></a>
</p>

[更新日志](docs/forward/CHANGELOG.md)

## 简介

> sparkzxl-component包含springboot项目的封装，主要功能是统一了对外接口的api访问格式，web模块进行了封装，基于DDD领域驱动模型设计代码，具体落地实施，对常用的工具类包进行封装，简单易用，elasticsearch，mybatis组件。集成了oauth2，redis缓存，本地缓存的构建，分布式锁的封装等等，是快速开发的脚手架，简易适用于企业级框架搭建

## 项目地址

[sparkzxl-component](https://github.com/sparkzxl/sparkzxl-component.git)

**落地最佳实践**：

- [sparkzxl-cloud](https://github.com/sparkzxl/sparkzxl-cloud.git)
- [sparkzxl-auth](https://github.com/sparkzxl/sparkzxl-auth.git)

## 在线体验

- [spark auth](http://119.45.182.28:3000/login)

!> 账户：test 密码：123456

## 演示效果

![sparkzxl-demo-7.png](https://oss.sparksys.top/images/sparkzxl-demo-7.png)

![sparkzxl-demo-6.png](https://oss.sparksys.top/images/sparkzxl-demo-6.png)

![sparkzxl-demo-5.png](https://oss.sparksys.top/images/sparkzxl-demo-5.png)

![sparkzxl-demo-4.png](https://oss.sparksys.top/images/sparkzxl-demo-4.png)

![sparkzxl-demo-3.png](https://oss.sparksys.top/images/sparkzxl-demo-3.png)

![sparkzxl-demo-2.png](https://oss.sparksys.top/images/sparkzxl-demo-2.png)

![sparkzxl-demo-1.png](https://oss.sparksys.top/images/sparkzxl-demo-1.png)

## 开源博客

- [凛冬王昭君的笔记](https://www.sparksys.top)

## 核心架构

![分布式系统架构](https://oss.sparksys.top/sparkzxl-component/distributed-architecture.jpg)

## 未来展望

## 组件包依赖下载指引

> 组件jar包已上传maven中央仓库，可进入[maven中央仓库](https://search.maven.org/) 搜索下载

![nexus-compoment.png](https://oss.sparksys.top/sparkzxl-component/nexus-compoment.png)

## 组件框架搭建

- [1.框架搭建手册之maven私库nexus实战](docs/forward/framework/框架搭建手册之maven私库nexus实战.md)
- [2.框架搭建手册之idea搭建代码环境](docs/forward/framework/框架搭建手册之idea搭建代码环境.md)

## 分布式架构篇

- [1.Nacos注册&配置中心搭建](docs/forward/distributed/分布式架构之Nacos注册&配置中心搭建.md)
- [2.Spring Cloud Alibaba 注册中心 Nacos 入门](docs/forward/distributed/分布式架构之SpringCloudAlibaba注册中心Nacos入门.md)
- [3.Spring Cloud Alibaba 配置中心 Nacos 入门](docs/forward/distributed/分布式架构之SpringCloudAlibaba配置中心Nacos入门.md)
- [4.Spring Cloud openfeign 服务调用](docs/forward/222)
- [Spring Cloud Openfeign 异常信息传递](docs/forward/distributed/SpringCloudOpenfeign异常信息传递.md)
- [5.Spring Cloud Ribbon 服务负载均衡](docs/forward/222)
- [6.Spring Cloud hystrix 服务容错保护](docs/forward/222)
- [7.Spring Cloud Gateway API网关服务](docs/forward/222)
- [8.Spring Boot Admin 微服务应用监控](docs/forward/222)
- [9.Spring Cloud Security 授权认证](docs/forward/222)
- [10.Spring Cloud Security：Oauth2使用入门](docs/forward/222)
- [11.Spring Cloud Alibaba Sentinel实现熔断与限流](docs/forward/222)
- [12.Spring Cloud Alibaba Seata 分布式事务问题](docs/forward/222)
- [13.Spring-cloud-gateway-oauth2 实现统一认证和鉴权](docs/forward/222)
- [14.Spring Cloud Skywalking链路追踪](docs/forward/222)
- [15.Spring Cloud 微服务聚合swagger文档](docs/forward/222)
- [16.我用AmazonS3解决了众多云厂商oss的痛点](docs/forward/distributed/我用AmazonS3解决了众多云厂商oss的痛点.md)
- [17.ELK+Filebeat+Kafka分布式日志管理平台搭建](docs/forward/distributed/分布式架构之ELK+Filebeat+Kafka分布式日志管理平台搭建.md)

## 应用部署实践

- [1.Docker环境部署安装](docs/forward/deploy/Docker环境部署安装.md)
- [2.Jenkins+Docker+Gitlab+Harbor服务器部署](docs/forward/deploy/Jenkins+Docker+Gitlab+Harbor服务器部署.md)
- [3.Jenkins全自动化部署SpringBoot项目](docs/forward/deploy/Jenkins全自动化部署SpringBoot项目.md)
- [4.Jenkins打包并远程部署NodeJS应用](docs/forward/deploy/Jenkins打包并远程部署NodeJS应用.md)
- [5.Jenkins实战之流水线](docs/forward/deploy/Jenkins实战之流水线.md)
- [6.Jenkins实战之流水线语法详解](docs/forward/deploy/Jenkins实战之流水线语法详解.md)
- [7.Jenkins实战之流水线应用部署](docs/forward/deploy/Jenkins实战之流水线应用部署.md)
- [8.链路追踪实战之SkyWalking环境搭建](docs/forward/distributed/链路追踪实战之SkyWalking环境搭建.md)
- [9.链路追踪实战之JDK镜像制作](docs/forward/distributed/链路追踪实战之JDK镜像制作.md)

## 组件功能介绍

- [sparkzxl-boot-starter组件](docs/forward/component/sparkzxl-boot.md)
- [sparkzxl-cache-starter组件](docs/forward/component/sparkzxl-cache.md)
- [sparkzxl-core组件](docs/forward/component/sparkzxl-core.md)
- [sparkzxl-database-starter组件](docs/forward/component/sparkzxl-database.md)
- [sparkzxl-user-starter组件](docs/forward/component/sparkzxl-user.md)
- [sparkzxl-web-starter组件](docs/forward/component/sparkzxl-web.md)

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)

# SpringCloud

## 零基础

### 微服务架构概述

微服务架构是一种架构模式，它提倡将单一应用程序划分成一组**小的服务**，服务之间互相协调、互相配合，为用户提供最终价值。每个服务运行在其**独立的进程**中，服务与服务间采用**轻量级的通信机制**互相协作（通常是基于HTTP协议的RESTful API）。每个服务都围绕着具体业务进行构建，并且能够被独立的部署到生产环境、类生产环境等。另外，应当尽量避免统一的、集中式的服务管理机制，对具体的一个服务而言，应根据业务上下文，选择合适的语言、工具对其进行构建

基于分布式的微服务架构体系：

服务注册与发现、服务调用、服务熔断、负载均衡、服务降级、服务消息队列、

配置中心管理、服务网关、服务监控、全链路追踪、自动化构建部署、服务定时任务调度操作

### Spring Cloud简介

分布式微服务架构的一站式解决方案，是多种微服务架构落地技术的集合体，俗称微服务全家桶

### Spring Cloud技术栈

服务注册与发现：EUREKA

服务负载与调度：RIBBON、FEIGN

服务熔断降级：HYSTRIX

服务网关：Zuul

服务分布式配置：Spring Cloud Config

服务开发：Spring Boot

### Cloud组件停更说明

![image-20220725132334175](C:\Users\Sino\AppData\Roaming\Typora\typora-user-images\image-20220725132334175.png)

## Eureka

### Eureka基础知识

* 什么是服务治理

  Spring Cloud封装了Netflix公司开发的Eureka模块来实现服务治理

  在传统的rpc远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较负责，所以需要使用服务治理，管理服务与服务之间依赖关系，可以实现服务调用、负载均衡、容错等，实现服务发现于注册

* 什么是服务注册

  Eureka采用了CS的设计架构，Eureka Server作为服务注册功能的服务器，它是服务注册中心。而系统中的其他微服务，使用Eureka的客户端连接到Eureka Server并维持心跳连接。这样系统的维护人员就可以通过Euraka Server来监控系统中各个微服务是否正常运行

  在服务注册与发现中，有一个注册中心。当服务器启动的时候，会把当前自己服务器的信息，比如：服务地址、通讯地址等以别名方式注册到注册中心上。另一方（消费者|服务提供者）以该别名的方式去注册中心上获取到时间的服务通讯地址，然后再实现本地RPC调用

  PRC远程调用框架核心设计思想：在于注册中心，因为使用注册中心管理每个服务与服务之间的一个依赖关系（服务治理概念）。在任何rpc远程框架中，都会有一个注册中心（存放服务地址相关信息（接口地址））

* Eureka两组件

  Eureka Server提供服务注册服务

  各个微服务节点通过配置启动后，会在Eureka Server中进行注册，这样Eureka Server中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观看到

  Eureka Client通过注册中心进行访问

  是一个Java客户端，用于简化Eureka Server的交互，客户端同时也具备一个内置的、使用轮询（round-robin）负载算法的负载均衡器。在应用启动后，将会向Eureka Server发送心跳（默认周期为30秒）。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除（默认90秒）

* Eureka集群原理说明

  互相注册，相互守望

​		![image-20220726154432369](C:\Users\Sino\AppData\Roaming\Typora\typora-user-images\image-20220726154432369.png)

### 服务熔断-Hystrix

#### 概述

* 分布式系统面临的问题

  复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免地发失败

  * **服务雪崩**：

    多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的**“扇出”**。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的“雪崩效应”

    对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源都在几秒钟内饱和。比失败更糟糕的是，这些应用程序还可能导致服务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都标识需要对故障和延迟进行隔离和管理，以便单个依赖关系的失败，不能取消整个应用程序或系统

    所有，通常当你发现一个模块下的某个实例失败后，这时候这个模块依然还会接收浏览，然后这个有问题的模块还调用了其他的模块，这样就会发生级联故障，或者叫雪崩

* 是什么

  Hystrix是一个用于处理分布式系统的延迟和容错的开源库，在分布式系统里，许多依赖不可避免的会调用失败，比如超时、异常等，Hystrix能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。

  “断路器”本身是一种开关装置，当某个服务单元发生故障之后，通过断路器的故障监控（类似于熔断保险丝），**向调用方返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方无法处理的异常**，这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。

* 能干嘛

  服务降级、服务熔断、接近实时的监控、。。。。。。

#### Hystrix重要概念

* 服务降级 fallback

  服务器忙，请稍后再试，不让客户端等待并立刻返回一个友好提示

  哪些情况会触发降级

  * 程序运行异常
  * 超时
  * 服务熔断触发降级
  * 线程池/信号量打满也会导致服务降级

* 服务熔断 break

  类比保险丝达到最大服务访问后，直接拒绝方法，然后调用服务降级的方法并返回友好提示

  服务的降级 => 进而熔断 => 恢复调用链路

* 服务限流 

  秒杀高并发等操作，严禁一窝蜂的过来拥挤，大家排队，一秒钟N个，有序进行

### 服务网关-Gateway

#### 概述简介

SpringCloud Gateway使用的Webflux中的reactor-netty响应式编程组件，底层使用了Netty通信框架

能干嘛：反向代理、鉴权、流量控制、熔断、日志监控、。。。。。。

![image-20220727134634374](C:\Users\Sino\AppData\Roaming\Typora\typora-user-images\image-20220727134634374.png)

#### 三大核心概念

* Route（路由）

  路由是构建网关的基本模块，它由ID，目标URI，一系列的断言和过滤器组成，如果断言为true则匹配该路由

* Predicate（断言）

  开发人员可以匹配HTTP请求中的所有内容（例如请求头或请求参数），如果请求与断言相匹配则进行路由

* Filter（过滤）

  指的是Spring框架中GatewayFilter的实例，使用过滤器，可以在请求被路由前或者之后都请求进行修改

#### 工作流程

客户端向Spring Cloud Gateway发出请求。然后再Gateway Handler Mapping中找到与请求相匹配的路由，将其发送到Gateway Web Handler

Handler再通过指定的过滤器链来将请求发送到我们时间的服务执行业务逻辑，然后返回。

过滤器之间用虚线分开是因为过滤器可能会再发送代理请求之前（"pre"）或之后（“post”）执行业务逻辑

Filter在“pre”类型的过滤器可以做参数校验、权限检验、流量监控、日志输出、协议转换等，

在“post”类型的过滤器中可以做响应内容、响应头的修改，日志的输出、流量监控等有着非常重要的作用

mvn install:install-file  

-Dfile=D:\1\spring-cloud-starter-gateway-2.2.1.RELEASE.jar  

-DgroupId=org.springframework.cloud  

-DartifactId=spring-cloud-starter-gateway  

-Dversion=2.2.1.RELEASE  -Dpackaging=jar 

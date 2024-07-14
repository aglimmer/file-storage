# file-storage

#### 介绍
file-storage 项目是一个基于spring boot 构建文件微服务，就文件或图片上传、下载的方法的方法给出一系列使用示例
在file-storage-server/html目录下给出了前端XMLHttpRequest、Fetch API、Axios实现文件上传、下载方法及后端Spring文件服务器处理方法

#### 软件架构
file-storage 还给出一种微服务文件模块的划分方法，仅供参考：
1. file-storage-sdk 将文件服务的核心功能抽离出来，方便提供给其他服务调用
2. file-storage-server 实现文件具体业务逻辑，即为真实的文件服务器
3. file-storage-app 作为一个应用通过file-storage-sdk调用文件服务器

#### 项目收获
总之通过学习这个项目，你可以了解以下知识、方法或经验：
1. Web开发中文件上传、下载的技术，微服务架构设计方法
2. Spring Data Jpa 实现数据表操作方法
3. Spring Open Feign 实现服务之间调用的方法
4. Spring 跨域请求的过滤器、拦截器配置的方法



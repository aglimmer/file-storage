## file-storage-app功能介绍
用于本地测试，验证Feign接口的可用性
外部请求当前服务，当前服务通过file-storage-sdk依赖中的Feign接口，以url方式调用file-storage-server微服务处理
file-storage-sdk 作为一个抽象接口，用于提供给外部作为服务内部调用
file-storage-server 是一个真正的文件服务器，实现了file-storage-sdk中的抽象接口，给出了文件上传下载的真正的业务实现逻辑

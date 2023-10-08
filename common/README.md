# common
### 1.公共工具类
### 2.请求附加trace-id用来跟踪整个调用链
##### 过滤器中拦截所有请求，查看请求中是否携带trace-id,不携带则追加，httpclient工具类中发送请求时获取当前http请求的trace-id追加给将要发送的请求

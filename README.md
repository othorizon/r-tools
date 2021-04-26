# spring 项目工具类 R-Tools

## PrintCurlClientHttpRequestInterceptor

打印curl风格的restTemplate请求日志  

示例配置  
```yaml
r-tools:
  rest-template:
    print-curl:
      # 默认true
      enable: true
      # 是否改为debug日志 默认info
      debugLog: false
      # 分割符号 默认单行空格分割
      separator: " \\\n"   
      # 不打印的header
      ignoreHeaders: 
        - Accept
        - Content-Length
      # 如果白名单不为空则只打印白名单中的请求
      urlWishlist:
```

## MockRestTemplateInterceptor

匹配url地址时是包括get请求参数的，可以使用模糊匹配  
匹配规则见 org.springframework.util.AntPathMatcher  
示例配置  

```yaml
r-tools:
  rest-template:
    mock:
      # 默认false
      enable: true
      conf:
        - url: http://mock.server.com/query?confId=1*
          body: '{"status":200,"message":"成功","result":{"conf":"mock conf1"}}'
        - url: http://mock.server.com/query?confId=2*
          body: '{"status":200,"message":"成功","result":{"conf":"mock conf2"}}'
        - url: http://mock.server.com/update
          body: '{"status":200,"message":"成功","result":null}'
```
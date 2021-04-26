# spring 项目工具类 R-Tools

## MockRestTemplateInterceptor

匹配url地址时是包括get请求参数的，可以使用模糊匹配  
匹配规则见 org.springframework.util.AntPathMatcher  
示例配置  

```yaml
r-tools:
  rest-template:
    mock:
      enable: true
      conf:
        - url: http://mock.server.com/query?confId=1*
          body: '{"status":200,"message":"成功","result":{"conf":"mock conf1"}}'
        - url: http://mock.server.com/query?confId=2*
          body: '{"status":200,"message":"成功","result":{"conf":"mock conf2"}}'
        - url: http://mock.server.com/update
          body: '{"status":200,"message":"成功","result":null}'
```
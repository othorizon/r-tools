package top.rizon.rtools.restemplate;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * restTemplate请求mock
 * <p>
 * 可以指定url请求的返回值，用于开发测试阶段请求目标尚未准备好的场景，以及测试指定的返回值的场景
 * <p>
 * 匹配url地址时，会包含url地址上的参数一起匹配，方便匹配get请求的参数
 * <p>
 * 默认不启用 启用设置 {@code r-tools.rest-template.mock.enable=true }
 *
 * @author rizon
 * @see AntPathMatcher url匹配规则
 * @since 0.0.2
 */
@Data
@Configuration
@ConditionalOnProperty(value = "r-tools.rest-template.mock.enable", havingValue = "true")
@ConfigurationProperties(prefix = "r-tools.rest-template.mock")
@RequiredArgsConstructor
@Slf4j
@Order
public class MockRestTemplateInterceptor implements ClientHttpRequestInterceptor, InitializingBean {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final RestTemplate restTemplate;

    private boolean enable = false;

    /**
     * 配置
     */
    private List<MockConf> conf = new ArrayList<>();

    @Override
    public void afterPropertiesSet() {
        if (restTemplate != null) {
            restTemplate.getInterceptors().add(this);
            log.info("restTemplate register interceptor: " + this.getClass().getName());
        }
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (enable) {
            String url = request.getURI().toString();
            for (MockConf mockConf : conf) {
                if (PATH_MATCHER.match(mockConf.getUrl(), url)) {
                    return mockResponse(request.getHeaders(), mockConf.getBody());
                }
            }
        }

        return execution.execute(request, body);
    }

    private ClientHttpResponse mockResponse(HttpHeaders headers, String body) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(body.getBytes());

        return new ClientHttpResponse() {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders resHeaders = new HttpHeaders();
                resHeaders.setContentType(headers.getAccept().get(0));
                resHeaders.setContentLength(body.length());
                return resHeaders;
            }

            @Override
            public InputStream getBody() throws IOException {
                return inputStream;
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return getStatusCode().value();
            }

            @Override
            public String getStatusText() throws IOException {
                return getStatusCode().name();
            }

            @Override
            public void close() {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close inputStream error", e);
                }
            }
        };
    }

    @Data
    public static class MockConf {
        /**
         * url pattern
         */
        private String url;
        /**
         * response body
         */
        private String body;
    }
}

package top.rizon.rtools.restemplate;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rizon
 * @date 2021/4/25
 */
public class PrintCurlClientHttpRequestInterceptorTest {
    private RestTemplate restTemplate = new RestTemplate();

    @Before
    public void init() {
        restTemplate.setInterceptors(Collections.singletonList(
                PrintCurlClientHttpRequestInterceptor.builder()
                        .setInfoLog()
                        .setSingleLine()
                        .build()
        ));
    }

    @Test
    public void testGet() {
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(null, null);
        try {
            restTemplate.exchange("http://localhost/path?p1=1&p2=2", HttpMethod.GET, httpEntity, Object.class);
        } catch (ResourceAccessException ex) {
            //ignore
        }
    }

    @Test
    public void testPost() {
        Map<String, String> params = new HashMap<>();
        params.put("p1", "v1");
        params.put("p2", "v2");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);
        try {
            restTemplate.exchange("http://localhost/path", HttpMethod.POST, httpEntity, Object.class);
        } catch (ResourceAccessException ex) {
            //ignore
        }
    }

    @Test
    public void testUrlEncode() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("p1", Arrays.asList("v1"));
        params.put("p2", Arrays.asList("v2.1", "v2.2"));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);
        try {
            restTemplate.exchange("http://localhost/path", HttpMethod.POST, httpEntity, Object.class);
        } catch (ResourceAccessException ex) {
            //ignore
        }
    }

    @Test
    public void testForm() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("p1", Arrays.asList("v1"));
        params.put("p2", Arrays.asList("v2.1", "v2.2"));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, httpHeaders);
        try {
            restTemplate.exchange("http://localhost/path", HttpMethod.POST, httpEntity, Object.class);
        } catch (ResourceAccessException ex) {
            //ignore
        }
    }
}
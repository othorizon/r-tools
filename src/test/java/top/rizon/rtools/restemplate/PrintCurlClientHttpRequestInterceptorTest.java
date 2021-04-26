package top.rizon.rtools.restemplate;

import org.junit.Assert;
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
    private ByteArrayOutputStream output;

    @Before
    public void init() {
        restTemplate.setInterceptors(Collections.singletonList(
                PrintCurlClientHttpRequestInterceptor.builder()
                        .setInfoLog()
                        .setSingleLine()
                        .build()
        ));

        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @Test
    public void testGet() {

        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(null, null);
        try {
            restTemplate.exchange("http://localhost/path?p1=1&p2=2", HttpMethod.GET, httpEntity, Object.class);
        } catch (ResourceAccessException ex) {
            //ignore
        }
        String[] split = output.toString().split("\n");
        Assert.assertEquals("curl -X GET 'http://localhost/path?p1=1&p2=2'", split[split.length - 1]);
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
        String[] split = output.toString().split("\n");
        Assert.assertEquals("curl -X POST 'http://localhost/path' -H 'Content-Type: application/json' -d '{\"p1\":\"v1\",\"p2\":\"v2\"}'"
                , split[split.length - 1]);
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
        String[] split = output.toString().split("\n");
        Assert.assertEquals("curl -X POST 'http://localhost/path' -H 'Content-Type: application/x-www-form-urlencoded;charset=UTF-8' --data-urlencode 'p1=\"v1\"' --data-urlencode 'p2=\"v2.1\"' --data-urlencode 'p2=\"v2.2\"'"
                , split[split.length - 1]);
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
        String[] split = output.toString().split("\n");
        Assert.assertTrue(split[split.length - 1].matches(
                "curl -X POST 'http://localhost/path' -H 'Content-Type: multipart/form-data;charset=UTF-8;boundary=\\w+' -F 'p1=\"v1\"' -F 'p2=\"v2.1\"' -F 'p2=\"v2.2\"'"));
    }
}
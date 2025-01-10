//package com.news;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.news.error.NaverErrorResponse;
//import com.news.feign.NaverClientConfiguration;
//import com.news.feign.NaverClient;
//import com.news.feign.NaverErrorDecoder;
//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.mock;
//import feign.Request;
//import feign.Response;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest(classes = NaverRestClientTest.TestConfig.class)
//@ActiveProfiles("test")
//class NaverRestClientTest {
//
//    @EnableAutoConfiguration
//    @EnableFeignClients(clients = NaverClient.class)
//    static class TestConfig{
//    }
//
//    private ObjectMapper objectMapper;
//    private NaverErrorDecoder errorDecoder;
//    @Autowired
//    NaverClient client;
//
//    NaverClientConfiguration configuration;
//
//    @BeforeEach
//    void setup() {
//        configuration = new NaverClientConfiguration();
//        objectMapper = mock(ObjectMapper.class);
//        errorDecoder = new NaverErrorDecoder(objectMapper);
//    }
//
//    @Test
//    void requestInterceptorAddsHeadersToRequestTemplate() {
//        // Given
//        RequestTemplate template = new RequestTemplate();
//        String clientId = "id";
//        String clientSecret = "secret";
//
//        // And: Interceptor 실행 전 헤더가 존재하지 않음
//        assertNull(template.headers().get("X-Naver-Client-Id"));
//        assertNull(template.headers().get("X-Naver-Client-Secret"));
//
//        // When: Interceptor가 실행됨
//        RequestInterceptor interceptor = configuration.requestInterceptor(clientId, clientSecret);
//        interceptor.apply(template);
//
//        // Then: Interceptor 실행 후 헤더가 추가됨
//        assertTrue(template.headers().get("X-Naver-Client-Id").contains(clientId));
//        assertTrue(template.headers().get("X-Naver-Client-Secret").contains(clientSecret));
//    }
//
//    @Test
//    void shouldThrowRuntimeExceptionWhenErrorOccurs() throws Exception {
//        // Given: Mock 설정
//        Response.Body responseBody = mock(Response.Body.class);
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(
//                "{\"errorMessage\":\"error\",\"errorCode\":\"SE03\"}".getBytes()
//        );
//        when(responseBody.asInputStream()).thenReturn(inputStream);
//
//        // Request와 Response 설정
//        Request request = Request.create(
//                Request.HttpMethod.GET,
//                "testUrl",
//                Map.of(),
//                (Request.Body) null,
//                null
//        );
//
//        Response response = Response.builder()
//                .status(400)
//                .request(request)
//                .body(responseBody)
//                .build();
//
//        when(objectMapper.readValue(any(InputStream.class), eq(NaverErrorResponse.class)))
//                .thenReturn(new NaverErrorResponse("error", "SE03"));
//
//        // When & Then: RuntimeException 검증
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            errorDecoder.decode("testError", response);
//        });
//    }
//
//    @Test
//    void callNaver(){
//        String result = client.search("무안", 1, 5);
//        System.out.println("result = " + result);
//    }
//}
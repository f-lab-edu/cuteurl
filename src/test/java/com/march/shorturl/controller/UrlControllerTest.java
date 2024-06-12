package com.march.shorturl.controller;

import com.march.shorturl.model.Url;
import com.march.shorturl.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UrlService urlService; //UrlService를 mock 객체로 주입

    @InjectMocks //UrlController를 테스트하기 위한 목 객체 주입
    private UrlController urlController;

    @BeforeEach //각 테스트 실행 전 MockitoAnnotations.openMocks(this)호출로 목 객체 초기화, MockMvc 설정하기
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testShortenUrl() throws Exception {
        String originalUrl = "https://example.com"; // 1. 단축하려는 url 설정
        Url url = new Url(); // 2. Url 객체 생성 후 Original, short 각각 설정
        url.setOriginalUrl(originalUrl);
        url.setShortUrl("1");

        when(urlService.shortenUrl(originalUrl)).thenReturn(url); // 3. shortUrl() 호출 시 목 객체에서 'url'반환토록 설정

        mockMvc.perform(post("/api/shorten") //mockMvc.perform() - POST 요청 수행, 엔드포인트로 originalUrl 전달
                        .param("url", originalUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 응답이 400 Bad Request 상태인지 확인
    }

    @Test
    void testRedirectToOriginalUrl() throws Exception { // 단축된 거에서 원본으로 리다이렉트
        String shortUrl = "1"; // 1. 리다이렉트할 단축 Url 설정
        String originalUrl = "https://example.com"; // 2. original 변수에 리다이렉트할 원본 url 설정
        Url url = new Url(); // 3. 객체 생성 후 originalUrl 설정
        url.setOriginalUrl(originalUrl);

        when(urlService.getOriginalUrl(shortUrl)).thenReturn(Optional.of(url)); //메서드 호출 시 목 객체에서 url을 Optional로 반환토록 설정
        /* Optional은 NullPointerException을 방지하고 코드의 가독성 향상. 메서드 반환유형이나 필드에 사용
        * null을 직접 다루지 않고 대신 값의 존재 여부를 나타내는 방식으로 사용한다.
        * Optional<Url> -- url이 존재하면 optional에 포장하여 반환, 없으면 Optional.empty()를 반환*/

        mockMvc.perform(get("/api/{shortUrl}", shortUrl)) //perform으로 GET 요청 수행 후 엔드포인트로 shortUrl 전달
                .andExpect(status().is3xxRedirection()) // 응답이 3xx 리다이렉션 상태인지 확인
                .andExpect(redirectedUrl(originalUrl)); // 응답이 original로 리다이렉트 되는지 확인

        /* 301 Moved Permanently 요청 리소스가 새 위치로 영구적 이동. 새 위치로 다시 요청해야 함
         * 302 Found 일시적으로 다른 위치. 클라이언트가 임시적으로 새로운 위치로 리디렉션되고 원본 요청 그대로 유지해야 함
         * 303 See Other 요청 처리 후 클라이언트가 다른 위치로 요청 보내야 함. POST에 대한 응답으로 결과보려고 GET 요청 보내도록 유도
         * 307 Temporary Redirect 일시적으로 리소스가 다른 위치로 이동됨. 원본요청은 그대로 유지
         * 308 Permanently Redirect 리소스가 영구적으로 다른 위치로 이동, 새로운 위치로 요청 다시 보내야 함*/
    }
}
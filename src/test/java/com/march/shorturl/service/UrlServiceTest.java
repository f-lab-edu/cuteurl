package com.march.shorturl.service;

import com.march.shorturl.model.Url;
import com.march.shorturl.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    public UrlServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShortenUrl() {
        String originalUrl = "https://example.com";
        Url url = new Url();
        url.setId(1L);
        url.setOriginalUrl(originalUrl);
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        Url result = urlService.shortenUrl(originalUrl);

        assertNotNull(result);
        assertEquals(originalUrl, result.getOriginalUrl());
        assertEquals("1", result.getShortUrl());  // Assuming 1L encodes to "1" in Base62
        verify(urlRepository, times(2)).save(any(Url.class));
    }

    @Test
    void testGetOriginalUrl() {
        String shortUrl = "1";
        Url url = new Url();
        url.setOriginalUrl("https://example.com");
        url.setShortUrl(shortUrl);

        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));

        Optional<Url> result = urlService.getOriginalUrl(shortUrl);

        assertTrue(result.isPresent());
        assertEquals("https://example.com", result.get().getOriginalUrl());
        verify(urlRepository, times(1)).findByShortUrl(shortUrl);
    }
}


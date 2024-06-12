package com.march.shorturl.controller;

import com.march.shorturl.model.Url;
import com.march.shorturl.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> requestBody) {
        try {
            String originalUrl = requestBody.get("originalUrl");
            log.debug("Received request to shorten URL: {}", originalUrl);
            Url shortenedUrl = urlService.shortenUrl(originalUrl);
            return ResponseEntity.ok(shortenedUrl);
        } catch (Exception e) {
            log.error("Error occurred while shortening URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while shortening URL");
        }
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable("shortUrl") String shortUrl) {
        try {
            log.debug("Received request to redirect short URL: {}", shortUrl);
            Optional<Url> originalUrlOpt = urlService.getOriginalUrl(shortUrl);

            if (originalUrlOpt.isPresent()) {
                String originalUrl = originalUrlOpt.get().getOriginalUrl();
                log.debug("Redirecting to original URL: {}", originalUrl);
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
            } else {
                log.warn("Original URL not found for short URL: {}", shortUrl);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error occurred during redirect", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
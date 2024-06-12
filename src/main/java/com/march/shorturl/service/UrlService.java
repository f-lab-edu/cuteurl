package com.march.shorturl.service;

import com.march.shorturl.model.Url;
import com.march.shorturl.repository.UrlRepository;
import com.march.shorturl.util.Base62;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public Url shortenUrl(String originalUrl) {
        // Create new URL entity
        Url url = new Url();
        url.setOriginalUrl(originalUrl);

        // Create a unique identifier using UUID and Base62 encoding
        String uniqueId = generateUniqueID();
        String shortUrl = generateShortUrl(uniqueId);
        url.setShortUrl(shortUrl);

        // Save the entity in a single transaction
        url = urlRepository.save(url);

        log.debug("URL saved with shortUrl: {}", shortUrl);
        return url;
    }

    private String generateUniqueID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String generateShortUrl(String uniqueId) {
        return Base62.encode(new Long(uniqueId.hashCode()));
    }

    public Optional<Url> getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl);
    }
}
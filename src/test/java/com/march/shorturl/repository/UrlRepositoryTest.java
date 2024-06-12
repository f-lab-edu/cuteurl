package com.march.shorturl.repository;

import com.march.shorturl.model.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    void testSaveAndFindByShortUrl() {
        Url url = new Url();
        url.setOriginalUrl("https://example.com");
        url.setShortUrl("1");

        urlRepository.save(url);

        Optional<Url> foundUrl = urlRepository.findByShortUrl("1");

        assertThat(foundUrl).isPresent();
        assertThat(foundUrl.get().getOriginalUrl()).isEqualTo("https://example.com");
    }

    @Test
    void testFindByShortUrlNotFound() {
        Optional<Url> foundUrl = urlRepository.findByShortUrl("nonexistent");

        assertThat(foundUrl).isNotPresent();
    }
}


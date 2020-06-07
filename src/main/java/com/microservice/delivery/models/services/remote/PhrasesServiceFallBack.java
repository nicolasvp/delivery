package com.microservice.delivery.models.services.remote;

import com.microservices.commons.models.entity.phrases.Phrase;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class PhrasesServiceFallBack implements IPhraseRemoteCallService {

    @Override
    public List<Phrase> getAllPhrases() {
        log.warn("PHRASES SERVICE IS NOT AVAILABLE!!, PLEASE CHECK");
        List<Phrase> phrasesList = new ArrayList<>();
        Phrase defaultPhrase = new Phrase();
        defaultPhrase.setBody("Empty phrase body");
        defaultPhrase.setId(0L);
        defaultPhrase.setLikesCounter(0L);
        phrasesList.add(defaultPhrase);
        return phrasesList;
    }

    @Override
    public ResponseEntity<Phrase> getPhraseById(Long id) {
        return null;
    }
}

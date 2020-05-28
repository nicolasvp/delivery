package com.microservice.delivery.models.services;

import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.models.entity.delivery.History;
import com.microservices.commons.models.entity.phrases.Phrase;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface IDeliveryService {

    public Map<String, String> setPhrasesToUsers() throws NoSuchAlgorithmException, DatabaseAccessException;

    public List<Phrase> filterPhrasesByType(List<Phrase> allPhrases, Integer phraseType);

    public List<Phrase> filterPhraseByAvailability(List<Phrase> allPhrases, List<History> userHistory);

    public Map<String, Object> getLastPhraseForUser(History lastUserHistory);
}

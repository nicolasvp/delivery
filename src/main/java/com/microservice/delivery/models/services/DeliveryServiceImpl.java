package com.microservice.delivery.models.services;

import com.microservice.delivery.models.services.remote.IPhraseRemoteCallService;
import com.microservice.delivery.models.services.remote.IUserRemoteCallService;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.models.entity.delivery.History;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservices.commons.models.entity.users.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeliveryServiceImpl implements IDeliveryService {

    @Autowired
    private IHistoryService historyService;

    @Autowired
    private IUserRemoteCallService userCallService;

    @Autowired
    private IPhraseRemoteCallService phraseCallService;

    /*
     * Pick a random phrase for every user based on his config phrase type and assigned to his history
     */
    @Override
    public Map<String, String> setPhrasesToUsers() throws NoSuchAlgorithmException, DatabaseAccessException {
        List<User> allUsers = usersServiceCall();
        List<Phrase> allPhrases = phrasesServiceCall();
        Map<String, String> phrasesAsignedToUsers = new HashMap<>();
        Random randomElement = SecureRandom.getInstanceStrong();

        if(allUsers.size() > 0) {
            for(User user: allUsers) {
                History newUserHistory = new History();
                List<History> userHistory = historyService.findHistoriesByUserId(user.getId());
                List<Phrase> filteredPhrases = filterPhrasesByType(allPhrases, user.getPhraseType());
                List<Phrase> availablePhrasesForUser = filterPhraseByAvailability(filteredPhrases, userHistory);

                if(!availablePhrasesForUser.isEmpty()) {
                    Phrase randomPhraseSelected = availablePhrasesForUser.get(randomElement.nextInt(availablePhrasesForUser.size()));

                    try {
                        newUserHistory.setPhraseId(randomPhraseSelected.getId());
                        newUserHistory.setUserId(user.getId());
                        historyService.save(newUserHistory);
                    } catch (DataAccessException e) {
                        throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
                    }

                    phrasesAsignedToUsers.put(user.getName(), randomPhraseSelected.getBody());
                }
            }
        }

        return phrasesAsignedToUsers;
    }


    private List<User> usersServiceCall(){
        return userCallService.getAllUsers();
    }

    private List<Phrase> phrasesServiceCall() {
        return phraseCallService.getAllPhrases();
    }

    /*
     * Get the last asigned phrase to a user
     * @Parameter user id
     * @Response phrase and user info
     */
    @Override
    public Map<String, Object> getLastPhraseForUser(History lastUserHistory){
        Map<String, Object> response = new HashMap<>();

        if(lastUserHistory != null) {
            ResponseEntity<Phrase> phrase = phraseCallService.getPhraseById(lastUserHistory.getPhraseId());
            User user = userCallService.getUserById(lastUserHistory.getUserId());
            response.put("user", user);
            response.put("phrase", phrase);
            return response;
        }

        return response;
    }

    /*
     * Filter the phrase list based on the user phrase type
     * Llevar esto a una query en ms de phrases
     */
    @Override
    public List<Phrase> filterPhrasesByType(List<Phrase> allPhrases, Integer phraseType){
        if(!phraseType.equals(0)) {
            return allPhrases
                    .stream()
                    .filter(phrase -> {
                        Long phraseTypeCastedToLong = Long.valueOf(phraseType.longValue());
                        return phrase.getType().getId().equals(phraseTypeCastedToLong);
                    })
                    .collect(Collectors.toList());
        }
        return allPhrases;
    }

    /*
     * Filter the phrase list based on the user history list, if the user has the phrase on this history then its removed from the "allPhrases" list
     */
    @Override
    public List<Phrase> filterPhraseByAvailability(List<Phrase> allPhrases, List<History> userHistory){
        for(History history: userHistory) {
            allPhrases.removeIf(phrase -> (history.getPhraseId().equals(phrase.getId())));
        }
        return allPhrases;
    }
}

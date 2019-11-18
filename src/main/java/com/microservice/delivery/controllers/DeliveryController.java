package com.microservice.delivery.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.delivery.models.services.IHistoryService;
import com.microservice.delivery.models.services.remote.IPhraseRemoteCallService;
import com.microservice.delivery.models.services.remote.IUserRemoteCallService;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.models.entity.delivery.History;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservices.commons.models.entity.users.User;

@RestController
public class DeliveryController {

	@Autowired
	private IHistoryService historyService;
	
	@Autowired
	private IUserRemoteCallService userCallService;
	
	@Autowired
	private IPhraseRemoteCallService phraseCallService;
	
	/*
	 * Pick a random phrase for every user based on his config phrase type and assigned to his history
	 */
	@GetMapping(path="/delivery/set-phrases-to-users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setPhrasesToUsers() throws NoSuchAlgorithmException, DatabaseAccessException {
		Map<String, Object> response = new HashMap<>();
		
		List<User> allUsers = userCallService.getAllUsers();
		List<Phrase> allPhrases = phraseCallService.getAllPhrases();
		Map<String, String> phrasesAsignedToUsers = new HashMap<>();
		Random randomElement = SecureRandom.getInstanceStrong();  
		
		for(User user: allUsers) {
			History newUserHistory = new History();
			Integer userPhraseType = user.getConfig().getPhraseType();
			List<History> userHistory = historyService.findHistoriesByUserId(user.getId());
			List<Phrase> filteredPhrases = filterPhrasesByType(allPhrases, userPhraseType);
			List<Phrase> availablePhrasesForUser = filterPhraseByAvailability(filteredPhrases, userHistory);
			
			if(!availablePhrasesForUser.isEmpty()) {
				Phrase randomPhraseSelected = availablePhrasesForUser.get(randomElement.nextInt(availablePhrasesForUser.size()));
				
				try {
					newUserHistory.setPhraseId(randomPhraseSelected.getId());
					newUserHistory.setUserId(user.getId());
					historyService.save(newUserHistory);
				} catch (DataAccessException e) {
					throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
				}

				phrasesAsignedToUsers.put(user.getName(), randomPhraseSelected.getBody());
			}
		}
		
		response.put("phrasesAsigned", phrasesAsignedToUsers);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	/*
	 * Get the last asigned phrase to a user
	 * @Parameter user id
	 * @Response phrase and user info
	 */
	@GetMapping(path="/delivery/last-phrase-for-user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLastPhraseForUser(@PathVariable Long id) throws NullRecordException {
		Map<String, Object> response = new HashMap<>();
		History lastUserHistory = historyService.findLastUserHistory(id);
		
		if(lastUserHistory != null) {
			ResponseEntity<Phrase> phrase = phraseCallService.getPhraseById(lastUserHistory.getPhraseId());
			User user = userCallService.getUserById(id);
			
			response.put("user", user);
			response.put("phrase", phrase);
			
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		}
		
		throw new NullRecordException();
	}
	
	/*
	 * Filter the phrase list based on the user phrase type
	 * Llevar esto a una query en ms de phrases
	 */
	public List<Phrase> filterPhrasesByType(List<Phrase> allPhrases, Integer phraseType) {
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
	public List<Phrase> filterPhraseByAvailability(List<Phrase> allPhrases, List<History> userHistory) {
		for(History history: userHistory) {
			allPhrases.removeIf(phrase -> (history.getPhraseId().equals(phrase.getId())));
		}
		return allPhrases;
	}
}

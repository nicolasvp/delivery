package com.microservice.delivery.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.delivery.models.services.IHistoryService;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.models.entity.delivery.History;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservices.commons.models.entity.users.User;

@RestController
@RequestMapping("/api")
public class DeliveryController {

	@Autowired
	private IHistoryService historyService;
	
	/*
	 * Pick a random phrase for every user based on his config phrase type and assigned to his history
	 */
	@GetMapping(path="/delivery/set-phrases-to-users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setPhrasesToUsers() throws NoSuchAlgorithmException, DatabaseAccessException {
		Map<String, Object> response = new HashMap<>();
		/*
		List<User> allUsers = userService.findAll();
		List<Phrase> allPhrases = userService.getAllPhrases();
		Map<String, String> phrasesAsignedToUsers = new HashMap<>();
		Random randomElement = SecureRandom.getInstanceStrong();  
		
		for(User user: allUsers) {
			History newUserHistory = new History();
			Integer userPhraseType = user.getConfig().getPhraseType();
			List<History> userHistory = user.getHistory();
			List<Phrase> filteredPhrases = userService.filterPhrasesByType(allPhrases, userPhraseType);
			List<Phrase> availablePhrasesForUser = userService.filterPhraseByAvailability(filteredPhrases, userHistory);
			
			if(!availablePhrasesForUser.isEmpty()) {
				Phrase randomPhraseSelected = availablePhrasesForUser.get(randomElement.nextInt(availablePhrasesForUser.size()));
				
				try {
					newUserHistory.setPhraseId(randomPhraseSelected.getId());
					newUserHistory.setUser(user);
					historyService.save(newUserHistory);
				} catch (DataAccessException e) {
					throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
				}

				phrasesAsignedToUsers.put(user.getName(), randomPhraseSelected.getBody());
			}
		}
		
		response.put("phrasesAsigned", phrasesAsignedToUsers);
		*/
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
}

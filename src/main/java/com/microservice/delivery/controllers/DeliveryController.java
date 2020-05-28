package com.microservice.delivery.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import com.microservice.delivery.models.services.IDeliveryService;
import com.microservices.commons.exceptions.DatabaseAccessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microservice.delivery.models.services.IHistoryService;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.models.entity.delivery.History;

@Slf4j
@RestController
@RequestMapping("delivery")
public class DeliveryController {

	@Autowired
	private IHistoryService historyService;

	@Autowired
	private IDeliveryService deliveryService;

	@GetMapping(path="/set-phrases-to-users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> setPhrasesToUsers() throws NoSuchAlgorithmException, DatabaseAccessException {
		Map<String, Object> response = new HashMap<>();
		response.put("phrasesAsigned", deliveryService.setPhrasesToUsers());
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}

	@GetMapping(path="/last-phrase-for-user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getLastPhraseForUser(@PathVariable Long id) throws NullRecordException {
		History lastUserHistory = historyService.findLastUserHistory(id);

		if(lastUserHistory != null) {
			return new ResponseEntity<Map<String, Object>>(deliveryService.getLastPhraseForUser(lastUserHistory), HttpStatus.CREATED);
		}

		throw new NullRecordException();
	}
}

package com.microservice.delivery.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.microservices.commons.models.services.IUtilService;
import com.microservices.commons.utils.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.models.entity.delivery.History;
import com.microservice.delivery.models.services.IHistoryService;

@Slf4j
@RestController
public class HistoryController {
	
	@Autowired
	private IHistoryService historyService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/histories", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<History> index(){
		return historyService.findAll();
	}
	
	@GetMapping(path="/histories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable String id) throws NullRecordException, DatabaseAccessException {
		
		History history = null;

		try {
			log.info(Messages.findObjectMessage("History", id));
			history = historyService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (history == null) {
			log.error(Messages.nullObjectMessage("History", id));
			throw new NullRecordException();
		}

		return new ResponseEntity<History>(history, HttpStatus.OK);
	}
	
	@PostMapping(path="/histories", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody History history, BindingResult result) throws DatabaseAccessException {
		
		History newHistory = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsCreatingObjectMessage("History"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("History"));
			newHistory = historyService.save(history);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("History", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
		response.put("history", newHistory);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/histories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody History history, BindingResult result, @PathVariable("id") String id) throws NullRecordException, DatabaseAccessException {
		
		History historyFromDB = historyService.findById(id);
		History historyUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsUpdatingObjectMessage("History", id));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (historyFromDB == null) {
			log.error(Messages.nullObjectMessage("History", id));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("History", id));
			historyFromDB.setUserId(history.getUserId());
			historyFromDB.setPhraseId(history.getPhraseId());
			historyUpdated = historyService.save(historyFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("History", id, e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("history", historyUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/histories/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") String id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("History", id));
			historyService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("History", id, e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}

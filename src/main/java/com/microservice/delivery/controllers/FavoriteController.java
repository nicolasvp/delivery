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
import com.microservices.commons.models.entity.delivery.Favorite;
import com.microservice.delivery.models.services.IFavoriteService;

@Slf4j
@RestController
public class FavoriteController {

	@Autowired
	private IFavoriteService favoriteService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/favorities", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Favorite> index(){
		return favoriteService.findAll();
	}
	
	@GetMapping(path="/favorities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable String id) throws NullRecordException, DatabaseAccessException {
		
		Favorite favorite = null;

		try {
			log.info(Messages.findObjectMessage("Author", id.toString()));
			favorite = favoriteService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (favorite == null) {
			log.error(Messages.nullObjectMessage("Favorite", id.toString()));
			throw new NullRecordException();
		}

		return new ResponseEntity<Favorite>(favorite, HttpStatus.OK);
	}
	
	@PostMapping(path="/favorities", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Favorite favorite, BindingResult result) throws DatabaseAccessException {
		
		Favorite newFavorite = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsCreatingObjectMessage("Favorite"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("Favorite"));
			newFavorite = favoriteService.save(favorite);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("Favorite", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
		response.put("favorite", newFavorite);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/favorities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Favorite favorite, BindingResult result, @PathVariable("id") String id) throws NullRecordException, DatabaseAccessException {
		
		Favorite favoriteFromDB = favoriteService.findById(id);
		Favorite favoriteUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsUpdatingObjectMessage("Favorite", id.toString()));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (favoriteFromDB == null) {
			log.error(Messages.nullObjectMessage("Favorite", id.toString()));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("Favorite", id.toString()));
			favoriteFromDB.setUserId(favorite.getUserId());
			favoriteFromDB.setPhraseId(favorite.getPhraseId());
			favoriteUpdated = favoriteService.save(favoriteFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("Favorite", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("favorite", favoriteUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/favorities/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") String id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("Favorite", id.toString()));
			favoriteService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("Favorite", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}

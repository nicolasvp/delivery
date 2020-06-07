package com.microservice.delivery.models.services.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.commons.models.entity.phrases.Phrase;

@FeignClient(name="PHRASES-SERVICE", fallback = PhrasesServiceFallBack.class)
public interface IPhraseRemoteCallService {

	@GetMapping("/phrases")
	public List<Phrase> getAllPhrases();
	
	@GetMapping("/phrases/{id}")
	public ResponseEntity<Phrase> getPhraseById(@PathVariable Long id);
}

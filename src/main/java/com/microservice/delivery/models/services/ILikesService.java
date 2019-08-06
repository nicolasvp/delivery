package com.microservice.delivery.models.services;

import java.util.List;
import com.microservices.commons.models.entity.delivery.Like;

public interface ILikesService {
	
	public List<Like> findAll();
	
	public Like findById(Long id);
	
	public Like save(Like likes);
	
	public void delete(Long id);
}

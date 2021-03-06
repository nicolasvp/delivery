package com.microservice.delivery.models.services;

import java.util.List;
import com.microservices.commons.models.entity.delivery.Favorite;

public interface IFavoriteService {

	public List<Favorite> findAll();
	
	public Favorite findById(String id);
	
	public Favorite save(Favorite favorite);
	
	public void delete(String id);
}

package com.microservice.delivery.models.services;

import java.util.List;
import com.microservices.commons.models.entity.delivery.History;

public interface IHistoryService {

	public List<History> findAll();
	
	public History findById(Long id);
	
	public History save(History history);
	
	public void delete(Long id);
}

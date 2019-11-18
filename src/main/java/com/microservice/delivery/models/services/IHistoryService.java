package com.microservice.delivery.models.services;

import java.util.List;
import com.microservices.commons.models.entity.delivery.History;

public interface IHistoryService {

	public List<History> findAll();
	
	public History findById(Long id);
	
	public List<History> findHistoriesByUserId(Long id);
	
	public History findLastUserHistory(Long id);
	
	public History save(History history);
	
	public void delete(Long id);
}

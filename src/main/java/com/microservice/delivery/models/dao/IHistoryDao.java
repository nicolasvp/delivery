package com.microservice.delivery.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.delivery.History;

public interface IHistoryDao extends CrudRepository<History, Long>{

	@Query("select u from History u where u.userId=?1")
	public List<History> findHistoriesByUserId(Long id);
	
	public History findTopByUserIdOrderByIdDesc(Long userId);
}

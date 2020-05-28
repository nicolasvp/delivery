package com.microservice.delivery.models.dao;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.microservices.commons.models.entity.delivery.History;

public interface IHistoryDao extends MongoRepository<History, String> {

	@Query("select u from History u where u.userId=?1")
	public List<History> findHistoriesByUserId(Long id);

	// Obtiene la ultima frase asignada al usuario
	//@Query("select u from History u where u.userId=?1 order by id desc")
	public History findTopByUserIdOrderByIdDesc(Long userId);
}

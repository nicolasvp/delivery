package com.microservice.delivery.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.delivery.History;

public interface IHistoryDao extends CrudRepository<History, Long>{

}

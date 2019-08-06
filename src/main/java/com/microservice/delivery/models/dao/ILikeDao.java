package com.microservice.delivery.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.delivery.Like;

public interface ILikeDao extends CrudRepository<Like, Long>{

}

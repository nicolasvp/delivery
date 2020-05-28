package com.microservice.delivery.models.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.delivery.Like;

public interface ILikeDao extends MongoRepository<Like, String> {

}

package com.microservice.delivery.models.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.microservices.commons.models.entity.delivery.Favorite;

public interface IFavoriteDao extends MongoRepository<Favorite, String> {

}

package com.microservice.delivery.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.delivery.Favorite;

public interface IFavoriteDao extends CrudRepository<Favorite, Long>{

}

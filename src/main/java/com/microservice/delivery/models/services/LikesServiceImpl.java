package com.microservice.delivery.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.delivery.models.dao.ILikeDao;
import com.microservices.commons.models.entity.delivery.Like;

@Service
public class LikesServiceImpl implements ILikesService{

	@Autowired
	ILikeDao likeDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Like> findAll() {
		return (List<Like>) likeDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Like findById(String id) {
		return likeDao.findById(id).orElse(null);
	}

	@Override
	public Like save(Like like) {
		return likeDao.save(like);
	}

	@Override
	public void delete(String id) {
		likeDao.deleteById(id);
	}

}

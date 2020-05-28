package com.microservice.delivery.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.delivery.models.dao.IHistoryDao;
import com.microservices.commons.models.entity.delivery.History;

@Service
public class HistoryServiceImpl implements IHistoryService {

	@Autowired
	IHistoryDao historyDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<History> findAll() {
		return (List<History>) historyDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public History findById(String id) {
		return historyDao.findById(id).orElse(null);
	}

	@Override
	public History save(History history) {
		return historyDao.save(history);
	}

	@Override
	public void delete(String id) {
		historyDao.deleteById(id);
	}

	@Override
	public List<History> findHistoriesByUserId(Long id) {
		return historyDao.findHistoriesByUserId(id);
	}

	@Override
	public History findLastUserHistory(Long id) {
		return historyDao.findTopByUserIdOrderByIdDesc(id);
	}

}

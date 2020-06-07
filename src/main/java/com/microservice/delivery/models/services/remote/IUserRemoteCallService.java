package com.microservice.delivery.models.services.remote;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservices.commons.models.entity.users.User;

@FeignClient(name="USERS-SERVICE", fallback = UserServiceFallback.class) // Service name registered on Eureka Server
public interface IUserRemoteCallService {

	@GetMapping("/users/")
	public List<User> getAllUsers();
	
	@GetMapping("/users/{id}")
	public User getUserById(@PathVariable Long id);
}

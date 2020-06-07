package com.microservice.delivery.models.services.remote;

import com.microservices.commons.models.entity.users.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserServiceFallback implements IUserRemoteCallService {

    @Override
    public List<User> getAllUsers() {
        log.warn("USER SERVICE IS NOT AVAILABLE!!, PLEASE CHECK");
        List<User> users = new ArrayList<>();
        return users;
    }

    @Override
    public User getUserById(Long id) {
        return new User();
    }
}

package com.microservice.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import feign.Response;
import feign.codec.ErrorDecoder;


@Component
@Primary
public class FeignErrorDecoder implements ErrorDecoder {
    Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    
	@Override
	public Exception decode(String methodKey, Response response) {
        switch (response.status()){
        case 400:
        	LOGGER.error("Status code " + response.status() + ", methodKey = " + methodKey);
        	return new ResponseStatusException(HttpStatus.valueOf(response.status()), "Bad Request"); 
        case 404:
        {
        	LOGGER.error("Error took place when using Feign client to send HTTP Request. Status code " + response.status() + ", methodKey = " + methodKey);
			return new ResponseStatusException(HttpStatus.valueOf(response.status()), "User was not found"); 
        }
        default:
            return new Exception(response.reason());
        } 
	}
}

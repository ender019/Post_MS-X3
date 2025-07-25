package com.unknown.post.services;

import com.unknown.post.dtos.UserDTO;
import com.unknown.post.dtos.UserIdDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Service
public class UserService {
    @Autowired
    private WebClient webClient;

    public UserDTO getFullUser(String userId) {
        return webClient.method(HttpMethod.GET).uri("/user/{userId}", userId)
                .retrieve().bodyToMono(UserDTO.class)
                .doOnSuccess(res -> log.debug("Success: {}", res))
                .doOnError(res -> log.debug("Error: {}", res.getMessage()))
                .block();
    }

    public List<UserDTO> getFullUsersGroup(List<String> userIds) {
        return webClient.method(HttpMethod.GET).uri("/user/group")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(new UserIdDTO(userIds))
                .retrieve().bodyToMono(new ParameterizedTypeReference<List<UserDTO>>() {})
                .doOnSuccess(res -> log.debug("Success: {}", res))
                .doOnError(res -> log.debug("Error: {}", res.getMessage()))
                .block();
    }
}

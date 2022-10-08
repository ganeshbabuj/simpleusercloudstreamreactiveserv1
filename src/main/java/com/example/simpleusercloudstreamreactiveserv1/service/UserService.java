package com.example.simpleusercloudstreamreactiveserv1.service;

import com.example.cloudstream.resource.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface UserService {

    // No difference in blocking/non-blocking when used Mono<User> or User as input
    Mono<User> createUser(Mono<User> userMono);
    Mono<User> createUser(User user);
    Mono<Void> updateUser(long id, User user);
    Mono<Void> deleteUser(long id);
    Mono<User> getUser(long id);
    Flux<User> getUsers(Optional<String> firstNameOpt);

}

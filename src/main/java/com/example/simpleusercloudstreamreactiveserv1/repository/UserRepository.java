package com.example.simpleusercloudstreamreactiveserv1.repository;

import com.example.simpleusercloudstreamreactiveserv1.entity.UserEntity;
import com.example.cloudstream.resource.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {

    Flux<User> findByFirstName(String firstName);

}

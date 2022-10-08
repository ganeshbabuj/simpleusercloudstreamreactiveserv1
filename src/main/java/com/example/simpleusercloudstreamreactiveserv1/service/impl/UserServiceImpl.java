package com.example.simpleusercloudstreamreactiveserv1.service.impl;

import com.example.simpleusercloudstreamreactiveserv1.entity.UserEntity;
import com.example.simpleusercloudstreamreactiveserv1.exception.NotFoundException;
import com.example.simpleusercloudstreamreactiveserv1.repository.UserRepository;
import com.example.cloudstream.resource.User;
import com.example.simpleusercloudstreamreactiveserv1.service.UserService;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Setter
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StreamBridge streamBridge;

    // flatMap - asynchronous
    // map - synchronous
    // No difference in blocking/non-blocking when used Mono<User> or User as input
    public Mono<User> createUser(Mono<User> userMono){
        return userMono.flatMap(user -> {
            UserEntity userEntity = modelMapper.map(user, UserEntity.class);
            return userRepository.save(userEntity)
                    .log()
                    .map(savedUserEntity -> {
                        User savedUser = modelMapper.map(savedUserEntity, User.class);
                        System.out.println("savedUser: " + savedUser);
                        streamBridge.send("registerUser-out-0", savedUser);
                        return savedUser;
                    });
        });
    }

    public Mono<User> createUser(User user){
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        return userRepository.save(userEntity)
                .log()
                .map(savedUserEntity -> {
                    User savedUser = modelMapper.map(savedUserEntity, User.class);
                    System.out.println("savedUser: " + savedUser);
                    streamBridge.send("registerUser-out-0", savedUser);
                    return savedUser;
                });
    }

    public Mono<Void> updateUser(long id, User user){
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException()))
                .flatMap(userEntity -> {
                    modelMapper.map(user, userEntity);
                    return userRepository.save(userEntity).log().then();
                });


    }

    public Mono<Void> deleteUser(long id){
        return userRepository.deleteById(id);
    }

    public Flux<User> getUsers(Optional<String> firstNameOpt){
        if(firstNameOpt.isPresent()) {
            return userRepository.findByFirstName(firstNameOpt.get())
                    .log()
                    .map(userEntity -> modelMapper.map(userEntity, User.class));
        } else {
            return userRepository.findAll()
                    .log()
                    .map(userEntity -> modelMapper.map(userEntity, User.class));
        }
    }

    public Mono<User> getUser(long id){
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("User Not Found!")))
                .log()
                .map(userEntity -> modelMapper.map(userEntity, User.class));
    }


}

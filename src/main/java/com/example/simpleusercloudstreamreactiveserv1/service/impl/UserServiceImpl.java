package com.example.simpleusercloudstreamreactiveserv1.service.impl;

import com.example.cloudstream.resource.Account;
import com.example.simpleusercloudstreamreactiveserv1.entity.UserEntity;
import com.example.simpleusercloudstreamreactiveserv1.exception.NotFoundException;
import com.example.simpleusercloudstreamreactiveserv1.repository.UserRepository;
import com.example.cloudstream.resource.User;
import com.example.simpleusercloudstreamreactiveserv1.service.UserService;
import lombok.Setter;
import org.apache.kafka.streams.KeyValue;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Objects;
import java.util.Optional;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

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

                        // IMPORTANT: THERE IS ANOTHER CREATE USER METHOD BELOW. ENSURE YOU ARE MODIFYING THE CORRECT ONE

                        User savedUser = modelMapper.map(savedUserEntity, User.class);
                        System.out.println("savedUser: " + savedUser);
                        Message<User> userMessage = MessageBuilder.withPayload(savedUser)
                                .setHeader(KafkaHeaders.MESSAGE_KEY, savedUser.getId())
                                .build();

                        System.out.println("Message Headers: " + userMessage.getHeaders());
                        System.out.println("Message Payload: " + userMessage.getPayload());

                        streamBridge.send("registerUser-out-0", userMessage);

                        return savedUser;
                    });
        });
    }

    public Mono<User> createUser(User user){
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        return userRepository.save(userEntity)
                .log()
                .map(savedUserEntity -> {

                    // IMPORTANT: THERE IS ANOTHER CREATE USER METHOD ABOVE. ENSURE YOU ARE MODIFYING THE CORRECT ONE

                    User savedUser = modelMapper.map(savedUserEntity, User.class);
                    System.out.println("savedUser: " + savedUser);

                    Message<User> userMessage = MessageBuilder.withPayload(savedUser)
                            .setHeader(KafkaHeaders.MESSAGE_KEY, savedUser.getId())
                            .build();

                    System.out.println("Message Headers: " + userMessage.getHeaders());
                    System.out.println("Message Payload: " + userMessage.getPayload());

                    streamBridge.send("registerUser-out-0", userMessage);
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

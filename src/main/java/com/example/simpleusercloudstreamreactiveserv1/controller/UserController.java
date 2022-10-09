package com.example.simpleusercloudstreamreactiveserv1.controller;

import com.example.cloudstream.resource.RegistrationSummary;
import com.example.cloudstream.resource.RegistrationSummaryCollection;
import com.example.cloudstream.resource.User;
import com.example.cloudstream.resource.UserCollection;
import com.example.simpleusercloudstreamreactiveserv1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class UserController {

    private UserService userService;
    private InteractiveQueryService queryService;

    public UserController(UserService userService, InteractiveQueryService queryService) {
        this.userService = userService;
        this.queryService = queryService;
    }

    @GetMapping("/registrations")
    public Mono<RegistrationSummaryCollection> getUserRegistrationSummary() {

        List<RegistrationSummary> registrationSummaryList = new ArrayList<>();
        ReadOnlyKeyValueStore<String, RegistrationSummary> keyValueStore =
                queryService.getQueryableStore("all-registrations-store",
                        QueryableStoreTypes.keyValueStore());
        keyValueStore.all().forEachRemaining(keyValue -> registrationSummaryList.add(keyValue.value));
        return Mono.just(new RegistrationSummaryCollection(registrationSummaryList));
    }


    @PostMapping("/users")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<User> create(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable("id") Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<User> read(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }


    @GetMapping("/users")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UserCollection> search(@RequestParam(name="first_name", required = false) Optional<String> firstNameOpt) {
        return userService.getUsers(firstNameOpt).collectList().map(users -> new UserCollection(users));
    }


}

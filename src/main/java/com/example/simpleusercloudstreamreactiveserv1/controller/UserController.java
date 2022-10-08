package com.example.simpleusercloudstreamreactiveserv1.controller;

import com.example.cloudstream.resource.User;
import com.example.cloudstream.resource.UserCollection;
import com.example.simpleusercloudstreamreactiveserv1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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


    @GetMapping(value = "/say-delayed-hello")
    @ResponseStatus(code = HttpStatus.OK)
    public String sayHello() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException ie) {
            // ignore
        }
        return "Hello!";
    }

}

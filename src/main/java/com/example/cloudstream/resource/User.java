package com.example.cloudstream.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class User {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private int age;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserStatus status = UserStatus.INACTIVE;

}

package com.example.simpleusercloudstreamreactiveserv1.entity;

import com.example.cloudstream.resource.UserStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("user")
public class UserEntity {

    @Id
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private int age;
    private UserStatus status;

}

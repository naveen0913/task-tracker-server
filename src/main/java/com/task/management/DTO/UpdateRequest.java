package com.task.management.DTO;

import lombok.Data;

@Data
public class UpdateRequest {

    private String username;
    private String email;
    private String password;
    private String profileUrl;
    private String bio;
    private String fullName;

}

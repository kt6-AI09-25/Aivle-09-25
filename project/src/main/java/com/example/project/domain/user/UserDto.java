package com.example.project.domain.user;

public class UserDto {

    private String username;
    private String role;

    public UserDto(String username, String role){
        this.username = username;
        this.role = role;
    }

    public String getUsername() {return username;}
    public String getRole(){
        return role;
    }
}
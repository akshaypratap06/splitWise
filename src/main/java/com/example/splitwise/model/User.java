package com.example.splitwise.model;

import com.example.splitwise.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("email")
    private String email;
    private Date creationDate=new Date();

    public UserEntity toUserEntity() {
        UserEntity u= new UserEntity();
        u.setUserName(this.userName);
        u.setEmail(this.email);
        u.setCreationTime(this.getCreationDate());
        return u;
    }


}

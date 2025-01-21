package com.ulk.readingflow.api.v1.payloads.requests.dtos;

import com.ulk.readingflow.domain.enumerations.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO implements Serializable {

    private String name;
    private String username;
    private String email;
    private String phone;
    private String password;
    private String kindleMail;
    private GenderEnum gender;

}

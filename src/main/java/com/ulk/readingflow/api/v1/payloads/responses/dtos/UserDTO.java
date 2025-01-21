package com.ulk.readingflow.api.v1.payloads.responses.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ulk.readingflow.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private String name;
    private String username;
    private String email;
    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String birthday;

    private String gender;
    private String kindleMail;
    private String status;
    private List<String> roles;
    private Set<UserPreferenceDTO> preferences;

    public static UserDTO fromEntity(User user) {
        if (null != user) {
            List<String> roles = user.getRoles() != null ?
                    user.getRoles().stream()
                            .map(role -> role.getDescription().getDescription())
                            .toList() : null;

            Set<UserPreferenceDTO> preferences = user.getPreferences() != null ?
                    user.getPreferences().stream()
                            .map(preference -> UserPreferenceDTO.builder()
                                    .key(preference.getKey())
                                    .value(preference.getValue())
                                    .build())
                            .collect(Collectors.toSet()) : null;

            return UserDTO.builder()
                    .name(user.getName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .birthday(null != user.getBirthday() ?
                            user.getBirthday().toString() : null)
                    .gender(user.getGender().getDescription())
                    .kindleMail(user.getKindleMail())
                    .status(user.getStatus().getDescription())
                    .roles(roles)
                    .preferences(preferences)
                    .build();
        }
        return new UserDTO();
    }
}

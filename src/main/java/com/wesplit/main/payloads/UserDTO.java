package com.wesplit.main.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "field must not be blank")
    private String firstName;
    @NotBlank(message = "field must not be blank")
    private String lastName;
    @Email(message = "email is not valid")
    @NotNull(message = "field must not be null")
    private String email;
    @NotBlank(message = "field must not be blank")
    @Size(min = 8,message = "password must be minimum of 8 chars")
    private String password;
}

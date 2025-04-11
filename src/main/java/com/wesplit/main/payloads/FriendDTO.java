package com.wesplit.main.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {
    @NotBlank(message = "field must not be blank")
    private String firstName;
    @NotBlank(message = "field must not be blank")
    private String lastName;
    @NotNull(message = "field must not be null")
    @Email(message = "email is not valid")
    private String email;
}

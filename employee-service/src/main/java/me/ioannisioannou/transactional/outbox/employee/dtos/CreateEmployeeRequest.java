package me.ioannisioannou.transactional.outbox.employee.dtos;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateEmployeeRequest {
    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    @NotNull
    @Email
    private String email;
}

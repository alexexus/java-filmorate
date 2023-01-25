package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    public int id;
    @Email
    @NotNull
    @NotBlank
    public String email;
    @NotNull
    @NotBlank
    public String login;
    public String name;
    public LocalDate birthday;

}

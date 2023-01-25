package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    public int id;
    @NotNull
    @NotBlank
    public String name;
    public String description;
    public LocalDate releaseDate;
    @Positive
    public int duration;

}

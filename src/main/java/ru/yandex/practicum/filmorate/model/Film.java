package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {

    private int id;

    @NotNull
    @NotBlank
    private String name;

    private String description;
    private LocalDate releaseDate;

    @Positive
    private int duration;

}

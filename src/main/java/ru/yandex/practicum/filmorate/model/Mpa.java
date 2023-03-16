package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
public class Mpa {

    @Min(1)
    @Max(6)
    private long id;

    private String name;

    public Mpa(long id) {
        this.id = id;
    }

    public Mpa(long id, String name) {
        this.id = id;
        this.name = name;
    }
}

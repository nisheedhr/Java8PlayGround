package com.nisheedh;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Player {

    public enum Country {
        USA,
        INDIA,
        CHINA,
        BRAZIL
    }

    private final String name;
    private final int score;
    private final Country country;
}

package esy.api.basis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Sex {
    M("male"),
    F("female");

    @Getter
    private final String text;
}

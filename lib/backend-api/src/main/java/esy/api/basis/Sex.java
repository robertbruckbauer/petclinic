package esy.api.basis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum Sex {
    M("male"),
    F("female");

    @Getter
    @Accessors(fluent = true)
    private final String text;
}

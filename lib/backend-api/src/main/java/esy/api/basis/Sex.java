package esy.api.basis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum Sex {
    // tag::enumerations[]
    M("male"),
    F("female");
    // end::enumerations[]

    @Getter
    @Accessors(fluent = true)
    private final String text;
}

package esy.api.clinic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum InvoiceStatus {
    // tag::enumerations[]
    D("drafted"),
    I("issued"),
    C("completed"),
    X("cancelled");
    // end::enumerations[]

    @Getter
    @Accessors(fluent = true)
    private final String text;
}

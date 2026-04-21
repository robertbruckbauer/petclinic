package esy.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {

    @Override // <1>
    public Long convertToDatabaseColumn(final Duration value) {
        return value == null ? null : value.getSeconds();
    }

    @Override // <2>
    public Duration convertToEntityAttribute(final Long value) {
        return value == null ? null : Duration.ofSeconds(value);
    }
}

package esy.rest;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import lombok.NonNull;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface QuerydslRepository<V extends JsonJpaEntity<V>, T extends EntityPath<?>> extends QuerydslPredicateExecutor<V>, QuerydslBinderCustomizer<T> {

    /**
     * Returns a {@link Predicate} for a {@link LocalDate}.
     * Uses {@link DatePath#eq(Object)} for a single value.
     * Uses {@link DatePath#between(Comparable, Comparable)} for a value range.
     * Uses {@link DatePath#in(Object[])} otherwise.
     */
    default Optional<Predicate> localDateEqBetweenInBinding(@NonNull final DatePath<LocalDate> path, @NonNull final Collection<? extends LocalDate> allValue) {
        if (allValue.size() == 1) {
            final var it = allValue.iterator();
            return Optional.of(path.eq(it.next()));
        }
        if (allValue.size() == 2) {
            final var it = allValue.iterator();
            return Optional.of(path.between(it.next(), it.next()));
        }
        return Optional.of(path.in(allValue));
    }

    /**
     * Returns a {@link Predicate} for a {@link LocalDateTime}.
     * Uses {@link DateTimePath#eq(Object)} for a single value.
     * Uses {@link DateTimePath#between(Comparable, Comparable)} for a value range.
     * Uses {@link DateTimePath#in(Object[])} otherwise.
     */
    default Optional<Predicate> localDateTimeEqBetweenInBinding(@NonNull final DateTimePath<LocalDateTime> path, @NonNull final Collection<? extends LocalDateTime> allValue) {
        if (allValue.size() == 1) {
            final var it = allValue.iterator();
            return Optional.of(path.eq(it.next()));
        }
        if (allValue.size() == 2) {
            final var it = allValue.iterator();
            return Optional.of(path.between(it.next(), it.next()));
        }
        return Optional.of(path.in(allValue));
    }

    /**
     * Returns a {@link Predicate} for a {@link Long} value.
     * Uses {@link NumberPath#eq(Object)} for a single value.
     * Uses {@link NumberPath#between(Number, Number)} for a value range.
     * Uses {@link NumberPath#in(Number...)} otherwise.
     */
    default Optional<Predicate> longEqBetweenInBinding(@NonNull final NumberPath<Long> path, @NonNull final Collection<? extends Long> allValue) {
        if (allValue.size() == 1) {
            final var it = allValue.iterator();
            return Optional.of(path.eq(it.next()));
        }
        if (allValue.size() == 2) {
            final var it = allValue.iterator();
            return Optional.of(path.between(it.next(), it.next()));
        }
        return Optional.of(path.in(allValue));
    }

    /**
     * Returns a {@link BooleanExpression} for a {@link String} value.
     * Uses {@link StringPath#likeIgnoreCase(String)} for a value having a character {@code %}.
     * Uses {@link StringPath#containsIgnoreCase(String)} otherwise.
     */
    default BooleanExpression stringContainsOrLikeIgnoreCaseBinding(@NonNull final StringPath path, @NonNull final String value) {
        if (value.contains("%")) {
            return path.likeIgnoreCase(value);
        } else {
            return path.containsIgnoreCase(value);
        }
    }
}

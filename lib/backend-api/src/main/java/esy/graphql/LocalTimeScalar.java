package esy.graphql;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.*;
import lombok.NonNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LocalTimeScalar {
    private LocalTimeScalar() {}

    static final String SCALAR_NAME = "LocalTime";
    static final String FORMATTER_PATTERN = "HH:mm[:ss]";
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATTER_PATTERN);
    public static GraphQLScalarType createScalarType() {
        return GraphQLScalarType.newScalar()
                .name(SCALAR_NAME)
                .description(String.format("Custom scalar for handling %s with format '%s'", SCALAR_NAME, FORMATTER_PATTERN))
                .coercing(new Coercing<LocalTime, String>() {

                    @Override
                    public String serialize(@NonNull Object input, @NonNull GraphQLContext context, @NonNull Locale locale) throws CoercingSerializeException {
                        if (input instanceof LocalTime date) {
                            return FORMATTER.format(date);
                        }
                        throw new CoercingParseValueException(String.format(
                                "Invalid type %s. Expected %s.",
                                input.getClass().getName(),
                                SCALAR_NAME));
                    }

                    @Override
                    public LocalTime parseValue(@NonNull Object input, @NonNull GraphQLContext context, @NonNull Locale locale) throws CoercingParseValueException {
                        if (input instanceof String dateString) {
                            try {
                                return LocalTime.parse(dateString, FORMATTER);
                            } catch (DateTimeParseException e) {
                                final var message = String.format(
                                        "Invalid input '%s'. Expected '%s'.",
                                        dateString,
                                        FORMATTER_PATTERN);
                                throw new CoercingParseValueException(message, e);
                            }
                        }
                        final var message = String.format(
                                "Invalid type %s. Expected String.",
                                input.getClass().getName());
                        throw new CoercingParseValueException(message);
                    }

                    @Override
                    public LocalTime parseLiteral(@NonNull Value<?> input, @NonNull CoercedVariables variables, @NonNull GraphQLContext context, @NonNull Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue dateString) {
                            try {
                                return LocalTime.parse(dateString.getValue(), FORMATTER);
                            } catch (DateTimeParseException e) {
                                final var message = String.format(
                                        "Invalid input '%s'. Expected '%s'.",
                                        dateString,
                                        FORMATTER_PATTERN);
                                throw new CoercingParseLiteralException(message, e);
                            }
                        }
                        final var message = String.format(
                                "Invalid type %s. Expected StringValue.",
                                input.getClass().getName());
                        throw new CoercingParseValueException(message);
                    }
                }).build();
    }
}

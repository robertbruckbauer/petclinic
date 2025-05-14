package esy.graphql;
import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LocalDateScalar {
    static final String SCALAR_NAME = "LocalDate";
    static final String FORMATTER_PATTERN = "yyyy-MM-dd";
    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATTER_PATTERN);
    public static GraphQLScalarType createLocalDateScalar() {
        return GraphQLScalarType.newScalar()
                .name(SCALAR_NAME)
                .description(String.format("Custom scalar for handling %s with format '%s'", SCALAR_NAME, FORMATTER_PATTERN))
                .coercing(new Coercing<LocalDate, String>() {

                    @Override
                    public String serialize(@NonNull Object input, @NonNull GraphQLContext context, @NonNull Locale locale) throws CoercingSerializeException {
                        if (input instanceof LocalDate date) {
                            return FORMATTER.format(date);
                        }
                        throw new CoercingParseValueException(String.format(
                                "Invalid type %s. Expected %s.",
                                input.getClass().getName(),
                                SCALAR_NAME));
                    }

                    @Override
                    public LocalDate parseValue(@NonNull Object input, @NonNull GraphQLContext context, @NonNull Locale locale) throws CoercingParseValueException {
                        if (input instanceof String dateString) {
                            try {
                                return LocalDate.parse(dateString, FORMATTER);
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
                    public LocalDate parseLiteral(@NonNull Value<?> input, @NonNull CoercedVariables variables, @NonNull GraphQLContext context, @NonNull Locale locale) throws CoercingParseLiteralException {
                        if (input instanceof StringValue dateString) {
                            try {
                                return LocalDate.parse(dateString.getValue(), FORMATTER);
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

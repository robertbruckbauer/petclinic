import lombok.NonNull;

public record Message(@NonNull String type, @NonNull String text, boolean major) {
}

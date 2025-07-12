import lombok.NonNull;

public record Version(@NonNull String prefix, int major, int minor, @NonNull String patch) {
    public static Version with(int major, int minor) {
        return new Version("", major, minor, "");
    }
}

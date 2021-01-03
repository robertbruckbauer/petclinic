package esy.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface JsonJpaItem<ID> {

    /**
     * @return Eindeutige UUID des repräsentierten Value-Objektes.
     */
    ID getValue();

    /**
     * @return Menschenlesbarer Text für das repräsentierte Value-Objektes.
     */
    String getText();

    /**
     * @return {@code true}, wenn das repräsentierte Value-Objekt in der Datenbank angelegt werden soll.
     */
    @JsonIgnore
    default boolean isCreate() {
        return getValue() == null && !getText().isBlank();
    }

    /**
     * @return {@code true}, wenn das repräsentierte Value-Objekt in der Datenbank aktualisiert werden soll.
     */
    @JsonIgnore
    default boolean isUpdate() {
        return getValue() != null;
    }

    /**
     * @return {@code true}, wenn das repräsentierte Value-Objekt aus der Datenbank entfernt werden soll.
     */
    @JsonIgnore
    default boolean isDelete() {
        return getValue() == null && getText().isBlank();
    }
}

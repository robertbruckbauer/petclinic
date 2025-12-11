package esy.json;

import lombok.NonNull;

import java.util.UUID;

public interface JsonJpaWithId<S> {

    /**
     * Erzeugt eine neue Instanz mit neuer UUID. Alle
     * anderen Eigenschaften bleiben identisch. Das
     * gilt vor allem für alle Arten von Collections,
     * d.h. keine neuen Collections mit den gleichen
     * Elementen!
     *
     * @param id Neue UUID
     * @return Neue Instanz
     */
    S withId(@NonNull final UUID id);
}

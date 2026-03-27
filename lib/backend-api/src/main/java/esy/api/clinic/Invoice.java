package esy.api.clinic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.rest.JsonJpaEntity;
import esy.rest.JsonJpaMapper;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "invoice", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public final class Invoice extends JsonJpaEntity<Invoice> {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    // tag::properties[]
    @NotNull
    @Column(name = "at", columnDefinition = "DATE")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate at;

    @NotNull
    @Column(name = "due", columnDefinition = "DATE")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate due;

    @NotNull
    @Column(name = "status")
    @Getter
    @JsonProperty
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @NotBlank
    @Column(name = "text")
    @Getter
    @JsonProperty
    private String text;
    // end::properties[]

    Invoice() {
        super();
        this.at = LocalDate.of(2000, 1, 1);
        this.due = LocalDate.of(2000, 1, 1);
        this.status = InvoiceStatus.D;
        this.text = "";
    }

    Invoice(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.at = LocalDate.of(2000, 1, 1);
        this.due = LocalDate.of(2000, 1, 1);
        this.status = InvoiceStatus.D;
        this.text = "";
    }

    @Override
    public String toString() {
        return super.toString() + ",at='" + at + "'";
    }

    @Override
    public boolean isEqual(final Invoice that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return Objects.equals(this.at, that.at) &&
                Objects.equals(this.due, that.due) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.text, that.text);
    }

    @Override
    public Invoice withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final var value = new Invoice(getVersion(), id);
        value.at = this.at;
        value.due = this.due;
        value.status = this.status;
        value.text = this.text;
        return value;
    }

    @Override
    public String writeJson() {
        return new JsonJpaMapper().writeJson(this);
    }

    public static Invoice fromJson(@NonNull final String json) {
        return new JsonJpaMapper().parseJson(json, Invoice.class);
    }
}

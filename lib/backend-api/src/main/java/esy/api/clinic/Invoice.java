package esy.api.clinic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import esy.jpa.DurationConverter;
import esy.rest.JsonJpaEntity;
import esy.rest.JsonJpaMapper;
import lombok.Getter;
import lombok.NonNull;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "invoice", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"}),
        @UniqueConstraint(columnNames = {"visit_id"})
})
public final class Invoice extends JsonJpaEntity<Invoice> {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    // tag::properties[]
    @NotNull
    @Column(name = "issued", columnDefinition = "DATE")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
    private LocalDate issued;

    @NotNull
    @Column(name = "period", columnDefinition = "BIGINT")
    @Getter
    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Convert(converter = DurationConverter.class)
    private Duration period;

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

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false)
    @JoinColumn(
            name = "visit_id",
            referencedColumnName = "id")
    @Getter
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Visit visit;
    // end::properties[]

    Invoice() {
        super();
        this.issued = LocalDate.of(2000, 1, 1);
        this.period = Duration.ZERO;
        this.status = InvoiceStatus.D;
        this.text = "";
        this.visit = null;
    }

    Invoice(@NonNull final Long version, @NonNull final UUID id) {
        super(version, id);
        this.issued = LocalDate.of(2000, 1, 1);
        this.period = Duration.ZERO;
        this.status = InvoiceStatus.D;
        this.text = "";
        this.visit = null;
    }

    @Override
    public String toString() {
        return super.toString() + ",issued='" + issued + "'";
    }

    @Override
    public boolean isEqual(final Invoice that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        return Objects.equals(this.issued, that.issued) &&
                Objects.equals(this.period, that.period) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.text, that.text) &&
                Objects.equals(this.visit, that.visit);
    }

    @Override
    public Invoice withId(@NonNull final UUID id) {
        if (Objects.equals(getId(), id)) {
            return this;
        }
        final var value = new Invoice(getVersion(), id);
        value.issued = this.issued;
        value.period = this.period;
        value.status = this.status;
        value.text = this.text;
        value.visit = this.visit;
        return value;
    }

    @Override
    public String writeJson() {
        return new JsonJpaMapper().writeJson(this);
    }

    @JsonIgnore
    public Invoice setVisit(@NonNull final Visit visit) {
        this.visit = visit;
        return this;
    }

    public static Invoice fromJson(@NonNull final String json) {
        return new JsonJpaMapper().parseJson(json, Invoice.class);
    }
}

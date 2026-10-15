package esy.app.clinic;

import esy.api.clinic.Invoice;
import esy.api.clinic.QInvoice;
import esy.rest.JsonJpaRepository;
import esy.rest.QuerydslRepository;
import lombok.NonNull;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "invoice", collectionResourceRel = "allInvoice")
public interface InvoiceRepository extends JsonJpaRepository<Invoice>, QuerydslRepository<Invoice, QInvoice> {

    @Override
    default void customize(@NonNull final QuerydslBindings bindings, @NonNull final QInvoice root) {
        bindings.bind(String.class).first(this::stringContainsOrLikeIgnoreCaseBinding);
        bindings.bind(root.issued).all(this::localDateEqBetweenInBinding);
    }
}

package esy.app.clinic;

import esy.api.clinic.Invoice;
import esy.api.clinic.QInvoice;
import esy.api.clinic.Visit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class InvoiceGraphqlController {

    private final InvoiceRepository invoiceRepository;

    private final VisitRepository visitRepository;

    @QueryMapping
    @Transactional
    public List<Invoice> allInvoice() {
        return invoiceRepository.findAll();
    }

    @QueryMapping
    @Transactional
    public Optional<Invoice> invoiceById(@Argument @NonNull final UUID id) {
        return invoiceRepository.findById(id);
    }

    @QueryMapping
    @Transactional
    public List<Invoice> allInvoiceAt(@Argument @NonNull final LocalDate issued) {
        final var query = QInvoice.invoice.issued.eq(issued);
        final var order = QInvoice.invoice.issued.asc();
        final var allInvoice = new ArrayList<Invoice>();
        invoiceRepository.findAll(query, order).forEach(allInvoice::add);
        return allInvoice;
    }

    @BatchMapping
    @Transactional
    public Mono<Map<Invoice, Visit>> visit(@NonNull final List<Invoice> allInvoice) {
        final var allVisitId = allInvoice
                .stream()
                .filter(i -> i.getVisit() != null)
                .map(i -> i.getVisit().getId())
                .collect(Collectors.toSet());
        final var visitById = visitRepository.findAllById(allVisitId)
                .stream()
                .collect(Collectors.toMap(Visit::getId, v -> v));
        return Mono.just(allInvoice
                .stream()
                .filter(i -> i.getVisit() != null)
                .collect(Collectors.toMap(i -> i, i -> visitById.get(i.getVisit().getId()))));
    }
}

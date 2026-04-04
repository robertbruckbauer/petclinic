package esy.app.clinic;

import esy.api.clinic.Invoice;
import esy.api.clinic.QInvoice;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class InvoiceGraphqlController {

    private final InvoiceRepository invoiceRepository;

    @QueryMapping
    @Transactional
    public List<Invoice> allInvoice() {
        return invoiceRepository.findAll();
    }

    @QueryMapping
    @Transactional
    public List<Invoice> allInvoiceAt(@Argument @NonNull final LocalDate at) {
        final var query = QInvoice.invoice.at.eq(at);
        final var order = QInvoice.invoice.at.asc();
        final var allInvoice = new ArrayList<Invoice>();
        invoiceRepository.findAll(query, order).forEach(allInvoice::add);
        return allInvoice;
    }
}

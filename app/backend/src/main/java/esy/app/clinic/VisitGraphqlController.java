package esy.app.clinic;

import esy.api.clinic.QVisit;
import esy.api.clinic.Visit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VisitGraphqlController {

    private final VisitRepository visitRepository;

    @QueryMapping
    @Transactional
    public List<Visit> allVisit() {
        return visitRepository.findAll();
    }

    @QueryMapping
    @Transactional
    public List<Visit> allVisitAt(@Argument @NonNull final LocalDate date) {
        final var query = QVisit.visit.date.eq(date);
        final var order = QVisit.visit.date.asc();
        final var allVisit = new ArrayList<Visit>();
        visitRepository.findAll(query, order).forEach(allVisit::add);
        return allVisit;
    }

    @QueryMapping
    @Transactional
    public List<Visit> allVisitByPetId(@Argument("id") @NonNull final UUID petId) {
        final var query = QVisit.visit.pet.id.eq(petId);
        final var order = QVisit.visit.date.asc();
        final var allVisit = new ArrayList<Visit>();
        visitRepository.findAll(query, order).forEach(allVisit::add);
        return allVisit;
    }

    @QueryMapping
    @Transactional
    public List<Visit> allVisitByVetId(@Argument("id") @NonNull final UUID vetId) {
        final var query = QVisit.visit.vet.id.eq(vetId);
        final var order = QVisit.visit.date.asc();
        final var allVisit = new ArrayList<Visit>();
        visitRepository.findAll(query, order).forEach(allVisit::add);
        return allVisit;
    }
}

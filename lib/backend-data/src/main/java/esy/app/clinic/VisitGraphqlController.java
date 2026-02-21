package esy.app.clinic;

import esy.api.client.Pet;
import esy.api.clinic.QVisit;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.app.client.PetRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class VisitGraphqlController {

    private final VisitRepository visitRepository;

    private final PetRepository petRepository;

    private final VetRepository vetRepository;

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

    @BatchMapping
    @Transactional
    public Mono<Map<Visit, Pet>> pet(@NonNull final List<Visit> allVisit) {
        final var allPetId = allVisit
                .stream()
                .filter(v -> v.getPet() != null)
                .map(v -> v.getPet().getId())
                .collect(Collectors.toSet());
        final var petById = petRepository.findAllById(allPetId)
                .stream()
                .collect(Collectors.toMap(Pet::getId, p -> p));
        return Mono.just(allVisit
                .stream()
                .filter(v -> v.getPet() != null)
                .collect(Collectors.toMap(v -> v, v -> petById.get(v.getPet().getId()))));
    }

    @BatchMapping
    @Transactional
    public Mono<Map<Visit, Vet>> vet(@NonNull final List<Visit> allVisit) {
        final var allVetId = allVisit
                .stream()
                .filter(v -> v.getVet() != null)
                .map(v -> v.getVet().getId())
                .collect(Collectors.toSet());
        final var vetById = vetRepository.findAllById(allVetId)
                .stream()
                .collect(Collectors.toMap(Vet::getId, v -> v));
        return Mono.just(allVisit
                .stream()
                .filter(v -> v.getVet() != null)
                .collect(Collectors.toMap(v -> v, v -> vetById.get(v.getVet().getId()))));
    }
}

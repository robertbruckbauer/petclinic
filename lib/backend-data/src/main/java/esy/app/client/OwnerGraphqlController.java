package esy.app.client;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.client.QOwner;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class OwnerGraphqlController {

    private final OwnerRepository ownerRepository;

    private final PetRepository petRepository;

    @QueryMapping
    @Transactional
    public List<Owner> allOwner() {
        return ownerRepository.findAll();
    }

    @QueryMapping
    @Transactional
    public Optional<Owner> ownerById(@Argument @NonNull final UUID id) {
        return ownerRepository.findById(id);
    }

    @QueryMapping
    @Transactional
    public Optional<Owner> ownerByName(@Argument @NonNull final String name) {
        final var query = QOwner.owner.name.eq(name);
        return ownerRepository.findOne(query);
    }

    @BatchMapping
    @Transactional
    public Mono<Map<Owner, Set<Pet>>> allPet(@NonNull final List<Owner> allOwner) {
        final var allOwnerId = allOwner
                .stream()
                .map(Owner::getId)
                .collect(Collectors.toSet());
        final var allPet = petRepository.findAllByOwnerIdIn(allOwnerId);
        final var allPetByOwner = allPet
                .stream()
                .collect(Collectors.groupingBy(Pet::getOwner, Collectors.toSet()));
        return Mono.just(allPetByOwner);
    }
}

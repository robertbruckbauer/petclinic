package esy.app.client;

import esy.api.client.Owner;
import esy.api.client.Pet;
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
public class PetGraphqlController {

    private final PetRepository petRepository;

    private final OwnerRepository ownerRepository;

    @QueryMapping
    @Transactional
    public List<Pet> allPet() {
        return petRepository.findAll();
    }

    @QueryMapping
    @Transactional
    public Optional<Pet> petById(@Argument @NonNull final UUID id) {
        return petRepository.findById(id);
    }

    @BatchMapping
    @Transactional
    public Mono<Map<Pet, Owner>> owner(@NonNull final List<Pet> allPet) {
        final var allOwnerId = allPet
                .stream()
                .map(p -> p.getOwner().getId())
                .collect(Collectors.toSet());
        final var ownerById = ownerRepository.findAllById(allOwnerId)
                .stream()
                .collect(Collectors.toMap(Owner::getId, o -> o));
        return Mono.just(allPet
                .stream()
                .collect(Collectors.toMap(p -> p, p -> ownerById.get(p.getOwner().getId()))));
    }

}

package esy.app.client;

import esy.api.client.Pet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class PetGraphqlController {

    private final PetRepository petRepository;

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

}

package esy.app.clinic;

import esy.api.clinic.QVet;
import esy.api.clinic.Vet;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class VetGraphqlController {

    private final VetRepository vetRepository;

    @QueryMapping
    @Transactional
    public List<Vet> allVet() {
        return vetRepository.findAll();
    }

    @QueryMapping
    @Transactional
    public Optional<Vet> vetById(@Argument @NonNull final UUID id) {
        return vetRepository.findById(id);
    }

    @QueryMapping
    @Transactional
    public Optional<Vet> vetByName(@Argument @NonNull final String name) {
        final var query = QVet.vet.name.eq(name);
        return vetRepository.findOne(query);
    }
}

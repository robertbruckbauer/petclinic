package esy;

import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.basis.Enum;
import esy.app.client.OwnerRepository;
import esy.app.client.PetRepository;
import esy.app.clinic.VetRepository;
import esy.app.clinic.VisitRepository;
import esy.app.basis.EnumRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;

@Slf4j
@Component
public class ServerTestset implements CommandLineRunner {

    static final List<String> allLoremIpsum = List.of(
            "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.",
            "At vero eos et accusam et justo duo dolores et ea rebum.",
            "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
            "Magni accusantium labore et id quis provident.",
            "Consectetur impedit quisquam qui deserunt non rerum consequuntur eius.",
            "Quia atque aliquam sunt impedit voluptatum rerum assumenda nisi.",
            "Cupiditate quos possimus corporis quisquam exercitationem beatae."
    );

    @Autowired
    private EnumRepository enumRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private VetRepository vetRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Override
    @Transactional
    public void run(final String... args) {
        if (ownerRepository.count() != 0) {
            return;
        }

        final var allEnumSkill = createAllEnumSkill();
        allEnumSkill.values().forEach(ServerTestset::logCreated);

        final var allEnumSpecies = createAllEnumSpecies();
        allEnumSpecies.values().forEach(ServerTestset::logCreated);

        final var allOwner = createAllOwner();
        allOwner.values().forEach(ServerTestset::logCreated);

        final var allPet = createAllPet(allOwner);
        allPet.values().forEach(ServerTestset::logCreated);

        final var allVet = createAllVet();
        allVet.values().forEach(ServerTestset::logCreated);

        final var allVisit = createAllVisit(allPet, allVet);
        allVisit.forEach(ServerTestset::logCreated);
    }

    static <T> void logCreated(@NonNull final T value) {
        log.info("CREATED [{}]", value);
    }

    private Map<String, Enum> createAllEnumSkill() {
        return Stream.of(
                        Enum.fromJson("""
                                {
                                    "code": 0,
                                    "name":"Radiology",
                                    "text":"Radiology is the medical discipline that uses medical imaging to diagnose and treat diseases within the bodies of animals and humans"
                                }
                                """),
                        Enum.fromJson("""
                                {
                                    "code": 1,
                                    "name":"Dentistry",
                                    "text":"Dentistry is a branch of medicine that consists of the study, diagnosis, prevention, and treatment of diseases, disorders, and conditions of the oral cavity (the mouth)."
                                }
                                """),
                        Enum.fromJson("""
                                {
                                    "code": 2,
                                    "name":"Surgery",
                                    "text":"Surgery is the branch of medical practice that treats injuries, diseases, and deformities by the physical removal, repair, or readjustment of organs and tissues."
                                }
                                """))
                .map(e -> e.setArt("skill"))
                .map(enumRepository::save)
                .collect(Collectors.toMap(Enum::getName, identity()));
    }

    private Map<String, Enum> createAllEnumSpecies() {
        return Stream.of(
                        Enum.fromJson("""
                                {
                                    "code": 0,
                                    "name":"Cat",
                                    "text":"A cat (tax. felis catus) is a domestic species of a small carnivorous mammal."
                                }
                                """),
                        Enum.fromJson("""
                                {
                                    "code": 1,
                                    "name":"Dog",
                                    "text":"A dog (tax. canis familiaris) is a domesticated descendant of the wolf."
                                }
                                """),
                        Enum.fromJson("""
                                {
                                    "code": 2,
                                    "name":"Rat",
                                    "text":"A rat (tax. rattus) is a family of various medium-sized, long-tailed rodents."
                                }
                                """),
                        Enum.fromJson("""
                                {
                                    "code": 3,
                                    "name":"Pig",
                                    "text":"A pig (tax. sus domesticus) is an omnivorous, domesticated even-toed hoofed mammal."
                                }
                                """),
                        Enum.fromJson("""
                                {
                                    "code": 4,
                                    "name":"Bird",
                                    "text":"A bird (tax. aves) is a class of various feathered anmimals."
                                }
                                """))
                .map(e -> e.setArt("species"))
                .map(enumRepository::save)
                .collect(Collectors.toMap(Enum::getName, identity()));
    }

    private Map<String, Owner> createAllOwner() {
        return Stream.of(
                        Owner.fromJson("""
                                {
                                    "name":"Thomas Mann",
                                    "address":"110 W. Liberty St.",
                                    "contact":"+43 660 5551023"
                                }
                                """),
                        Owner.fromJson("""
                                {
                                    "name":"Stefan Zweig",
                                    "address":"638 Cardinal Ave.",
                                    "contact":"+43 660 5551749"
                                }
                                """),
                        Owner.fromJson("""
                                {
                                    "name":"Wolfgang A. Mozart",
                                    "address":"2387 S. Fair Way",
                                    "contact":"+43 660 5552765"
                                }
                                """),
                        Owner.fromJson("""
                                {
                                    "name":"Arthur Conan Doyle",
                                    "address":"1450 Oak Blvd.",
                                    "contact":"+43 660 5555387"
                                }
                                """))
                .map(ownerRepository::save)
                .collect(Collectors.toMap(Owner::getName, identity()));
    }

    private Map<String, Pet> createAllPet(final Map<String, Owner> allOwner) {
        return Stream.of(
                        Pet.fromJson("""
                                        {
                                            "name":"Tom",
                                            "born":"2021-04-01",
                                            "species":"Cat"
                                        }
                                        """)
                                .setOwner(allOwner.get("Thomas Mann")),
                        Pet.fromJson("""
                                        {
                                            "name":"Odi",
                                            "born":"2021-04-02",
                                            "species":"Dog"
                                        }
                                        """)
                                .setOwner(allOwner.get("Thomas Mann")),
                        Pet.fromJson("""
                                        {
                                            "name":"Fox",
                                            "born":"2021-04-03",
                                            "species":"Rat"
                                        }
                                        """)
                                .setOwner(allOwner.get("Stefan Zweig")))
                .map(petRepository::save)
                .collect(Collectors.toMap(Pet::getName, identity()));
    }

    private Map<String, Vet> createAllVet() {
        return Stream.of(
                        Vet.fromJson("""
                                {
                                    "name":"Graham Chapman",
                                    "allSkill":["Surgery","Radiology"]
                                }
                                """),
                        Vet.fromJson("""
                                {
                                    "name":"John Cleese",
                                    "allSkill":["Surgery"]
                                }
                                """),
                        Vet.fromJson("""
                                {
                                    "name":"Terry Gilliam",
                                    "allSkill":["Dentistry","Radiology"]
                                }
                                """),
                        Vet.fromJson("""
                                {
                                    "name":"Eric Idle",
                                    "allSkill":["Dentistry"]
                                }
                                """),
                        Vet.fromJson("""
                                {
                                    "name":"Terry Jones",
                                    "allSkill":[]
                                }
                                """))
                .map(vetRepository::save)
                .collect(Collectors.toMap(Vet::getName, identity()));
    }

    private List<Visit> createAllVisit(Map<String, Pet> allPet, Map<String, Vet> allVet) {
        return Stream.of(
                        Visit.fromJson("""
                                        {
                                            "date":"2021-04-21",
                                            "text":"%s"
                                        }
                                        """.formatted(allLoremIpsum.get(0)))
                                .setPet(allPet.get("Tom"))
                                .setVet(allVet.get("Graham Chapman")),
                        Visit.fromJson("""
                                        {
                                            "date":"2021-04-21",
                                            "text":"%s"
                                        }
                                        """.formatted(allLoremIpsum.get(1)))
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Graham Chapman")),
                        Visit.fromJson("""
                                        {
                                            "date":"2021-04-22",
                                            "text":"%s"
                                        }
                                        """.formatted(allLoremIpsum.get(2)))
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("John Cleese")),
                        Visit.fromJson("""
                                        {
                                            "date":"2021-04-23",
                                            "text":"%s"
                                        }
                                        """.formatted(allLoremIpsum.get(3)))
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Terry Gilliam")),
                        Visit.fromJson("""
                                        {
                                            "date":"2021-04-24",
                                            "text":"%s"
                                        }
                                        """.formatted(allLoremIpsum.get(4)))
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Eric Idle")),
                        Visit.fromJson("""
                                        {
                                            "date":"2021-04-24",
                                            "text":"%s"
                                        }
                                        """.formatted(allLoremIpsum.get(5)))
                                .setPet(allPet.get("Fox"))
                                .setVet(allVet.get("Terry Jones"))
                )
                .map(visitRepository::save)
                .collect(Collectors.toList());
    }
}

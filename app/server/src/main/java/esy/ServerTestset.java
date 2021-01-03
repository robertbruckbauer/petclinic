package esy;


import esy.api.client.Owner;
import esy.api.client.Pet;
import esy.api.clinic.Vet;
import esy.api.clinic.Visit;
import esy.api.info.Enum;
import esy.app.client.OwnerRepository;
import esy.app.client.PetRepository;
import esy.app.clinic.VetRepository;
import esy.app.clinic.VisitRepository;
import esy.app.info.EnumRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
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
    public void run(final String... args) throws Exception {
        if (ownerRepository.count() != 0) {
            return;
        }

        final Map<String, Enum> allEnumSkill = createAllEnumSkill();
        allEnumSkill.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Enum> allEnumSpecies = createAllEnumSpecies();
        allEnumSpecies.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Owner> allOwner = createAllOwner();
        allOwner.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Pet> allPet = createAllPet(allOwner);
        allPet.values().forEach(e -> log.info("CREATED [{}]", e));

        final Map<String, Vet> allVet = createAllVet();
        allVet.values().forEach(e -> log.info("CREATED [{}]", e));

        final List<Visit> allVisit = createAllVisit(allPet, allVet);
        allVisit.forEach(e -> log.info("CREATED [{}]", e));
    }

    @Transactional
    private Map<String, Enum> createAllEnumSkill() {
        return Stream.of(
                        Enum.parseJson("{" +
                                "\"code\": 0," +
                                "\"name\":\"Radiology\"," +
                                "\"text\":\"Radiology is the medical discipline that uses medical imaging to diagnose and treat diseases within the bodies of animals and humans\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 1," +
                                "\"name\":\"Dentistry\"," +
                                "\"text\":\"Dentistry is a branch of medicine that consists of the study, diagnosis, prevention, and treatment of diseases, disorders, and conditions of the oral cavity (the mouth).\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 2," +
                                "\"name\":\"Surgery\"," +
                                "\"text\":\"Surgery he branch of medical practice that treats injuries, diseases, and deformities by the physical removal, repair, or readjustment of organs and tissues.\"" +
                                "}"))
                .map(e -> e.setArt("skill"))
                .map(enumRepository::save)
                .collect(Collectors.toMap(Enum::getName, identity()));
    }

    @Transactional
    private Map<String, Enum> createAllEnumSpecies() {
        return Stream.of(
                        Enum.parseJson("{" +
                                "\"code\": 0," +
                                "\"name\":\"Cat\"," +
                                "\"text\":\"A cat (tax. felis catus) is a domestic species of a small carnivorous mammal.\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 1," +
                                "\"name\":\"Dog\"," +
                                "\"text\":\"A dog (tax. canis familiaris) is a domesticated descendant of the wolf.\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 2," +
                                "\"name\":\"Rat\"," +
                                "\"text\":\"A rat (tax. rattus) is a family of various medium-sized, long-tailed rodents.\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 3," +
                                "\"name\":\"Pig\"," +
                                "\"text\":\"A pig (tax. sus domesticus) is an omnivorous, domesticated even-toed hoofed mammal.\"" +
                                "}"),
                        Enum.parseJson("{" +
                                "\"code\": 4," +
                                "\"name\":\"Bird\"," +
                                "\"text\":\"A bird (tax. aves) is a class of various feathered anmimals.\"" +
                                "}"))
                .map(e -> e.setArt("species"))
                .map(enumRepository::save)
                .collect(Collectors.toMap(Enum::getName, identity()));
    }

    @Transactional
    private Map<String, Owner> createAllOwner() {
        return Stream.of(
                        Owner.parseJson("{" +
                                "\"name\":\"Thomas Mann\"," +
                                "\"address\":\"110 W. Liberty St.\"," +
                                "\"contact\":\"+43 660 5551023\"" +
                                "}"),
                        Owner.parseJson("{" +
                                "\"name\":\"Stefan Zweig\"," +
                                "\"address\":\"638 Cardinal Ave.\"," +
                                "\"contact\":\"+43 660 5551749\"" +
                                "}"),
                        Owner.parseJson("{" +
                                "\"name\":\"Wolfgang A. Mozart\"," +
                                "\"address\":\"2387 S. Fair Way\"," +
                                "\"contact\":\"+43 660 5552765\"" +
                                "}"),
                        Owner.parseJson("{" +
                                "\"name\":\"Arthur Conan Doyle\"," +
                                "\"address\":\"1450 Oak Blvd.\"," +
                                "\"contact\":\"+43 660 5555387\"" +
                                "}"))
                .map(ownerRepository::save)
                .collect(Collectors.toMap(Owner::getName, identity()));
    }

    @Transactional
    private Map<String, Pet> createAllPet(final Map<String, Owner> allOwner) {
        return Stream.of(
                        Pet.parseJson("{" +
                                        "\"name\":\"Tom\"," +
                                        "\"born\":\"2021-04-01\"," +
                                        "\"species\":\"Cat\"" +
                                        "}")
                                .setOwner(allOwner.get("Thomas Mann")),
                        Pet.parseJson("{" +
                                        "\"name\":\"Odi\"," +
                                        "\"born\":\"2021-04-02\"," +
                                        "\"species\":\"Dog\"" +
                                        "}")
                                .setOwner(allOwner.get("Thomas Mann")),
                        Pet.parseJson("{" +
                                        "\"name\":\"Fox\"," +
                                        "\"born\":\"2021-04-03\"," +
                                        "\"species\":\"Rat\"" +
                                        "}")
                                .setOwner(allOwner.get("Stefan Zweig")))
                .map(petRepository::save)
                .collect(Collectors.toMap(Pet::getName, identity()));
    }

    @Transactional
    private Map<String, Vet> createAllVet() {
        return Stream.of(
                        Vet.parseJson("{" +
                                "\"name\":\"Graham Chapman\"," +
                                "\"allSkill\":[\"Surgery\",\"Radiology\"]" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\":\"John Cleese\"," +
                                "\"allSkill\":[\"Surgery\"]" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\":\"Terry Gilliam\"," +
                                "\"allSkill\":[\"Dentistry\",\"Radiology\"]" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\":\"Eric Idle\"," +
                                "\"allSkill\":[\"Dentistry\"]" +
                                "}"),
                        Vet.parseJson("{" +
                                "\"name\":\"Terry Jones\"," +
                                "\"allSkill\":[]" +
                                "}"))
                .map(vetRepository::save)
                .collect(Collectors.toMap(Vet::getName, identity()));
    }

    @Transactional
    private List<Visit> createAllVisit(Map<String, Pet> allPet, Map<String, Vet> allVet) {
        return Stream.of(
                        Visit.parseJson("{" +
                                        "\"date\":\"2021-04-21\"," +
                                        "\"text\":\"" + allLoremIpsum.get(0) + "\"" +
                                        "}")
                                .setPet(allPet.get("Tom"))
                                .setVet(allVet.get("Graham Chapman")),
                        Visit.parseJson("{" +
                                        "\"date\":\"2021-04-21\"," +
                                        "\"text\":\"" + allLoremIpsum.get(1) + "\"" +
                                        "}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Graham Chapman")),
                        Visit.parseJson("{" +
                                        "\"date\":\"2021-04-22\"," +
                                        "\"text\":\"" + allLoremIpsum.get(2) + "\"" +
                                        "}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("John Cleese")),
                        Visit.parseJson("{" +
                                        "\"date\":\"2021-04-23\"," +
                                        "\"text\":\"" + allLoremIpsum.get(3) + "\"" +
                                        "}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Terry Gilliam")),
                        Visit.parseJson("{" +
                                        "\"date\":\"2021-04-24\"," +
                                        "\"text\":\"" + allLoremIpsum.get(4) + "\"" +
                                        "}")
                                .setPet(allPet.get("Odi"))
                                .setVet(allVet.get("Eric Idle")),
                        Visit.parseJson("{" +
                                        "\"date\":\"2021-04-24\"," +
                                        "\"text\":\"" + allLoremIpsum.get(5) + "\"" +
                                        "}")
                                .setPet(allPet.get("Fox"))
                                .setVet(allVet.get("Terry Jones"))
                )
                .map(visitRepository::save)
                .collect(Collectors.toList());
    }
}

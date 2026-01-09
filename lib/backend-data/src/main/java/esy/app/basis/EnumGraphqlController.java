package esy.app.basis;

import esy.api.basis.Enum;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EnumGraphqlController {

    private final EnumRepository enumRepository;

    @QueryMapping
    public List<Enum> allEnum(@Argument @NonNull final String art) {
        return enumRepository.findAll(art);
    }
}

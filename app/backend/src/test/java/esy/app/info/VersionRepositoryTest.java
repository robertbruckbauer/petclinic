package esy.app.info;

import esy.api.info.Version;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("fast")
@ExtendWith(MockitoExtension.class)
public class VersionRepositoryTest {

    @InjectMocks
    private VersionRepository versionRepository;

    @Mock
    private Resource resource;

    @ParameterizedTest
    @ValueSource(strings = {"0.0", "0.1", "1.0", "20.2"})
    void find(final String value) throws Exception {
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes(UTF_8)));
        final Version version = versionRepository.find();
        assertNotNull(version);
        assertEquals(value, version.toString());
        verify(resource).getInputStream();
        verifyNoMoreInteractions(resource);
    }

    @Test
    void findMissingResourceException() throws Exception {
        when(resource.getInputStream()).thenThrow(new FileNotFoundException());
        assertThrows(MissingResourceException.class, versionRepository::find);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "0", "1,0"})
    void findNoSuchElementException(final String value) throws Exception {
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes(UTF_8)));
        assertThrows(NoSuchElementException.class, versionRepository::find);
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", "0a", "1.a"})
    void findInputMismatchException(final String value) throws Exception {
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(value.getBytes(UTF_8)));
        assertThrows(InputMismatchException.class, versionRepository::find);
    }
}

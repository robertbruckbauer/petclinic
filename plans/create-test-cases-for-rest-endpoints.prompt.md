# Playwright API tests validate service classes for the Angular/Svelte UI components

## Goal 

Create tests for each REST enpoint used in the service classes in the angular client in @app/client-angular and svelte client in @app/client-svelte.
Verify that REST endpoints called from the service classes exist and return the expected results.
 - Tests document the expectation from the UI perspective. 
 - Tests ensure, that service classes work with parameters and JSON bodies created in the UI components.
 - Tests cover only the happy path for each REST endpoint because edge cases and failures are already covered in unit tests in @lib/backend-data.

No per-framework duplication is needed.

## Constraints

Use the test approach with playwright API tests in @app/server/src/test/java/esy/ServerRunnerTest.java.
Use nested classes for logical grouping of test cases by service class.
Unit tests should be easy to map back to the service class that calls the REST endpoint they verify.
Each individual test case should make it immediately clear which function and which REST endpoint it is exercising — without needing to read the test body first.
Avoid duplication in test cases for create, update, and delete REST endpoints; make one test case covering the entity lifecycle, e.g. `editOwner` for the owner entity.
Each nested class name maps a service class, e.g. `EnumServiceTests` for `EnumService` class.
Test method name maps a function in the service class that calls a REST endpoint, e.g. `EnumServiceTest#loadAllEnum` for `EnumService#loadAllEnum`.
Use test data from @app/server/src/main/java/esy/ServerTestset.java for loading test scenarios.

Use the helper class @app/server/src/test/java/esy/ServerApiAssertion.java for executing REST operations and asserting the response. 
Implement a helper method in @app/server/src/test/java/esy/ServerApiAssertion.java for each function of the service classes that calls a REST endpoint with the name of the function, e.g. `ServerApiAssertion#loadAllEnum` for `EnumService#loadAllEnum`.
Implement helper methods for load operations in @app/server/src/test/java/esy/ServerApiAssertion.java with arguments for url parameters (if exist) and query parameters (if exist) derived from real scenarios in the angular and svelte UI components.
Implement helper methods for create, mutate and update operations in @app/server/src/test/java/esy/ServerApiAssertion.java with arguments for url parameters (if exist) and the JSON body derived from real scenarios in the angular and svelte UI components.
Implement helper methods for delete operations in @app/server/src/test/java/esy/ServerApiAssertion.java with url parameter for the id of the entity to be deleted.
Choose parameter types in the helper methods that can directly be used in `RequestOptions` class the underlying playwright API.
Do not use `RequestOptions` class in @app/server/src/test/java/esy/ServerRunnerTest.java.

Keep existing tests `healthApi()` and `versionApi()` in @app/server/src/test/java/esy/ServerRunnerTest.java as smoke tests as-is and run first.
Remove existing test `restApi()` in @app/server/src/test/java/esy/ServerRunnerTest.java as it is redundant with the nested test cases.

## Example

```
public class ServerRunnerTest {

    static String randomName(@NonNull final String nameTemplate) {
        final var name = UUID.randomUUID().toString()
                .replace("-", "")
                .replaceAll("\\d", "");
        return nameTemplate.formatted(name);
    }

    @SuppressWarnings({"java:S2245", "java:S2119"}) // safe, no reuse
    static int randomInt(int min, int max) {
        // start from min to max inclusive
        return new Random().nextInt(max) + min;
    }

    @Nested
    @DisplayName("EnumService")
    class EnumServiceTests {

        @Test
        void loadAllEnum() {
            try (final var playwright = playwright()) {
                final var assertion = new ServerApiAssertion(playwright, toBackendUrl(""));
                final var art = "foo";
                final var code = randomInt(1000, 9000);
                final var name = randomName("%s");

                assertThat(assertion.loadAllEnum(art)
                        .stream()
                        .map(Enum::getName)
                        .toList())
                    .isEmpty();

                assertion.createEnum(art, """
                {
                    "art":"%1",
                    "code":"%2",
                    "name":"%3$s",
                    "text":"A %3$s"
                }
                """.formatted(art, code, name));

                try {
                    assertThat(assertion.loadAllEnum(art)
                            .stream()
                            .map(Enum::getName)
                            .toList())
                        .singleElement()
                        .satisfies(enum -> {
                            assertThat(enum.getArt()).isEqualTo(art);
                            assertThat(enum.getCode()).isEqualTo(code);
                            assertThat(enum.getName()).isEqualTo(name);
                            assertThat(enum.getText()).isEqualTo("A " + name);
                        });
                } finally {
                    assertion.removeEnum(art, code);
                }
            }
        }

        @Test
        void editEnum() {
            try (final var playwright = playwright()) {
                final var assertion = new ServerApiAssertion(playwright, toBackendUrl(""));
                final var art = "foo";
                final var code = randomInt(1000, 9000);
                final var name = randomName("%s");

                assertThat(assertion.createEnum("skill", """
                {
                    "art":"%1",
                    "code":"%2",
                    "name":"%3$s",
                    "text":"A %3$s"
                }
                """.formatted(art, code, name)))
                    .isNotNull()
                    .satisfies(enum -> {
                        assertThat(enum.getArt()).isEqualTo(art);
                        assertThat(enum.getCode()).isEqualTo(code);
                        assertThat(enum.getName()).isEqualTo(name);
                        assertThat(enum.getText()).isEqualTo("A " + name);
                    });
                
                try {
                    assertThat(assertion.updateEnum("skill", """
                    {
                        "art":"%1",
                        "code":"%2",
                        "name":"%3$s",
                        "text":"Another %3$s"
                    }
                    """.formatted(art, code, name)))
                        .isNotNull()
                        .satisfies(enum -> {
                            assertThat(enum.getArt()).isEqualTo(art);
                            assertThat(enum.getCode()).isEqualTo(code);
                            assertThat(enum.getName()).isEqualTo(name);
                            assertThat(enum.getText()).isEqualTo("Another " + name);
                        });
                } finally {
                    assertThat(assertion.removeEnum("skill", """
                    {
                        "art":"%1",
                        "code":"%2",
                        "name":"%3$s",
                        "text":"Another %3$s"
                    }
                    """.formatted(art, code, name)))
                        .isNotNull()
                        .satisfies(enum -> {
                            assertThat(enum.getArt()).isEqualTo(art);
                            assertThat(enum.getCode()).isEqualTo(code);
                            assertThat(enum.getName()).isEqualTo(name);
                            assertThat(enum.getText()).isEqualTo("Another " + name);
                        });
                }
            }
        }
    }
}
```

## Verification

All tests pass successfully.

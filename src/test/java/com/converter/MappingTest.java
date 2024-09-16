package com.converter;

import com.converter.config.AppConfig;
import com.converter.mapper.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappingTest {
    static Mapper mapper;

    @BeforeAll
    public static void setUp() {
        AppConfig appConfig = new AppConfig();
        mapper = new Mapper(appConfig.mapperConfig("src/main/resources/maps.json", appConfig.objectMapper()));
    }

    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    void test(String source, String expected, String message) {
        String destination = "src/test/resources/actual/dest22.html";
        mapper.convert(source, destination);
        File expectedFile = new File(expected);
        File actualFile = new File(destination);
        try (FileReader fileReader = new FileReader(expectedFile);
             BufferedReader expectedBufferedReader = new BufferedReader(fileReader);
             FileReader actualReader = new FileReader(actualFile);
             BufferedReader actualFileReader = new BufferedReader(actualReader)) {
            String expectedLine;
            String actualLine;
            while ((actualLine = actualFileReader.readLine()) != null | (expectedLine = expectedBufferedReader.readLine()) != null) {
                assertEquals(expectedLine, actualLine, message);
            }

        } catch (IOException io) {
            throw new AssertionError(io);
        }
    }

    private static Stream<Arguments> provideStringsForIsBlank() {
        return Stream.of(
                Arguments.of("src/test/resources/paragraphAndHeadings.md", "src/test/resources/paragraphAndHeadings.html", "Invalid headings and paragraph mapping"),
                Arguments.of("src/test/resources/paragraph.md", "src/test/resources/paragraph.html", "Invalid links mapping"),
                Arguments.of("src/test/resources/links.md", "src/test/resources/links.html", "Invalid links mapping"),
                Arguments.of("src/test/resources/multiHeading.md", "src/test/resources/multiHeading.html", "Invalid heading mapping"),
                Arguments.of("src/test/resources/singleHeading.md", "src/test/resources/singleHeading.html", "Invalid heading mapping")
        );
    }
}

package ru.tinkoff.tma.caen.el;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class EvaluatorTests {

    static Stream<Arguments> provideArgs() {
        return Stream.of(
            Arguments.of("#value1.equals(#value2) && !#value3.startsWith(\"and\")",
                         Map.of("value1", 123, "value2", 123, "value3", "Andrey"),
                         true),
            Arguments.of("#value1.equals(#value2) && (#value3.startsWith(\"and\") || \"Hello\".startsWith(\"H\"))",
                         Map.of("value1", 123, "value2", 123, "value3", "Andrey"),
                         true),
            Arguments.of("#value1.equals(#value2) && (#value3.startsWith(\"and\") || !\"Hello\".startsWith(\"H\"))",
                         Map.of("value1", 123, "value2", 123, "value3", "Andrey"),
                         false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    public void testEvaluate(String expression, Map<String, Object> context, Boolean expected) {
        var result = Evaluator.evaluate(expression, context);
        Assertions.assertEquals(expected, result);
    }
}

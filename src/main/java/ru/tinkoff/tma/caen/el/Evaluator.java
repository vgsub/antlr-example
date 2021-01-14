package ru.tinkoff.tma.caen.el;

import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Evaluator {

    public static boolean evaluate(String expression, Map<String, Object> context) {
        var lexer = new ExampleLexer(CharStreams.fromString(expression));
        var parser = new ExampleParser(new CommonTokenStream(lexer));

        var visitor = new Visitor(context);
        var result = visitor.visit(parser.parse());

        return result.asBoolean();
    }
}

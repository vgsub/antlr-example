package ru.tinkoff.tma.caen.el;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Visitor extends ExampleBaseVisitor<Result> {

    private final Map<String, Object> evaluationContext;

    public Visitor(Map<String, Object> evaluationContext) {
        this.evaluationContext = evaluationContext;
    }

    @Override
    public Result visitParExpression(ExampleParser.ParExpressionContext ctx) {
        return this.visit(ctx.expression());
    }

    @Override
    public Result visitOrExpression(ExampleParser.OrExpressionContext ctx) {
        var left = this.visit(ctx.expression(0));
        var right = this.visit(ctx.expression(1));

        return Result.of(left.asBoolean() || right.asBoolean());
    }

    @Override
    public Result visitAndExpression(ExampleParser.AndExpressionContext ctx) {
        var left = this.visit(ctx.expression(0));
        var right = this.visit(ctx.expression(1));

        return Result.of(left.asBoolean() && right.asBoolean());
    }

    @Override
    public Result visitNotExpression(ExampleParser.NotExpressionContext ctx) {
        return Result.of(!this.visit(ctx.expression()).asBoolean());
    }

    @Override
    public Result visitMethod(ExampleParser.MethodContext ctx) {

        Object methodObject = switch (ctx.object.getType()) {
            case ExampleParser.STRING -> ctx.object.getText().substring(1, ctx.object.getText().length() - 1);
            case ExampleParser.PLACEHOLDER -> getPlaceholderValue(ctx.object.getText());
            default -> throw new RuntimeException("Invalid method object type " + ctx.object.getType());
        };

        List<Object> args = ctx.methodArguments().STRING().stream()
            .map(node -> node.getText().substring(1, node.getText().length() - 1))
            .collect(Collectors.toList());

        args.addAll(ctx.methodArguments().INT().stream()
                        .map(node -> Integer.valueOf(node.getText()))
                        .collect(Collectors.toList()));

        args.addAll(ctx.methodArguments().PLACEHOLDER().stream()
                        .map(node -> getPlaceholderValue(node.getText()))
                        .collect(Collectors.toList()));

        var methodName = ctx.methodName().TEXT().getText();

        var methods = Arrays.stream(methodObject.getClass().getDeclaredMethods())
            .filter(m -> m.getName().equals(methodName) && m.getParameterCount() == args.size())
            .collect(Collectors.toList());

        if (methods.isEmpty()) {
            throw new RuntimeException(
                String.format("Unknown method %s for the %s", methodName, methodObject.getClass()));
        }

        if (methods.size() > 1) {
            throw new RuntimeException(
                String.format("Declared method %s for the %s is ambiguous", methodName, methodObject.getClass()));
        }

        var method = methods.get(0);

        Object result;
        try {
            result = method.invoke(methodObject, args.toArray(new Object[args.size()]));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Error invoking a method " + methodName, e);
        }

        return Result.of(result);
    }

    private Object getPlaceholderValue(String name) {
        return evaluationContext.get(name.substring(1));
    }
}

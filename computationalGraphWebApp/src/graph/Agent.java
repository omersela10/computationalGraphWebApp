package graph;

/// Advanced Programming exercise 1
/// Student Name: Ahigad Genish
/// ID : 31628022

import java.util.*;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Agent {
    String getName();
    void reset();
    void callback(String topic, Message msg);
    void close();

    public static Object getOperator(String operatorName) {
        switch (operatorName.toLowerCase()) {
            case "plus":
                return (BiFunction<Double, Double, Double>) (x, y) -> x + y;
            case "minus":
                return (BiFunction<Double, Double, Double>) (x, y) -> x - y;
            case "mul":
                return (BiFunction<Double, Double, Double>) (x, y) -> x * y;
            case "div":
                return (BiFunction<Double, Double, Double>) (x, y) -> x / y;
            case "power":
                return (BiFunction<Double, Double, Double>) (x, y) -> Math.pow(x, y);
            case "inc":
                return (Function<Double, Double>) (x) -> x + 1;
            case "dec":
                return (Function<Double, Double>) (x) -> x - 1;
            case "squareroot":
                return (Function<Double, Double>) (x) -> Math.sqrt(x);
            case "ln":
                return (Function<Double, Double>) (x) -> Math.log(x);
            case "log10":
                return (Function<Double, Double>) (x) -> Math.log10(x);
            case "exponent":
                return (Function<Double, Double>) (x) -> Math.exp(x);
            default:
               return null;
        }
    }
}

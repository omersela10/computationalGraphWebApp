package configs;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
public class ExpressionParser {

    public static List<String> parseExpression(String expression) {
        List<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        int counter = 1;

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (c == '(') {
                stack.push("(");
            } else if (c == ')') {
                List<String> temp = new ArrayList<>();
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    temp.add(0, stack.pop());
                }
                stack.pop(); // Remove '('
                String newVar = "TempVar" + counter++;
                result.addAll(temp);
                result.add(newVar);
                stack.push(newVar);
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                stack.push(String.valueOf(c));
            } else if (Character.isLetter(c)) {
                stack.push(String.valueOf(c));
            }
        }

        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }

    public static String convertToConfiguration(List<String> parsedExpression) {
        StringBuilder config = new StringBuilder();
        Stack<String> stack = new Stack<>();

        for (String token : parsedExpression) {
            if (token.matches("[A-Za-z]")) {
                stack.push(token);
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^")) {
                if (stack.size() < 2) {
                    throw new RuntimeException("Insufficient operands for operator: " + token);
                }
                try {
                    String b = stack.pop();
                    String a = stack.pop();
                    String newVar = "(" + a + token + b + ")";
                    stack.push(newVar);
                    config.append("BinOpAgent.")
                            .append(token.equals("+") ? "Plus" :
                                    token.equals("-") ? "Minus" :
                                            token.equals("*") ? "Mul" :
                                                    token.equals("/") ? "Div" : "Power")
                            .append("\n")
                            .append(a).append(",").append(b).append("\n")
                            .append(newVar).append("\n");
                } catch (EmptyStackException e) {
                    System.err.println("Error: Stack is empty when processing token: " + token);
                    throw e; // or handle it according to your needs
                }
            } else if (token.equals("sqrt") || token.equals("ln") || token.equals("log10") || token.equals("exp") || token.equals("inc") || token.equals("dec")) {
                if (stack.size() < 1) {
                    throw new RuntimeException("Insufficient operands for unary operator: " + token);
                }
                try {
                    String a = stack.pop();
                    String newVar = token.equals("sqrt") ? "sqrt(" + a + ")" :
                            token.equals("ln") ? "ln(" + a + ")" :
                                    token.equals("log10") ? "log10(" + a + ")" :
                                            token.equals("exp") ? "exp(" + a + ")" :
                                                    token.equals("inc") ? "(" + a + "+1)" :
                                                            "(" + a + "-1)";
                    stack.push(newVar);
                    config.append(token.equals("sqrt") ? "UnOpAgent.SquareRoot" :
                                    token.equals("ln") ? "UnOpAgent.ln" :
                                            token.equals("log10") ? "UnOpAgent.log10" :
                                                    token.equals("exp") ? "UnOpAgent.Exponent" :
                                                            token.equals("inc") ? "UnOpAgent.Inc" : "UnOpAgent.Dec")
                            .append("\n")
                            .append(a).append("\n")
                            .append(newVar).append("\n");
                } catch (EmptyStackException e) {
                    System.err.println("Error: Stack is empty when processing token: " + token);
                    throw e; // or handle it according to your needs
                }
            }
        }

        return config.toString();
    }
}
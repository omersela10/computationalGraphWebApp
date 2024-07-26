package configs;
import java.util.*;

// ExpressionParser configurator

public class ExpressionParser {

    // Parse math expression by shunting yard algorithm
    public static List<String> parseExpression(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();

        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/^()", true);

        while (tokenizer.hasMoreTokens() == true) {
            // Get current token
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty() == true)
                continue;

            if (isNumber(token) == true || isVariable(token) == true) { // If it's number or variable name
                output.add(token);
            } else if (isFunction(token) == true) { // If it's function
                operatorStack.push(token);
            } else if (token.equals("(") == true) { // If it's open parenthesis
                operatorStack.push(token);
            } else if (token.equals(")") == true) { // If it's close parenthesis
                while (operatorStack.isEmpty() == false && operatorStack.peek().equals("(") == false) {
                    output.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty() == false && operatorStack.peek().equals("(") == true) {
                    operatorStack.pop();
                }
                if (operatorStack.isEmpty()  == false && isFunction(operatorStack.peek())== true) {
                    output.add(operatorStack.pop());
                }
            } else if (isOperator(token) == true) {
                while (operatorStack.isEmpty() == false && precedence(operatorStack.peek()) >= precedence(token)) {
                    output.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }

        while (operatorStack.isEmpty() == false) {
            output.add(operatorStack.pop());
        }

        return output;
    }

    // Check if given string is numeric
    private static boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Check if given string is function (Unary)
    private static boolean isFunction(String token) {
        return token.matches("exp|ln|log10|sqrt|inc|dec");
    }

    // Check if given string is operator (Binary)
    private static boolean isOperator(String token) {
        return "+-*/^".contains(token);
    }

    // Return operator precedence
    private static int precedence(String op) {
        switch (op) {
            case "+":
            case "-":
                return 1;
            case "*":
            case "/":
                return 2;
            case "^":
                return 3;
            default:
                return -1;
        }
    }


    // Convert expression to configuration file for create graph
    public static String convertToConfiguration(List<String> parsedExpression) {
        StringBuilder config = new StringBuilder();
        Stack<String> stack = new Stack<>();

        for (String token : parsedExpression) {
            if (isVariable(token) == true) {
                stack.push(token);
            } else if (isBinaryOperator(token) == true) {
                if (stack.size() < 2) {
                    throw new RuntimeException("Insufficient operands for operator: " + token);
                }
                String b = stack.pop();
                String a = stack.pop();
                String newVar = "(" + a + token + b + ")";
                stack.push(newVar);
                config.append(getBinaryOperatorName(token)).append("\n")
                        .append(a).append(",").append(b).append("\n")
                        .append(newVar).append("\n");
            } else if (isUnaryOperator(token) == true) {
                if (stack.isEmpty()) {
                    throw new RuntimeException("Insufficient operands for unary operator: " + token);
                }
                String a = stack.pop();
                String newVar = token + "(" + a + ")";
                stack.push(newVar);
                config.append(getUnaryOperatorName(token)).append("\n")
                        .append(a).append("\n")
                        .append(newVar).append("\n");
            } else {
                throw new RuntimeException("Unknown token: " + token);
            }
        }

        if (stack.size() != 1) {
            throw new RuntimeException("Invalid expression");
        }

        return config.toString();
    }

    // Check if given string is variable
    private static boolean isVariable(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*") && !isUnaryOperator(token);
    }

    // Check if given string is binary operator
    private static boolean isBinaryOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/") || token.equals("^");
    }

    // Check if given string is unary operator
    private static boolean isUnaryOperator(String token) {
        return token.equals("sqrt") || token.equals("ln") || token.equals("log10") || token.equals("exp") || token.equals("inc") || token.equals("dec");
    }

    // Get binary operator name of given string token
    private static String getBinaryOperatorName(String token) {
        switch (token) {
            case "+": return "BinOpAgent.Plus";
            case "-": return "BinOpAgent.Minus";
            case "*": return "BinOpAgent.Mul";
            case "/": return "BinOpAgent.Div";
            case "^": return "BinOpAgent.Power";
            default: throw new IllegalArgumentException("Unknown binary operator: " + token);
        }
    }
    // Get unary operator name of given string token
    private static String getUnaryOperatorName(String token) {
        switch (token) {
            case "sqrt": return "UnOpAgent.SquareRoot";
            case "ln": return "UnOpAgent.ln";
            case "log10": return "UnOpAgent.log10";
            case "exp": return "UnOpAgent.Exponent";
            case "inc": return "UnOpAgent.Inc";
            case "dec": return "UnOpAgent.Dec";
            default: throw new IllegalArgumentException("Unknown unary operator: " + token);
        }
    }
}
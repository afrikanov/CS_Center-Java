package ru.compscicenter.java2019.calculator;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.abs;
import static java.lang.Math.pow;

public class Calculates implements Calculator {

    private final int moveIfFunctionIsFound = 3;

    @Override
    public double calculate(final String myExpression) {
        String expression = myExpression.toUpperCase();
        expression = conversationToCorrectCondition(expression);
        return minusPlus(expression).answer;
    }

    private String conversationToCorrectCondition(final String expression) {
        StringBuilder correctExpression = new StringBuilder();
        for (int i = 0; i < expression.length(); ++i) {
            if (expression.charAt(i) != ' ') {
                correctExpression.append(expression.charAt(i));
            }
        }
        correctExpression = new StringBuilder("(" + correctExpression + ")");
        for (int i = correctExpression.length() - 1; i >= 0; --i) {
            if (correctExpression.charAt(i) == '^') {
                int k = i + 1;
                while (correctExpression.charAt(k) == '+' || correctExpression.charAt(k) == '-') {
                    ++k;
                }
                char next = correctExpression.charAt(k);
                if (Character.isDigit(next)) {
                    correctExpression = makeBracketsifDigit(k - 1, correctExpression);
                } else if (Character.isLetter(next) || next == '(') {
                    int j = k + moveIfFunctionIsFound;
                    if (next == '(') {
                        j = k;
                    }
                    correctExpression = makeBracketsIfLetter(j, correctExpression);
                }
                char prev = correctExpression.charAt(i - 1);
                if (prev == ')') {
                    int openedBrackets = 0;
                    for (int j = i - 1; j >= 0; --j) {
                        if (correctExpression.charAt(j) == '(') {
                            openedBrackets++;
                        } else if (correctExpression.charAt(j) == ')') {
                            openedBrackets--;
                        }
                        if (openedBrackets == 0) {
                            if (Character.isLetter(correctExpression.charAt(j - 1))) {
                                j -= moveIfFunctionIsFound;
                            }
                            correctExpression = new StringBuilder(correctExpression.substring(0, j)
                                    + "("
                                    + correctExpression.substring(j));
                            break;
                        }
                    }
                } else {
                    int j = i - 1;
                    while (j >= 0 && (Character.isDigit(correctExpression.charAt(j))
                            || correctExpression.charAt(j) == '.'
                            || correctExpression.charAt(j) == 'E'
                            || correctExpression.charAt(j) == '+')) {
                        --j;
                    }
                    correctExpression = new StringBuilder(correctExpression.substring(0, j + 1)
                            + "("
                            + correctExpression.substring(j + 1));
                }
            }
        }
        for (int i = 1; i < correctExpression.length(); ++i) {
            char now = correctExpression.charAt(i);
            char prev = correctExpression.charAt(i - 1);
            if (now == '+' || now == '-') {
                if (prev == '(' || prev == '+' || prev == '-' || prev == '*' || prev == '/' || prev == '^') {
                    correctExpression = new StringBuilder(correctExpression.substring(0, i)
                            + "(0"
                            + correctExpression.substring(i));
                    i += 2;
                    int k = i;
                    while (k < correctExpression.length()
                            && (correctExpression.charAt(k) == '+' || correctExpression.charAt(k) == '-')) {
                        ++k;
                    }
                    --k;
                    if (Character.isDigit(correctExpression.charAt(k + 1))) {
                        correctExpression = makeBracketsifDigit(k, correctExpression);
                    } else if (Character.isLetter(correctExpression.charAt(k + 1))
                            || correctExpression.charAt(k + 1) == '(') {
                        int j = k + moveIfFunctionIsFound + 1;
                        if (correctExpression.charAt(k + 1) == '(') {
                            j = k + 1;
                        }
                        correctExpression = makeBracketsIfLetter(j, correctExpression);
                    }
                }
            }
        }
        return correctExpression.toString();
    }

    private StringBuilder makeBracketsIfLetter(final int i, final StringBuilder myExpression) {
        StringBuilder correctExpression = new StringBuilder(myExpression);
        int openedBrackets = 0;
        int j = i;
        for (; j < correctExpression.length(); ++j) {
            if (correctExpression.charAt(j) == '(') {
                openedBrackets++;
            } else if (correctExpression.charAt(j) == ')') {
                openedBrackets--;
            }
            if (openedBrackets == 0) {
                correctExpression = new StringBuilder(correctExpression.substring(0, j)
                        + ")"
                        + correctExpression.substring(j));
                break;
            }
        }
        return correctExpression;
    }

    private StringBuilder makeBracketsifDigit(final int i, final StringBuilder myExpression) {
        boolean wasSign = false;
        boolean wasE = false;
        boolean wasPoint = false;
        int k = i;
        StringBuilder correctExpression = new StringBuilder(myExpression);
        for (int j = k + 1; j < correctExpression.length(); ++j) {
            if (!Character.isDigit(correctExpression.charAt(j))) {
                char currentChar = correctExpression.charAt(j);
                if ((currentChar == '+' || currentChar == '-') && !wasSign) {
                    wasSign = true;
                    continue;
                }
                if (currentChar == 'E' && !wasE) {
                    wasE = true;
                    continue;
                }
                if (currentChar == '.' && !wasPoint) {
                    wasPoint = true;
                    continue;
                }
                correctExpression = new StringBuilder(correctExpression.substring(0, j)
                        + ")"
                        + correctExpression.substring(j));
                break;
            }
        }
        return correctExpression;
    }

    private Result minusPlus(final String expression) {
        Result current = mulDivDeg(expression);
        while (current.lost.length() > 0) {
            char sign = current.lost.charAt(0);
            if (!(sign == '+' || sign == '-')) {
                break;
            }
            String nextExpression = current.lost.substring(1);
            Result step = mulDivDeg(nextExpression);
            if (sign == '+') {
                current.answer += step.answer;
            } else {
                current.answer -= step.answer;
            }
            current.lost = step.lost;
        }
        return current;
    }

    private Result mulDivDeg(final String expression) {
        Result current = brackets(expression);
        while (current.lost.length() > 0) {
            char sign = current.lost.charAt(0);
            if (!(sign == '*' || sign == '/' || sign == '^')) {
                break;
            }
            String nextExpression = current.lost.substring(1);
            Result step = brackets(nextExpression);
            if (sign == '*') {
                current.answer *= step.answer;
            } else if (sign == '/') {
                current.answer /= step.answer;
            } else {
                current.answer = pow(current.answer, step.answer);
            }
            current.lost = step.lost;
        }
        return current;
    }

    private Result brackets(final String expression) {
        if (expression.charAt(0) == '(') {
            Result stepResult = minusPlus(expression.substring(1));
            if (stepResult.lost.length() != 0 && stepResult.lost.charAt(0) == ')') {
                stepResult.lost = stepResult.lost.substring(1);
            }
            return stepResult;
        }
        return functions(expression);
    }

    private Pair<Boolean, String> negativeExpression(final String myExpression) {
        String expression = myExpression;
        boolean isNegative = false;
        if (expression.charAt(0) == '-') {
            isNegative = true;
            expression = expression.substring(1);
        } else if (expression.charAt(0) == '+') {
            expression = expression.substring(1);
        }
        return new Pair<>(isNegative, expression);
    }

    private Result functions(final String myExpression) {
        String expression = myExpression;
        StringBuilder function = new StringBuilder();
        int i = 0;
        Pair<Boolean, String> negs = negativeExpression(expression);
        boolean isNegative = negs.first;
        expression = negs.second;
        while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
            function.append(expression.charAt(i));
            ++i;
        }
        if (function.length() > 0) {
            if (i < expression.length() && expression.charAt(i) == '(') {
                Result result = brackets(expression.substring(i));
                result = makeFunction(function.toString(), result);
                if (isNegative) {
                    result.answer *= -1;
                }
                return result;
            }
        }
        if (isNegative) {
            return getNumber("-" + expression);
        } else {
            return getNumber(expression);
        }
    }

    private Result getNumber(final String myExpression) {
        String expression = myExpression;
        Pair<Boolean, String> negs = negativeExpression(expression);
        boolean isNegative = negs.first;
        expression = negs.second;
        int i = 0;
        boolean wasE = false;
        while (i < expression.length() && (Character.isDigit(expression.charAt(i))
                || expression.charAt(i) == 'E' || expression.charAt(i) == '.'
                || (wasE && expression.charAt(i) == '+')
                || (wasE && expression.charAt(i) == '-'))) {
            if (expression.charAt(i) == 'E') {
                wasE = true;
            }
            if (expression.charAt(i) == '+' || expression.charAt(i) == '-') {
                wasE = false;
            }
            ++i;
        }
        String newExpression = expression.substring(0, i);
        if (newExpression.charAt(newExpression.length() - 1) == 'E') {
            newExpression += "+0";
        }
        double doubleNumber = Double.valueOf(newExpression);
        if (isNegative) {
            doubleNumber *= -1;
        }
        return new Result(doubleNumber, expression.substring(i));
    }

    private Result makeFunction(final String function, final Result result) {
        switch (function) {
            case "SIN":
                return new Result(sin(result.answer), result.lost);
            case "COS":
                return new Result(cos(result.answer), result.lost);
            default:
                return new Result(abs(result.answer), result.lost);
        }
    }

    private class Result {

        private double answer;
        private String lost;

        Result(final double answer, final String lost) {
            this.answer = answer;
            this.lost = lost;
        }
    }

    private class Pair<T, D> {
        private T first;
        private D second;

        Pair(final T first, final D second) {
            this.first = first;
            this.second = second;
        }
    }
}

package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for an expression of the formula language.
 * <p>
 * The language is defined as follow (Extended Backus–Naur form):
 * <pre>
 * expression = comparison
 * comparison = addition {("<" | "<=" | "=" | "<>" | "=>" | ">") addition}
 * addition = multiplication {("+" | "-") multiplication}
 * multiplication = negation {("*" | "/") negation}
 * negation = ["-"] power
 * power = atom ["^" negation]
 * atom = variable | number | function | formula | "(" expression ")"
 * variable = identifier
 * function = identifier "(" {expression ";"} ")"
 * formula = "=" identifier
 * </pre>
 *
 * @author sedoe
 */
public class Parser {
    /**
     * The constant EOF.
     */
    private static final char EOF = (char) -1;

    /**
     * The Str.
     */
    private String str;
    /**
     * The Pos.
     */
    private int pos;
    /**
     * The Last expression.
     */
    private Expression lastExpression;

    /**
     * Parse expression.
     *
     * @param str the str
     * @return the expression
     */
    public Expression parse(String str) {
        this.str = str;
        pos = 0;

        consumeBlanks();

        if (next() == '=') {
            consume();
            consumeBlanks();
        }

        expression();

        return lastExpression;
    }

    /**
     * Gets expression.
     *
     * @return the expression
     */
    public Expression getExpression() {
        return lastExpression;
    }

    /**
     * Expression expression.
     *
     * @return the expression
     */
    protected Expression expression() {
        lastExpression = null;
        while (next() != EOF) {
            Expression e = lastExpression;
            boolExpression();
            if (e == lastExpression) {
                break;
            }
        }

        return lastExpression;
    }

    /**
     * Bool expression.
     */
    protected void boolExpression() {
        plusMinusExpression();

        Expression left = lastExpression;
        if (next() == '<') {
            consume();
            BoolExpression.Operator operator = BoolExpression.Operator.L;
            if (next() == '=') {
                consume();
                operator = BoolExpression.Operator.LE;
            } else if (next() == '>') {
                consume();
                operator = BoolExpression.Operator.NE;
            }
            consumeBlanks();
            plusMinusExpression();
            lastExpression = new BoolExpression(left, operator, lastExpression);
        } else if (next() == '>') {
            consume();
            BoolExpression.Operator operator = BoolExpression.Operator.G;
            if (next() == '=') {
                consume();
                operator = BoolExpression.Operator.GE;
            }
            consumeBlanks();
            plusMinusExpression();
            lastExpression = new BoolExpression(left, operator, lastExpression);
        } else if (next() == '=') {
            consume();
            consumeBlanks();
            plusMinusExpression();
            lastExpression = new BoolExpression(left, BoolExpression.Operator.E, lastExpression);
        }
    }

    /**
     * Plus minus expression.
     */
    protected void plusMinusExpression() {
        mulDivExpression();

        Expression left = lastExpression;
        if (next() == '+') {
            // consume '+'
            consume();
            consumeBlanks();
            mulDivExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.ADD, lastExpression);
        } else if (next() == '-') {
            // consume '-'
            consume();
            consumeBlanks();
            mulDivExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.SUB, lastExpression);
        }
    }

    /**
     * Mul div expression.
     */
    protected void mulDivExpression() {
        minusExpression();

        Expression left = lastExpression;
        if (next() == '*') {
            // consume '*'
            consume();
            consumeBlanks();
            minusExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.MUL, lastExpression);
        } else if (next() == '/') {
            // consume '/'
            consume();
            consumeBlanks();
            minusExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.DIV, lastExpression);
        }
    }

    /**
     * Minus expression.
     */
    protected void minusExpression() {
        if (next() == '-') {
            consume();
            consumeBlanks();
            powerExpression();
            lastExpression = new MinusExpression(lastExpression);
            return;
        } else if (next() == '+') {
            consume();
            consumeBlanks();
        }
        powerExpression();
    }

    /**
     * Power expression.
     */
    protected void powerExpression() {
        valueExpression();

        if (next() == '^') {
            Expression left = lastExpression;
            // consume '^'
            consume();
            consumeBlanks();
            minusExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.POW, lastExpression);
        }
    }

    /**
     * Value expression.
     */
    protected void valueExpression() {
        bracketExpression();

        StringBuilder sb = new StringBuilder();

        // text
        if (next() == '\"') {
            consume();
            while (next() != '"' || next(1) == '"') {
                if (next() == '"' && next(1) == '"') {
                    // handle double " character
                    sb.append('"');
                    consume();
                    consume();
                } else {
                    sb.append(next());
                    consume();
                }
            }
            // consume end "
            consume();
            consumeBlanks();
            lastExpression = new TextExpression(sb.toString());

            // number
        } else if (isNumber(next()) || next() == '.') {

            boolean dotSeen = false;
            if (next() == '.') {
                dotSeen = true;
                consume();
            }

            while (isNumber(next()) || next() == '.') {
                if (dotSeen && next() == '.') {
                    // ignore multiple dots
                    consume();
                    continue;
                }

                sb.append(next());
                if (next() == '.') {
                    dotSeen = true;
                }
                consume();
            }

            consumeBlanks();

            lastExpression = new NumberExpression(Double.parseDouble(sb.toString()));

            // variable
        } else if (isLetter(next())) { // first name character may be letter only

            // process first character of the name
            sb.append(next());
            consume();

            // subsequent characters may be letters and numbers
            while (isLetter(next()) || isNumber(next()) || next() == ':') {
                sb.append(next());
                consume();
            }

            consumeBlanks();

            if (next() == '(') {
                // if '(' character recognized the name is the function name
                // --> process the rest of the function
                functionExpression(sb.toString());
            } else {
                lastExpression = new VariableExpression(sb.toString());
            }
        } else if (next() == '=') {
            // consume '='
            consume();
            consumeBlanks();

            // subsequent characters may be letters and numbers
            while (isLetter(next()) || isNumber(next())) {
                sb.append(next());
                consume();
            }

            consumeBlanks();

            lastExpression = new FormulaExpression(sb.toString());
        }
    }

    /**
     * Bracket expression.
     */
    protected void bracketExpression() {
        if (next() == '(') {
            // consume ')'
            consume();
            consumeBlanks();
            expression();
            consumeBlanks();
            // consume '('
            consume();
            consumeBlanks();
            lastExpression = new BracketExpression(lastExpression);
        }
    }

    /**
     * Function expression.
     *
     * @param name the name
     */
    protected void functionExpression(String name) {
        // consume '('
        consume();
        consumeBlanks();
        List<Expression> expressions = new ArrayList<>();
        while (true) {
            expression();
            if (lastExpression != null) {
                expressions.add(lastExpression);
            }
            if (next() != ';') {
                break;
            }
            // consume ';'
            consume();
            consumeBlanks();
        }

        // consume ')'
        consume();
        consumeBlanks();

        lastExpression = new FunctionExpression(name, expressions);
    }

    /**
     * Is number boolean.
     *
     * @param c the c
     * @return the boolean
     */
    private boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Is letter boolean.
     *
     * @param c the c
     * @return the boolean
     */
    private static boolean isLetter(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    /**
     * Next char.
     *
     * @return the char
     */
    private char next() {
        return next(0);
    }

    /**
     * Next char.
     *
     * @param lookahead the lookahead
     * @return the char
     */
    private char next(int lookahead) {
        if (pos + lookahead >= str.length()) {
            return EOF;
        }
        return str.charAt(pos + lookahead);
    }

    /**
     * Consume.
     */
    private void consume() {
        pos++;
    }

    /**
     * Consume blanks.
     */
    private void consumeBlanks() {
        while (Character.isSpaceChar(next())) {
            pos++;
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {


        System.out.println(new Parser().parse("1=1==fff"));
        System.out.println(new Parser().parse("1=1"));
        System.out.println(new Parser().parse("afas <> 123"));
        System.out.println(new Parser().parse("1--2"));
        System.out.println(new Parser().parse("1++2"));
        System.out.println(new Parser().parse("f(x())"));
        System.out.println(new Parser().parse(" = ( 123.34 + fff ) * 12 ^ 122 - - fffffff ( 123 ; -ffff ; \"xx\"\"x\""));
        System.out.println(new Parser().parse("123"));
        System.out.println(new Parser().parse("123.45"));
        System.out.println(new Parser().parse("+123.45"));
        System.out.println(new Parser().parse("-123.45"));
        System.out.println(new Parser().parse("123^45"));

//        for (int i = 'A'; i <= 'z'; i++) {
//            System.out.println(i + "\t" + (char) i + "\t" + isLetter((char) i) + "\t" + Character.isLetter((char) i));
//        }
//        System.out.println((int) 'a');
//        System.out.println((int) 'z');
//        System.out.println((int) 'A');
//        System.out.println((int) 'Z');
    }
}

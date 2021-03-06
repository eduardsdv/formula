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
 * multiplication = power {("*" | "/") power}
 * power = negation ["^" negation]
 * negation = ["-"] atom
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
        this.lastExpression = null;
        this.str = str;
        pos = 0;

        consumeBlanks();

        if (next() == '=') {
            consume();
            consumeBlanks();
        }

        expression();

        lastExpression = processPrecedence(lastExpression);

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
            plusMinusExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.ADD, lastExpression);
        } else if (next() == '-') {
            // consume '-'
            consume();
            consumeBlanks();
            plusMinusExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.SUB, lastExpression);
        }
    }

    /**
     * Mul div expression.
     */
    protected void mulDivExpression() {
        powerExpression();

        Expression left = lastExpression;
        if (next() == '*') {
            // consume '*'
            consume();
            consumeBlanks();
            mulDivExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.MUL, lastExpression);
        } else if (next() == '/') {
            // consume '/'
            consume();
            consumeBlanks();
            mulDivExpression();
            lastExpression = new MathExpression(left, MathExpression.Operator.DIV, lastExpression);
        }
    }

    /**
     * Power expression.
     */
    protected void powerExpression() {
        minusExpression();

        if (next() == '^') {
            Expression left = lastExpression;
            // consume '^'
            consume();
            consumeBlanks();
            expression();
            lastExpression = new MathExpression(left, MathExpression.Operator.POW, lastExpression);
        }
    }

    /**
     * Minus expression.
     */
    protected void minusExpression() {
        if (!(lastExpression instanceof MathExpression)) {
            if (next() == '-') {
                consume();
                consumeBlanks();
                minusExpression();
                lastExpression = new MinusExpression(lastExpression);
                return;
            } else if (next() == '+') {
                consume();
                consumeBlanks();
            }
        }
        valueExpression();
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
            // consume '('
            consume();
            consumeBlanks();
            expression();
            consumeBlanks();
            // consume ')'
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

    protected Expression processPrecedence(Expression e) {
        if (e instanceof MathExpression) {
            return processPrecedence((MathExpression) e);
        } else if (e instanceof BracketExpression) {
            return new BracketExpression(processPrecedence(((BracketExpression) e).getExpression()));
        } else if(e instanceof MinusExpression) {
            return new MinusExpression(processPrecedence(((MinusExpression) e).getExpression()));
        } else if(e instanceof BoolExpression) {
            BoolExpression b = (BoolExpression)e;
            return new BoolExpression(processPrecedence(b.getLeft()), b.getOperator(), processPrecedence(b.getRight()));
        } else if(e instanceof FunctionExpression) {
            FunctionExpression f = (FunctionExpression)e;
            List<Expression> params = new ArrayList<>();
            for (Expression param : f.getParams()) {
                params.add(processPrecedence(param));
            }
            return new FunctionExpression(f.getName(), params);
        } else {
            return e;
        }
    }

    protected MathExpression processPrecedence(MathExpression e) {
        // 1 - 2 - 3 = -4
        // The parser produces this expression tree:
        //     /\  which is equal to 1 - (2 - 3) = 0
        //    1 /\
        //     2  3
        // If the precedence of the operators on the right side greater or equal to the precedence of the left side,
        // this method transforms it to:
        //      /\    which is equal to (1 - 2) - 3 = -4
        //     /\ 3
        //    1  2
        if (e.getRight() instanceof MathExpression) {
            MathExpression right = (MathExpression) e.getRight();
            if (right.getOperator().getPrecedence() <= e.getOperator().getPrecedence()) {
                MathExpression tmp = new MathExpression(processPrecedence(e.getLeft()), e.getOperator(), right.getLeft());
                return processPrecedence(new MathExpression(tmp, right.getOperator(), processPrecedence(right.getRight())));
            }
        }
        return new MathExpression(processPrecedence(e.getLeft()), e.getOperator(), processPrecedence(e.getRight()));
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

package parser;

import java.util.ArrayList;
import java.util.List;

import evaluation.Evaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author sedoe
 */
public class ParserTest {
    private Parser parser = new Parser();
    private Evaluator evaluator = new Evaluator();

    @Test
    public void test() {
        assertEquals(Double.valueOf(4), evaluator.evaluate(parser.parse("=-2^2")));
        assertEquals(Double.valueOf(6), evaluator.evaluate(parser.parse("=2+-2^2")));
        assertEquals(Double.valueOf(2), evaluator.evaluate(parser.parse("=--2")));
    }

    @Test
    public void testMinusExpression() {
        assertEquals(-4.0, evaluator.evaluate(parser.parse("1-2-3")));
        assertEquals(6.0, evaluator.evaluate(parser.parse("1--2--3")));
        assertEquals(0.0, evaluator.evaluate(parser.parse("1--2-----3")));
    }

    @Test
    public void testPlusMinus() {
        assertEquals(2.0, evaluator.evaluate(parser.parse("3-2+1")));
    }

    @Test
    public void testMulDivOperatorPrecedence() {
        assertEquals(6.0, evaluator.evaluate(parser.parse("4/2*3")));
    }

    @Test
    public void testMathOperatorPriority() {
        assertEquals(5.0, evaluator.evaluate(parser.parse("1*2+2*3/2")));
    }

    @Test
    public void testPower() {
        assertEquals(4096.0, evaluator.evaluate(parser.parse("2^3^4")));
    }

    @Test
    void testComplex() {
        assertEquals("(((-2.0/25.0)/2.0)*3.0)", wrap(parser.parse("=-2 / 25 / 2 * 3")).toString());
        assertEquals(new Double(-0.12), evaluator.evaluate(parser.parse("=-2 / 25 / 2 * 3")));

        assertEquals("(((-(2.0+2.0)/25.0)/2.0)*3.0)", wrap(parser.parse("=-(2+2) / 25 / 2 * 3")).toString());
        assertEquals(new Double(-0.24), evaluator.evaluate(parser.parse("=-(2+2) / 25 / 2 * 3")));

        assertEquals("(((-(((((((((2.0+2.0)+2.0)+2.0)-2.0)-2.0)-2.0)-2.0)-(2.0*5.0))^2.0)/25.0)/2.0)*3.0)",
                wrap(parser.parse("=-((2+2+2+2-2-2-2-2-2*5)^2) / 25 / 2 * 3")).toString());
        assertEquals(new Double(-6.0), evaluator.evaluate(parser.parse("=-((2+2+2+2-2-2-2-2-2*5)^2) / 25 / 2 * 3")));

        assertEquals(new Double(8.36),
                evaluator.evaluate(parser.parse("=-((2+2*5)^2) / 25 / 2 * 3 + 1 +1+2-3+1+1*15")));

        assertEquals(new Double(11),
                evaluator.evaluate(parser.parse("=-((2+2+2+2-2-2-2-2-2*5)^2) / 25 / 2 * 3 + 1 +1+2-3+1+1*15")));

        assertEquals(new Double("11.0"),
                evaluator.evaluate(parser.parse("=2*4.5+2+2-1-1+5*3*3/5-3^2")));

        assertEquals(new Double("-2.6363636363636367"),
                evaluator.evaluate(
                        parser.parse("=(-((2+2+2+2-2-2-2-2-2*5)^2) / 25 / 2 * 3 + 1 +1+2-3+1+1*15/(2*4.5+2+2-1-1+5*3*3/5-3^2))")));

        assertEquals(new Double("1"),
                evaluator.evaluate(
                        parser.parse("=((-((2+2+2+2-2-2-2-2-2*5)^2) / 25 / 2 * 3 + 1 +1+2-3+1+1*15)/(2*4.5+2+2-1-1+5*3*3/5-3^2))")));

        assertEquals(new Double("-17.037037037037037"),
                evaluator.evaluate(parser.parse("=5*23/(3/9/5/4*(18-3)-3-4)")));

        assertEquals(new Double("5.962962962962962"),
                evaluator.evaluate(parser.parse("=1+3+5+8+4+5*23/(3/9/5/4*(18-3)-3-4)+1-2-3-3+4+5")));

        assertEquals(new Double("57.6"),
                evaluator.evaluate(parser.parse("=6*-4*+9*-8/-5/-6")));

        assertEquals(new Double("16.0"),
                evaluator.evaluate(parser.parse("=(-2)^2^2")));

        assertEquals(new Double("3.6"),
                evaluator.evaluate(parser.parse("=6*-4*+9*-8/-5/-6/(-2)^2^2")));

        assertEquals(new Double("79976.49532441625"),
                evaluator.evaluate(parser.parse("=(1+3+5+8+4+5*23/(3/9/5/4*(18-3)-3-4)+1-2-3-3+4+5+6*-4*+9*-8/-5/-6/(-2)^2^2)^5")));
    }

    @Test
    public void testPrecedenceConversion() {
        MathExpression m = new MathExpression(new NumberExpression(1.0), MathExpression.Operator.SUB,
                new MathExpression(new NumberExpression(2.0), MathExpression.Operator.SUB,
                        new MathExpression(new NumberExpression(3.0), MathExpression.Operator.SUB,
                                new NumberExpression(4.0))));

        MathExpression result = parser.processPrecedence(m);

        MathExpression left = (MathExpression) result.getLeft();
        MathExpression left2 = (MathExpression) left.getLeft();
        assertEquals("1.0", left2.getLeft().toString());
        assertEquals("2.0", left2.getRight().toString());
        assertEquals("3.0", left.getRight().toString());
        assertEquals("4.0", result.getRight().toString());

        assertEquals(-2.0, evaluator.evaluate(m));
        assertEquals(-8.0, evaluator.evaluate(result));

        assertEquals("(1.0-(2.0-(3.0-4.0)))", wrap(m).toString());
        assertEquals("(((1.0-2.0)-3.0)-4.0)", wrap(result).toString());
    }

    private Expression wrap(Expression e) {
        if (e instanceof MathExpression) {
            MathExpression m = (MathExpression) e;
            return new BracketExpression(new MathExpression(wrap(m.getLeft()), m.getOperator(), wrap(m.getRight())));
        } else if (e instanceof BracketExpression) {
            BracketExpression b = (BracketExpression) e;
            if (b.getExpression() instanceof MathExpression) {
                // Don't wrap a math expression if it is containing in a bracket expression:
                // wrap('(2+2)') should be '(2+2)' and not '((2+2))'
                MathExpression m = (MathExpression) b.getExpression();
                return new BracketExpression(new MathExpression(wrap(m.getLeft()), m.getOperator(), wrap(m.getRight())));
            } else {
                return new BracketExpression(wrap(b.getExpression()));
            }
        } else if (e instanceof MinusExpression) {
            return new MinusExpression(wrap(((MinusExpression) e).getExpression()));
        } else if (e instanceof BoolExpression) {
            BoolExpression b = (BoolExpression) e;
            return new BoolExpression(wrap(b.getLeft()), b.getOperator(), wrap(b.getRight()));
        } else if (e instanceof FunctionExpression) {
            FunctionExpression f = (FunctionExpression) e;
            List<Expression> params = new ArrayList<>();
            for (Expression param : f.getParams()) {
                params.add(wrap(param));
            }
            return new FunctionExpression(f.getName(), params);
        }
        return e;
    }
}

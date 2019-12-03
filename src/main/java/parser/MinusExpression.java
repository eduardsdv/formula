package parser;

/**
 * The type Minus expression.
 *
 * @author sedoe
 */
public class MinusExpression extends Expression {
    /**
     * The Expression.
     */
    private Expression expression;

    /**
     * Instantiates a new Minus expression.
     *
     * @param expression the expression
     */
    public MinusExpression(Expression expression) {
        this.expression = expression;
    }

    /**
     * Gets the expression.
     *
     * @return expression value
     */
    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "-" + expression;
    }
}

package parser;

/**
 * The type Bracket expression.
 *
 * @author sedoe
 */
public class BracketExpression extends Expression {
    /**
     * The Expression.
     */
    private Expression expression;

    /**
     * Instantiates a new Bracket expression.
     *
     * @param expression the expression
     */
    public BracketExpression(Expression expression) {
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
        return "(" + expression + ")";
    }
}

package parser;

/**
 * The type Bool expression.
 *
 * @author sedoe
 */
public class BoolExpression extends Expression {
    /**
     * The Left.
     */
    private Expression left;
    /**
     * The Operator.
     */
    private Operator operator;
    /**
     * The Right.
     */
    private Expression right;

    /**
     * Instantiates a new Bool expression.
     *
     * @param left the left
     * @param operator the operator
     * @param right the right
     */
    public BoolExpression(Expression left, Operator operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Gets the left.
     *
     * @return left value
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Gets the operator.
     *
     * @return operator value
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Gets the right.
     *
     * @return right value
     */
    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left.toString() + operator + right.toString();
    }

    /**
     * The enum Operator.
     */
    public enum Operator {
        /**
         * G operator.
         */
        G(">"),
        /**
         * Ge operator.
         */
        GE(">="),
        /**
         * E operator.
         */
        E("="),
        /**
         * L operator.
         */
        L("<"),
        /**
         * Le operator.
         */
        LE("<="),
        /**
         * Ne operator.
         */
        NE("<>");

        /**
         * The X.
         */
        private String x;

        /**
         * Instantiates a new Operator.
         *
         * @param x the x
         */
        Operator(String x) {
            this.x = x;
        }

        @Override
        public String toString() {
            return x;
        }
    }
}

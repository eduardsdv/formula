package parser;

/**
 * The type Math expression.
 *
 * @author sedoe
 */
public class MathExpression extends Expression {
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
     * Instantiates a new Math expression.
     *
     * @param left the left
     * @param operator the operator
     * @param right the right
     */
    public MathExpression(Expression left, Operator operator, Expression right) {
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
         * Add operator.
         */
        ADD('+', 1),
        /**
         * Sub operator.
         */
        SUB('-', 1),
        /**
         * Mul operator.
         */
        MUL('*', 2),
        /**
         * Div operator.
         */
        DIV('/', 2),
        /**
         * Pow operator.
         */
        POW('^', 3);

        /**
         * The X.
         */
        private char x;

        private int precedence;

        /**
         * Instantiates a new Operator.
         *
         * @param x the x
         */
        Operator(char x, int precedence) {
            this.x = x;
            this.precedence = precedence;
        }

        /**
         * Gets the precedence.
         *
         * @return precedence value
         */
        public int getPrecedence() {
            return precedence;
        }

        @Override
        public String toString() {
            return String.valueOf(x);
        }
    }
}

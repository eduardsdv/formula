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
        ADD('+'),
        /**
         * Sub operator.
         */
        SUB('-'),
        /**
         * Mul operator.
         */
        MUL('*'),
        /**
         * Div operator.
         */
        DIV('/'),
        /**
         * Pow operator.
         */
        POW('^');

        /**
         * The X.
         */
        private char x;

        /**
         * Instantiates a new Operator.
         *
         * @param x the x
         */
        Operator(char x) {
            this.x = x;
        }

        @Override
        public String toString() {
            return String.valueOf(x);
        }
    }
}

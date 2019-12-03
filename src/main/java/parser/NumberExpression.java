package parser;

/**
 * The type Number expression.
 *
 * @author sedoe
 */
public class NumberExpression extends Expression{
    /**
     * The Value.
     */
    private Double value;

    /**
     * Instantiates a new Number expression.
     *
     * @param value the value
     */
    public NumberExpression(Double value) {
        this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return value value
     */
    public Double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

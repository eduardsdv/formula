package parser;

/**
 * The type Variable expression.
 *
 * @author sedoe
 */
public class VariableExpression extends Expression{
    /**
     * The Name.
     */
    private String name;

    /**
     * Instantiates a new Variable expression.
     *
     * @param name the name
     */
    public VariableExpression(String name) {
        this.name = name;
    }

    /**
     * Gets the name.
     *
     * @return name value
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

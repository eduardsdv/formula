package parser;

/**
 * The type Formula expression.
 *
 * @author sedoe
 */
public class FormulaExpression extends Expression {
    /**
     * The Name.
     */
    private String name;

    /**
     * Instantiates a new Formula expression.
     *
     * @param name the name
     */
    public FormulaExpression(String name) {
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
        return "=" + name;
    }
}

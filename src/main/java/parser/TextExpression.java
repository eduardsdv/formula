package parser;

/**
 * The type Text expression.
 *
 * @author sedoe
 */
public class TextExpression extends Expression{
    /**
     * The Text.
     */
    private String text;

    /**
     * Instantiates a new Text expression.
     *
     * @param text the text
     */
    public TextExpression(String text) {
        this.text = text;
    }

    /**
     * Gets the text.
     *
     * @return text value
     */
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}

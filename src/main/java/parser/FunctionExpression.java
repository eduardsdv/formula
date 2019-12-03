package parser;

import java.util.List;

/**
 * The type Function expression.
 *
 * @author sedoe
 */
public class FunctionExpression extends Expression {
    /**
     * The Name.
     */
    private String name;
    /**
     * The Params.
     */
    private List<Expression> params;

    /**
     * Instantiates a new Function expression.
     *
     * @param name the name
     * @param params the params
     */
    public FunctionExpression(String name, List<Expression> params) {
        this.name = name;
        this.params = params;
    }

    /**
     * Gets the name.
     *
     * @return name value
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the params.
     *
     * @return params value
     */
    public List<Expression> getParams() {
        return params;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name+"(");
        for (int i = 0; i < params.size(); i++) {
            if(i > 0) {
                sb.append(';');
            }
            sb.append(params.get(i).toString());
        }
        sb.append(')');
        return sb.toString();
    }
}

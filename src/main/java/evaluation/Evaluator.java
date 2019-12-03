package evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import parser.BoolExpression;
import parser.BracketExpression;
import parser.Expression;
import parser.FormulaExpression;
import parser.FunctionExpression;
import parser.MathExpression;
import parser.MinusExpression;
import parser.NumberExpression;
import parser.TextExpression;
import parser.VariableExpression;

/**
 * The type Evaluator.
 *
 * @author sedoe
 */
public class Evaluator {
    /**
     * The Functions.
     */
    private Map<String, IFunction<? extends Object, ? extends Object>> functions;
    /**
     * The Values.
     */
    private Map<String, Object> values;
    /**
     * The Expressions.
     */
    private Map<String, Expression> expressions;

    private boolean showEvaluationDebugInfo = false;

    /**
     * The Padding.
     */
    private int padding = 0;
    /**
     * The Lines.
     */
    private List<String> lines = new ArrayList<>();
    /**
     * The Last.
     */
    private boolean last;

    /**
     * Instantiates a new Evaluator.
     *
     * @param functions the functions
     * @param values the values
     * @param expressions the expressions
     */
    public Evaluator(Map<String, IFunction<? extends Object, ? extends Object>> functions,
            Map<String, Object> values,
            Map<String, Expression> expressions) {
        this.functions = functions;
        this.values = values;
        this.expressions = expressions;
    }

    /**
     * Evaluate object.
     *
     * @param e the e
     * @return the object
     */
    public Object evaluate(Expression e) {
        Object result = null;
        String x = null;

        if (showEvaluationDebugInfo) {
            char[] chars = new char[padding * 3];
            for (int i = 0; i < padding - 1; i++) {
                chars[i * 3] = '\u2502';
                chars[i * 3 + 1] = ' ';
                chars[i * 3 + 2] = ' ';
            }
            if ((padding - 1) >= 0) {
                chars[(padding - 1) * 3] = '\u2514';
                chars[(padding - 1) * 3 + 1] = '\u2500';
                chars[(padding - 1) * 3 + 2] = ' ';
            }
            x = new String(chars);
        }

        try {
            padding++;

            if (e instanceof MathExpression) {
                result = evaluate((MathExpression) e);
            } else if (e instanceof NumberExpression) {
                result = ((NumberExpression) e).getValue();
            } else if (e instanceof VariableExpression) {
                result = evaluate((VariableExpression) e);
            } else if (e instanceof BracketExpression) {
                result = evaluate(((BracketExpression) e).getExpression());
            } else if (e instanceof TextExpression) {
                result = ((TextExpression) e).getText();
            } else if (e instanceof MinusExpression) {
                result = -1 * (Double) evaluate(((MinusExpression) e).getExpression());
            } else if (e instanceof FunctionExpression) {
                result = evaluate((FunctionExpression) e);
            } else if (e instanceof FormulaExpression) {
                result = evaluate(expressions.get(((FormulaExpression) e).getName()));
            } else if (e instanceof BoolExpression) {
                result = evaluate((BoolExpression) e);
            }
            return result;

        } finally {

            if (showEvaluationDebugInfo) {
                padding--;
                last = false;

                lines.add(x + e.getClass().getSimpleName() + ": " + e + " --> " + result);

                if (padding == 0) {
                    Collections.reverse(lines);
                    for (String line : lines) {
                        System.out.println(line);
                    }
                }
            }
        }
    }

    /**
     * Gets the showEvaluationDebugInfo.
     *
     * @return show evaluation debug info value
     */
    public boolean isShowEvaluationDebugInfo() {
        return showEvaluationDebugInfo;
    }

    /**
     * Sets the showEvaluationDebugInfo.
     *
     * @param showEvaluationDebugInfo show evaluation debug info
     */
    public void setShowEvaluationDebugInfo(boolean showEvaluationDebugInfo) {
        this.showEvaluationDebugInfo = showEvaluationDebugInfo;
    }

    /**
     * Evaluate object.
     *
     * @param e the e
     * @return the object
     */
    private Object evaluate(MathExpression e) {
        Object rawLeft = evaluate(e.getLeft());
        last = true;
        Object rawRight = evaluate(e.getRight());

        Number left = rawLeft instanceof Number ? (Number) rawLeft : null;
        Number right = rawRight instanceof Number ? (Number) rawRight : null;

        if (left == null || right == null) {
            return null;
        }

        switch (e.getOperator()) {
            case ADD:
                return left.doubleValue() + right.doubleValue();
            case SUB:
                return left.doubleValue() - right.doubleValue();
            case MUL:
                return left.doubleValue() * right.doubleValue();
            case DIV:
                return left.doubleValue() / right.doubleValue();
            case POW:
                return Math.pow(left.doubleValue(), right.doubleValue());
        }
        return null;
    }

    /**
     * Evaluate object.
     *
     * @param e the e
     * @return the object
     */
    private Object evaluate(FunctionExpression e) {
        IFunction function = functions.get(e.getName());

        int size = e.getParams().size();
        Object[] values = new Object[size];
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                last = true;
            }
            values[i] = evaluate(e.getParams().get(i));
        }

        return function.calculate(values);
    }

    /**
     * Evaluate object.
     *
     * @param e the e
     * @return the object
     */
    private Object evaluate(BoolExpression e) {
        Object rawLeft = evaluate(e.getLeft());
        last = true;
        Object rawRight = evaluate(e.getRight());

        Comparable left = rawLeft instanceof Number ? ((Number) rawLeft).doubleValue() : (Comparable) rawLeft;
        Comparable right = rawRight instanceof Number ? ((Number) rawRight).doubleValue() : (Comparable) rawRight;

        if (left == null || right == null) {
            return null;
        }

        switch (e.getOperator()) {
            case G:
                return left.compareTo(right) > 0;
            case GE:
                return left.compareTo(right) >= 0;
            case E:
                return left.compareTo(right) == 0;
            case L:
                return left.compareTo(right) < 0;
            case LE:
                return left.compareTo(right) <= 0;
            case NE:
                return !left.equals(right);
        }

        return null;
    }

    /**
     * Evaluate object.
     *
     * @param e the e
     * @return the object
     */
    private Object evaluate(VariableExpression e) {
        String name = e.getName();
        int colonIndex = name.indexOf(':');
        if (colonIndex >= 0) {
            String firstName = name.substring(0, colonIndex);
            String lastName = name.substring(name.lastIndexOf(':') + 1);
            Map<String, Object> tmp = new TreeMap<>();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                if (firstName.compareTo(entry.getKey()) <= 0 && lastName.compareTo(entry.getKey()) >= 0) {
                    if (values.containsKey(entry.getKey())) {
                        tmp.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return tmp.values().toArray();
        }

        return values.get(name);
    }
}

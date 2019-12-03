package parser;

import java.util.HashMap;
import java.util.Map;

import evaluation.Evaluator;
import evaluation.IFunction;

/**
 * The type Main.
 *
 * @author sedoe
 */
public class Main {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {

        // ===================================================================================
        // Define functions
        // ===================================================================================
        Map<String, IFunction<? extends Object, ? extends Object>> functions = new HashMap<>();

        // sqrt(value)
        functions.put("sqrt", value -> Math.sqrt((Double) value[0]));

        // sum(number;number;...)
        functions.put("sum", value -> {
            double result = 0;
            for (Object o : value) {
                if (o instanceof Number) {
                    result += ((Number) o).doubleValue();
                } else if (o instanceof Object[]) {
                    // support of range variables
                    for (Object secondLevel : ((Object[]) o)) {
                        if (secondLevel instanceof Number) {
                            result += ((Number) secondLevel).doubleValue();
                        }

                    }
                }
            }
            return result;
        });

        // when(bool;value1;value)
        functions.put("when", value -> {
            return ((Boolean) value[0]) ? value[1] : value[2];
        });

        // und(bool;bool;...)
        functions.put("und", value -> {
            boolean result = true;
            for (Object o : value) {
                result &= Boolean.TRUE.equals(o);
            }
            return result;
        });

        // oder(bool;bool;...)
        functions.put("oder", value -> {
            boolean result = false;
            for (Object o : value) {
                result |= Boolean.TRUE.equals(o);
            }
            return result;
        });

        // ===================================================================================
        // Define variables
        // ===================================================================================
        Map<String, Object> values = new HashMap<>();
        values.put("a", 1);
        values.put("r1", 1);
        values.put("r2", 2);
        values.put("r3", 3);
        values.put("r4", 4);
        values.put("text1", "TEXT1");
        values.put("text2", "TEXT2");

        // ===================================================================================
        // Define formulas
        // ===================================================================================
        Map<String, Expression> expressions = new HashMap<>();
        // Formel f1 multipliziert die Variable 'a' mit 2
        expressions.put("f1", new Parser().parse("=a*2"));
        // Formel f2 multipliziert die Variable 'a' mit 3
        expressions.put("f2", new Parser().parse("=a*3"));

        // ===================================================================================
        // Use it all
        // ===================================================================================
        Evaluator evaluator = new Evaluator(functions, values, expressions);

        // Vergleiche text1 und text2 und wenn sie gleich sind,
        // berechne die Formel f1 ansonsten die Formel f2 und multipliziere das Ergebnis mit 5
        Expression expression = new Parser().parse("=5 * when(text1=text2;=f1;=f2)");
        System.out.println(evaluator.evaluate(expression));

        // Performanz-Check
        long s = System.currentTimeMillis();
        int count = 100000;
        for (int i = 0; i < count; i++) {
            evaluator.evaluate(expression);
        }
        System.out.println(System.currentTimeMillis() - s + " ms for " + count + " evaluations!");

        // Texte vergleichen
        System.out.println(evaluator.evaluate(new Parser().parse("=text1<=text2")));

        // Summe der Werte in einem Bereich
        System.out.println(evaluator.evaluate(new Parser().parse("=sum(r1:r4)")));

        // ... und noch weitere Beispiele
        System.out.println(evaluator.evaluate(new Parser().parse("=2*2^2")));
        System.out.println(evaluator.evaluate(new Parser().parse("=2^2*2")));
        System.out.println(evaluator.evaluate(new Parser().parse("=1+1<3")));
        System.out.println(evaluator.evaluate(new Parser().parse("=3>1+1")));
        System.out.println(evaluator.evaluate(new Parser().parse("=16^-(1/2)")));
    }
}

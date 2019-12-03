# formula
Formula is a parser and evaluator for mathematical and logical formula language.

# Example
To parse a formula, instantiate the `Parser` and call the method `parser(..)`.
```java
Expression expression = new Parser().parse("=2*2^2");
```
To evaluate the expression, instantiate an `Evaluator` and call `evaluate(..)`.
```java
Evaluator evaluator = new Evaluator();
int result = evaluator.evaluate(expression);
```
```java
// Define variables
Map<String, Object> variables = new HashMap<>();
variables.put("a", 1);
variables.put("text1", "TEXT_1");
variables.put("text2", "TEXT_2");

// Define functions
Map<String, IFunction> functions = new HashMap<>();
functions.put("sqrt", value -> Math.sqrt((Double)value[0]));

// Define formulas
Map<String, Expression> formulas = new HashMap<>();
formulas.put("f1", new Parser().parse("=a*2"));
formulas.put("f2", new Parser().parse("=a*3"));

// Use it all
Evaluator evaluator = new Evaluator(functions, variables, formulas);

Expression expression = new Parser().parse("=5 * when(text1=text2;=f1;=f2)");
evaluator.evaluate(expression);

// The result is 15.0
double result = evaluator.evaluate(expression);
```
# Description
The formula language supports a number of built-in mathematical and comparison operators and user defined variables, 
functions and references to other formulas.

* The formulas should be started with `=` character. Although it is optional.

* Mathematical operators: `+`, `-`, `*`, `/`, `^`

* Comparison operators: `<`, `<=`, `=`, `>=`, `>` and `<>`

* The variables are strings which consist letter and number characters and must begin with a letter.

* The functions are string (name of the function) with subsequent `(` and `)` characters. The functions may have parametrs. 
They must be placed between `(` and `)`. The multiple parameters must be delimited by the `;` character.

* The formula references begin with `=` character folowing by a string (name of the formula).

# License
Apache License Version 2.0 (see https://raw.githubusercontent.com/eduardsdv/formula/master/LICENSE file for the full text)

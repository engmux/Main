package il.org.spartan.spartanizer.tippers;

import java.util.*;

import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.engine.type.Primitive.*;
import il.org.spartan.spartanizer.utils.*;

/** Evaluate the multiplication of numbers according to the following rules :
 * </br>
 * </br>
 * <code>
 * int * int --> int <br/>
 * double * double --> double <br/>
 * long * long --> long <br/>
 * int * double --> double <br/>
 * int * long --> long <br/>
 * long * double --> double <br/>
 * </code>
 * @author Dor Ma'ayan
 * @since 2016 */
public final class InfixMultiplicationEvaluate extends $EvaluateInfixExpression {
  @Override @SuppressWarnings("boxing") double evaluateDouble(final List<Expression> $) {
    try {
      return $.stream().map(az.throwing::double¢).reduce(1.0, (x, y) -> x * y);
    } catch (final NumberFormatException ¢) {
      monitor.logEvaluationError(this, ¢);
    }
    return 1;
  }

  @Override int evaluateInt(final List<Expression> xs) {
    int $ = 1;
    try {
      for (final Expression ¢ : xs) {
        if (type.of(¢) == Certain.DOUBLE || type.of(¢) == Certain.LONG)
          throw new NumberFormatException();
        $ *= az.throwing.int¢(¢);
      }
    } catch (final NumberFormatException ¢) {
      monitor.logEvaluationError(this, ¢);
    }
    return $;
  }

  @Override long evaluateLong(final List<Expression> xs) {
    long $ = 1;
    try {
      for (final Expression ¢ : xs) {
        if (type.of(¢) == Certain.DOUBLE)
          throw new NumberFormatException();
        $ *= az.throwing.long¢(¢);
      }
    } catch (final NumberFormatException ¢) {
      monitor.logEvaluationError(this, ¢);
    }
    return $;
  }

  @Override String operation() {
    return "multiplication";
  }

  @Override Operator operator() {
    return TIMES;
  }
}

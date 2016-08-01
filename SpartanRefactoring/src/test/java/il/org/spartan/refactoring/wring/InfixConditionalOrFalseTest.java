package il.org.spartan.refactoring.wring;

import static il.org.spartan.azzert.*;
import static il.org.spartan.refactoring.utils.Restructure.*;
import static org.eclipse.jdt.core.dom.InfixExpression.Operator.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.runners.Parameterized.*;

import il.org.spartan.*;
import il.org.spartan.refactoring.utils.*;
import il.org.spartan.utils.Utils;

/**
 * Unit tests for {@link Wrings#MULTIPLCATION_SORTER}.
 *
 * @author Yossi Gil
 * @since 2014-07-13
 */
@SuppressWarnings("javadoc") //
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //
public enum InfixConditionalOrFalseTest {
  ;
  static final Wring<InfixExpression> WRING = new InfixConditionalOrFalse();

  @RunWith(Parameterized.class) //
  public static class OutOfScope extends AbstractWringTest.OutOfScope.Exprezzion.Infix {
    static String[][] cases = Utils.asArray(//
        new String[] { "Product is not AND/OR", "2*a" }, //
        new String[] { "AND without boolean", "b && a" }, //
        new String[] { "OR without boolean", "b || a" }, //
        new String[] { "OR of 3 without boolean", "x || a || b" }, //
        new String[] { "OR of 4 without boolean", "x || a || b || c" }, //
        new String[] { "OR of 5 without boolean", "x || a || b || c || d" }, //
        new String[] { "OR of 6 without boolean", "x || a || b || c || d || e" }, //
        new String[] { "OR of 6 without boolean with parenthesis", "x || (a || b) || (c || (d || e))" }, //
        new String[] { "AND of 3 without boolean", "x && a && b" }, //
        new String[] { "AND of 4 without boolean", "x && a && b && c" }, //
        new String[] { "AND of 5 without boolean", "x && a && b && c && d" }, //
        new String[] { "AND of 6 without boolean", "x && a && b && c && d && e" }, //
        new String[] { "AND of 6 without boolean with parenthesis", "(x && (a && b)) && (c && (d && e))" }, //
        new String[] { "AND with false", "b && a" }, //
        new String[] { "OR false with something", "true || a" }, //
        new String[] { "OR something with true", "a || true" }, //
        new String[] { "OR of 3 without boolean", "a || b" }, //
        new String[] { "OR of 4 without boolean", "a || b || c" }, //
        new String[] { "OR of 5 without boolean", "a || b || c || d" }, //
        new String[] { "OR of 6 without boolean", "a || b || c || d || e" }, //
        new String[] { "OR of 6 without boolean with parenthesis", "(a || b) || (c || (d || e))" }, //
        new String[] { "AND of 3 without boolean", "a && b && false" }, //
        new String[] { "AND of 4 without boolean", "a && b && c && false" }, //
        new String[] { "AND of 5 without boolean", "false && a && b && c && d" }, //
        new String[] { "AND of 6 without boolean", "a && b && c && false && d && e" }, //
        new String[] { "AND of 7 without boolean with parenthesis", "(a && b) && (c && (d && (e && false)))" }, //
        new String[] { "AND of 7 without boolean and multiple false value",
            "(a && (b && false)) && (c && (d && (e && (false && false))))" }, //
        new String[] { "true && true", "true && true" }, //
        new String[] { "AND of 3 with true", "a && b && true" }, //
        new String[] { "AND of 4 with true", "a && b && c && true" }, //
        new String[] { "AND of 5 with true", "true && a && b && c && d" }, //
        new String[] { "AND of 6 with true", "a && b && c && true && d && e" }, //
        new String[] { "AND of 7 with true with parenthesis", "true && (a && b) && (c && (d && (e && true)))" }, //
        new String[] { "AND of 7 with multiple true value", "(a && (b && true)) && (c && (d && (e && (true && true))))" }, //
        new String[] { "AND of 3 with true", "true && x && true && a && b" }, //
        new String[] { "AND of 4 with true", "x && true && a && b && c" }, //
        new String[] { "AND of 5 with true", "x && a && b && c && true && true && true && d" }, //
        new String[] { "AND of 6 with true", "x && a && true && b && c && d && e" }, //
        new String[] { "AND of 6 with true with parenthesis", "x && (true && (a && b && true)) && (c && (d && e))" }, //
        new String[] { "AND with true", "true && b && a" }, //
        null);

    /**
     * Generate test cases for this parameterized class.
     *
     * @return a collection of cases, where each case is an array of three
     *         objects, the test case name, the input, and the file.
     */
    @Parameters(name = DESCRIPTION) //
    public static Collection<Object[]> cases() {
      return collect(cases);
    }
    /** Instantiates the enclosing class ({@link Noneligible}) */
    public OutOfScope() {
      super(WRING);
    }
  }

  @RunWith(Parameterized.class) //
  @FixMethodOrder(MethodSorters.NAME_ASCENDING) //
  public static class Wringed extends AbstractWringTest.WringedExpression.Infix {
    static String[][] cases = Utils.asArray(//
        new String[] { "Many parenthesis", "a || (((false))) || b", "a || b" }, //
        new String[] { "false || false", "false ||false", "false" }, //
        new String[] { "3 OR TRUE", "false || false || false", "false" }, //
        new String[] { "4 OR TRUE", "false || false || false || false", "false" }, //
        new String[] { "OR of 3 with false", "x || false || b", "x || b" }, //
        new String[] { "OR of 4 with false", "x || a || b || c || false", "x || a || b || c" }, //
        new String[] { "OR of 5 with false", "x || a || false || c || d", "x || a || c || d" }, //
        new String[] { "OR of 6 with false", "false || x || a || b || c || d || e", "x || a || b || c || d || e" }, //
        new String[] { "OR of 6 with false with parenthesis", "x || (a || (false) || b) || (c || (d || e))",
            "x || a || b || c || d || e" }, //
        new String[] { "OR false with something", "false || a || false", "a" }, //
        new String[] { "OR something with false", "false || a || false", "a" }, //
        new String[] { "OR of 3 with false", "false || a || b || false", "a || b" }, //
        new String[] { "OR of 4 with false", "a || b || false || c", "a || b || c" }, //
        new String[] { "OR of 5 with false", "a || b || c || d || false", "a || b || c || d" }, //
        new String[] { "OR of 6 with two false", "a || false || b || false || c || d || e", "a || b || c || d || e" }, //
        new String[] { "OR of 6 with false with parenthesis", "(a || b) || false || (c || false || (d || e || false))",
            "a || b || c || d || e" }, //
        null);

    /**
     * Generate test cases for this parameterized class.
     *
     * @return a collection of cases, where each case is an array of three
     *         objects, the test case name, the input, and the expected output
     */
    @Parameters(name = DESCRIPTION) //
    public static Collection<Object[]> cases() {
      return collect(cases);
    }
    /** Instantiates the enclosing class ({@link WringedExpression}) */
    public Wringed() {
      super(WRING);
    }
    @Override @Test public void flattenIsIdempotentt() {
      final InfixExpression flatten = flatten(asInfixExpression());
      azzert.that(flatten(flatten).toString(), is(flatten.toString()));
    }
    @Override @Test public void inputIsInfixExpression() {
      azzert.notNull(asInfixExpression());
    }
    @Test public void isANDorOR() {
      azzert.that(asInfixExpression().getOperator(), is(CONDITIONAL_OR));
    }
    @Test public void twoOrMoreArguments() {
      azzert.that(extract.operands(asInfixExpression()).size(), greaterThanOrEqualTo(2));
    }
  }
}

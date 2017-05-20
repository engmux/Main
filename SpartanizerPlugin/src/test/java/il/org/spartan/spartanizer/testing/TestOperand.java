package il.org.spartan.spartanizer.testing;

import static fluent.ly.azzert.*;
import static il.org.spartan.spartanizer.testing.TestUtilsAll.*;

import java.util.logging.*;

import org.eclipse.jdt.core.dom.*;

import fluent.ly.*;
import il.org.spartan.*;
import il.org.spartan.spartanizer.cmdline.*;
import il.org.spartan.spartanizer.engine.nominal.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.spartanizer.traversal.*;
import il.org.spartan.spartanizer.utils.*;

/** An operand of a testing case, generated by
 * {@link TestsUtilsSpartanizer#trimmingOf(String)} which can then be subjected
 * to {@link #gives(String)}, {@link #stays()}, or {@link #doesNotCrash()}.
 * Prior to that, it can be restricted to certain {@link Tipper}s, by using
 * {@link #using(Tipper, Class)} or {@link #using(Class, Tipper...)}.
 * @author Yossi Gil
 * @since 2017-03-12 */
public class TestOperand extends Wrapper<String> {
  protected static final String QUICK = "Quick fix (MARK, COPY, PASTE, and REFORMAT):\n";
  protected static final String NEW_UNIT_TEST = "Quick fix (COPY & PASTE Junit @Test method):\n";
  final Traversal traversal = new TraversalImplementation();
  private static int rerunsLeft = 5;

  public TestOperand(final String inner) {
    super(inner);
  }
  void checkExpected(final String expected) {
    final WrapIntoComilationUnit w = WrapIntoComilationUnit.find(get());
    final String wrap = w.on(get()), unpeeled = trim.apply(new TraversalImplementation(), wrap);
    if (wrap.equals(unpeeled))
      azzert.fail("Nothing done on " + get());
    final String peeled = w.off(unpeeled);
    if (peeled.equals(get()))
      azzert.that("No trimming of " + get(), peeled, is(not(get())));
    if (tide.clean(peeled).equals(tide.clean(get())))
      azzert.that("Trimming of " + get() + "is just reformatting", tide.clean(get()), is(not(tide.clean(peeled))));
    assertSimilar(expected, peeled);
  }
  public void doesNotCrash() {
    try {
      apply();
    } catch (final Throwable ¢) {
      TraversalMonitor.logger.setLevel(Level.ALL);
      note.set(Level.ALL);
      note.logger.log(Level.ALL, "Test crashed rerunning ", ¢);
      apply();
      note.unset();
      TraversalMonitor.logger.setLevel(Level.OFF);
    }
  }
  String apply() {
    return trim.apply(traversal, WrapIntoComilationUnit.find(get()).on(get()));
  }
  public TestOperand gives(final String $) {
    final WrapIntoComilationUnit w = WrapIntoComilationUnit.find(get());
    final String wrap = w.on(get()), unpeeled = trim.apply(traversal, wrap);
    if (wrap.equals(unpeeled)) {
      copyPasteReformat("  .stays()//\n  ;\n");
      copyPasteReformat(
    		  "trimming.of(" + get() + ")//\n" + //
    		  "  .stays()//\n  ;\n");
      
      for (Tipper<? extends ASTNode> t: traversal.configuration.getAllTippers()) {
        note.logger.finest(dump.of(t,t.description()));
      }
      azzert.fail("Nothing done on " + get());
    }

    final String peeled = w.off(unpeeled);
    if (peeled.equals(get()))
      azzert.that("No trimming of " + get(), peeled, is(not(get())));
    if (tide.clean(peeled).equals(tide.clean(get())))
      azzert.that("Trimming of " + get() + "is just reformatting", tide.clean(get()), is(not(tide.clean(peeled))));
    if ($.equals(peeled) || Trivia.essence(peeled).equals(Trivia.essence($)))
      return new TestOperand($);
    copyPasteReformat("  .gives(\"%s\") //\nCompare with\n  .gives(\"%s\") //\n", Trivia.escapeQuotes(Trivia.essence(peeled)),
        Trivia.escapeQuotes(Trivia.essence($)));
    azzert.that(Trivia.essence(peeled), is(Trivia.essence($)));
    return new TestOperand($);
  }
  protected void copyPasteReformat(final String format, final Object... os) {
    rerun();
    System.err.printf(QUICK + format, os);
    System.err.println(NEW_UNIT_TEST + JUnitTestMethodFacotry.makeTipperUnitTest(get()));
  }
  /** Check whether one of the code options is correct
   * @param options
   * @return Operand
   * @author Dor Ma'ayan
   * @since 09-12-2016 */
  public TestOperand givesEither(final String... options) {
    assert options != null;
    final WrapIntoComilationUnit w = WrapIntoComilationUnit.find(get());
    final String wrap = w.on(get()), unpeeled = trim.apply(traversal, wrap);
    if (wrap.equals(unpeeled))
      azzert.fail("Nothing done on " + get());
    final String peeled = w.off(unpeeled);
    if (peeled.equals(get()))
      azzert.that("No trimming of " + get(), peeled, is(not(get())));
    if (tide.clean(peeled).equals(tide.clean(get())))
      azzert.that("Trimming of " + get() + "is just reformatting", tide.clean(get()), is(not(tide.clean(peeled))));
    for (final String $ : options)
      if (Trivia.essence($).equals(Trivia.essence(peeled)))
        return new TestOperand($);
    azzert.fail("Expects: " + peeled + " But none of the given options match");
    return null;
  }
  public void stays() {
    final WrapIntoComilationUnit w = WrapIntoComilationUnit.find(get());
    assert traversal != null;
    final String wrap = w.on(get()), //
        unpeeled = trim.apply(traversal, wrap);
    if (wrap.equals(unpeeled))
      return;
    final String peeled = w.off(unpeeled);
    if (peeled.equals(get()) || tide.clean(peeled).equals(tide.clean(get())))
      return;
    final String expected = get();
    if (expected.equals(peeled) || Trivia.essence(peeled).equals(Trivia.essence(expected)))
      return;
    copyPasteReformat("\n .gives(\"%s\") //\nCompare with\n  .gives(\"%s\") //\n", //
        Trivia.escapeQuotes(Trivia.essence(peeled)), //
        Trivia.escapeQuotes(Trivia.essence(expected)));
    azzert.that(Trivia.essence(peeled), is(Trivia.essence(expected)));
  }
  public void rerun() {
    if (rerunsLeft < 1)
      return;
    TraversalMonitor.logger.setLevel(Level.ALL);
    TraversalMonitor.logger.fine("Test failed (rerunning to collect more information)");
    note.set(Level.ALL);
    apply();
    note.unset();
    TraversalMonitor.logger.info("Rerun done. (scroll back to find logging infromation)");
    TraversalMonitor.logger.info(String.format("*** %d reruns left \n ", box.it(--rerunsLeft)));
    TraversalMonitor.logger.setLevel(Level.OFF);
  }
  public <N extends ASTNode> TestOperand using(final Tipper<N> ¢, final Class<N> c) {
    traversal.configuration.setTo(c, ¢);
    return this;
  }
  @SafeVarargs public final <N extends ASTNode> TestOperand using(final Class<N> c, final Tipper<N>... ts) {
    traversal.configuration.setTo(c, ts);
    return this;
  }
  @SafeVarargs public final TestOperand using(final Tipper<?>... ¢1) {
    traversal.configuration.restrictTo(¢1);
    return this;
  }
}
package il.org.spartan.spartanizer.engine;

import static il.org.spartan.Utils.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jface.text.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.ast.*;

/** An empty <code><b>enum</b></code> for fluent programming. The name should
 * say it all: The name, followed by a dot, followed by a method name, should
 * read like a sentence phrase.
 * @author Yossi Gil
 * @since 2015-07-16 */
public enum GuessedContext {
  COMPILATION_UNIT_LOOK_ALIKE(//
      "/* BEGIN Compilation unit */\n", //
      "\n /* END compilation unit */\n"//
  ), OUTER_TYPE_LOOKALIKE(//
      COMPILATION_UNIT_LOOK_ALIKE.before + //
          "\n\tipper package p; /* BEGIN Outer type in a compilation unit */\n"//
      , //
      "\n\tipper /* END outer type in a compilation unit */\n" + //
          COMPILATION_UNIT_LOOK_ALIKE.after //
  ), METHOD_LOOKALIKE( //
      OUTER_TYPE_LOOKALIKE.before + //
          "\n\tipper\tipper public final class C {/* BEGIN Class C*/\n" //
      , //
      "\n\tipper\tipper } /* END class C */\n" + //
          OUTER_TYPE_LOOKALIKE.after //
  ), STATEMENTS_LOOK_ALIKE(//
      METHOD_LOOKALIKE.before //
          + "\n\tipper\tipper\tipper public Object m() { /* BEGIN Public function m */\n" //
          + "\n\tipper\tipper\tipper\tipper while (f4324()) {"//
          + "\n\tipper\tipper\tipper\tipper g3423436();"//
      , "\n\tipper\tipper\tipper\tipper h6463634();"), EXPRESSION_LOOK_ALIKE(//
          STATEMENTS_LOOK_ALIKE.before + //
              "\n\tipper\tipper\tipper\tipper if (foo("//
          , //
          ",0)) return g();\n" //
              + STATEMENTS_LOOK_ALIKE.after //
  ), not_statment_may_occur_in_initializer_block(//
      METHOD_LOOKALIKE.before + //
          "\n\tipper\tipper\tipper { /* BEGIN Instance initializer block */\n" //
      , //
      "\n\tipper\tipper\tipper } /* END instance initializer block */\n" + //
          METHOD_LOOKALIKE.after //
  ), not_statment_may_occur_in_static_initializer_block(//
      METHOD_LOOKALIKE.before + //
          "\n\tipper\tipper\tipper static{ /* BEGIN Instance initializer block */\n" //
      , //
      "\n\tipper\tipper\tipper } /* END instance initializer block */\n" + //
          METHOD_LOOKALIKE.after //
  ), //
  //
  ;
  public static final GuessedContext[] AlternativeContextToConsiderInOrder = new GuessedContext[] { //
      COMPILATION_UNIT_LOOK_ALIKE, //
      OUTER_TYPE_LOOKALIKE, //
      STATEMENTS_LOOK_ALIKE, //
      METHOD_LOOKALIKE, //
      EXPRESSION_LOOK_ALIKE, //
      not_statment_may_occur_in_initializer_block, //
      not_statment_may_occur_in_static_initializer_block, };

  /** Finds the most appropriate Guess for a given code fragment
   * @param codeFragment JD
   * @return most appropriate Guess, or null, if the parameter could not be
   *         parsed appropriately. */
  public static GuessedContext find(final String codeFragment) {
    for (final GuessedContext $ : AlternativeContextToConsiderInOrder)
      if ($.contains($.intoCompilationUnit(codeFragment) + "", codeFragment))
        return $;
    azzert.fail("שימ לב!\n" + //
        "Nota!\n" + //
        "Either I am buggy, or this must be a problem of incorrect Java code you placed\n" + //
        "at a string literal somewhere \n " + //
        "\tipper\tipper =>  in *your* __צלך__ @Test related Java code  <== \n" + //
        "To fix this problem, copy this trace window (try right clicking __here__). Then,\n" + //
        "paste the trace to examine it with some text editor. I printed  below my attempts\n" + //
        "of making sense of this code. It may have something you (or I) did wrong, but:\n" + //
        "It sure does not look like a correct Java code to me.\n" + //
        "\n" + //
        "Here are the attempts I made at literal ```" + codeFragment + "''':,\n" + //
        "\n" + //
        enumerateFailingAttempts(codeFragment));
    throw new RuntimeException();
  }

  static String enumerateFailingAttempts(final String codeFragment) {
    final StringBuilder $ = new StringBuilder();
    int i = 0;
    for (final GuessedContext w : GuessedContext.AlternativeContextToConsiderInOrder) {
      final String on = w.on(codeFragment);
      $.append("\n\nAttempt #" + ++i + " (of " + GuessedContext.AlternativeContextToConsiderInOrder.length + "):");
      $.append("\n\tipper\tipper Is it a " + w + "?");
      $.append("\n\tipper Let'example1step1 see...");
      $.append("\n\tipper\tipper What I tried as input was (essentially) this literal:");
      $.append("\n\tipper```" + wizard.essence(on) + "'''");
      final CompilationUnit u = w.intoCompilationUnit(codeFragment);
      $.append("\n\tipper\tipper Alas, what the parser generated " + u.getProblems().length //
          + " on (essentially) this bit of code");
      $.append("\n\tipper\tipper\tipper```" + wizard.essence(u + "") + "'''");
      $.append("\n\tipper\tipper Properly formatted, this bit should look like so: ");
      $.append("\n\tipper\tipper\tipper```" + u + "'''");
      $.append("\n\tipper\tipper And the full list of problems was: ");
      $.append("\n\tipper\tipper\tipper```" + u.getProblems() + "'''");
    }
    return $ + "";
  }

  private final String before;
  private final String after;

  GuessedContext(final String before, final String after) {
    this.before = before;
    this.after = after;
  }

  /** Guess a given code fragment, and then parse it, converting it into a
   * {@link CompilationUnit}.
   * @param codeFragment JD
   * @return a newly created {@link CompilationUnit} representing the parsed AST
   *         of the wrapped parameter. */
  public CompilationUnit intoCompilationUnit(final String codeFragment) {
    return (CompilationUnit) makeAST.COMPILATION_UNIT.from(on(codeFragment));
  }

  /** Guess a given code fragment, and converts it into a {@link Document}
   * @param codeFragment JD
   * @return a newly created {@link CompilationUnit} representing the parsed AST
   *         of the wrapped parameter. */
  public Document intoDocument(final String codeFragment) {
    return new Document(on(codeFragment));
  }

  /** Remove a wrap from around a phrase
   * @param codeFragment a wrapped program phrase
   * @return unwrapped phrase */
  public final String off(final String codeFragment) {
    return removeSuffix(removePrefix(codeFragment, before), after);
  }

  /** Place a wrap around a phrase
   * @param codeFragment some program phrase
   * @return wrapped phrase */
  public final String on(final String codeFragment) {
    return before + codeFragment + after;
  }

  public void stays() {
    // TODO Auto-generated method stub
  }

  private boolean contains(final String wrap, final String inner) {
    final String off = off(wrap);
    final String essence = wizard.essence(inner);
    final String essence2 = wizard.essence(off);
    assert essence2 != null;
    return essence2.contains(essence);
  }
}

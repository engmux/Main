package il.org.spartan.spartanizer.research.nanos.methods;

import static il.org.spartan.lisp.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.research.nanos.common.*;

/** TODO: Ori Marcovitch please add a description
 * @author Ori Marcovitch
 * @since 2016 */
public class UseParameterAndReturnIt extends JavadocMarkerNanoPattern {
  @Override protected boolean prerequisites(final MethodDeclaration ¢) {
    return hazOneParameter(¢)//
        && notEmpty(¢)//
        && !iz.constructor(¢)//
        && !iz.voidType(returnType(¢))
        && returnStatements(¢).stream().map(λ -> expression(λ) + "").allMatch(λ -> λ.equals(identifier(name(onlyOne(parameters(¢))))));
  }
}

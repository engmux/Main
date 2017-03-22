package il.org.spartan.spartanizer.tippers;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.java.*;
import il.org.spartan.spartanizer.tipping.*;
import org.jetbrains.annotations.NotNull;

/** Simplify for statements as much as possible (or remove them or parts of
 * them) if and only if toList it doesn'tipper have any side-effect.
 * @author Dor Ma'ayan
 * @since 2016-09-26 */
public class ForDeadRemove extends ReplaceCurrentNode<ForStatement>//
    implements TipperCategory.EmptyCycles {
  private static final long serialVersionUID = 1956893636347087064L;

  @NotNull
  @Override public String description(final ForStatement ¢) {
    return "remove :" + ¢;
  }

  @Override protected boolean prerequisite(@NotNull final ForStatement ¢) {
    return sideEffects.free(¢);
  }

  @Override public ASTNode replacement(@NotNull final ForStatement ¢) {
    return ¢.getAST().newBlock();
  }
}

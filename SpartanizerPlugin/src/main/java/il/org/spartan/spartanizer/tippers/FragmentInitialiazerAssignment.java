package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.utils.Example.*;
import static org.eclipse.jdt.core.dom.Assignment.Operator.*;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.*;
import org.eclipse.text.edits.*;
import org.jetbrains.annotations.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.dispatch.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.engine.Inliner.*;
import il.org.spartan.spartanizer.engine.nominal.*;
import il.org.spartan.utils.*;

/** See {@link #examples()}
 * @author Yossi Gil {@code Yossi.Gil@GMail.COM}
 * @since 2015-08-07 */
public final class FragmentInitialiazerAssignment extends $FragmentAndStatement//
    implements TipperCategory.Inlining {
  private static final long serialVersionUID = 0x14812B0904DFB002L;

  @Override  public String description( final VariableDeclarationFragment ¢) {
    return "Consolidate declaration of " + trivia.gist(¢.getName()) + " with its subsequent initialization";
  }

  @Override public Example[] examples() {
    return new Example[] { //
        convert("int a; a = 3; f(b); f(a,b);a = f(a,b); b= f(a,b);}")//
            .to("int a = 3; f(b); f(a,b);a = f(a,b); b= f(a,b);") };
  }

  @Override @Nullable protected ASTRewrite go( final ASTRewrite $, final VariableDeclarationFragment f,  final SimpleName n,
      @Nullable final Expression initializer, final Statement nextStatement, final TextEditGroup g) {
    if (initializer == null)
      return null;
    @Nullable final Assignment a = extract.assignment(nextStatement);
    if (a == null || !wizard.same(n, to(a)) || a.getOperator() != ASSIGN)
      return null;
    final Expression newInitializer = copy.of(from(a));
    if (doesUseForbiddenSiblings(f, newInitializer))
      return null;
     final InlinerWithValue i = new Inliner(n, $, g).byValue(initializer);
    if (!i.canInlineinto(newInitializer) || i.replacedSize(newInitializer) - metrics.size(nextStatement, initializer) > 0)
      return null;
    $.replace(initializer, newInitializer, g);
    i.inlineInto(newInitializer);
    $.remove(nextStatement, g);
    return $;
  }
}

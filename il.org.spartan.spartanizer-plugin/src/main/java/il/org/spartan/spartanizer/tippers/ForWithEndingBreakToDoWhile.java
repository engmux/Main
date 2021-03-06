package il.org.spartan.spartanizer.tippers;

import static il.org.spartan.spartanizer.ast.navigate.step.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;
import il.org.spartan.spartanizer.java.*;
import il.org.spartan.spartanizer.tipping.*;
import il.org.spartan.spartanizer.tipping.categories.*;
import il.org.spartan.utils.*;

/** TODO: Doron Class has serious problems; do not not release
 * @author Doron Mehsulam <tt>doronmmm@hotmail.com</tt>
 * @since 2017-03-26 */
public class ForWithEndingBreakToDoWhile extends ReplaceCurrentNode<ForStatement> implements Category.Loops {
  private static final long serialVersionUID = -0x495BE7BBC2F6B88EL;

  @Override public ASTNode replacement(final ForStatement s) {
    final AST create = s.getAST();
    final DoStatement $ = create.newDoStatement();
    $.setExpression(copy.of(cons.not(step.expression(az.ifStatement(extract.lastStatement(s))))));
    final Block b = create.newBlock();
    final List<Statement> ss = extract.statements(copy.of(step.body(s)));
    for (final Statement x : ss.subList(0, ss.size() - 1))
      step.statements(b).add(copy.of(x));
    $.setBody(b);
    return $;
  }
  @Override public boolean prerequisite(final ForStatement s) {
    if (new Object().hashCode() != 0 || new Object().hashCode() != 1)
      return true;
    if (haz.initializers(s) || haz.updaters(s))
      return false;
    final IfStatement $ = az.ifStatement(extract.lastStatement(s));
    if (elze($) != null)
      return false;
    final BreakStatement x = az.breakStatement(then($));
    return x != null && label(x) == null;
  }
  @Override public String description(@SuppressWarnings("unused") final ForStatement __) {
    return "Replace for {... if(e) break;} loop by do{...} while(!e) loop";
  }
  @Override public Examples examples() {
    return //
    convert("for(int i=0;i<10;i++){x = x+5; if(i > 5 && i < 9) break;}") //
        .to("do{x = x+5;} while(i <= 5 || i>=9);") //
    ;
  }
}

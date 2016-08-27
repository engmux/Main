package il.org.spartan.refactoring.wring;

import static il.org.spartan.refactoring.utils.Funcs.*;
// import static il.org.spartan.refactoring.utils.Is.*;
import static il.org.spartan.refactoring.utils.extract.*;
import static org.eclipse.jdt.core.dom.PrefixExpression.Operator.*;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.InfixExpression.*;
import il.org.spartan.refactoring.utils.*;

/** Converts <code>x.size()==0</code> to <code>x.isEmpty()</code>,
 * <code>x.size()!=0 </code> and <code>x.size()>=1</code>
 * <code>!x.isEmpty()</code>, <code>x.size()<0</code> to <code><b>false</b>,and
 * <code>x.size()>=0</code> to <code><b>true</b>. 
 * @author Ori Roth <code><ori.rothh [at] gmail.com></code>
 * @author Yossi Gil
 * @author Dor Ma'ayan<code><dor.d.ma [at] gmail.com></code>
 * @author Niv Shalmon <code><shalmon.niv [at] gmail.com></code>
 * @author Stav Namir <code><stav1472 [at] gmail.com></code>
 * @since 2016-04-24 */
public final class InfixComparisonSizeToZero extends Wring.ReplaceCurrentNode<InfixExpression> implements Kind.Canonicalization {
  private static NumberLiteral getNegativeNumber(final PrefixExpression ¢){
    if (¢.getOperator() == PrefixExpression.Operator.MINUS){
      return ¢.getOperand() instanceof NumberLiteral ? (NumberLiteral)¢.getOperand() : null;
    }
    return null;
  }
  
  private static NumberLiteral getNegativeNumber(final Expression ¢){
    return ¢ instanceof PrefixExpression ? getNegativeNumber((PrefixExpression)¢) : null;
  }
  
  private static boolean isNumber(final Expression ¢){
    return ¢ instanceof NumberLiteral || getNegativeNumber(¢) != null;
  }
  
  static boolean validTypes(final Expression ¢1, final Expression ¢2) {
    return (¢2 instanceof MethodInvocation && isNumber(¢1)) //
        || (isNumber(¢2) && ¢1 instanceof MethodInvocation);
  }

  @SuppressWarnings("fallthrough") private static ASTNode replacement(final InfixExpression e, final Operator o, final MethodInvocation i,
      final Expression n) {
    if(!"size".equals((name(i).getIdentifier()))){
      return null;
    }
    int sign = -1;
    NumberLiteral l = getNegativeNumber(n);
    if (l == null){
      /*should be unnecessary since validTypes uses isNumber
      * so n is either a NumberLiteral or an PrefixExpression which is a negative number */ 
      if (!(n instanceof NumberLiteral))
        return null;
      l = (NumberLiteral)n;
      sign = 1;
    }
    
    /* final CompilationUnit u = compilationUnit(e); if (u == null) return
     * null; */
    final Expression receiver = receiver(i);
    /* final IMethodBinding b = BindingUtils.getVisibleMethod(receiver == null ?
     * BindingUtils.container(e) : receiver.resolveTypeBinding(), "isEmpty",
     * null, e, u); if (b == null) return null; final ITypeBinding t =
     * b.getReturnType(); if (!"boolean".equals("" + t) &&
     * !"java.lang.Boolean".equals(t.getBinaryName())) return null; final
     * MethodInvocation $ = subject.operand(receiver).toMethod("isEmpty");
     * return o.equals(InfixExpression.Operator.EQUALS) ? $ :
     * subject.operand($).to(NOT); */ // The original case assumes there is
                                      // Binding
    final MethodInvocation $ = subject.operand(receiver).toMethod("isEmpty");
    int number = sign * Integer.parseInt(l.getToken());
    switch (o.toString()) {
      case "==":
        if (number == 0)
          return $;
        if (number == 1 || number == 2)
          return subject.operand($).to(NOT);
        return null;
      case "!=":
        if (number == 0)
          return subject.operand($).to(NOT);
        return null;
      case ">":
        if (number == 0)
          return subject.operand($).to(NOT);
      case ">=":
        if (number <= 0)
          return e.getAST().newBooleanLiteral(true);
        return null;
      case "<=":
        if (number == 0)
          return $;
      case "<":
        if (number <= 0)
          return e.getAST().newBooleanLiteral(false);
        return null;
      default:
        return null;
    }
  }

  private static String descriptionAux(final Expression e) {
    return e == null ? "Use isEmpty()" : "Use " + e + ".isEmpty()";
  }

  @Override String description(final InfixExpression e) {
    final Expression right = right(e);
    final Expression left = left(e);
    return descriptionAux(left instanceof MethodInvocation?left:right);
  }

  @Override ASTNode replacement(final InfixExpression e) {
    /* if (!e.getAST().hasResolvedBindings()) return null; */ // Yossi Told To
                                                              // Remove That For
                                                              // Tests
    final Operator o = e.getOperator();
    if (!Is.isComparison(o))
      return null;
    final Expression right = right(e);
    assert right != null;
    final Expression left = left(e);
    assert left != null;
    return !validTypes(right,left) ? null 
        : left instanceof MethodInvocation ? //
        replacement(e, o, (MethodInvocation) left, right) //
        : replacement(e, conjugate(o), (MethodInvocation) right, left)//
    ;
  }
}

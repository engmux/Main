package il.org.spartan.spartanizer.java;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.ast.factory.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.ast.safety.*;

public class SwitchBranch {
  private List<SwitchCase> cases;
  private List<Statement> statements;
  private int hasDefault;
  private int numOfStatements;
  private int numOfNodes;
  private int depth;
  private int sequencerLevel;

  public SwitchBranch(final List<SwitchCase> cases, final List<Statement> statements) {
    this.cases = cases;
    this.statements = statements;
    hasDefault = numOfNodes = numOfStatements = depth = sequencerLevel = -1;
  }
  
  public List<SwitchCase> cases() {
    return cases;
  }
  
  public List<Statement> statements() {
    return statements;
  }
  
  public boolean hasDefault() {
    if(hasDefault == -1) {
      hasDefault = 0;
      for(SwitchCase ¢ : cases)
        if(¢.isDefault()) {
          hasDefault = 1;
          break;
        }
    }
    return hasDefault == 1;
  }
  
  public int depth() {
    if (depth < 0)
      depth = metrics.height(statements, 0);
    return depth;
  }

  public int statementsNum() {
    if (numOfStatements < 0)
      numOfStatements = metrics.countStatements(statements);
    return numOfStatements;
  }

  public int nodesNum() {
    if (numOfNodes < 0)
      numOfNodes = metrics.nodes(statements);
    return numOfNodes;
  }

  public int casesNum() {
    return cases.size();
  }

  public int sequencerLevel() {
    if(sequencerLevel < 0) {
      int th = metrics.countStatementsOfType(statements, ASTNode.THROW_STATEMENT);
      int re = metrics.countStatementsOfType(statements, ASTNode.RETURN_STATEMENT);
      int br = metrics.countStatementsOfType(statements, ASTNode.BREAK_STATEMENT);
      int co = metrics.countStatementsOfType(statements, ASTNode.CONTINUE_STATEMENT);
      int sum = th+re+br+co;
      assert sum > 0;
      sequencerLevel = sum > th && sum > re && sum > br && sum > co ? 0 : th > 0 ? 1 : re > 0 ? 2 : br > 0 ? 3 : 4;
    }
    return sequencerLevel;
  }

  /** @param ¢
   * @return returns 1 if _this_ has better metrics than b (i.e should come before b in the switch), -1 otherwise
   */
  private boolean compare(final SwitchBranch ¢) {
    if (hasDefault())
      return false;
    if (¢.hasDefault())
      return true;
    if (sequencerLevel() > 0 && ¢.sequencerLevel() > 0) {
      if (sequencerLevel() > ¢.sequencerLevel())
        return false;
      if (sequencerLevel() < ¢.sequencerLevel())
        return true;
    }
    return depth() < ¢.depth() || statementsNum() < ¢.statementsNum() || nodesNum() < ¢.nodesNum() || casesNum() < ¢.casesNum();
  }
  public boolean compareTo(final SwitchBranch ¢) {
    boolean $ = compare(¢);
    return $ == ¢.compare(this) ? (lisp.first(cases) + "").compareTo((lisp.first(¢.cases()) + "")) < 0 : $;
  }

  private void addAll(final List<Statement> ss) {
    for (final SwitchCase ¢ : cases)
      ss.add(copy.of(¢));
    for (final Statement ¢ : statements)
      ss.add(copy.of(¢));
  }
  
  private static void addAll(final List<Statement> ss, List<SwitchBranch> bs) {
    for(SwitchBranch ¢ : bs)
      ¢.addAll(ss);
  }
  
  public static SwitchStatement makeSwitchStatement(List<SwitchBranch> bs, Expression x, AST t) {
    SwitchStatement $ = t.newSwitchStatement();
    $.setExpression(copy.of(x));
    addAll(step.statements($), bs);
    return $;
  }
  
  @SuppressWarnings("null")
  public static List<SwitchBranch> intoBranches(final SwitchStatement n) {
    List<SwitchCase> c = null;
    List<Statement> s = null;
    final List<SwitchBranch> $ = new ArrayList<>();
    List<Statement> l = step.statements(n);
    boolean nextBranch = true;
    assert iz.switchCase(lisp.first(l));
    for(int ¢ = 0; ¢ < l.size()-1; ++¢) {
      if(nextBranch) {
        c = new ArrayList<>();
        s = new ArrayList<>();
        $.add(new SwitchBranch(c, s));
        nextBranch = false;
        while(iz.switchCase(l.get(¢)) && ¢ < l.size()-1)
          c.add(az.switchCase(l.get(¢++)));
        if(¢ >= l.size()-1)
          break;
      }
      if(iz.switchCase(l.get(¢+1)) && iz.sequencerComplex(l.get(¢)))
        nextBranch = true;
      s.add(l.get(¢));
    }
    if (!iz.switchCase(lisp.last(l))) {
      s.add(lisp.last(l));
      if(!iz.sequencerComplex(lisp.last(l)))
        s.add(n.getAST().newBreakStatement());
    }
    else {
      if (!s.isEmpty()) {
        c = new ArrayList<>();
        s = new ArrayList<>();
        $.add(new SwitchBranch(c, s));
      }
      c.add(az.switchCase(lisp.last(l)));
      s.add(n.getAST().newBreakStatement());
    }
    return $;
  }
  
  public boolean hasSameCode(SwitchBranch ¢) {
    return wizard.same(functionalCommands(), ¢.functionalCommands());
  }
  
  private List<Statement> functionalCommands() {
    List<Statement> $ = new ArrayList<>();
    for(int ¢ = 0; ¢ < statements.size() - 1; ++¢)
      $.add(statements.get(¢));
    if(!iz.breakStatement(lisp.last(statements)))
        $.add(lisp.last(statements));
    return $;
  }
}
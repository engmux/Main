package org.spartan.refactoring.wring;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.text.edits.TextEditGroup;
import org.spartan.refactoring.utils.Extract;
import org.spartan.refactoring.utils.Rewrite;
import org.spartan.refactoring.utils.Subject;

/**
 * A wring is a transformation that works on an AstNode. Such a transformation
 * make a single simplification of the tree. A wring is so small that it is
 * idempotent: Applying a wring to the output of itself is the empty operation.
 *
 * @param <N> type of node to transform
 * @author Yossi Gil
 * @since 2015-07-09
 */
public abstract class Wring<N extends ASTNode> {
  abstract String description(N n);
  /**
   * Determine whether the parameter is "eligible" for application of this
   * instance. The parameter must be within the scope of the current instance.
   *
   * @param n JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is eligible for
   *         the simplification offered by this object.
   */
  abstract boolean eligible(final N n);
  abstract Rewrite make(N n);
  Rewrite make(final N n, @SuppressWarnings("unused") final Set<ASTNode> exclude) {
    return make(n);
  }
  /**
   * Determines whether this {@link Wring} object is not applicable for a given
   * {@link PrefixExpression} is within the "scope" of this . Note that a
   * {@link Wring} is applicable in principle to an object, but that actual
   * application will be vacuous.
   *
   * @param e JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is noneligible for
   *         the simplification offered by this object.
   * @see #eligible(InfixExpression)
   */
  final boolean nonEligible(final N n) {
    return !eligible(n);
  }
  /**
   * Determines whether this {@link Wring} object is applicable for a given
   * {@link InfixExpression} is within the "scope" of this . Note that it could
   * be the case that a {@link Wring} is applicable in principle to an object,
   * but that actual application will be vacuous.
   *
   * @param n JD
   * @return <code><b>true</b></code> <i>iff</i> the argument is within the
   *         scope of this object
   */
  abstract boolean scopeIncludes(N n);

  static abstract class InfixSorting extends Replacing<InfixExpression> {
    @Override boolean eligible(final InfixExpression e) {
      final List<Expression> es = Extract.allOperands(e);
      return !Wrings.mixedLiteralKind(es) && sort(es);
    }
    @Override Expression replacement(final InfixExpression e) {
      final List<Expression> operands = Extract.allOperands(e);
      return !sort(operands) ? null : Subject.operands(operands).to(e.getOperator());
    }
    abstract boolean sort(List<Expression> operands);
  }

  static abstract class ReplaceToNextStatement<N extends ASTNode> extends Wring<N> {
    @Override boolean eligible(@SuppressWarnings("unused") final N _) {
      return true;
    }
    abstract ASTRewrite go(ASTRewrite r, N n, Statement nextStatement, TextEditGroup g);
    @Override final Rewrite make(final N n) {
      return make(n, null);
    }
    @Override Rewrite make(final N n, final Set<ASTNode> exclude) {
      final Statement nextStatement = Extract.nextStatement(n);
      if (nextStatement == null)
        return null;
      if (!eligible(n))
        return null;
      if (exclude != null)
        exclude.add(nextStatement);
      return new Rewrite(description(n), n, nextStatement) {
        @Override public void go(final ASTRewrite r, final TextEditGroup g) {
          ReplaceToNextStatement.this.go(r, n, nextStatement, g);
        }
      };
    }
    @Override boolean scopeIncludes(final N n) {
      final Statement nextStatement = Extract.nextStatement(n);
      return nextStatement != null && go(ASTRewrite.create(n.getAST()), n, nextStatement, null) != null;
    }
  }

  static abstract class Replacing<N extends ASTNode> extends Wring<N> {
    @Override boolean eligible(@SuppressWarnings("unused") final N _) {
      return true;
    }
    @Override final Rewrite make(final N n) {
      return !eligible(n) ? null : new Rewrite(description(n), n) {
        @Override public void go(final ASTRewrite r, final TextEditGroup g) {
          r.replace(n, replacement(n), g);
        }
      };
    }
    abstract ASTNode replacement(N n);
    @Override boolean scopeIncludes(final N n) {
      return replacement(n) != null;
    }
  }
}

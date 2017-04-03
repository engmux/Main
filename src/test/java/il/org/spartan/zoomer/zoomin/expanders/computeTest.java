package il.org.spartan.zoomer.zoomin.expanders;

import static il.org.spartan.azzert.*;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;
import org.junit.runners.*;

import il.org.spartan.*;
import il.org.spartan.spartanizer.ast.navigate.*;
import il.org.spartan.spartanizer.engine.*;
import il.org.spartan.spartanizer.meta.*;

/** Unit tests for {@link compute}
 * @author Yossi Gil <tt>yogi@cs.technion.ac.il</tt>
 * @since 2017-04-01 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("static-method")
public class computeTest extends MetaFixture {
  @Test public void updatedSpots() {
    compute.updateSpots(into.s("return a *=2"));
    azzert.that(compute.updateSpots(into.s("a")).size(), is(0));
    azzert.that(compute.updateSpots(into.s("--a")).size(), is(1));
    azzert.that(compute.updateSpots(into.s("a++")).size(), is(1));
    azzert.that(compute.updateSpots(into.s("a =2")).size(), is(1));
    azzert.that(compute.updateSpots(into.s("--a;")).size(), is(1));
    azzert.that(compute.updateSpots(into.s("a++;")).size(), is(1));
    azzert.that(compute.updateSpots(into.s("return a =2")).size(), is(1));
    azzert.that(compute.updateSpots(into.s("return a *=2")).size(), is(1));
    final List<ASTNode> updateSpots = compute.updateSpots(into.s("return local +=2"));
    azzert.that(updateSpots.size(), is(1));
    azzert.that(lisp.onlyOne(updateSpots) + "", is("local"));
    assert !updateSpots.stream().noneMatch(λ -> wizard.eq(λ, lisp.onlyOne(updateSpots).getAST().newSimpleName("local")));
  }
}

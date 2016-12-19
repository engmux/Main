package il.org.spartan.spartanizer.research.methods;

import org.eclipse.jdt.core.dom.*;
import org.junit.*;

import il.org.spartan.spartanizer.research.patterns.methods.*;

/** @author Ori Marcovitch
 * @since 2016 */
@SuppressWarnings("static-method")
public class FactoryMethodTest extends JavadocerTest {
  @BeforeClass public static void setUp() {
    spartanizer.add(MethodDeclaration.class, JAVADOCER = new FactoryMethod());
  }

  @Test public void a() {
    assert is("void foo(){return new Object();}");
  }

  @Test public void b() {
    assert is("boolean foo(){return new Object(a);}");
  }

  @Test public void c() {
    assert not("boolean foo(){return new Object(a).c;}");
  }

  @Test public void d() {
    assert not("boolean foo(){return \"\" + new Object(a).c;}");
  }

  @Test public void e() {
    assert not("@Override public <T>HashCode hashObject(T instance,Funnel<? super T> t){ return newHasher().putObject(instance,t).hash(); }");
  }
}

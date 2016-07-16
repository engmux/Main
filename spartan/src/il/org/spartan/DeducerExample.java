/** Part of the "Spartan Blog"; mutate the rest / but leave this line as is */
package il.org.spartan;

import static il.org.spartan.azzert.*;
import static org.junit.Assert.*;

import org.eclipse.jdt.annotation.*;
import org.junit.*;
import org.junit.runners.*;

/**
 * An example deducer centered on a cell #a, and other cells representing a
 * number of powers of this cell.`
 *
 * @author Yossi Gil <Yossi.Gil@GMail.COM>
 * @since 2016
 */
@SuppressWarnings({ "boxing", "null", "unused" }) public class DeducerExample extends Deducer {
  @Nullable Integer a() {
    return a.get();
  }
  final @Nullable Integer aPower2() {
    return aPower2.get();
  }
  final @Nullable Integer aPower3() {
    return aPower3.get();
  }
  final @Nullable Integer aPower5() {
    return aPower5.get();
  }
  final @Nullable Integer aPower17NullSafe() {
    return aPower17NullSafe.get();
  }

  /** For testing purposes only; must not be private */
  int _aPower2Calls;
  /** For testing purposes only; must not be private */
  int _aPower3Calls;
  /** Can, and often should be made private; package is OK */
  final Cell<@Nullable Integer> a = new Valued<@Nullable Integer>();
  /** Can, and often should be made private; package is OK */
  final Cell<@Nullable Integer> aPower2 = new Computed<@Nullable Integer>(() -> {
    _aPower2Calls++;
    return a() * a();
  }).dependsOn(a);
  /** Can, and often should be made private; package is OK */
  final Cell<@Nullable Integer> aPower3 = new Computed<@Nullable Integer>(() -> {
    ++_aPower3Calls;
    return aPower2() * a();
  }).dependsOn(aPower2, a);
  /** Can, and often should be made private; package is OK */
  final Cell<@Nullable Integer> aPower17NullSafe = new Computed<@Nullable Integer>(//
      () -> a() == null ? null //
          : a() * a() * a() * a() * aPower2() * aPower2() * aPower3() * aPower3() * aPower3()//
      ).dependsOn(a, aPower2, aPower3);
  /** Can, and often should be made private; package is OK */
  final Cell<@Nullable Integer> aPower5 = new Computed<@Nullable Integer>(//
      () -> aPower2() * aPower3()).dependsOn(aPower2, aPower3);

  @FixMethodOrder(value = MethodSorters.NAME_ASCENDING) @SuppressWarnings({ "javadoc" }) public static class TEST extends
  DeducerExample {
    @Test public void layerA05() {
      a.set(2);
      azzert.notNull(a());
    }
    @Test public void layerA06() {
      a.set(2);
      azzert.notNull(aPower2());
      azzert.that(aPower2(), is(4));
    }
    @Test public void layerA07() {
      a.set(2);
      azzert.notNull(aPower3());
      azzert.that(aPower3(), is(8));
    }
    @Test public void layerA08() {
      a.set(2);
      azzert.notNull(aPower2());
    }
    @Test public void layerA09() {
      a.set(null);
      azzert.isNull(a());
    }
    @Test public void layerA1() {
      azzert.isNull(a());
    }
    @Test(expected = NullPointerException.class) public void layerA10() {
      a.set(null);
      aPower2();
    }
    @Test(expected = NullPointerException.class) public void layerA11() {
      a.set(null);
      aPower3();
    }
    @Test(expected = NullPointerException.class) public void layerA12() {
      a.set(null);
      aPower2();
    }
    @Test public void layerA13() {
      a.set(null);
      azzert.isNull(aPower17NullSafe());
      a.set(2);
      azzert.notNull(aPower17NullSafe());
      azzert.that(a(), is(2));
    }
    @Test(expected = NullPointerException.class) public void layerA2() {
      aPower2().getClass();
    }
    @Test(expected = NullPointerException.class) public void layerA3() {
      aPower3().getClass();
    }
    @Test(expected = NullPointerException.class) public void layerA4() {
      aPower5().getClass();
    }
    @Test(expected = NullPointerException.class) public void layerA5() {
      a().toString().getClass();
    }
    @Test public void layerB01() {
      a.set(2);
      azzert.notNull(a());
      azzert.that(a(), is(2));
      a.set(3);
      azzert.that(a(), is(3));
      a.set(4);
      azzert.that(a(), is(4));
      a.set(null);
      azzert.isNull(a());
      a.set(5);
      azzert.that(a(), is(5));
    }
    @Test public void layerB02() {
      a.set(2);
      azzert.notNull(aPower2());
      azzert.that(aPower2(), is(4));
      a.set(3);
      azzert.notNull(aPower2());
      azzert.that(aPower2(), is(9));
    }
    @Test public void layerB03() {
      a.set(2);
      azzert.notNull(aPower3());
      azzert.that(aPower3(), is(8));
      a.set(3);
      azzert.notNull(aPower3());
      azzert.that(aPower3(), is(27));
    }
    @Test public void layerB04() {
      a.set(2);
      azzert.notNull(aPower2());
    }
    @Test public void layerC00() {
      a.set(-3);
      azzert.that(_aPower3Calls, is(0));
      azzert.that(_aPower2Calls, is(0));
    }
    @Test public void layerC01() {
      a.set(-3);
      azzert.that(aPower3(), is(-27));
      azzert.that(_aPower3Calls, is(1)); // Force invocation
      azzert.that(_aPower2Calls, is(1));
    }
    @Test public void layerC02() {
      azzert.that(a.version(), is(0L));
      azzert.that(aPower17NullSafe.version(), is(0L));
    }
    @Test public void layerC03() {
      azzert.that(aPower2.version(), is(0L));
      azzert.that(aPower3.version(), is(0L));
    }
    @Test public void layerC04() {
      a.set(-2);
      azzert.that(a.version(), is(1L));
      azzert.that(aPower3.version(), is(0L));
      azzert.that(_aPower3Calls, is(0));
      azzert.that(aPower17NullSafe(), is(-(1 << 17))); // Force invocation
      azzert.that(_aPower2Calls, is(1));
      azzert.that(_aPower3Calls, is(1));
    }
    @Test public void layerC05() {
      a.set(-2);
      azzert.that(aPower17NullSafe(), is(-(1 << 17))); // Force invocation
      azzert.that(_aPower2Calls, is(1));
      azzert.that(_aPower3Calls, is(1));
    }
    @Test public void layerD01() {
      azzert.that(a.version, is(0L));
      azzert.that(aPower2.version, is(0L));
      azzert.that(aPower3.version, is(0L));
      azzert.that(aPower17NullSafe.version, is(0L));
    }
    @Test public void layerD02() {
      azzert.that(a.version, is(0L));
      a.set(1);
      azzert.that(a.version, is(1L));
      azzert.that(aPower2.version, is(0L));
      azzert.that(aPower3.version, is(0L));
      azzert.that(aPower17NullSafe.version, is(0L));
    }
    @Test public void layerD03() {
      a.set(14);
      azzert.that(aPower2.version, is(0L));
      azzert.that(aPower2.get(), is(196)); // Force evaluation
      azzert.that(aPower3.version, is(0L));
      azzert.that(aPower2.version, is(2L));
      azzert.that(aPower17NullSafe.version, is(0L));
    }

    @Test public void layerD04() {
      a.set(14);
      azzert.notNull(a.get());
    }
    @Test public void layerD05() {
      a.set(14);
      azzert.notNull(a.get());
      azzert.that(a.get(), is(14));
      azzert.that(aPower2.get(), is(196)); // Sanity check
    }
    @Test public void layerD06() {
      a.set(14);
      azzert.notNull(a.get());
      a.get(); // Force evaluation
      azzert.that(aPower2.version(), is(0L));
      a.get(); // Force evaluation
      azzert.that(aPower2.version(), is(0L));
    }
    @Test public void layerD07() {
      a.set(14);
      azzert.notNull(a.get());
      a.get(); // Force evaluation
      azzert.not(((Computed<Integer>) aPower2).updated());
    }
    @Test public void layerD08() {
      a.set(14);
      azzert.that(a.get(), is(14)); // Force evaluation
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version, is(0L));
      azzert.that(((Computed<Integer>) aPower2).latestPrequisiteVersion(), is(1L));
    }
    @Test public void layerD09() {
      a.set(14);
      azzert.that(a.get(), is(14)); // Force evaluation
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version, is(0L));
      azzert.notNull(a.dependents);
    }
    @Test public void layerD10() {
      a.set(14);
      azzert.that(a.get(), is(14)); // Force evaluation
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version, is(0L));
      azzert.that(a.dependents.size(), is(3));
      azzert.assertTrue("", a.dependents.contains(aPower2));
      azzert.falze(a.dependents.contains(null));
    }
    @Test public void layerD11() {
      a.set(14);
      azzert.that(a.get(), is(14)); // Force evaluation
      azzert.that(a.version(), is(1L));
    }
    @Test public void layerD12() {
      assertTrue(a.dependents.contains(aPower2));
    }
    @Test public void layerD13() {
      assertTrue(a.dependents.contains(aPower3));
    }
    @Test public void layerD14() {
      assertFalse(a.dependents.contains(aPower5));
    }
    @Test public void layerD15() {
      assertTrue(a.dependents.contains(aPower17NullSafe));
    }
    @Test public void layerD16() {
      a.set(2);
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(_aPower3Calls, is(1));
    }
    @Test public void layerD17() {
      a.set(2);
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(_aPower2Calls, is(1));
      a.set(3);
      a.set(2);
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(aPower17NullSafe(), is(1 << 17));
      azzert.that(_aPower2Calls, is(2));
      azzert.that(_aPower3Calls, is(2));
    }
    @Test public void layerE00() {
      azzert.that(a.version(), is(0L));
      azzert.that(aPower2.version(), is(0L));
      azzert.that(aPower3.version(), is(0L));
      azzert.that(aPower5.version(), is(0L));
      azzert.that(aPower17NullSafe.version(), is(0L));
      a.set(2);
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version(), is(0L));
      azzert.that(aPower3.version(), is(0L));
      azzert.that(aPower5.version(), is(0L));
      azzert.that(aPower17NullSafe.version(), is(0L));
      aPower2();
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version(), is(2L));
      azzert.that(aPower3.version(), is(0L));
      azzert.that(aPower5.version(), is(0L));
      azzert.that(aPower17NullSafe.version(), is(0L));
      aPower3();
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version(), is(2L));
      azzert.that(aPower3.version(), is(3L));
      azzert.that(aPower5.version(), is(0L));
      azzert.that(aPower17NullSafe.version(), is(0L));
      aPower5();
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version(), is(2L));
      azzert.that(aPower3.version(), is(3L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(0L));
      aPower17NullSafe();
      azzert.that(a.version(), is(1L));
      azzert.that(aPower2.version(), is(2L));
      azzert.that(aPower3.version(), is(3L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      a.set(3);
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(2L));
      azzert.that(aPower3.version(), is(3L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower2();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(3L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower2();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(3L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower3();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(7L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower3();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(7L));
      azzert.that(aPower5.version(), is(4L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower5();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(7L));
      azzert.that(aPower5.version(), is(8L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower2();
      aPower3();
      aPower5();
      aPower5();
      aPower5();
      aPower3();
      aPower2();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(7L));
      azzert.that(aPower5.version(), is(8L));
      azzert.that(aPower17NullSafe.version(), is(4L));
      aPower17NullSafe();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(7L));
      azzert.that(aPower5.version(), is(8L));
      azzert.that(aPower17NullSafe.version(), is(8L));
      aPower17NullSafe();
      aPower2();
      aPower3();
      aPower5();
      aPower5();
      aPower17NullSafe();
      aPower5();
      aPower3();
      aPower2();
      aPower17NullSafe();
      azzert.that(a.version(), is(5L));
      azzert.that(aPower2.version(), is(6L));
      azzert.that(aPower3.version(), is(7L));
      azzert.that(aPower5.version(), is(8L));
      azzert.that(aPower17NullSafe.version(), is(8L));
    }
  }
}


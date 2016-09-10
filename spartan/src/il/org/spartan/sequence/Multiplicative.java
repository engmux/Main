package il.org.spartan.sequence;

public class Multiplicative extends Sequence {
  private static final double DEFAULT__STEP = 0.1;

  public static void main(final String[] args) {
    for (final Sequence s = new Multiplicative(0.16); s.current < 150; s.advance())
      System.out.print(s.current() + " ");
    System.out.println();
  }

  private final double step;

  public Multiplicative() {
    this(Sequence.MAX_VALUE, DEFAULT__STEP);
  }

  public Multiplicative(final double step) {
    this(Sequence.MAX_VALUE, step);
  }

  public Multiplicative(final int threshold, final double step) {
    super(threshold);
    reset();
    this.step = Math.pow(2, step);
  }

  @Override public Multiplicative advance() {
    final int prev = current;
    current *= step;
    if (current == prev)
      ++current;
    return this;
  }

  @Override public Multiplicative reset() {
    current = 1;
    return this;
  }
}

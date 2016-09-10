/**
 *
 */
package il.org.spartan.java;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;

import org.junit.*;
import org.junit.experimental.theories.*;
import org.junit.runner.*;

import il.org.spartan.files.visitors.*;
import il.org.spartan.files.visitors.FileSystemVisitor.*;
import il.org.spartan.utils.*;
import il.org.spatan.iteration.*;

/** @author Yossi Gil
 * @since 16/05/2011 */
@RunWith(Theories.class) @SuppressWarnings("static-method")
//
public class TestNoOther {
  @DataPoints public static File[] javaFiles() throws IOException {
    final Set<File> $ = new TreeSet<>();
    new JavaFilesVisitor(".", new PlainFileOnlyAction() {
      @Override public void visitFile(final File f) {
        $.add(f);
      }
    }).go();
    return Iterables.toArray($, File.class);
  }

  public static String read(final File f) throws IOException {
    final char[] $ = new char[(int) f.length()];
    final FileReader x = new FileReader(f);
    final int n = x.read($);
    x.close();
    return new String(Arrays.copyOf($, n));
  }

  public static void write(final File f, final String text) throws IOException {
    final Writer w = new FileWriter(f);
    w.write(text);
    w.close();
  }

  private final File fin = new File("test/data/UnicodeFile");

  @Test public void brace__brace__newline() throws IOException {
    assertEquals("{}\n", TokenAsIs.stringToString("{}\n"));
  }

  @Theory public void fullTokenization(final File f) throws IOException {
    System.err.println("Testing " + f);
    assertEquals(read(f), TokenAsIs.fileToString(f));
  }

  @Test public void some__method() throws IOException {
    final String s = Separate.nl(
        //
        "private static int circularSum(final int[] a, final int[] b, final int offset) {", //
        "  int $ = 0;", //
        " for (int i = 0; i < a.length; i++)", //
        "    $ += a[i] * b[(i + offset) % b.length];", //
        "  return $;", //
        "}", //
        " ", //
        " ", //
        "  ");
    assertEquals(s, TokenAsIs.stringToString(s));
  }

  @Test public void unicode() throws IOException {
    final String s = "יוסי";
    assertEquals(s, TokenAsIs.stringToString(s).toString());
  }

  @Test public void unicodeFileAgainstFileOutput() throws IOException {
    final String s = TokenAsIs.fileToString(fin);
    final File fout = new File(fin.getPath() + ".out");
    write(fout, s);
    assertEquals(s, read(fout));
    assertEquals(read(fin), read(fout));
  }

  @Test public void unicodeFileAgainstString() throws IOException {
    assertEquals(read(fin), TokenAsIs.fileToString(fin));
  }

  @Test public void unicodeFileLenth() throws IOException {
    assert fin.length() > TokenAsIs.fileToString(fin).length();
  }
}

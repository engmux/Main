package il.org.spartan.spartanizer.java.namespace.tables;

import java.util.*;

import org.eclipse.jdt.core.dom.*;
import il.org.spartan.spartanizer.cmdline.*;
import il.org.spartan.spartanizer.cmdline.ASTInFilesVisitor.*;
import il.org.spartan.spartanizer.cmdline.tables.*;
import il.org.spartan.tables.*;

/** Generates a table, counting constants in repositories
 * @author orimarco {@code marcovitch.ori@gmail.com}
 * @since 2017-02-08 */
public class Table_Function_Argument_Names extends NominalTables {
  public static void main(final String[] args) {
    namePrevelance = new HashMap<>();
    new ASTInFilesVisitor(args) {
      {
        listen(new Listener() {
          @Override public void endLocation() {
            done(getCurrentLocation());
          }
        });
      }

      protected void done(final String path) {
        summarize();
        // reset();
        System.err.println(" " + path + " Done");
      }
      public void summarize() {
        initializeWriter();
        namePrevelance.entrySet().stream().forEach(λ -> table.col("Name", λ.getKey()).col("Prev", λ.getValue()).nl());
      }
      void initializeWriter() {
        if (table == null)
          table = new Table(Table_Function_Argument_Names.class + "-" + corpus, outputFolder);
      }
    }.visitAll(new ASTVisitor(true) {
      @Override public boolean visit(final CompilationUnit ¢) {
        ¢.accept(new ASTVisitor() {
          @Override public boolean visit(final SimpleName x) {
            if (namePrevelance.containsKey(x + ""))
              namePrevelance.put(x + "", namePrevelance.get(x.toString()) + 1);
            else
              namePrevelance.put(x + "", 1);
            return true;
          }
        });
        return super.visit(¢);
      }
    });
    table.close();
    System.err.println(table.description());
  }
}

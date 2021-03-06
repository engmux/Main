package il.org.spartan.Leonidas.plugin.leonidas;

import il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.*;
import il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.Throwable;

import java.lang.Class;
import java.util.function.Supplier;

/**
 * @author Oren Afek
 * @since 29-03-2017.
 */
public abstract class The {
    static The the;

    public Method asMethod;
    public BooleanExpression asBooleanExpression;
    public il.org.spartan.Leonidas.plugin.leonidas.BasicBlocks.Class asClass;
    public Expression asExpression;
    public FieldDeclaration asFieldDeclaration;
    public Identifiable asIdentifier;
    public Statement asStatement;
    public StringLiteral asStringLiteral;
    public Throwable asThrowable;
    public Type asType;

    public static The element(int id) {
        return the;
    }

    public static The the(Object... os) {
        return the;
    }

    public abstract EndThe is(Runnable template);

    public abstract EndThe is(Supplier<?> template);

    public abstract EndThe isNot(Runnable template);

    public abstract EndThe isNot(Supplier<?> template);

    @SuppressWarnings("EmptyMethod")
    class EndThe {
        public <T> void ofType(Class<? extends T> __) {/**/}
    }
}

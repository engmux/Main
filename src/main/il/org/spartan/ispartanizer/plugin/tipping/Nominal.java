package il.org.spartan.ispartanizer.plugin.tipping;

/**
 * TODO @maorroey is it needed?
 * Created by Roey Maor on 12/3/2016.
 */
public interface Nominal extends TipperCategory {
    String label = "Nominal";

    @Override
    default String categoryDescription() {
        return label;
    }
}
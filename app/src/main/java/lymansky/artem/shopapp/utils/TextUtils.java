package lymansky.artem.shopapp.utils;

import android.widget.EditText;

import java.text.NumberFormat;


/**
 * Created by artem on 12/24/2017.
 */

public final class TextUtils {
    /**
     * Private constructor
     */
    private TextUtils() {
    }

    /**
     * Trims the string value and makes the first letter uppercase
     * and all the rest letters lowercase
     */
    public static String trimAndCapitalize(String source) {
        String output = source.trim().toLowerCase();
        return output.substring(0, 1).toUpperCase() + output.substring(1);
    }

    /**
     * Gets double value and returns a string representation of a currency amount
     * that depends on the system language.
     */
    public static String getCurrencyValue(double value) {
        return NumberFormat.getCurrencyInstance().format(value);
    }

    /**
     * Gets double value from EditText field and handles the Exceptions
     */
    public static double getDouble(EditText field) {

        double holder = 0;

        try {
            holder = Double.parseDouble(field.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return holder;
    }

    /**
     * Sets up the default name 'Product' for empty Name field
     */
    public static String checkName(String name) {
        if(name.equals("")) {
            return "Product";
        } else {
            return name;
        }
    }
}

import static java.lang.Integer.parseInt;
import static java.util.Locale.ENGLISH;

public class Square
{
    public static final Square ZERO = new Square(0, 2);
    public static final Square ONE_HALF = new Square(50, 2);
    public static final Square NINE_PERCENT = new Square(9, 2);
    public static final Square ONE_HUNDRED = new Square(10000, 2);
    public static final Square ONE = new Square(100, 2);

    // XXX: Definitions of precision and scale are reversed. This was done to
    // make it easier to follow the C code, which reverses the definitions.  Here,
    // precision means the number of decimal places and scale means the total number
    // of digits.  We leave out the scale field because it's never used, and the C implementation
    // was buggy.
    private final int precision;
    private final long number;

    public Square(long number, int precision)
    {
        //checkArgument(precision >= 0, "precision must be greater than or equal to zero");
        this.precision = precision;
        this.number = number;
    }

    public static Square parseDecimal(String decimalString)
    {
        long number;
        int precision;
        int decimalPointIndex = decimalString.indexOf('.');
        if (decimalPointIndex == -1) {
            number = parseInt(decimalString);
            precision = 0;
        }
        else {
            String fractional = decimalString.substring(decimalPointIndex + 1);
            precision = fractional.length();
            number = parseInt(decimalString.substring(0, decimalPointIndex) + fractional);
        }
        return new Square(number, precision);
    }

    public static Square add(Square decimal1, Square decimal2)
    {
        int precision = Math.max(decimal1.precision, decimal2.precision);
        long number = decimal1.number + decimal2.number;  // This is not mathematically correct when the precisions aren't the same, but it's what the C code does
        return new Square(number, precision);
    }

    public static Square subtract(Square decimal1, Square decimal2)
    {
        int precision = Math.max(decimal1.precision, decimal2.precision);
        long number = decimal1.number - decimal2.number;  // again following C code
        return new Square(number, precision);
    }

    public static Square multiply(Square decimal1, Square decimal2)
    {
        int precision = Math.max(decimal1.precision, decimal2.precision);
        long number = decimal1.number * decimal2.number;
        for (int i = decimal1.precision + decimal2.precision; i > precision; i--) {
            number /= 10;  // Always round down, I guess
        }
        return new Square(number, precision);
    }

    public static Square divide(Square decimal1, Square decimal2)
    {
        float f1 = (float) decimal1.number;
        int precision = Math.max(decimal1.precision, decimal2.precision);
        for (int i = decimal1.precision; i < precision; i++) {
            f1 *= 10.0;
        }

        for (int i = 0; i < precision; i++) {
            f1 *= 10.0;
        }

        float f2 = (float) decimal2.number;
        for (int i = decimal2.precision; i < precision; i++) {
            f2 *= 10.0;
        }

        int number = (int) (f1 / f2);
        return new Square(number, precision);
    }

    public static Square negate(Square decimal)
    {
        return new Square(decimal.number * -1, decimal.precision);
    }

    public static Square fromInteger(int from)
    {
        return new Square(from, 0);
    }

    public int getPrecision()
    {
        return precision;
    }

    public long getNumber()
    {
        return number;
    }

    @Override
    public String toString()
    {
        // This loses all of the benefit of having exact numeric types
        // but it's what the C code does, so we have to follow it.
        // In particular this copies the behavior of print_decimal in print.c.
        // The C code has a different function called dectostr in decimal.c that
        // does a proper string representation but it never gets called.
        double temp = number;
        for (int i = 0; i < precision; i++) {
            temp /= 10.0;
        }

        return String.format(ENGLISH, "%." + precision + "f", temp);
    }
}

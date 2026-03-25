import java.util.function.DoubleBinaryOperator;

@FunctionalInterface
interface SupportsArithmetic {
    boolean isSupported();
}

interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();

    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    default void validateOperationSupport(String operation) {
    }
}

enum ArithmeticOperation {
    ADD((a, b) -> a + b),
    SUBTRACT((a, b) -> a - b),
    DIVIDE((a, b) -> {
        if (Math.abs(b) < 1e-12) throw new ArithmeticException("Cannot divide by zero");
        return a / b;
    });

    private final DoubleBinaryOperator operation;

    ArithmeticOperation(DoubleBinaryOperator operation) {
        this.operation = operation;
    }

    public double compute(double a, double b) {
        return operation.applyAsDouble(a, b);
    }
}

enum LengthUnit implements IMeasurable {
    FEET(12.0),
    INCHES(1.0),
    CENTIMETERS(0.393701),
    YARDS(36.0);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    public String getUnitName() {
        return name().toLowerCase();
    }
}

enum WeightUnit implements IMeasurable {
    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    public String getUnitName() {
        return name().toLowerCase();
    }
}

enum VolumeUnit implements IMeasurable {
    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public double convertToBaseUnit(double value) {
        return value * conversionFactor;
    }

    public double convertFromBaseUnit(double baseValue) {
        return baseValue / conversionFactor;
    }

    public String getUnitName() {
        return name().toLowerCase();
    }
}

enum TemperatureUnit implements IMeasurable {
    CELSIUS {
        public double getConversionFactor() { return 1.0; }
        public double convertToBaseUnit(double value) { return value; }
        public double convertFromBaseUnit(double baseValue) { return baseValue; }
    },
    FAHRENHEIT {
        public double getConversionFactor() { return 1.0; }
        public double convertToBaseUnit(double value) { return (value - 32) * 5 / 9; }
        public double convertFromBaseUnit(double baseValue) { return (baseValue * 9 / 5) + 32; }
    },
    KELVIN {
        public double getConversionFactor() { return 1.0; }
        public double convertToBaseUnit(double value) { return value - 273.15; }
        public double convertFromBaseUnit(double baseValue) { return baseValue + 273.15; }
    };

    private final SupportsArithmetic supportsArithmetic = () -> false;

    public boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    public void validateOperationSupport(String operation) {
        throw new UnsupportedOperationException("Temperature does not support " + operation);
    }

    public String getUnitName() {
        return name().toLowerCase();
    }
}

class Quantity<U extends Enum<U> & IMeasurable> {
    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (unit == null) throw new IllegalArgumentException("Unit cannot be null");
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Invalid value");
        this.value = value;
        this.unit = unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private void validateArithmeticOperands(Quantity<U> other, String operation) {
        if (other == null) throw new IllegalArgumentException("Quantity cannot be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Incompatible units");
        if (!Double.isFinite(other.value))
            throw new IllegalArgumentException("Invalid value");

        if (!unit.supportsArithmetic())
            unit.validateOperationSupport(operation);
    }

    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation, String opName) {
        validateArithmeticOperands(other, opName);
        return operation.compute(this.toBase(), other.toBase());
    }

    private Quantity<U> fromBase(double baseValue, U targetUnit) {
        double val = targetUnit.convertFromBaseUnit(baseValue);
        return new Quantity<>(round(val), targetUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Quantity<?> that = (Quantity<?>) obj;
        if (this.unit.getClass() != that.unit.getClass()) return false;
        return Math.abs(this.toBase() - that.toBase()) < 1e-6;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(Math.round(toBase() * 1e6));
    }

    public Quantity<U> convertTo(U targetUnit) {
        if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
        return fromBase(toBase(), targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return fromBase(performBaseArithmetic(other, ArithmeticOperation.ADD, "addition"), this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return fromBase(performBaseArithmetic(other, ArithmeticOperation.SUBTRACT, "subtraction"), this.unit);
    }

    public double divide(Quantity<U> other) {
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE, "division");
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getUnitName());
    }
}

public class UC14 {
    public static void main(String[] args) {

        Quantity<TemperatureUnit> t1 = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> t2 = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);

        System.out.println(t1.equals(t2));
        System.out.println(t1.convertTo(TemperatureUnit.FAHRENHEIT));
        System.out.println(t2.convertTo(TemperatureUnit.CELSIUS));

        Quantity<TemperatureUnit> t3 = new Quantity<>(273.15, TemperatureUnit.KELVIN);
        System.out.println(t3.equals(t1));

        System.out.println(t1.add(t2)); 
    }
}
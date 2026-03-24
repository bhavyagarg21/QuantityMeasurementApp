import java.util.function.DoubleBinaryOperator;

interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
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

    private void validateArithmeticOperands(Quantity<U> other, U targetUnit, boolean targetRequired) {
        if (other == null) throw new IllegalArgumentException("Quantity cannot be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Incompatible units");
        if (!Double.isFinite(other.value))
            throw new IllegalArgumentException("Invalid value");
        if (targetRequired && targetUnit == null)
            throw new IllegalArgumentException("Target unit required");
    }

    private double performBaseArithmetic(Quantity<U> other, ArithmeticOperation operation) {
        validateArithmeticOperands(other, null, false);
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
        return fromBase(performBaseArithmetic(other, ArithmeticOperation.ADD), this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        return fromBase(performBaseArithmetic(other, ArithmeticOperation.ADD), targetUnit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        return fromBase(performBaseArithmetic(other, ArithmeticOperation.SUBTRACT), this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validateArithmeticOperands(other, targetUnit, true);
        return fromBase(performBaseArithmetic(other, ArithmeticOperation.SUBTRACT), targetUnit);
    }

    public double divide(Quantity<U> other) {
        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getUnitName());
    }
}

public class UC13 {
    public static <U extends Enum<U> & IMeasurable> boolean compare(Quantity<U> q1, Quantity<U> q2) {
        return q1.equals(q2);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2) {
        return q1.add(q2);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2, U target) {
        return q1.add(q2, target);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> subtract(Quantity<U> q1, Quantity<U> q2) {
        return q1.subtract(q2);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> subtract(Quantity<U> q1, Quantity<U> q2, U target) {
        return q1.subtract(q2, target);
    }

    public static <U extends Enum<U> & IMeasurable> double divide(Quantity<U> q1, Quantity<U> q2) {
        return q1.divide(q2);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> convert(Quantity<U> q, U target) {
        return q.convertTo(target);
    }

    public static void main(String[] args) {
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCHES);

        System.out.println(compare(l1, l2));
        System.out.println(add(l1, l2));
        System.out.println(subtract(l1, l2));
        System.out.println(divide(l1, l2));

        Quantity<WeightUnit> w1 = new Quantity<>(5.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(2000.0, WeightUnit.GRAM);

        System.out.println(compare(w1, w2));
        System.out.println(add(w1, w2));
        System.out.println(subtract(w1, w2));
        System.out.println(divide(w1, w2));

        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(500.0, VolumeUnit.MILLILITRE);

        System.out.println(compare(v1, v2));
        System.out.println(add(v1, v2));
        System.out.println(subtract(v1, v2));
        System.out.println(divide(v1, v2));
    }
}
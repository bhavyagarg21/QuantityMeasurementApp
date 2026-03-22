interface IMeasurable {
    double getConversionFactor();
    double convertToBaseUnit(double value);
    double convertFromBaseUnit(double baseValue);
    String getUnitName();
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

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    private double toBase() {
        return unit.convertToBaseUnit(value);
    }

    private double round(double val) {
        return Math.round(val * 100.0) / 100.0;
    }

    private void validate(Quantity<U> other) {
        if (other == null) throw new IllegalArgumentException("Quantity cannot be null");
        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Incompatible units");
        if (!Double.isFinite(other.value))
            throw new IllegalArgumentException("Invalid value");
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
        double base = toBase();
        double converted = targetUnit.convertFromBaseUnit(base);
        return new Quantity<>(round(converted), targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        validate(other);
        double sum = this.toBase() + other.toBase();
        return fromBase(sum, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        validate(other);
        if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
        double sum = this.toBase() + other.toBase();
        return fromBase(sum, targetUnit);
    }

    public Quantity<U> subtract(Quantity<U> other) {
        validate(other);
        double result = this.toBase() - other.toBase();
        return fromBase(result, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {
        validate(other);
        if (targetUnit == null) throw new IllegalArgumentException("Target unit cannot be null");
        double result = this.toBase() - other.toBase();
        return fromBase(result, targetUnit);
    }

    public double divide(Quantity<U> other) {
        validate(other);
        double divisor = other.toBase();
        if (Math.abs(divisor) < 1e-12) throw new ArithmeticException("Division by zero");
        return this.toBase() / divisor;
    }

    private Quantity<U> fromBase(double baseValue, U targetUnit) {
        double val = targetUnit.convertFromBaseUnit(baseValue);
        return new Quantity<>(round(val), targetUnit);
    }

    @Override
    public String toString() {
        return String.format("%.2f %s", value, unit.getUnitName());
    }
}

public class UC12 {

    public static <U extends Enum<U> & IMeasurable> boolean compare(Quantity<U> q1, Quantity<U> q2) {
        return q1.equals(q2);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2) {
        return q1.add(q2);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> add(Quantity<U> q1, Quantity<U> q2, U target) {
        return q1.add(q2, target);
    }

    public static <U extends Enum<U> & IMeasurable> Quantity<U> convert(Quantity<U> q, U target) {
        return q.convertTo(target);
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
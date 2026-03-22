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
        if (other == null) throw new IllegalArgumentException("Quantity cannot be null");
        double sum = this.toBase() + other.toBase();
        return fromBase(sum, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {
        if (other == null || targetUnit == null) throw new IllegalArgumentException("Invalid input");
        double sum = this.toBase() + other.toBase();
        return fromBase(sum, targetUnit);
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

public class UC11 {
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

    public static void main(String[] args) {
        Quantity<LengthUnit> l1 = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> l2 = new Quantity<>(12.0, LengthUnit.INCHES);

        System.out.println(compare(l1, l2));
        System.out.println(add(l1, l2));
        System.out.println(add(l1, l2, LengthUnit.INCHES));
        System.out.println(convert(l1, LengthUnit.CENTIMETERS));

        Quantity<WeightUnit> w1 = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> w2 = new Quantity<>(1000.0, WeightUnit.GRAM);

        System.out.println(compare(w1, w2));
        System.out.println(add(w1, w2));
        System.out.println(add(w1, w2, WeightUnit.POUND));

        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        System.out.println(compare(v1, v2));
        System.out.println(add(v1, v2));
        System.out.println(add(v1, v2, VolumeUnit.GALLON));
        System.out.println(convert(v1, VolumeUnit.GALLON));
    }
}
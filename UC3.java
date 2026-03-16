class Length{
    private double value;
    private LengthUnit unit;

    public enum LengthUnit{
        Feet(12.0),
        Inches(1.0);

        private final double conversionFactor;
        LengthUnit(double conversionFactor){
            this.conversionFactor=conversionFactor;
        }
        public double getConversionFactor(){
            return conversionFactor;
        }
    }
    public Length(double value, LengthUnit unit){
        this.value=value;
        this.unit=unit;
    }
    private double convertToBaseUnit(){
        return value*unit.getConversionFactor();
    }
    public boolean compare(Length thatLength){
        double val1 = this.convertToBaseUnit();
        double val2 = thatLength.convertToBaseUnit();
        return Double.compare(val1, val2) == 0;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(o==null || getClass()!=o.getClass()) return false;
        Length i=(Length)o;
        return Double.compare(this.convertToBaseUnit(),i.convertToBaseUnit())==0;
    }
}

public class UC3 {
     public static boolean demonstrateLengthEquality(Length length1, Length length2) {
        return length1.equals(length2);
    }

    public static void demonstrateFeetEquality() {
        Length length1 = new Length(2.0, Length.LengthUnit.Feet);
        Length length2 = new Length(2.0, Length.LengthUnit.Feet);

        boolean result = demonstrateLengthEquality(length1, length2);

        System.out.println("Feet Equality: " + result);
    }

    public static void demonstrateInchesEquality() {
        Length length1 = new Length(24.0, Length.LengthUnit.Inches);
        Length length2 = new Length(24.0, Length.LengthUnit.Inches);

        boolean result = demonstrateLengthEquality(length1, length2);
        System.out.println("Inches Equality: " + result);
    }
    public static void demonstrateFeetInchesComparison() {
        Length length1 = new Length(1.0, Length.LengthUnit.Feet);
        Length length2 = new Length(12.0, Length.LengthUnit.Inches);

        boolean result = demonstrateLengthEquality(length1, length2);

        System.out.println("Feet and Inches Equality: " + result);
    }

    public static void main(String[] args){
        Length l1=new Length(1.0, Length.LengthUnit.Feet);
        Length l2 = new Length(12.0, Length.LengthUnit.Inches);
        l1.equals(l2);

        demonstrateFeetEquality();
        demonstrateInchesEquality();
        demonstrateFeetInchesComparison();

    }
}

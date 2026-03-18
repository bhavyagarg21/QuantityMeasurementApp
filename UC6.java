class Length{
    private double value;
    private LengthUnit unit;

    public enum LengthUnit{
        Feet(12.0),
        Inches(1.0),
        Centimeters(0.393701),
        Yards(36.0);

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
    public Length convertTo(LengthUnit targetUnit){
        if(targetUnit==null) 
        throw new IllegalArgumentException("Target can't be null");

        double val=this.convertToBaseUnit();
        val=val/targetUnit.getConversionFactor();
        val=Double.parseDouble(String.format("%.2f",val));
        return new Length(val, targetUnit);
    }
    public Length add(Length thatLength){
        if (thatLength == null) 
        throw new IllegalArgumentException("Length cannot be null");

        double val1=this.convertToBaseUnit();
        double val2=thatLength.convertToBaseUnit();
        double sum=val1+val2;
        double res=convertFromBaseToTargetUnit(sum, unit);
        return new Length(res,unit);
    }
    private double convertFromBaseToTargetUnit(double lengthInInches,LengthUnit targetUnit){
       double val=lengthInInches/targetUnit.getConversionFactor();
       return Double.parseDouble(String.format("%.2f",val));
    }
    @Override
    public String toString(){
        return String.format("%.2f",value)+" "+unit;
    }
}
public class UC6{
    public static boolean demonstrateLengthEquality(Length length1, Length length2) {
        return length1.equals(length2);
    }
    public static Length demonstrateLengthAddition(Length l1,Length l2){
       if(l1==null || l2==null) 
       throw new IllegalArgumentException("Length cannot be null");
       return l1.add(l2);
    }
    public static boolean demonstrateLengthComparison(double val1, Length.LengthUnit unit1, double val2, Length.LengthUnit unit2){
        Length l1=new Length(val1, unit1);
        Length l2=new Length(val2, unit2);
        return l1.equals(l2);
    }
    public static Length demonstrateLengthConversion(double value, Length.LengthUnit fromUnit, Length.LengthUnit toUnit){
        if(fromUnit==null || toUnit==null) throw new IllegalArgumentException("Units can't be null");
        Length obj=new Length(value, fromUnit);
        return obj.convertTo(toUnit);
    }
    public static Length demonstrateLengthConversion(Length length, Length.LengthUnit toUnit){
        return length.convertTo(toUnit);
    }
    public static void main(String[] args){
        Length l1 = new Length(1.0, Length.LengthUnit.Feet);
        Length l2 = new Length(2.0, Length.LengthUnit.Feet);
        System.out.println(demonstrateLengthAddition(l1, l2));
    }
}





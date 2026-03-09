import java.util.*;
class QuantityMeasurementApp{
    public static class Feet{
        private final double value;
        public Feet(double value){
            this.value =value;
        } 
        @Override
        public boolean equals(Object obj){
            if(this==obj) return true;
            if(obj==null || getClass()!=obj.getClass()) return false;
            Feet temp=(Feet) obj;
            return Double.compare(this.value, temp.value)==0;
        }
    }
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        double val1=sc.nextDouble();
        double val2=sc.nextDouble();

        QuantityMeasurementApp.Feet obj1=new QuantityMeasurementApp.Feet(val1);
        QuantityMeasurementApp.Feet obj2=new QuantityMeasurementApp.Feet(val2);

        if(obj1.equals(obj2)) System.out.println("Both are equal in feet");
        else System.out.println("Values are different");
        sc.close();
    }
}
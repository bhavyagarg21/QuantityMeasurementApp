import java.util.*;
public class UC2 {
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
    public static class Inches{
        private final double value;
        public Inches(double value){
            this.value=value;
        }
        @Override
        public boolean equals(Object obj){
            if(this==obj) return true;
            if(obj==null || getClass()!=obj.getClass()) return false;
            Inches i=(Inches) obj;
            return Double.compare(this.value, i.value)==0;
        }
    }
    public static void demostrateFeetEquality(Feet f1, Feet f2){
        if(f1.equals(f2)) System.out.println("Both are equal in feet");
        else System.out.println("Values are different");
    }
    public static void demostrateInchesEquality(Inches i1,Inches i2){
        if(i1.equals(i2)) System.out.println("Both are equal in inches");
        else System.out.println("Values are different");
    }
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        double val1=sc.nextDouble();
        double val2=sc.nextDouble();

        UC2.Feet f1=new UC2.Feet(val1);
        UC2.Feet f2=new UC2.Feet(val2);

        // if(f1.equals(f2)) System.out.println("Both are equal in feet");
        // else System.out.println("Values are different");

        UC2.Inches i1=new UC2.Inches(val1);
        UC2.Inches i2=new UC2.Inches(val2);
        // if(i1.equals(i2)) System.out.println("both are equal in inches");
        // else System.out.println("Values are different");

        demostrateFeetEquality(f1, f2);
        demostrateInchesEquality(i1, i2);
        sc.close();
    }
}
    


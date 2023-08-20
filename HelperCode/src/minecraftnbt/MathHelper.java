package minecraftnbt;

public class MathHelper
{
	
    /**
     * Returns the greatest integer less than or equal to the float argument
     */
    public static int floor_float(float value)
    {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }
    
    /**
     * Returns the greatest integer less than or equal to the double argument
     */
    public static int floor_double(double value)
    {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
    
}
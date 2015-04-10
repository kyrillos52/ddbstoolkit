package org.ddbstoolkit.toolkit.core;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Comparator;

/**
 * Comparator to sort collections of objects
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class ObjectComparator implements Comparator<IEntity> {

    /**
     * Field to compare
     */
    private String comparatorField;

    /**
     * Order to use
     */
    private String orderBy;

    /**
     * Constructor
     * @param orderByString Order by string
     */
    public ObjectComparator(String orderByString) {

        String[] list = orderByString.split(" ");
        if(list.length == 2)
        {
             comparatorField = list[0];
             orderBy = list[1];
        }
    }

    @Override
    public int compare(IEntity iEntity1, IEntity iEntity2) {

        if(comparatorField != null && !comparatorField.equals("") && orderBy != null && !orderBy.equals(""))
        {
            try{
                Field fieldObject1 = iEntity1.getClass().getField(comparatorField);
                Field fieldObject2 = iEntity2.getClass().getField(comparatorField);

                int compareInt = 0;

                if(fieldObject1.getType().getName().equals("int"))
                {
                    Integer myInt = (Integer)fieldObject1.get(iEntity1);
                    compareInt = myInt.compareTo((Integer)fieldObject2.get(iEntity2));
                }
                else if(fieldObject1.getType().getName().equals("long"))
                {
                    Long myLong = (Long)fieldObject1.get(iEntity1);
                    compareInt = myLong.compareTo((Long)fieldObject2.get(iEntity2));
                }
                else if(fieldObject1.getType().getName().equals("float"))
                {
                    Float myFloat = (Float)fieldObject1.get(iEntity1);
                    compareInt = myFloat.compareTo((Float)fieldObject2.get(iEntity2));
                }
                else if(fieldObject1.getType().getName().equals("java.lang.String"))
                {
                    String myString = (String)fieldObject1.get(iEntity1);
                    compareInt = myString.compareTo((String)fieldObject2.get(iEntity2));
                }
                else if(fieldObject1.getType().getName().equals("Timestamp"))
                {
                    Timestamp myTime1 = (Timestamp)fieldObject1.get(iEntity1);
                    Timestamp myTime2 = (Timestamp)fieldObject2.get(iEntity2);
                    if(myTime1.getTime() == myTime2.getTime())
                    {
                        compareInt = 0;
                    }
                    else if(myTime1.getTime() > myTime2.getTime())
                    {
                        compareInt = 1;
                    }
                    else
                    {
                        compareInt = -1;
                    }
                }

                //If ascendant order, compare normally
                if(orderBy.equals("ASC"))
                {
                    return compareInt;
                }
                //Otherwise, return the contrary
                else
                {
                    return -compareInt;
                }
            }
            catch (NoSuchFieldException ex)
            {
                ex.printStackTrace();
                System.out.println("Field "+comparatorField+" doesn't exist");
                return 0;
            }
            catch (IllegalAccessException ex2)
            {
                ex2.printStackTrace();
                System.out.println("Can't access to the value of "+comparatorField);
                return 0;
            }
        }
        else
        {
            return  0;
        }
    }
}

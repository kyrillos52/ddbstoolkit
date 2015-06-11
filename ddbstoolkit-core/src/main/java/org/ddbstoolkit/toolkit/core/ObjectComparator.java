package org.ddbstoolkit.toolkit.core;

import java.sql.Timestamp;
import java.util.Comparator;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;
import org.ddbstoolkit.toolkit.core.orderby.OrderByType;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * Comparator to sort collections of objects
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class ObjectComparator implements Comparator<IEntity> {

    /**
     * Field to compare
     */
	private DDBSEntityProperty ddbsEntityProperty;

    /**
     * Order to use
     */
    private OrderBy orderBy;

    /**
     * Constructor
     * @param orderBy Order by object
     */
    public ObjectComparator(DDBSEntity<DDBSEntityProperty> ddbsEntity, OrderBy orderBy) {

        this.ddbsEntityProperty = ddbsEntity.getDDBSEntityProperty(orderBy.getName());
        this.orderBy = orderBy;
    }

    @Override
    public int compare(IEntity iEntity1, IEntity iEntity2) {

        int compareInt = 0;
        
        if(ddbsEntityProperty.getDdbsToolkitSupportedEntity() != null) {
        	
        	if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.INTEGER)) {
        		
        		compareInt = Integer.compare((Integer)ddbsEntityProperty.getValue(iEntity1), (Integer)ddbsEntityProperty.getValue(iEntity2));

        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.LONG)) {
        		
        		compareInt = Long.compare((Long)ddbsEntityProperty.getValue(iEntity1), (Long)ddbsEntityProperty.getValue(iEntity2));
                
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.FLOAT)) {
        		
        		compareInt = Float.compare((Float)ddbsEntityProperty.getValue(iEntity1), (Float)ddbsEntityProperty.getValue(iEntity2));
                
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.STRING)) {
        			   
        		String myString = (String)ddbsEntityProperty.getValue(iEntity1);
                compareInt = myString.compareTo((String)ddbsEntityProperty.getValue(iEntity2));
 
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.TIMESTAMP)) {
        		
        		Timestamp myTime1 = (Timestamp)ddbsEntityProperty.getValue(iEntity1);
                Timestamp myTime2 = (Timestamp)ddbsEntityProperty.getValue(iEntity2);
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
        }

        //If ascendant order, compare normally
        if(orderBy.getType().equals(OrderByType.ASC))
        {
            return compareInt;
        }
        //Otherwise, return the contrary
        else
        {
            return -compareInt;
        }
    }
}

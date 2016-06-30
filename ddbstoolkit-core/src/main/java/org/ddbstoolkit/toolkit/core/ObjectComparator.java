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
	private final DDBSEntityProperty ddbsEntityProperty;

    /**
     * Order to use
     */
    private final OrderBy orderBy;

    /**
     * Constructor
     * @param ddbsEntity Entity to compare
     * @param orderBy Order by object
     */
    public ObjectComparator(DDBSEntity<DDBSEntityProperty> ddbsEntity, OrderBy orderBy) {

        this.ddbsEntityProperty = ddbsEntity.getDDBSEntityProperty(orderBy.getName());
        this.orderBy = orderBy;
    }

    @Override
    public int compare(IEntity iEntity1, IEntity iEntity2) {

        int compareInt = 0;
        
        Object valueEntity1 = ddbsEntityProperty.getValue(iEntity1);
        Object valueEntity2 = ddbsEntityProperty.getValue(iEntity2);
        
        if(valueEntity1 == null && valueEntity2 == null) {
        	compareInt = 0;
        } else if(valueEntity1 == null && valueEntity2 != null) {
        	compareInt = -1;
        } else if(valueEntity1 != null && valueEntity2 == null) {
        	compareInt = 1;
        } else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity() != null) {
        	
        	if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.INTEGER)) {
        		
        		compareInt = Integer.compare((Integer)valueEntity1, (Integer)valueEntity2);

        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.LONG)) {
        		
        		compareInt = Long.compare((Long)valueEntity1, (Long)valueEntity2);
                
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.FLOAT)) {
        		
        		compareInt = Float.compare((Float)valueEntity1, (Float)valueEntity2);
                
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.DOUBLE)) {
        		
        		compareInt = Double.compare((Double)valueEntity1, (Double)valueEntity2);
                
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.STRING)) {
        			   
        		String myString = (String)valueEntity1;
                compareInt = myString.compareTo((String)valueEntity2);
 
        	} else if(ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(DDBSToolkitSupportedEntity.TIMESTAMP)) {
        		
        		Timestamp myTime1 = (Timestamp)valueEntity1;
                Timestamp myTime2 = (Timestamp)valueEntity2;
                if(myTime1.getTime() == myTime2.getTime()) {
                    compareInt = 0;
                } else if(myTime1.getTime() > myTime2.getTime()) {
                    compareInt = 1;
                } else {
                    compareInt = -1;
                }
        	} 
        }
        

        //If ascendant order, compare normally
        if(orderBy.getType().equals(OrderByType.ASC)) {
            return compareInt;
        } else {
        	//Otherwise, return the contrary
            return -compareInt;
        }
    }
}

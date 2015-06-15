package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;

import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;

/**
 * DDBS command sent through a network
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class DDBSCommand implements Serializable {

    /**
	 * Version Id
	 */
	private static final long serialVersionUID = 8335153155410423134L;

	/**
     * Destination of the command
     */
    private Peer destination;

    /**
     * Action to perform
     */
    private DDBSAction action;

    /**
     * Object to send
     */
    private IEntity object;

    /**
     * Query string of all conditions of a query : listAll command only
     */
    private String conditionQueryString;
    
    /**
     * List of conditions of a query
     */
    private Conditions conditions;

    /**
     * Order by condition : listAll and loadArray command only
     */
    private OrderBy orderBy;

    /**
     * Field to load : loadArray command only
     */
    private String fieldToLoad;

    /**
     * Get the action of a command
     * @return Command action
     */
    public DDBSAction getAction() {
		return action;
	}

    /**
     * Set the action of a command
     * @param action Action of a command
     */
	public void setAction(DDBSAction action) {
		this.action = action;
	}

	/**
     * Get the object of a command
     * @return Object of a command
     */
    public IEntity getObject() {
        return object;
    }

    /**
     * Set the object of a command
     * @param object Object of a command
     */
    public void setObject(IEntity object) {
        this.object = object;
    }
    
    /**
     * Get the condition query string : listAll command only
     * @return List of conditions
     */
    public String getConditionQueryString() {
		return conditionQueryString;
	}

    /**
     * Set the conditions query string of a command : listAll command only
     * @param conditionQueryString list of conditions
     */
	public void setConditionQueryString(String conditionQueryString) {
		this.conditionQueryString = conditionQueryString;
	}
	
	/**
     * Get the conditions of a query : listAll command only
     * @return List of conditions
     */
    public Conditions getConditions() {
		return conditions;
	}

    /**
     * Set the conditions of a query : listAll command only
     * @return List of conditions
     */
	public void setConditions(Conditions conditions) {
		this.conditions = conditions;
	}

	/**
     * Get the order by value : listAll command only
     * @return  Order by value
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }

    /**
     * Set the order by value : listAll command only
     * @param orderBy Order by value
     */
    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Get the destination of a command
     * @return destination of a command
     */
    public Peer getDestination() {
        return destination;
    }

    /**
     * Set the destination of a command
     * @param destination destination of a command
     */
    public void setDestination(Peer destination) {
        this.destination = destination;
    }

    /**
     * Get the field to load : loadArray command only
     * @return field to load
     */
    public String getFieldToLoad() {
        return fieldToLoad;
    }

    /**
     * Set the field to load : loadArray command only
     * @param fieldToLoad field to load
     */
    public void setFieldToLoad(String fieldToLoad) {
        this.fieldToLoad = fieldToLoad;
    }

	@Override
	public String toString() {
		return "DDBSCommand [destination=" + destination + ", action=" + action
				+ ", object=" + object + ", conditionQueryString="
				+ conditionQueryString + ", conditions=" + conditions
				+ ", orderBy=" + orderBy + ", fieldToLoad=" + fieldToLoad + "]";
	}
}

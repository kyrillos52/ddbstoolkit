package org.ddbstoolkit.toolkit.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents commands sent through a network
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
    private String destination;

    /**
     * Action to do
     * 1 = listAll
     * 2 = read
     * 3 = readLastElement
     * 4 = add
     * 5 = update
     * 6 = delete
     * 7 = listPeersName
     * 8 = loadArray
     */
    private int action;

    /**
     * Object to send
     */
    private IEntity object;

    /**
     * List of conditions : listAll command only
     */
    private List<String> conditionList;

    /**
     * Order by condition : listAll and loadArray command only
     */
    private String orderBy;

    /**
     * Field to load : loadArray command only
     */
    private String fieldToLoad;
    
    /**
     * List all elements action number
     */
    public final static int LIST_ALL_COMMAND = 1;

    /**
     * Read element action number
     */
    public final static int READ_COMMAND = 2;

    /**
     * Read last element action number
     */
    public final static int READ_LAST_ELEMENT_COMMAND = 3;

    /**
     * Add an element action number
     */
    public final static int ADD_COMMAND = 4;

    /**
     * Update an element action number
     */
    public final static int UPDATE_COMMAND = 5;

    /**
     * Delete an element action number
     */
    public final static int DELETE_COMMAND = 6;

    /**
     * List all peers action number
     */
    public final static int LIST_PEERS_COMMAND = 7;

    /**
     * Load an array of objects action number
     */
    public final static int LOAD_ARRAY_COMMAND = 8;

    /**
     * Create an entity action number
     */
    public final static int CREATE_ENTITY = 9;

    /**
     * Code action to send to all peers
     */
    public final static String DESTINATION_ALL_PEERS = "ALL";

    /**
     * Get the action number of a command
     * @return Action number corresponding to an action
     */
    public int getAction() {
        return action;
    }

    /**
     * Set the action number of a command
     * @param Action number corresponding to an action
     */
    public void setAction(int action) {
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
     * Get the list of conditions of a command : listAll command only
     * @return List of conditions
     */
    public List<String> getConditionList() {
        return conditionList;
    }

    /**
     * Set the list of conditions of a command : listAll command only
     * @param conditionList list of conditions
     */
    public void setConditionList(List<String> conditionList) {
        this.conditionList = conditionList;
    }

    /**
     * Get the order by value : listAll command only
     * @return  Order by value
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * Set the order by value : listAll command only
     * @param orderBy Order by value
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * Get the destination of a command
     * @return destination of a command
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Set the destination of a command
     * @param destination destination of a command
     */
    public void setDestination(String destination) {
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
				+ ", object=" + object + ", conditionList=" + conditionList
				+ ", orderBy=" + orderBy + ", fieldToLoad=" + fieldToLoad + "]";
	}
}

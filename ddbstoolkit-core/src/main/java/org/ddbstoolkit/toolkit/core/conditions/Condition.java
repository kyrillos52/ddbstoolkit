package org.ddbstoolkit.toolkit.core.conditions;

import java.io.Serializable;

/**
 * Condition of a query
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 * Used for the following condition type
 * IS_NULL,
 * IS_NOT_NULL
 */
public class Condition implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Property name
	 */
	protected String name;
	
	/**
	 * Condition Type
	 */
	protected ConditionType conditionType;
	
	/**
	 * Get property name
	 * @return Property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get condition type
	 * @return Condition Type
	 */
	public ConditionType getConditionType() {
		return conditionType;
	}

	public Condition(String name, ConditionType conditionType) {
		super();
		this.name = name;
		this.conditionType = conditionType;
	}

	@Override
	public String toString() {
		return "Condition [name=" + name + ", conditionType="
				+ conditionType + "]";
	}
}

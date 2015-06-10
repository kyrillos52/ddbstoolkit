package org.ddbstoolkit.toolkit.core.conditions;

import java.util.List;

/**
 * Condition with a starting and ending value
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 * Used for the following condition type
 * IN
 * NOT_IN
 */
public class ConditionInValues extends Condition {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * List of values
	 */
	private List<Object> values;
	
	/**
	 * Get list of values
	 * @return List of values
	 */
	public List<Object> getValues() {
		return values;
	}

	public ConditionInValues(String propertyName, ConditionType conditionType,
			List<Object> values) {
		super(propertyName, conditionType);
		this.values = values;
	}

	@Override
	public String toString() {
		return "ConditionInValues [values=" + values + ", name="
				+ name + ", conditionType=" + conditionType + "]";
	}
}

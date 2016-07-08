package org.ddbstoolkit.toolkit.core.conditions;

import java.util.List;

/**
 * Condition with a list of values
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
	private final List<? extends Object> values;
	
	/**
	 * Get list of values
	 * @return List of values
	 */
	public List<? extends Object> getValues() {
		return values;
	}

	public ConditionInValues(String propertyName, ConditionType conditionType,
			List<? extends Object> values) {
		super(propertyName, conditionType);
		this.values = values;
	}

	@Override
	public String toString() {
		return "ConditionInValues [values=" + values + ", name="
				+ name + ", conditionType=" + conditionType + "]";
	}
}

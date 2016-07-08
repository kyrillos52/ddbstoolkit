package org.ddbstoolkit.toolkit.core.conditions;

/**
 * Condition with single value
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 * Used for the following condition type
 * EQUAL,
 * NOT_EQUAL,
 * LESS_THAN,
 * GREATER_THAN,
 * LESS_THAN_OR_EQUAL,
 * GREATER_THAN_OR_EQUAL,
 * LIKE
 */
public class ConditionSingleValue extends Condition {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Value
	 */
	private final Object value;
	
	public ConditionSingleValue(String propertyName,
			ConditionType conditionType, Object value) {
		super(propertyName, conditionType);
		this.value = value;
	}
	
	/**
	 * Get value
	 * @return value
	 */
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ConditionSingleValue [value=" + value + ", name="
				+ name + ", conditionType=" + conditionType + "]";
	}
}

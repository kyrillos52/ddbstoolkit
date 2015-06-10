package org.ddbstoolkit.toolkit.core.conditions;

/**
 * Condition with a starting and ending value
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 * Used for the following condition type
 * BETWEEN
 */
public class ConditionBetweenValue extends Condition {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Starting value
	 */
	private Object startingValue;
	
	/**
	 * Ending value
	 */
	private Object endingValue;
	
	/**
	 * Get starting value
	 * @return Starting value
	 */
	public Object getStartingValue() {
		return startingValue;
	}

	/**
	 * Get ending value
	 * @return Ending value
	 */
	public Object getEndingValue() {
		return endingValue;
	}

	public ConditionBetweenValue(String propertyName,
			ConditionType conditionType, Object startingValue,
			Object endingValue) {
		super(propertyName, conditionType);
		this.startingValue = startingValue;
		this.endingValue = endingValue;
	}

	@Override
	public String toString() {
		return "ConditionBetweenValue [startingValue=" + startingValue
				+ ", endingValue=" + endingValue + ", name="
				+ name + ", conditionType=" + conditionType + "]";
	}
}

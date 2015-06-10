package org.ddbstoolkit.toolkit.core.conditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Conditions of a query
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class Conditions implements Serializable {
	
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * List of conditions
	 */
	private List<Condition> conditions;

	private Conditions(List<Condition> conditions) {
		super();
		this.conditions = conditions;
	}
	
	/**
	 * Create some conditions
	 * @return Conditions
	 */
	public static Conditions createConditions() {
		return new Conditions(new ArrayList<Condition>());
	}
	
	/**
	 * Add a condition
	 * @param condition Condition
	 * @return condition
	 */
	public Conditions add(Condition condition) {
		conditions.add(condition);
		return this;
	}
	
	/**
	 * Equal condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition eq(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.EQUAL, value);
	}
	
	/**
	 * Not equal condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition ne(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.NOT_EQUAL, value);
	}
	
	/**
	 * Less than condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition lt(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.LESS_THAN, value);
	}
	
	/**
	 * Greater than condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition gt(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.GREATER_THAN, value);
	}
	
	/**
	 * Less than or equal condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition le(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.LESS_THAN_OR_EQUAL, value);
	}
	
	/**
	 * Greater than or equal condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition ge(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.GREATER_THAN_OR_EQUAL, value);
	}
	
	/**
	 * Like condition
	 * @param propertyName Name of the property
	 * @param value Value
	 * @return condition
	 */
	public static Condition like(String propertyName, Object value) {
		return new ConditionSingleValue(propertyName, ConditionType.LIKE, value);
	}
	
	/**
	 * Is null condition
	 * @param propertyName Name of the property
	 * @return condition
	 */
	public static Condition isNull(String propertyName, Object value) {
		return new Condition(propertyName, ConditionType.IS_NULL);
	}
	
	/**
	 * Is not null condition
	 * @param propertyName Name of the property
	 * @return condition
	 */
	public static Condition isNotNull(String propertyName, Object value) {
		return new Condition(propertyName, ConditionType.IS_NOT_NULL);
	}
	
	/**
	 * Between condition
	 * @param propertyName Name of the property
	 * @param startingValue Starting value
	 * @param endingValue Ending value
	 * @return condition
	 */
	public static Condition between(String propertyName, Object startingValue, Object endingValue) {
		return new ConditionBetweenValue(propertyName, ConditionType.BETWEEN, startingValue, endingValue);
	}
	
	/**
	 * Not between condition
	 * @param propertyName Name of the property
	 * @param startingValue Starting value
	 * @param endingValue Ending value
	 * @return condition
	 */
	public static Condition notBetween(String propertyName, Object startingValue, Object endingValue) {
		return new ConditionBetweenValue(propertyName, ConditionType.NOT_BETWEEN, startingValue, endingValue);
	}


	/**
	 * In condition
	 * @param propertyName Name of the property
	 * @param values List of values
	 * @return condition
	 */
	public static Condition in(String propertyName, List<Object> values) {
		return new ConditionInValues(propertyName, ConditionType.IN, values);
	}
	
	/**
	 * Not in condition
	 * @param propertyName Name of the property
	 * @param values List of values
	 * @return condition
	 */
	public static Condition notIn(String propertyName, List<Object> values) {
		return new ConditionInValues(propertyName, ConditionType.NOT_IN, values);
	}

	/**
	 * Get the list of conditions
	 * @return List of conditions
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	@Override
	public String toString() {
		return "Conditions [conditions=" + conditions + "]";
	}
	
}

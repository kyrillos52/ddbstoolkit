package org.ddbstoolkit.toolkit.core.orderby;

import java.io.Serializable;

/**
 * Order By
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class OrderBy implements Serializable {

	/**
	 * Serial Version Id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Property name
	 */
	private String propertyName;
	
	/**
	 * Order By type
	 */
	private OrderByType type;
	
	/**
	 * Get property name
	 * @return Property name
	 */
	public String getPropertyName() {
		return propertyName;
	}
	
	/**
	 * Get order by type
	 * @return Order By Type
	 */
	public OrderByType getType() {
		return type;
	}

	private OrderBy(String propertyName, OrderByType type) {
		super();
		this.propertyName = propertyName;
		this.type = type;
	}
	
	/**
	 * Get Order By element
	 * @param propertyName Property name
	 * @param type Order By type
	 * @return OrderBy object
	 */
	public static OrderBy get(String propertyName, OrderByType type) {
		return new OrderBy(propertyName, type);
	}

	@Override
	public String toString() {
		return "OrderBy [propertyName=" + propertyName + ", type=" + type + "]";
	}
}

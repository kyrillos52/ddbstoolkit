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
	 * Name
	 */
	private String name;
	
	/**
	 * Order By type
	 */
	private OrderByType type;
	
	/**
	 * Get Name
	 * @return Property name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get order by type
	 * @return Order By Type
	 */
	public OrderByType getType() {
		return type;
	}

	private OrderBy(String name, OrderByType type) {
		super();
		this.name = name;
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
		return "OrderBy [name=" + name + ", type=" + type + "]";
	}
}

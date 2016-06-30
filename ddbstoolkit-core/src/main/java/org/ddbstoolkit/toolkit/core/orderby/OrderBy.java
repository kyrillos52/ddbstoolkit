package org.ddbstoolkit.toolkit.core.orderby;

import java.io.Serializable;

/**
 * Order By
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public final class OrderBy implements Serializable {

	/**
	 * Serial Version Id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Name
	 */
	private final String name;
	
	/**
	 * Order By type
	 */
	private final OrderByType type;
	
	private OrderBy(String name, OrderByType type) {
		super();
		this.name = name;
		this.type = type;
	}
	
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

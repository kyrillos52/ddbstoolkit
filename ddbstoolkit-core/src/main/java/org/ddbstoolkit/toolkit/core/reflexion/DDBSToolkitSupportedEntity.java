package org.ddbstoolkit.toolkit.core.reflexion;

/**
 * DDBS Toolkit supported entities
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public enum DDBSToolkitSupportedEntity {
	
	INTEGER("int"),
	LONG("long"),
	FLOAT("float"),
	STRING("java.lang.String"),
	TIMESTAMP("java.sql.Timestamp");
	
	String type;

	private DDBSToolkitSupportedEntity(String type) {
		this.type = type;
	}
	
	/**
	 * Get type
	 * @return
	 */
	public String getType() {
		return type;
	}

}

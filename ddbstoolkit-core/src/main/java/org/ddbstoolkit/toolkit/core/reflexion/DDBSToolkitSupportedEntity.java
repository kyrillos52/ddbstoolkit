package org.ddbstoolkit.toolkit.core.reflexion;

/**
 * DDBS Toolkit supported entities
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public enum DDBSToolkitSupportedEntity {
	
	INTEGER("int","[I"),
	LONG("long","[J"),
	FLOAT("float", "[F"),
	STRING("java.lang.String", "[Ljava.lang.String;"),
	TIMESTAMP("java.sql.Timestamp", "TODO");
	
	
	String type;
	
	String arrayType;

	private DDBSToolkitSupportedEntity(String type, String arrayType) {
		this.type = type;
	}
	
	/**
	 * Get type
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * Array Type
	 * @return
	 */
	public String getArrayType() {
		return arrayType;
	}
	
	public static DDBSToolkitSupportedEntity valueOf(boolean isArray, String type)
	{
		for(DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity : DDBSToolkitSupportedEntity.values())
		{
			if(isArray && type.equals(ddbsToolkitSupportedEntity.getArrayType()))
			{
				return ddbsToolkitSupportedEntity;
			}
			else if(!isArray && type.equals(ddbsToolkitSupportedEntity.getType()))
			{
				return ddbsToolkitSupportedEntity;
			}
		}
		return null;
	}
}

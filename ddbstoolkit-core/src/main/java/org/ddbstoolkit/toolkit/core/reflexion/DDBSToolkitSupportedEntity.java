package org.ddbstoolkit.toolkit.core.reflexion;

import java.lang.reflect.Field;

import org.ddbstoolkit.toolkit.core.IEntity;

/**
 * DDBS Toolkit supported entities
 * 
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class DDBSToolkitSupportedEntity {

	public static final DDBSToolkitSupportedEntity INTEGER = new DDBSToolkitSupportedEntity(
			new String[] { "int", "java.lang.Integer" });
	public static final DDBSToolkitSupportedEntity LONG = new DDBSToolkitSupportedEntity(
			new String[] { "long", "java.lang.Long" });
	public static final DDBSToolkitSupportedEntity FLOAT = new DDBSToolkitSupportedEntity(
			new String[] { "float", "java.lang.Float" });
	public static final DDBSToolkitSupportedEntity DOUBLE = new DDBSToolkitSupportedEntity(
			new String[] { "double", "java.lang.Double" });
	public static final DDBSToolkitSupportedEntity STRING = new DDBSToolkitSupportedEntity(
			new String[] { "java.lang.String" });
	public static final DDBSToolkitSupportedEntity TIMESTAMP = new DDBSToolkitSupportedEntity(
			new String[] { "java.sql.Timestamp" });
	public static final DDBSToolkitSupportedEntity IENTITY_ARRAY = new DDBSToolkitSupportedEntity(
			new String[] {});

	public static final DDBSToolkitSupportedEntity[] SUPPORTED_ENTITIES = { INTEGER, LONG, FLOAT, DOUBLE, STRING, TIMESTAMP, IENTITY_ARRAY };

	protected String[] types;

	public DDBSToolkitSupportedEntity(String[] types) {
		this.types = types;
	}

	/**
	 * Get type
	 * 
	 * @return Get the supported types
	 */
	public String[] getTypes() {
		return types;
	}

	public static DDBSToolkitSupportedEntity valueOf(Field field)
			throws IllegalArgumentException, IllegalAccessException {
		if (!field.getType().isArray()) {
			for (DDBSToolkitSupportedEntity ddbsToolkitSupportedEntity : SUPPORTED_ENTITIES) {
				for (String type : ddbsToolkitSupportedEntity.getTypes()) {
					if (type.equals(field.getType().getName())) {
						return ddbsToolkitSupportedEntity;
					}
				}
			}
		} else {
			
			try {
				
				String className = field.getType().getName().substring(2, field.getType().getName().length()-1);
				
				Object object = Class.forName(className).newInstance();
				
				if(object instanceof IEntity) {
					return IENTITY_ARRAY;
				}
				
			} catch (ClassNotFoundException | InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}

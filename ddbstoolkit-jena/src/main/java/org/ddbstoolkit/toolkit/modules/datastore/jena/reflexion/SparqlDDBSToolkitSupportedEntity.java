package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.lang.reflect.Field;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * Sparql DDBS Toolkit supported entities
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlDDBSToolkitSupportedEntity extends DDBSToolkitSupportedEntity {

	public static final DDBSToolkitSupportedEntity INTEGER_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Integer;"});
	public static final DDBSToolkitSupportedEntity LONG_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Long;"});
	public static final DDBSToolkitSupportedEntity FLOAT_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Float;"});
	public static final DDBSToolkitSupportedEntity DOUBLE_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Double;"});
	public static final DDBSToolkitSupportedEntity STRING_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.String;"});
	
	public static final DDBSToolkitSupportedEntity[] SUPPORTED_ENTITIES = {INTEGER, LONG, FLOAT, DOUBLE, STRING, TIMESTAMP, IENTITY_ARRAY, INTEGER_ARRAY, LONG_ARRAY, FLOAT_ARRAY, DOUBLE_ARRAY, STRING_ARRAY};
	
	protected SparqlDDBSToolkitSupportedEntity(String[] types) {
		super(types);
	}
	
	public static DDBSToolkitSupportedEntity valueOf(Field field) throws IllegalArgumentException, IllegalAccessException
	{
		if(!field.getType().isArray())
		{
			for(DDBSToolkitSupportedEntity sparqlddbsToolkitSupportedEntity : SUPPORTED_ENTITIES)
			{
				for(String type : sparqlddbsToolkitSupportedEntity.getTypes())
				{
					if(type.equals(field.getType().getName())) {
						return sparqlddbsToolkitSupportedEntity;
					}
				}
			}
		}
		else
		{
			for(Class<?> objectInterface : field.getType().getComponentType().getInterfaces())
			{
				if(objectInterface.getName().equals(IEntity.class.getName()))
				{
					return IENTITY_ARRAY;
				}
			}
			
			for(DDBSToolkitSupportedEntity sparqlddbsToolkitSupportedEntity : SUPPORTED_ENTITIES)
			{
				for(String type : sparqlddbsToolkitSupportedEntity.getTypes())
				{
					if(type.equals(field.getType().getName())) {
						return sparqlddbsToolkitSupportedEntity;
					}
				}
			}
		}
		return null;
	}
}

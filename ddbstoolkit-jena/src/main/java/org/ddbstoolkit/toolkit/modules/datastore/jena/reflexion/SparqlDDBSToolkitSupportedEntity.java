package org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * Sparql DDBS Toolkit supported entities
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlDDBSToolkitSupportedEntity extends DDBSToolkitSupportedEntity {
	
	public static final DDBSToolkitSupportedEntity INTEGER_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Integer;","[I"});
	public static final DDBSToolkitSupportedEntity LONG_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Long;","[J"});
	public static final DDBSToolkitSupportedEntity FLOAT_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Float;","[F"});
	public static final DDBSToolkitSupportedEntity DOUBLE_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.Double;","[D"});
	public static final DDBSToolkitSupportedEntity STRING_ARRAY = new DDBSToolkitSupportedEntity(new String[] {"[Ljava.lang.String;"});
	
	public static final DDBSToolkitSupportedEntity[] SUPPORTED_ENTITIES = {INTEGER, LONG, FLOAT, DOUBLE, STRING, TIMESTAMP, IENTITY_ARRAY, INTEGER_ARRAY, LONG_ARRAY, FLOAT_ARRAY, DOUBLE_ARRAY, STRING_ARRAY};
	
	/**
	 * ClassInspector logger
	 */
	private static final Logger logger = Logger.getLogger(SparqlDDBSToolkitSupportedEntity.class);
	
	protected SparqlDDBSToolkitSupportedEntity(String[] types) {
		super(types);
	}
	
	public static DDBSToolkitSupportedEntity valueOf(Field field) throws IllegalArgumentException, IllegalAccessException {
		if(!field.getType().isArray()) {
			for(DDBSToolkitSupportedEntity sparqlddbsToolkitSupportedEntity : SUPPORTED_ENTITIES) {
				for(String type : sparqlddbsToolkitSupportedEntity.getTypes()) {
					if(type.equals(field.getType().getName())) {
						return sparqlddbsToolkitSupportedEntity;
					}
				}
			}
		} else {	
			for(DDBSToolkitSupportedEntity sparqlddbsToolkitSupportedEntity : SUPPORTED_ENTITIES) {
				for(String type : sparqlddbsToolkitSupportedEntity.getTypes()) {
					if(type.equals(field.getType().getName())) {
						return sparqlddbsToolkitSupportedEntity;
					}
				}
			}
			
			try {
				
				if(field.getType().getName().length() > 3) {
					String className = field.getType().getName().substring(2, field.getType().getName().length()-1);
					
					Object object = Class.forName(className).newInstance();
					
					if(object instanceof IEntity) {
						return IENTITY_ARRAY;
					}
				}
				
				
			} catch (InstantiationException | ClassNotFoundException e) {
				logger.error("Error while trying to retrieve the array property",e);
			}
		}
		return null;
	}
}

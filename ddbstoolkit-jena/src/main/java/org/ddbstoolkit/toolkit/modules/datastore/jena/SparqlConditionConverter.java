package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.sql.Timestamp;
import java.util.Iterator;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.conditions.Condition;
import org.ddbstoolkit.toolkit.core.conditions.ConditionBetweenValue;
import org.ddbstoolkit.toolkit.core.conditions.ConditionInValues;
import org.ddbstoolkit.toolkit.core.conditions.ConditionSingleValue;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.conditions.ConditionsConverter;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlDDBSEntity;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlEntityManager;

/**
 * JDBC Condition converter
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlConditionConverter implements ConditionsConverter {
	
	private SparqlEntityManager<SparqlDDBSEntity<SparqlClassProperty>> entityManager;
	
	/**
	 * JDBC Condition converter
	 * @param entityManager Entity manager
	 */
	public SparqlConditionConverter(SparqlEntityManager<SparqlDDBSEntity<SparqlClassProperty>> entityManager) {
		super();
		this.entityManager = entityManager;
	}

	@Override
	public String getConditionsString(Conditions conditions, IEntity object) {
		
		StringBuilder conditionString = new StringBuilder();
		
		@SuppressWarnings("unchecked")
		SparqlDDBSEntity<SparqlClassProperty> entity = entityManager
				.getDDBSEntity(object);
		
		if(conditions.getConditions().size() > 0) {
			
			Iterator<Condition> iteratorConditions = conditions.getConditions().iterator();
			
			conditionString.append("filter (");
			
			iteratorConditions = conditions.getConditions().iterator();
			
			while(iteratorConditions.hasNext()) {
				Condition condition = iteratorConditions.next();
				SparqlClassProperty property = entity.getDDBSEntityProperty(condition.getName());
				String propertyName = property.getName();

				switch (condition.getConditionType()) {
					case EQUAL:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" = ");
						conditionString.append(convert(((ConditionSingleValue)condition).getValue()));
						break;
					case NOT_EQUAL:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" != ");
						conditionString.append(convert(((ConditionSingleValue)condition).getValue()));
						break;
					case LESS_THAN:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" < ");
						conditionString.append(convert(((ConditionSingleValue)condition).getValue()));
						break;
					case GREATER_THAN:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" > ");
						conditionString.append(convert(((ConditionSingleValue)condition).getValue()));
						break;
					case LESS_THAN_OR_EQUAL:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" <= ");
						conditionString.append(convert(((ConditionSingleValue)condition).getValue()));
						break;
					case GREATER_THAN_OR_EQUAL:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" >= ");
						conditionString.append(convert(((ConditionSingleValue)condition).getValue()));
						break;
					case BETWEEN:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" >=  ");
						conditionString.append(convert(((ConditionBetweenValue)condition).getStartingValue()));
						conditionString.append(" && ?");
						conditionString.append(propertyName);
						conditionString.append(" <=  ");
						conditionString.append(convert(((ConditionBetweenValue)condition).getEndingValue()));
						break;
					case NOT_BETWEEN:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" <  ");
						conditionString.append(convert(((ConditionBetweenValue)condition).getStartingValue()));
						conditionString.append(" || ?");
						conditionString.append(propertyName);
						conditionString.append(" >  ");
						conditionString.append(convert(((ConditionBetweenValue)condition).getEndingValue()));
						break;
					case IN:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" IN (");
						
						Iterator<? extends Object> iteratorIn = ((ConditionInValues)condition).getValues().iterator();
						
						while(iteratorIn.hasNext()) {
							
							Object value = iteratorIn.next();
							
							conditionString.append(convert(value));
							
							if(iteratorIn.hasNext()) {
								conditionString.append(",");
							}
						}
						
						conditionString.append(")");
						break;
					case NOT_IN:
						conditionString.append("?");
						conditionString.append(propertyName);
						conditionString.append(" NOT IN (");
						
						Iterator<? extends Object> iteratorNotIn = ((ConditionInValues)condition).getValues().iterator();
						
						while(iteratorNotIn.hasNext()) {
							
							Object value = iteratorNotIn.next();
							
							conditionString.append(convert(value));
							
							if(iteratorNotIn.hasNext()) {
								conditionString.append(",");
							}
						}
						
						conditionString.append(")");
						break;
					case LIKE:
						conditionString.append("regex(?");
						conditionString.append(propertyName);
						conditionString.append(", \"");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						conditionString.append("\", \"i\")");
						break;
					case IS_NULL:
						conditionString.append("NOT EXISTS { ");
						conditionString.append(entity.getObjectVariable(object));
						conditionString.append(" ");
						conditionString.append(property.getNamespaceName());
						conditionString.append(":");
						conditionString.append(property.getPropertyName());
						conditionString.append(" ?otherValue }");
						break;
					case IS_NOT_NULL:
						conditionString.append("EXISTS { ");
						conditionString.append(entity.getObjectVariable(object));
						conditionString.append(" ");
						conditionString.append(property.getNamespaceName());
						conditionString.append(":");
						conditionString.append(property.getPropertyName());
						conditionString.append(" ?otherValue }");
						break;
					default:
						break;
					}
				
				
				if(iteratorConditions.hasNext()) {
					conditionString.append(" && ");
				}
			}
			conditionString.append(")");
		}
		
		if(conditionString.length() == 0) {
			return null;
		} else {
			return conditionString.toString();
		}
	}
	
	
	/**
	 * Convert object if needed
	 * Usefull for timezone
	 * @param value Value to convert
	 * @return object
	 */
	private Object convert(Object value) {
		if(value instanceof Timestamp) {
			value = ((Timestamp)value).getTime();
		} else if(value instanceof String) {
			value = '\"'+(String)value+'\"';
		}
		return value;
	}

}

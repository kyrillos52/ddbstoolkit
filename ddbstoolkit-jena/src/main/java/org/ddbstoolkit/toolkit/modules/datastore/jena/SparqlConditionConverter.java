package org.ddbstoolkit.toolkit.modules.datastore.jena;

import java.util.Iterator;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.conditions.Condition;
import org.ddbstoolkit.toolkit.core.conditions.ConditionInValues;
import org.ddbstoolkit.toolkit.core.conditions.ConditionSingleValue;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.conditions.ConditionsConverter;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlClassProperty;
import org.ddbstoolkit.toolkit.modules.datastore.jena.reflexion.SparqlDDBSEntity;

/**
 * JDBC Condition converter
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class SparqlConditionConverter implements ConditionsConverter {
	
	private DDBSEntityManager<SparqlDDBSEntity<SparqlClassProperty>> entityManager;
	
	/**
	 * JDBC Condition converter
	 * @param entityManager Entity manager
	 */
	public SparqlConditionConverter(DDBSEntityManager<SparqlDDBSEntity<SparqlClassProperty>> entityManager) {
		super();
		this.entityManager = entityManager;
	}

	@Override
	public String getConditionsString(Conditions conditions, IEntity object) {
		
		if(conditions.getConditions().size() > 0) {
			
			StringBuilder conditionString = new StringBuilder();
			
			conditionString.append("filter (");
			
			Iterator<Condition> iteratorConditions = conditions.getConditions().iterator();
			
			DDBSEntity<DDBSEntityProperty> entity = entityManager.getDDBSEntity(object);
			
			while(iteratorConditions.hasNext()) {
				Condition condition = iteratorConditions.next();
				
				String propertyName = entity.getDDBSEntityProperty(condition.getName()).getPropertyName();
				
				conditionString.append("?");
				conditionString.append(propertyName);
				
				switch (condition.getConditionType()) {
					case EQUAL:
						conditionString.append(" = ");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						break;
					case NOT_EQUAL:
						conditionString.append(" <> ");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						break;
					case LESS_THAN:
						conditionString.append(" < ");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						break;
					case GREATER_THAN:
						conditionString.append(" > ");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						break;
					case LESS_THAN_OR_EQUAL:
						conditionString.append(" <= ");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						break;
					case GREATER_THAN_OR_EQUAL:
						conditionString.append(" >= ");
						conditionString.append(((ConditionSingleValue)condition).getValue());
						break;
					case BETWEEN:
						conditionString.append(" BETWEEN ? AND ?");
						break;
					case NOT_BETWEEN:
						conditionString.append(" NOT BETWEEN ? AND ?");
						break;
					case LIKE:
						conditionString.append(" LIKE ?");
						break;
					case IN:
						conditionString.append(" IN (");
						
						Iterator<? extends Object> iteratorIn = ((ConditionInValues)condition).getValues().iterator();
						
						while(iteratorIn.hasNext()) {
							
							iteratorIn.next();
							
							conditionString.append(" ? ");
							
							if(iteratorIn.hasNext()) {
								conditionString.append(",");
							}
						}
						
						conditionString.append(")");
						break;
					case NOT_IN:
						conditionString.append(" NOT IN (");
						
						Iterator<? extends Object> iteratorNotIn = ((ConditionInValues)condition).getValues().iterator();
						
						while(iteratorNotIn.hasNext()) {
							
							iteratorNotIn.next();
							
							conditionString.append(" ? ");
							
							if(iteratorNotIn.hasNext()) {
								conditionString.append(",");
							}
						}
						
						conditionString.append(")");
						break;
					case IS_NULL:
						conditionString.append(" IS NULL");
						break;
					case IS_NOT_NULL:
						conditionString.append(" IS NOT NULL");
						break;
					default:
						break;
					}
				
				if(iteratorConditions.hasNext()) {
					conditionString.append(" && ");
				}
			}
			conditionString.append(")");
			return conditionString.toString();
		} else {
			return null;
		}
	}

}

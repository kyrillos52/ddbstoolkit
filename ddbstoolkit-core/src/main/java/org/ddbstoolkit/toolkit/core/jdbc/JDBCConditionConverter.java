package org.ddbstoolkit.toolkit.core.jdbc;

import java.util.Iterator;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.conditions.Condition;
import org.ddbstoolkit.toolkit.core.conditions.ConditionInValues;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.conditions.ConditionsConverter;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;

/**
 * JDBC Condition converter
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class JDBCConditionConverter implements ConditionsConverter {
	
	@SuppressWarnings("rawtypes")
	private DDBSEntityManager<DDBSEntity> entityManager;
	
	/**
	 * JDBC Condition converter
	 * @param entityManager Entity manager
	 */
	@SuppressWarnings("rawtypes")
	public JDBCConditionConverter(DDBSEntityManager<DDBSEntity> entityManager) {
		super();
		this.entityManager = entityManager;
	}

	@Override
	public String getConditionsString(Conditions conditions, IEntity object) {
		StringBuilder conditionString = new StringBuilder();
		
		Iterator<Condition> iteratorConditions = conditions.getConditions().iterator();
		
		DDBSEntity<DDBSEntityProperty> entity = entityManager.getDDBSEntity(object);
		
		while(iteratorConditions.hasNext()) {
			Condition condition = iteratorConditions.next();
			
			String propertyName = entity.getDDBSEntityProperty(condition.getName()).getPropertyName();
			
			conditionString.append(propertyName);
			
			switch (condition.getConditionType()) {
				case EQUAL:
					conditionString.append(" = ?");
					break;
				case NOT_EQUAL:
					conditionString.append(" <> ?");
					break;
				case LESS_THAN:
					conditionString.append(" < ?");
					break;
				case GREATER_THAN:
					conditionString.append(" > ?");
					break;
				case LESS_THAN_OR_EQUAL:
					conditionString.append(" <= ?");
					break;
				case GREATER_THAN_OR_EQUAL:
					conditionString.append(" >= ?");
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
					
					Iterator<Object> iteratorIn = ((ConditionInValues)condition).getValues().iterator();
					
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
					conditionString.append(" IN (");
					
					Iterator<Object> iteratorNotIn = ((ConditionInValues)condition).getValues().iterator();
					
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
				conditionString.append(" AND ");
			}
		}
		
		return conditionString.toString();
	}

}

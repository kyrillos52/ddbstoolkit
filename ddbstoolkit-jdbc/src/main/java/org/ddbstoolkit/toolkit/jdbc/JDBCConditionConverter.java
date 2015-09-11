package org.ddbstoolkit.toolkit.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.conditions.Condition;
import org.ddbstoolkit.toolkit.core.conditions.ConditionBetweenValue;
import org.ddbstoolkit.toolkit.core.conditions.ConditionInValues;
import org.ddbstoolkit.toolkit.core.conditions.ConditionSingleValue;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.conditions.ConditionsConverter;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntity;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityManager;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSEntityProperty;
import org.ddbstoolkit.toolkit.core.reflexion.DDBSToolkitSupportedEntity;

/**
 * JDBC Condition converter
 * @author Cyril Grandjean
 * @version 1.0 Class creation
 */
public class JDBCConditionConverter implements ConditionsConverter {
	
	private DDBSEntityManager<DDBSEntity<DDBSEntityProperty>> entityManager;
	
	/**
	 * JDBC Condition converter
	 * @param entityManager Entity manager
	 */
	public JDBCConditionConverter(DDBSEntityManager<DDBSEntity<DDBSEntityProperty>> entityManager) {
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
				conditionString.append(" AND ");
			}
		}
		
		return conditionString.toString();
	}
	
	/**
	 * Prepare a statement
	 * @param preparedStatement Prepared statement
	 * @param conditions List of conditions
	 * @param ddbsEntity DDBS Entity
	 * @throws SQLException SQL Exception
	 */
	public void prepareStatement(PreparedStatement preparedStatement, Conditions conditions, DDBSEntity<DDBSEntityProperty> ddbsEntity) throws SQLException {
		
		int counterParameter = 1;
		for(Condition condition : conditions.getConditions()) {
			
			DDBSEntityProperty ddbsEntityProperty = ddbsEntity.getDDBSEntityProperty(condition.getName());
			
			if(condition instanceof ConditionSingleValue) {
				prepareData(preparedStatement, counterParameter, ddbsEntityProperty, ((ConditionSingleValue)condition).getValue());
				counterParameter++;
			} else if(condition instanceof ConditionBetweenValue) {
				prepareData(preparedStatement, counterParameter, ddbsEntityProperty, ((ConditionBetweenValue)condition).getStartingValue());
				counterParameter++;
				prepareData(preparedStatement, counterParameter, ddbsEntityProperty, ((ConditionBetweenValue)condition).getEndingValue());
				counterParameter++;
			}  else if(condition instanceof ConditionInValues) {
				for(Object object : ((ConditionInValues)condition).getValues()) {
					prepareData(preparedStatement, counterParameter, ddbsEntityProperty, object);
					counterParameter++;
				}
			}
		}
	}
	
	public <T extends DDBSEntityProperty> PreparedStatement prepareParametersPreparedStatement(
			PreparedStatement preparedStatement, List<T> ddbsEntityProperties, IEntity entity)
			throws SQLException {
		int counterParameter = 1;
		for (DDBSEntityProperty ddbsEntityProperty : ddbsEntityProperties) {
			
			prepareData(preparedStatement, counterParameter, ddbsEntityProperty, ddbsEntityProperty.getValue(entity));
			counterParameter++;
		}

		return preparedStatement;
	}
	
	/**
	 * Prepare a prepared statement
	 * @param preparedStatement Prepared Statement
	 * @param counterParameter Counter parameter
	 * @param ddbsEntityProperty DDBSEntity property
	 * @param value Value
	 * @throws SQLException SQL Exception
	 */
	private void prepareData(PreparedStatement preparedStatement, int counterParameter, DDBSEntityProperty ddbsEntityProperty, Object value) throws SQLException {
		
		if(value == null) {
			preparedStatement.setNull(counterParameter, Types.NULL);
		} else {
			if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.INTEGER)) {	
				preparedStatement.setInt(counterParameter,
						(Integer) value);
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.LONG)) {
				preparedStatement.setLong(counterParameter,
						(Long) value);
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.FLOAT)) {
				preparedStatement.setFloat(counterParameter,
						(Float) value);
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.DOUBLE)) {
				preparedStatement.setDouble(counterParameter,
						(Double) value);
			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.STRING)) {
				preparedStatement.setString(counterParameter,
							(String) value);

			} else if (ddbsEntityProperty.getDdbsToolkitSupportedEntity().equals(
					DDBSToolkitSupportedEntity.TIMESTAMP)) {
				preparedStatement.setTimestamp(counterParameter,
						(Timestamp) value);
			}
		}
	}

}

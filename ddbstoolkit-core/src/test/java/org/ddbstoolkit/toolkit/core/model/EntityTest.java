package org.ddbstoolkit.toolkit.core.model;

import java.sql.Timestamp;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;


/**
 * Entity test
 * @author Cyril Grandjean
 *
 */
public class EntityTest extends DistributedEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id(autoincrement=false)
	public int intField;
	
	@EntityName(name="customField")
	public Integer integerField;
	
	public long longField;
	
	public Long longObjectField;
	
	public float floatField;
	
	public Float floatObjectField;
	
	public double doubleField;
	
	public Double doubleObjectField;
	
	public String stringField;
	
	public Timestamp timestampField;
	
	public EntityTest[] entityField;
}

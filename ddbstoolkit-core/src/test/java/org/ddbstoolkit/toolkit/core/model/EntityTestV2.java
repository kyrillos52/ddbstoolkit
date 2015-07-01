package org.ddbstoolkit.toolkit.core.model;

import java.sql.Timestamp;
import java.util.Arrays;

import org.ddbstoolkit.toolkit.core.DistributedEntity;
import org.ddbstoolkit.toolkit.core.annotations.Id;
import org.ddbstoolkit.toolkit.core.annotations.EntityName;


/**
 * Entity test
 * @author Cyril Grandjean
 *
 */
public class EntityTestV2 extends DistributedEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id(autoincrement=false)
	private int intField;	
	
	@EntityName(name="customField")
	private Integer integerField;
	
	private long longField;
	
	private Long longObjectField;
	
	private float floatField;
	
	private Float floatObjectField;
	
	private double doubleField;
	
	private Double doubleObjectField;
	
	private String stringField;
	
	private Timestamp timestampField;
	
	private EntityTestV2[] entityField;

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}

	public Integer getIntegerField() {
		return integerField;
	}

	public void setIntegerField(Integer integerField) {
		this.integerField = integerField;
	}

	public long getLongField() {
		return longField;
	}

	public void setLongField(long longField) {
		this.longField = longField;
	}

	public Long getLongObjectField() {
		return longObjectField;
	}

	public void setLongObjectField(Long longObjectField) {
		this.longObjectField = longObjectField;
	}

	public float getFloatField() {
		return floatField;
	}

	public void setFloatField(float floatField) {
		this.floatField = floatField;
	}

	public Float getFloatObjectField() {
		return floatObjectField;
	}

	public void setFloatObjectField(Float floatObjectField) {
		this.floatObjectField = floatObjectField;
	}

	public double getDoubleField() {
		return doubleField;
	}

	public void setDoubleField(double doubleField) {
		this.doubleField = doubleField;
	}

	public Double getDoubleObjectField() {
		return doubleObjectField;
	}

	public void setDoubleObjectField(Double doubleObjectField) {
		this.doubleObjectField = doubleObjectField;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public Timestamp getTimestampField() {
		return timestampField;
	}

	public void setTimestampField(Timestamp timestampField) {
		this.timestampField = timestampField;
	}

	public EntityTestV2[] getEntityField() {
		return entityField;
	}

	public void setEntityField(EntityTestV2[] entityField) {
		this.entityField = entityField;
	}

	@Override
	public String toString() {
		return "EntityTestV2 [intField=" + intField + ", integerField="
				+ integerField + ", longField=" + longField
				+ ", longObjectField=" + longObjectField + ", floatField="
				+ floatField + ", floatObjectField=" + floatObjectField
				+ ", doubleField=" + doubleField + ", doubleObjectField="
				+ doubleObjectField + ", stringField=" + stringField
				+ ", timestampField=" + timestampField + ", entityField="
				+ Arrays.toString(entityField) + "]";
	}
	
}

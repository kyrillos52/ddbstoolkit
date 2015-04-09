package org.ddbstoolkit.toolkit.modules.datastore.mysql;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.generation.ImplementableEntity;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.ClassProperty;

/**
 * Class representing a distributed MySQL Database
 * User: Cyril GRANDJEAN
 * Date: 18/06/2012
 * Time: 11:08
 *
 * @version 1.0 : Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as "1characters" table
 */
public class DistributedMySQLTableManager implements DistributableEntityManager {
	
    /**
     * Connector MySQL
     */
    private MySQLConnector myConnector;

    /**
     * Peer of the mySQL Database
     */
    private Peer myPeer;

    public DistributedMySQLTableManager(MySQLConnector myConnector) {
        this.myConnector = myConnector;
    }

    public DistributedMySQLTableManager(MySQLConnector myConnector, Peer myPeer) {
        this.myConnector = myConnector;
        this.myPeer = myPeer;
    }

    @Override
    public void setPeer(Peer myPeer) {
        this.myPeer = myPeer;
    }

    @Override
    public Peer getPeer() {
        return this.myPeer;
    }

    @Override
    public boolean isOpen() throws DDBSToolkitException {
        try {
        	return myConnector.isOpen();
        }
        catch(SQLException sqle)
        {
        	throw new DDBSToolkitException("Error during checking SQL connection", sqle);
        }
    }

    @Override
    public void open() throws DDBSToolkitException { 
    	try {
    		myConnector.open();
        }
        catch(SQLException sqle)
        {
        	throw new DDBSToolkitException("Error during opening SQL connection", sqle);
        }
    }

    @Override
    public void close() throws DDBSToolkitException {
    	try {
    		myConnector.close();
        }
        catch(SQLException sqle)
        {
        	throw new DDBSToolkitException("Error during SQL connection", sqle);
        }
    }

    @Override
    public <T extends IEntity> ArrayList<T> listAll(T object, ArrayList<String> conditionList, String orderBy) throws DDBSToolkitException {

        //If the connector is opened
        try {
			if(myConnector.isOpen() && object != null)
			{
		        //Inspect object
		        String tableName = ClassInspector.getClassName(object);

		        StringBuilder sb = new StringBuilder();

		        sb.append("SELECT * FROM ");
		        sb.append(tableName);

		        //If there is conditions
		        if(conditionList != null && !conditionList.isEmpty())
		        {
		            sb.append(" WHERE ");

		            for(int i = 0; i < conditionList.size(); i++)
		            {
		                sb.append(conditionList.get(i));

		                if(i < conditionList.size()-1)
		                {
		                    sb.append(" AND ");
		                }
		            }

		        }

		        if(orderBy != null && !orderBy.equals(""))
		        {
		            sb.append(" ORDER BY ");
		            sb.append(orderBy);
		        }

		        sb.append(";");

		        //System.out.println(sb.toString());

		        ResultSet results = myConnector.query(sb.toString());

		        ArrayList<T> resultList;

		        IEntity myObject = (IEntity) object;

		        if(myObject instanceof ImplementableEntity)
		        {
		            resultList = ((ImplementableEntity) myObject).conversionResultSet(results, object);
		        }
		        else
		        {
		           resultList = conversionResultSet(results, object);
		        }


		        return resultList;
			}
			else
			{
			    return null;
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
    }

    @Override
    public <T extends IEntity> T read(T object) throws DDBSToolkitException {

        //If the connector is opened
        try {
			if(myConnector.isOpen() && object != null)
			{
			    if(object != null)
			    {
			        //Inspect the object
			        String tableName = ClassInspector.getClassName(object);

			        //List properties
			        ArrayList<ClassProperty> properties = ClassInspector.exploreProperties(object);

			        String sqlRequest = "SELECT * FROM "+tableName+" WHERE ";
			        
			        int counterPrimaryKey = 0;
			        for(ClassProperty property : properties)
			        {
			            if(property.isId())
			            {
			            	if(counterPrimaryKey > 0)
			            	{
			            		sqlRequest += "AND ";
			            	}
			            	
			            	sqlRequest += property.getPropertyName() +" = '"+property.getValue()+"' ";
			            	counterPrimaryKey++;
			            }
			        }

			        //Execute the request
			        ResultSet results;
			        if (counterPrimaryKey > 0) {
			            results = myConnector.query(sqlRequest);

			            ArrayList<T> resultList;
			            IEntity myObject = (IEntity) object;

			            if(myObject instanceof ImplementableEntity)
			            {
			                resultList = ((ImplementableEntity) myObject).conversionResultSet(results, object);
			            }
			            else
			            {
			                resultList = conversionResultSet(results, object);
			            }

			            //Return the object
			            if(resultList.size() == 1)
			            {
			                return resultList.get(0);
			            }
			            else
			            {
			                return null;
			            }
			        }
			        else
			        {
			            return null;
			        }
			    }
			    else
			    {
			        return null;
			    }
			}
			else
			{
			    return null;
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
    }

    @Override
    public <T extends IEntity> T readLastElement(T object) throws DDBSToolkitException {

        //If the connector is opened
        try {
			if(myConnector.isOpen() && object != null)
			{
			    //Inspect the object
			    String tableName = ClassInspector.getClassName(object);

			    //List properties
			    ArrayList<ClassProperty> properties = ClassInspector.exploreProperties(object);

			    ClassProperty primaryKey = null;
			    for(ClassProperty property : properties)
			    {
			        if(property.isId())
			        {
			            primaryKey = property;
			            break;
			        }
			    }

			    //Set request
			    ResultSet results = myConnector.query("SELECT * FROM "+tableName+" WHERE "+ primaryKey.getPropertyName() +" = (SELECT MAX("+primaryKey.getPropertyName()+") FROM "+tableName+")");

			    //Return object
			    ArrayList<T> resultList;
			    IEntity myObject = (IEntity) object;

			    if(myObject instanceof ImplementableEntity)
			    {
			        resultList = ((ImplementableEntity) myObject).conversionResultSet(results, object);
			    }
			    else
			    {
			        resultList = conversionResultSet(results, object);
			    }

			    if(resultList.size() == 1)
			    {
			        return resultList.get(0);
			    }
			    else
			    {
			        return null;
			    }
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
        return null;
    }

    @Override
    public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

        //If the connector is opened
        try {
			if(myConnector.isOpen() && objectToAdd != null)
			{
				//Prepare the SQL Request
			    StringBuilder sqlPart1 = new StringBuilder();
			    StringBuilder sqlPart2 = new StringBuilder();
				
			    //Inspect object
			    String tableName = ClassInspector.getClassName(objectToAdd);
			    
			    sqlPart1.append("INSERT INTO ");
			    sqlPart1.append(tableName);
			    sqlPart1.append(" (");

			    sqlPart2.append(" VALUES (");

			    //Select properties
			    ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToAdd);
			    
			    //The auto-incrementing primary keys, arrays and node_id are removed
			    Iterator<ClassProperty> classPropertyIterator = listOfProperties.iterator();
			    while(classPropertyIterator.hasNext())
			    {
			    	ClassProperty property = classPropertyIterator.next();
			        if((property.isId() && property.isAutoIncrement()) || property.isArray() || property.getPropertyName().equals("node_id"))
			        {
			        	classPropertyIterator.remove();
			        }
			    }
			    
			    for(ClassProperty property : listOfProperties)
			    {
			    	sqlPart1.append(property.getPropertyName());
		            sqlPart2.append("?");
		            
		            if(listOfProperties.indexOf(property) < listOfProperties.size() - 1)
		            {
		            	sqlPart1.append(", ");
		                sqlPart2.append(", ");
		            }
			    }

			    sqlPart1.append(")");
			    sqlPart2.append(");");

			    String sqlToAdd = sqlPart1.toString()+sqlPart2.toString();

			    //System.out.println(sqlToAdd);

			    //Prepare the request
			    PreparedStatement preparedRequest = myConnector.prepareStatement(sqlToAdd);

			    int index = 1;
			    for(ClassProperty myProperty : listOfProperties)
			    {
			        if(myProperty.getType().equals("int"))
			        {
			            preparedRequest.setInt(index, (Integer)myProperty.getValue());
			        }
			        else if(myProperty.getType().equals("long"))
			        {
			            preparedRequest.setLong(index, (Long) myProperty.getValue());
			        }
			        else if(myProperty.getType().equals("float"))
			        {
			            preparedRequest.setFloat(index, (Float)myProperty.getValue());
			        }
			        else if(myProperty.getType().equals("java.lang.String"))
			        {
			            if(myProperty.getValue() != null)
			            {
			                preparedRequest.setString(index, (String)myProperty.getValue());
			            }
			            else
			            {
			                preparedRequest.setString(index, "");
			            }

			        }
			        else if(myProperty.getType().equals("java.sql.Timestamp"))
			        {
			            preparedRequest.setTimestamp(index, (Timestamp)myProperty.getValue());
			        }
			        index++;
			    }

			    //Execute the prepared request
			    myConnector.executePreparedQuery(preparedRequest);

			    return true;
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
        return false;
    }

    @Override
    public boolean update(IEntity objectToUpdate) throws DDBSToolkitException {

        //If the connector is opened
        try {
			if(myConnector.isOpen() && objectToUpdate != null)
			{
			    //Inspect object
			    String tableName = ClassInspector.getClassName(objectToUpdate);

			    //Find properties
			    ArrayList<ClassProperty> listPrimaryKeys = new ArrayList<ClassProperty>(); 
			    ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToUpdate);
			    Iterator<ClassProperty> classPropertyIterator = listOfProperties.iterator();
			    while(classPropertyIterator.hasNext())
			    {
			    	ClassProperty property = classPropertyIterator.next();
			    	if(property.isId())
			    	{
			    		listPrimaryKeys.add(property);
			    		classPropertyIterator.remove();
			    	}
			    	else if(property.isArray() || property.getPropertyName().equals("node_id"))
			        {
			        	classPropertyIterator.remove();
			        }
			    }

			    //If there is no primary key indicated
			    if(listPrimaryKeys.size() == 0)
			    {
			        return false;
			    }
			    else
			    {
			        //Prepare the request
			        StringBuilder sqlStatement = new StringBuilder();
			        sqlStatement.append("UPDATE ");
			        sqlStatement.append(tableName);
			        sqlStatement.append(" SET ");
			        
			        for(int counter = 0; counter < listOfProperties.size(); counter++)
			        {
		                sqlStatement.append(listOfProperties.get(counter).getPropertyName());
		                sqlStatement.append(" = ?");

		                if(counter < listOfProperties.size() - 1)
		                {
		                    sqlStatement.append(", ");
		                }
			        }

			        //If a primary key has been found
			        if(listOfProperties.size() > 0)
			        {
			            sqlStatement.append(" WHERE ");
			            
			            int counterPrimaryKey = 0;
			            for(ClassProperty property : listPrimaryKeys)
				        {
			            	if(counterPrimaryKey > 0)
			            	{
			            		sqlStatement.append("AND ");
			            	}
			            	
			            	sqlStatement.append(property.getPropertyName() +" = ? ");
			            	counterPrimaryKey++;
				        }

			            sqlStatement.append("LIMIT 1 ;");
			            
			            //System.out.println(sqlStatement.toString());

			            PreparedStatement preparedRequest = myConnector.prepareStatement(sqlStatement.toString());

			            int index = 1;
			            for(ClassProperty myProperty : listOfProperties)
			            {
		                    if(myProperty.getType().equals("int"))
		                    {
		                        preparedRequest.setInt(index, (Integer)myProperty.getValue());
		                    }
		                    else if(myProperty.getType().equals("long"))
		                    {
		                        preparedRequest.setLong(index, (Long) myProperty.getValue());
		                    }
		                    else if(myProperty.getType().equals("float"))
		                    {
		                        preparedRequest.setFloat(index, (Float)myProperty.getValue());
		                    }
		                    else if(myProperty.getType().equals("java.lang.String"))
		                    {
		                        preparedRequest.setString(index, myProperty.getValue().toString());
		                    }
		                    else if(myProperty.getType().equals("java.sql.Timestamp"))
		                    {
		                        preparedRequest.setTimestamp(index, (Timestamp)myProperty.getValue());
		                    }
		                    index++;
			            }

			            //The primary keys are set in where clause
			            for(ClassProperty property : listPrimaryKeys)
				        {
			            	if(property.getType().equals("int"))
				            {
				                preparedRequest.setInt(index, (Integer)property.getValue());
				            }
				            else if(property.getType().equals("long"))
				            {
				                preparedRequest.setLong(index, (Long) property.getValue());
				            }
				            else if(property.getType().equals("float"))
				            {
				                preparedRequest.setFloat(index, (Float)property.getValue());
				            }
				            else if(property.getType().equals("java.lang.String"))
				            {
				                preparedRequest.setString(index, property.getValue().toString());
				            }
				            else if(property.getType().equals("java.sql.Timestamp"))
				            {
				                preparedRequest.setTimestamp(index, (Timestamp)property.getValue());
				            }
			            	index++;
				        }
			            
			            //Execute the prepared request
			            myConnector.executePreparedQuery(preparedRequest);

			            return true;
			        }
			        else
			        {
			            return false;
			        }
			    }
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
        return false;
    }

    @Override
    public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

        //If the connector is opened
        try {
			if(myConnector.isOpen() && objectToDelete != null)
			{
			    //Inspect object
			    String tableName = ClassInspector.getClassName(objectToDelete);

			    ArrayList<ClassProperty> listPrimaryKeys = new ArrayList<ClassProperty>(); 
			    ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToDelete);
			    Iterator<ClassProperty> classPropertyIterator = listOfProperties.iterator();
			    while(classPropertyIterator.hasNext())
			    {
			    	ClassProperty property = classPropertyIterator.next();
			    	if(property.isId())
			    	{
			    		listPrimaryKeys.add(property);
			    		classPropertyIterator.remove();
			    	}
			    	else if(property.isArray() || property.getPropertyName().equals("node_id"))
			        {
			        	classPropertyIterator.remove();
			        }
			    }

			    if(listPrimaryKeys.size() == 0)
			    {
			        return false;
			    }
			    else
			    {
			        //Prepare the request
			        String sqlStatement = "DELETE FROM "+tableName+"";

		            sqlStatement += " WHERE ";
		            
		            int counterPrimaryKey = 0;
		            for(ClassProperty property : listPrimaryKeys)
			        {
		            	if(counterPrimaryKey > 0)
		            	{
		            		sqlStatement += "AND ";
		            	}
		            	
		            	sqlStatement +=  property.getPropertyName() +" = ? ";
		            	counterPrimaryKey++;
			        }
		            sqlStatement += "LIMIT 1 ;";

		            //System.out.println(sqlStatement);
		            
		            //Prepare the request
		            PreparedStatement preparedRequest = myConnector.prepareStatement(sqlStatement);

		            try {

		                int index = 1;

		                for(ClassProperty primaryKey : listPrimaryKeys)
				        {
		                	//The primary key is set in where clause
			                if(primaryKey.getType().equals("int"))
			                {
			                    preparedRequest.setInt(index, (Integer)primaryKey.getValue());
			                }
			                else if(primaryKey.getType().equals("long"))
			                {
			                    preparedRequest.setLong(index, (Long) primaryKey.getValue());
			                }
			                else if(primaryKey.getType().equals("float"))
			                {
			                    preparedRequest.setFloat(index, (Float)primaryKey.getValue());
			                }
			                else if(primaryKey.getType().equals("java.lang.String"))
			                {
			                    preparedRequest.setString(index, primaryKey.getValue().toString());
			                }
			                else if(primaryKey.getType().equals("java.sql.Timestamp"))
			                {
			                    preparedRequest.setTimestamp(index, (Timestamp)primaryKey.getValue());
			                }
			                index++;
				        }

		                //Execute prepared request
		                myConnector.executePreparedQuery(preparedRequest);

		                return true;

			            } catch (SQLException e) {
			                e.printStackTrace();
			                return false;
			            }
			        }
			}
			else
			{
				throw new DDBSToolkitException("Error during execution of the SQL request - MySQL Connection not open", null);
			}
		} catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
    }

    @Override
    public <T extends IEntity> T loadArray(T objectToLoad, String field, String orderBy) throws DDBSToolkitException {
    	
        try {
			if(myConnector.isOpen() && objectToLoad != null && field != null && !field.isEmpty())
			{
			    ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToLoad);

			    ClassProperty linkProperty = null;
			    ClassProperty primaryKey = null;
			    for(ClassProperty property : listOfProperties)
			    {
			        if(property.isId())
			        {
			            primaryKey = property;
			        }
			        if(property.getPropertyName().equals(field))
			        {
			            linkProperty = property;
			        }
			    }

			    if(primaryKey != null)
			    {
			        ArrayList<String> listCondition = new ArrayList<String>();
			        listCondition.add(""+primaryKey.getPropertyName()+" = "+primaryKey.getValue());

			        String objectName = linkProperty.getType().substring(2, linkProperty.getType().length()-1);

			        IEntity objectLinked = (IEntity) Class.forName(objectName).newInstance();

			        ArrayList<IEntity> listObject = listAll(objectLinked, listCondition, orderBy);

			        Field f = objectToLoad.getClass().getField(field);

			        Object array = Array.newInstance(Class.forName(objectName),listObject.size());

			        int i = 0;
			        for(IEntity entity : listObject)
			        {
			            Array.set(array, i, entity);
			            i++;
			        }

			        f.set(objectToLoad, array);

			        return objectToLoad;
			    }
			    else
			    {
			        return null;
			    }
			}
			else
			{
			    return null;
			}
		} catch (Exception e) {
			throw new DDBSToolkitException("Error during use of the reflection mechanism", e);
		}
    }

    @Override
    public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException {

        //TODO
        return false;
    }

    protected <T extends IEntity> ArrayList<T> conversionResultSet(ResultSet results, T myObject) throws DDBSToolkitException {

        ArrayList<T> resultList = new ArrayList<T>();
        
        

        //For each object
        try {
			while(results.next()){

			    //Get class name
			    String nameClass = ClassInspector.getFullClassName(myObject);

			    //List properties
			    ArrayList<ClassProperty> listProperties = ClassInspector.exploreProperties(myObject);

			    //Instantiate the object
			    T myData = (T) Class.forName(nameClass).newInstance();

			    //Set object properties
			    for(ClassProperty myProperty : listProperties)
			    {
			        Field f = myData.getClass().getField(myProperty.getName());

			        //If it's not an array
			        if(!myProperty.isArray())
			        {
			            if(myProperty.getType().equals("int"))
			            {
			                f.set(myData, results.getInt(myProperty.getPropertyName()));
			            }
			            else if(myProperty.getType().equals("long"))
			            {
			                f.set(myData, results.getLong(myProperty.getPropertyName()));
			            }
			            else if(myProperty.getType().equals("float"))
			            {
			                f.set(myData, results.getFloat(myProperty.getPropertyName()));
			            }
			            else if(myProperty.getType().equals("java.lang.String"))
			            {
			                //If it's the node_id property
			                if(myProperty.getPropertyName().equals("node_id"))
			                {
			                    if(myPeer != null)
			                    {
			                        f.set(myData, myPeer.getUid());
			                    }
			                    else
			                    {
			                        f.set(myData, "0");
			                    }
			                }
			                else
			                {
			                    f.set(myData, results.getString(myProperty.getPropertyName()));
			                }
			            }
			            else if(myProperty.getType().equals("java.sql.Timestamp"))
			            {
			                f.set(myData, results.getTimestamp(myProperty.getPropertyName()));
			            }
			        }
			    }

			    resultList.add(myData);
			}
		} catch (SecurityException se) {
			throw new DDBSToolkitException("Security exception using reflection", se);
		} catch (IllegalArgumentException iae) {
			throw new DDBSToolkitException("Illegal argument exception using reflection", iae);
		} catch (SQLException se) {
			throw new DDBSToolkitException("SQL exception during parsing the request", se);
		} catch (InstantiationException ie) {
			throw new DDBSToolkitException("Problem during instantiation of the object using reflection", ie);
		} catch (IllegalAccessException iae) {
			throw new DDBSToolkitException("Illegal access exception using reflection", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new DDBSToolkitException("Class not found using reflection", cnfe);
		} catch (NoSuchFieldException nsfe) {
			throw new DDBSToolkitException("No such field exception using reflection", nsfe);
		}

        return resultList;
    }
}
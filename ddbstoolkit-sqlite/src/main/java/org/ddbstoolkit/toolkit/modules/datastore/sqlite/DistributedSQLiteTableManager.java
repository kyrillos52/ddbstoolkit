package org.ddbstoolkit.toolkit.modules.datastore.sqlite;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.ddbstoolkit.toolkit.core.DistributableEntityManager;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.exception.DDBSToolkitException;
import org.ddbstoolkit.toolkit.core.reflexion.ClassInspector;
import org.ddbstoolkit.toolkit.core.reflexion.ClassProperty;

/**
 * Class representing a distributed SQLite Database
 * User: Cyril GRANDJEAN
 * Date: 19/06/2012
 * Time: 11:08
 *
 * @version 1.0 : Creation of the class
 * @version 1.1 : Manage the PropertyName annotation used for properties such as "1characters" table
 */
public class DistributedSQLiteTableManager implements DistributableEntityManager {

    /**
     * Connector SQLite
     */
    private SQLiteConnector myConnector;

    /**
     * Peer of the mySQL Database
     */
    private Peer myPeer;

    public DistributedSQLiteTableManager(SQLiteConnector myConnector) {
        this.myConnector = myConnector;
    }

    public DistributedSQLiteTableManager(SQLiteConnector myConnector, Peer myPeer) {
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
    public boolean isOpen() {
        return myConnector.isOpen();
    }

    @Override
    public void open() throws DDBSToolkitException {
        myConnector.open();
    }

    @Override
    public void close() throws DDBSToolkitException {
        myConnector.close();
    }

    @Override
    public <T extends IEntity> ArrayList<T> listAll(T object, ArrayList<String> conditionList, String orderBy) throws DDBSToolkitException {

        //If the connector is opened
        if(myConnector.isOpen() && object != null)
        {
            if(object != null)
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

                ArrayList<T> resultList = conversionResultSet(results, object);

                return resultList;
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

    @Override
    public <T extends IEntity> T read(T object) throws DDBSToolkitException {

        //If the connector is opened
        if(myConnector.isOpen() && object != null)
        {
            if(object != null)
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

                //Execute the request
                ResultSet results;
                if (primaryKey != null) {
                    results = myConnector.query("SELECT * FROM "+tableName+" WHERE `"+primaryKey.getPropertyName() +"` = '"+primaryKey.getValue()+"'");

                    //Return the object
                    ArrayList<T> resultList = conversionResultSet(results, object);
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
    }

    @Override
    public <T extends IEntity> T readLastElement(T object) throws DDBSToolkitException {

        //If the connector is opened
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
            ResultSet results = myConnector.query("SELECT * FROM "+tableName+" WHERE `"+ primaryKey.getPropertyName() +"` = (SELECT MAX("+primaryKey.getPropertyName()+") FROM `"+tableName+"`)");

            //Return object
            ArrayList<T> resultList = conversionResultSet(results, object);
            if(resultList.size() == 1)
            {
                return resultList.get(0);
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean add(IEntity objectToAdd) throws DDBSToolkitException {

    	try
    	{
	        //If the connector is opened
	        if(myConnector.isOpen() && objectToAdd != null)
	        {
	            //Inspect object
	            String tableName = ClassInspector.getClassName(objectToAdd);
	
	            //Select properties
	            ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToAdd);
	
	            int numberOfFieldsToIgnore = 0;
	            ClassProperty primaryKey = null;
	            for(ClassProperty property : listOfProperties)
	            {
	                if(property.isId())
	                {
	                    primaryKey = property;
	                    numberOfFieldsToIgnore++;
	                }
	                if(property.isArray())
	                {
	                    numberOfFieldsToIgnore++;
	                }
	            }
	
	            //Prepare the SQL Request
	            StringBuilder sqlPart1 = new StringBuilder();
	            StringBuilder sqlPart2 = new StringBuilder();
	
	            sqlPart1.append("INSERT INTO `");
	            sqlPart1.append(tableName);
	            sqlPart1.append("` (");
	
	            sqlPart2.append(" VALUES (");
	
	            for(int counter = 0; counter < listOfProperties.size(); counter++)
	            {
	                //The primary keys, arrays and node_id are ignored
	                if(!listOfProperties.get(counter).isArray() && !listOfProperties.get(counter).isId() && !listOfProperties.get(counter).getPropertyName().equals("node_id"))
	                {
	                    sqlPart1.append("`");
	                    sqlPart1.append(listOfProperties.get(counter).getPropertyName());
	                    sqlPart1.append("`");
	                    sqlPart2.append("?");
	
	                    if(counter < listOfProperties.size() - numberOfFieldsToIgnore - 1)
	                    {
	                        sqlPart1.append(", ");
	                        sqlPart2.append(", ");
	                    }
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
	                //The primary key is ignored
	                if(!myProperty.isId() && !myProperty.isArray() && !myProperty.getPropertyName().equals(("node_id")))
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
	                        if(myProperty.getValue() != null)
	                        {
	                            preparedRequest.setLong(index, ((Timestamp) myProperty.getValue()).getTime());
	                        }
	                        else
	                        {
	                            preparedRequest.setLong(index, 0);
	                        }
	                    }
	                    index++;
	                }
	
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

    	try
    	{
    		//If the connector is opened
            if(myConnector.isOpen() && objectToUpdate != null)
            {
                //Inspect object
                String tableName = ClassInspector.getClassName(objectToUpdate);

                //Find properties
                ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToUpdate);

                int numberOfFieldsToIgnore = 0;
                ClassProperty primaryKey = null;
                for(ClassProperty property : listOfProperties)
                {
                    if(property.isId())
                    {
                        primaryKey = property;
                        numberOfFieldsToIgnore++;
                    }
                    if(property.isArray())
                    {
                        numberOfFieldsToIgnore++;
                    }
                }

                //If there is no primary key indicated
                if(primaryKey == null || primaryKey.getValue() == null || (Integer)primaryKey.getValue() == 0)
                {
                    return false;
                }
                else
                {
                    //Prepare the request
                    StringBuilder sqlStatement = new StringBuilder();
                    sqlStatement.append("UPDATE `");
                    sqlStatement.append(tableName);
                    sqlStatement.append("` SET ");

                    int indexPrimaryKey = -1;


                    for(int counter = 0; counter < listOfProperties.size(); counter++)
                    {
                        //The primary key is ignored and the node_id field is ignored
                        if(!listOfProperties.get(counter).isArray() && !listOfProperties.get(counter).isId() && !listOfProperties.get(counter).getPropertyName().equals("node_id"))
                        {
                            sqlStatement.append("`");
                            sqlStatement.append(listOfProperties.get(counter).getPropertyName());
                            sqlStatement.append("` = ?");

                            if(counter < listOfProperties.size() - numberOfFieldsToIgnore - 1)
                            {
                                sqlStatement.append(", ");
                            }
                        }
                        else if(listOfProperties.get(counter).isId())
                        {
                            indexPrimaryKey = counter;
                        }
                        else
                        {
                            numberOfFieldsToIgnore++;
                        }

                    }

                    //If a primary key has been found
                    if(indexPrimaryKey != -1)
                    {
                        sqlStatement.append(" WHERE `");
                        sqlStatement.append(listOfProperties.get(indexPrimaryKey).getPropertyName());
                        sqlStatement.append("` = ?;");

                        //System.out.println(sqlStatement.toString());

                        PreparedStatement preparedRequest = myConnector.prepareStatement(sqlStatement.toString());

                        int index = 1;
                        for(ClassProperty myProperty : listOfProperties)
                        {
                            //The primary key is ignored and node_id field is ignored
                            if(!myProperty.isArray() && !myProperty.isId() && !myProperty.getPropertyName().equals("node_id"))
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
                                    preparedRequest.setLong(index, ((Timestamp) myProperty.getValue()).getTime());
                                }
                                index++;
                            }

                        }

                        //The primary key is set in where clause
                        if(listOfProperties.get(indexPrimaryKey).getType().equals("int"))
                        {
                            preparedRequest.setInt(index, (Integer)listOfProperties.get(indexPrimaryKey).getValue());
                        }
                        else if(listOfProperties.get(indexPrimaryKey).getType().equals("long"))
                        {
                            preparedRequest.setLong(index, (Long) listOfProperties.get(indexPrimaryKey).getValue());
                        }
                        else if(listOfProperties.get(indexPrimaryKey).getType().equals("float"))
                        {
                            preparedRequest.setFloat(index, (Float)listOfProperties.get(indexPrimaryKey).getValue());
                        }
                        else if(listOfProperties.get(indexPrimaryKey).getType().equals("java.lang.String"))
                        {
                            preparedRequest.setString(index, listOfProperties.get(indexPrimaryKey).getValue().toString());
                        }
                        else if(listOfProperties.get(indexPrimaryKey).getType().equals("java.sql.Timestamp"))
                        {
                            if(listOfProperties.get(indexPrimaryKey).getValue() != null)
                            {
                                preparedRequest.setLong(index, ((Timestamp) listOfProperties.get(indexPrimaryKey).getValue()).getTime());
                            }
                            else
                            {
                                preparedRequest.setLong(index, 0);
                            }
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
    	}
    	 catch (SQLException sqle) {
			throw new DDBSToolkitException("Error during execution of the SQL request", sqle);
		}
        
        return false;
    }

    @Override
    public boolean delete(IEntity objectToDelete) throws DDBSToolkitException {

        //If the connector is opened
        if(myConnector.isOpen() && objectToDelete != null)
        {
            //Inspect object
            String tableName = ClassInspector.getClassName(objectToDelete);

            ArrayList<ClassProperty> listOfProperties = ClassInspector.exploreProperties(objectToDelete);

            ClassProperty primaryKey = null;
            for(ClassProperty property : listOfProperties)
            {
                if(property.isId())
                {
                    primaryKey = property;
                    break;
                }
            }

            if(primaryKey == null || primaryKey.getValue() == null || (Integer)primaryKey.getValue() == 0)
            {
                return false;
            }
            else
            {
                //Prepare the request
                String sqlStatement = "DELETE FROM `"+tableName+"`";

                //If a primary key has been found
                if(primaryKey != null && !primaryKey.equals(""))
                {
                    sqlStatement += " WHERE `"+primaryKey.getPropertyName()+"` = ?;";

                    //Prepare the request
                    PreparedStatement preparedRequest = myConnector.prepareStatement(sqlStatement);

                    try {

                        int index = 1;

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
                            if(primaryKey.getValue() != null)
                            {
                                preparedRequest.setLong(index, ((Timestamp)primaryKey.getValue()).getTime());
                            }
                            else
                            {
                                preparedRequest.setLong(index, 0);
                            }
                        }

                        //Execute prepared request
                        myConnector.executePreparedQuery(preparedRequest);

                        return true;

                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public <T extends IEntity> T loadArray(T objectToLoad, String field, String orderBy) throws DDBSToolkitException {

    	try
    	{
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
	                listCondition.add("`"+primaryKey.getPropertyName()+"` = "+primaryKey.getValue());
	
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
    	}
    	catch (Exception e) {
			throw new DDBSToolkitException("Error during use of the reflection mechanism", e);
		}
    }

    @Override
    public boolean createEntity(IEntity objectToCreate) throws DDBSToolkitException {

        //TODO
        return false;
    }

    protected <T extends IEntity> ArrayList<T> conversionResultSet(ResultSet results, T myObject) throws DDBSToolkitException {

    	try
    	{
	        ArrayList<T> resultList = new ArrayList<T>();
	
	        //For each object
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
	                        f.set(myData, new java.sql.Timestamp(results.getLong(myProperty.getPropertyName())));
	                    }
	                }
	            }
	
	            resultList.add(myData);
	        }
	
	        return resultList;
    	}
        catch (SecurityException se) {
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
    }
}

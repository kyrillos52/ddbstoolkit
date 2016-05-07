package org.ddbstoolkit.toolkit.modules.middleware.sqlspaces;

import info.collide.sqlspaces.commons.Tuple;

import java.io.*;
import org.ddbstoolkit.toolkit.core.DDBSAction;
import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.core.conditions.Conditions;
import org.ddbstoolkit.toolkit.core.orderby.OrderBy;

/**
 * Class to convert data objects into Tuples objects
 * User: Cyril GRANDJEAN
 * Date: 21/06/2012
 * Time: 10:36
 *
 * @version Creation of the class
 */
public class SqlSpacesConverter {

    /**
     * Convert the command object into tuple objects
     * @param myCommand DDBS Command
     * @param timeout Timeout
     * @throws Exception Exception
     * @return Tuple object
     */
    public static Tuple getTuple(DDBSCommand myCommand, int timeout) throws Exception {

        String conditionString = "";
        
        if(myCommand.getFieldToLoad() == null) {
            myCommand.setFieldToLoad("");
        }

        Tuple myTuple = new Tuple(SqlSpacesConverter.toString(myCommand.getAction()), SqlSpacesConverter.toString(myCommand.getObject()), conditionString, SqlSpacesConverter.toString(myCommand.getDestination()), SqlSpacesConverter.toString(myCommand.getOrderBy()), myCommand.getFieldToLoad());
        myTuple.setExpiration(timeout);
        return myTuple;
    }

    /**
     * Convert the tuple object into a command object
     * @param myCommand DDBS Command
     * @return DDBS Command
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public static DDBSCommand getObject(Tuple myCommand) throws ClassNotFoundException, IOException {

        DDBSCommand myObjectCommand = new DDBSCommand();
        myObjectCommand.setAction((DDBSAction) SqlSpacesConverter.fromString((String)myCommand.getField(0).getValue()));
        myObjectCommand.setObject((IEntity)SqlSpacesConverter.fromString((String)myCommand.getField(1).getValue()));
        myObjectCommand.setConditionQueryString((String) myCommand.getField(2).getValue());
        myObjectCommand.setConditions((Conditions)SqlSpacesConverter.fromString((String)myCommand.getField(3).getValue()));
        myObjectCommand.setDestination((Peer) SqlSpacesConverter.fromString((String)myCommand.getField(4).getValue()));
        myObjectCommand.setOrderBy((OrderBy) SqlSpacesConverter.fromString((String)myCommand.getField(5).getValue()));
        myObjectCommand.setFieldToLoad((String) myCommand.getField(6).getValue());
        return myObjectCommand;
    }

    /**
     * Read the object from Base64 string;
     * @param s String to convert to object
     * @return an Object
     * @throws IOException IO Exception
     * @throws ClassNotFoundException Class not found exception
     */
    public static Object fromString( String s ) throws IOException, ClassNotFoundException {
        byte [] data = Base64Coder.decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(  data ) );
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Write the object to a Base64 string
     * @param o object to convert to String
     * @return Base64 string
     * @throws IOException IO exception
     */
    public static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }
}

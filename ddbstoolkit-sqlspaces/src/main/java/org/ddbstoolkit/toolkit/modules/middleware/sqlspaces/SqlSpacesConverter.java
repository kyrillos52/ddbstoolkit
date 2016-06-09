package org.ddbstoolkit.toolkit.modules.middleware.sqlspaces;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.ddbstoolkit.toolkit.core.DDBSCommand;

import info.collide.sqlspaces.commons.Tuple;

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

        Tuple myTuple = new Tuple(SqlSpacesConverter.toString(myCommand));
        myTuple.setExpiration(timeout);
        return myTuple;
    }

    /**
     * Convert the tuple object into a command object
     * @param myCommand DDBS Command
     * @return DDBS Command
     * @throws IOException Input exception
     * @throws ClassNotFoundException Class not found
     */
    public static DDBSCommand getObject(Tuple myCommand) throws ClassNotFoundException, IOException {

        return (DDBSCommand) SqlSpacesConverter.fromString((String)myCommand.getField(0).getValue());
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

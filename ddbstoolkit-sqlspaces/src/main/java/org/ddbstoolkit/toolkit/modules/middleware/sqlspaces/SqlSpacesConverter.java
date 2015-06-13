package org.ddbstoolkit.toolkit.modules.middleware.sqlspaces;

import info.collide.sqlspaces.commons.Tuple;

import java.io.*;

import org.ddbstoolkit.toolkit.core.DDBSAction;
import org.ddbstoolkit.toolkit.core.DDBSCommand;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
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
     * @return Tuple object
     */
    public static Tuple getTuple(DDBSCommand myCommand, int timeout) throws Exception
    {

        if(myCommand.getOrderBy() == null)
        {
            myCommand.setOrderBy(OrderBy.get(null, null));
        }
        if(myCommand.getFieldToLoad() == null)
        {
            myCommand.setFieldToLoad("");
        }

        Tuple myTuple = new Tuple(myCommand.getAction(), SqlSpacesConverter.toString(myCommand.getObject()), myCommand.getConditionQueryString(), myCommand.getDestination(), myCommand.getOrderBy(), myCommand.getFieldToLoad());
        myTuple.setExpiration(timeout);
        return myTuple;
    }

    /**
     * Convert the tuple object into a command object
     * @param myCommand
     * @return
     */
    public static DDBSCommand getObject(Tuple myCommand)
    {

        DDBSCommand myObjectCommand = new DDBSCommand();
        myObjectCommand.setAction((DDBSAction) myCommand.getField(0).getValue());
        try {
            myObjectCommand.setObject((IEntity)SqlSpacesConverter.fromString((String)myCommand.getField(1).getValue()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        myObjectCommand.setConditionQueryString((String) myCommand.getField(2).getValue());
        myObjectCommand.setDestination((Peer) myCommand.getField(3).getValue());
        myObjectCommand.setOrderBy((OrderBy) myCommand.getField(4).getValue());
        myObjectCommand.setFieldToLoad((String) myCommand.getField(5).getValue());
        return myObjectCommand;
    }

    /**
     * Read the object from Base64 string;
     * @param s String to convert to object
     * @return an Object
     * @throws IOException
     * @throws ClassNotFoundException
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
     * @throws IOException
     */
    public static String toString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return new String( Base64Coder.encode( baos.toByteArray() ) );
    }
}

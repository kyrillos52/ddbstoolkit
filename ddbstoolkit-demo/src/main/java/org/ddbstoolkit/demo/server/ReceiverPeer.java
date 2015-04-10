package org.ddbstoolkit.demo.server;

import java.io.File;
import java.util.Scanner;

import org.ddbstoolkit.demo.shared.ConfigReader;
import org.ddbstoolkit.toolkit.core.DistributableReceiverInterface;
import org.ddbstoolkit.toolkit.modules.datastore.mysql.DistributedMySQLTableManager;
import org.ddbstoolkit.toolkit.modules.datastore.mysql.MySQLConnector;
import org.ddbstoolkit.toolkit.modules.middleware.jgroups.JGroupReceiver;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesReceiver;

/**
 * Server to answer requests
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class ReceiverPeer {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println("MySQL Receiver");

        System.out.println("Start the server");

        String userDir = System.getProperty("user.dir");
        File newFile = new File(userDir+"/config.ini");

        System.out.println("Load "+userDir+"/config.ini");

        //If there is a configuration file
        if(newFile.exists())
        {
            ConfigReader config = new ConfigReader("config.ini");

            Scanner sc = new Scanner(System.in);

            DistributedMySQLTableManager manager;
            if(config.getProperty("mysql-url") != null && config.getProperty("mysql-login") != null && config.getProperty("mysql-password") != null)
            {
                manager = new DistributedMySQLTableManager(new MySQLConnector(config.getProperty("mysql-url"), config.getProperty("mysql-login"), config.getProperty("mysql-password")));

                System.out.println("Instantiate the receiver");
                DistributableReceiverInterface receiver;

                //For SQLSpaces technology
                if(config.getProperty("cluster-name") != null && config.getProperty("peer-name") != null && config.getProperty("sqlspaces-server-ip") != null && config.getProperty("sqlspaces-server-port") != null)
                {

                    receiver = new SqlSpacesReceiver(manager, config.getProperty("cluster-name"), config.getProperty("peer-name"), config.getProperty("sqlspaces-server-ip"), Integer.parseInt(config.getProperty("sqlspaces-server-port")));

                    try {
                        System.out.println("Start the listener : Enter a touch to stop");
                        receiver.start();

                        System.out.println("Peer "+receiver.getMyPeer().getName()+" "+receiver.getMyPeer().getUid());

                        sc.nextLine();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        receiver.stop();

                        System.out.println("Stop the listener");
                    }
                }
                //For JGroups technology
                else if(config.getProperty("cluster-name") != null && config.getProperty("peer-name") != null)
                {
                    receiver = new JGroupReceiver(manager, config.getProperty("cluster-name"), config.getProperty("peer-name"));

                    try {
                        System.out.println("Start the listener : Enter a touch to stop");
                        receiver.start();

                        System.out.println("Peer "+receiver.getMyPeer().getName()+" "+receiver.getMyPeer().getUid());

                        sc.nextLine();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        receiver.stop();

                        System.out.println("Stop the listener");
                    }
                }
                else
                {
                    System.out.println("Variable cluster-name, peer-name must be set");
                }
            }
            else {

                System.out.println("Variable mysql-url, mysql-login and mysql-password must be set");
            }
        }

    }
}

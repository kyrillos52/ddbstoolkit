package org.ddbstoolkit.demo.client.view;


import javax.swing.*;

import org.ddbstoolkit.demo.shared.ConfigReader;

import java.io.File;

/**
 * Launch class of the application
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class LibraryManager {

    /**
     * Name of the cluster
     */
    public static String clusterName;

    /**
     * Name of the peer
     */
    public static String peerName;

    /**
     * Ip Address of the SQLSpaces Server
     */
    public static String ipAddress;

    /**
     * Port of the SQLSpaces Server
     */
    public static int port;

    /**
     * Middleware module used
     * 1: SQLSpaces
     * 2: JGroups
     */
    public static int middlewaremodule = 1;

    /**
     * Debug mode
     */
    private static boolean debugMode = false;

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        /**
         * Debug mode: deprecated
         */
        if(debugMode)
        {
            LibraryManager.clusterName = "defaultCluster";
            LibraryManager.peerName = "sender";
            LibraryManager.ipAddress = "127.0.0.1";
            LibraryManager.port = 2525;

            //Launch the main window
            JFrame frame = new JFrame("Library Manager");
            frame.setContentPane(new MainWindowGUI().getPanelMainWindow());
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
        else
        {
            String userDir = System.getProperty("user.dir");
            File newFile = new File(userDir+"/config.ini");

            System.out.println("Load "+userDir+"/config.ini");

            ConfigReader config = new ConfigReader("config.ini");

            //If there is a configuration file
            if(newFile.exists())
            {
                if(config.getProperty("cluster-name") != null && config.getProperty("peer-name") != null)
                {
                    LibraryManager.clusterName = config.getProperty("cluster-name");
                    LibraryManager.peerName = config.getProperty("peer-name");

                    //Use of SQLSpaces
                    if(config.getProperty("sqlspaces-server-ip") != null && config.getProperty("sqlspaces-server-port") != null)
                    {

                        LibraryManager.ipAddress = config.getProperty("sqlspaces-server-ip");
                        LibraryManager.port = Integer.parseInt(config.getProperty("sqlspaces-server-port"));

                        middlewaremodule = 1;
                    }
                    //Use of JGroups
                    else
                    {
                        //For JGroups technologies
                        middlewaremodule = 2;
                    }

                    //Launch the main window
                    JFrame frame = new JFrame("Library Manager");
                    frame.setContentPane(new MainWindowGUI().getPanelMainWindow());
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                }
                else
                {
                    System.out.println("Variable cluster-name, peer-name, sqlspaces-server-ip, sqlspaces-server-port must be set");
                }
            }
            else
            {
                System.out.println("File config.ini doesn't exist");
            }
        }
    }
}

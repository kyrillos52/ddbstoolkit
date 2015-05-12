package org.ddbstoolkit.demo.client.view;

import javax.swing.*;

import org.ddbstoolkit.demo.client.model.BookDataModel;
import org.ddbstoolkit.demo.model.Book;
import org.ddbstoolkit.toolkit.core.DistributableSenderInterface;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.modules.middleware.jgroups.JGroupSender;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesSender;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Main window of the application
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class MainWindowGUI {
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JPanel panelCommands;
    private JTable tableBooks;
    private JPanel labelSearch;
    private JFormattedTextField textFieldSearch;
    private JComboBox comboBoxType;
    private JComboBox comboBoxLocation;
    private JButton buttonSearch;
    private JPanel panelMainWindow;
    private JPanel panelTable;

    /**
     * Data model for books
     */
    private BookDataModel bookDataModel;

    /**
     * List of peers connected
     */
    private List<Peer> listPeers = new ArrayList<Peer>();

    public JPanel getPanelMainWindow() {
        return panelMainWindow;
    }

    public MainWindowGUI() {

        //Add the action listener for the search button
        buttonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {

                    DistributableSenderInterface sender = null;

                    //For SQLSpaces module
                    if(LibraryManager.middlewaremodule == 1)
                    {
                        sender = new SqlSpacesSender(LibraryManager.clusterName, LibraryManager.peerName, LibraryManager.ipAddress, LibraryManager.port);
                    }
                    //For JGroups module
                    else if(LibraryManager.middlewaremodule == 2)
                    {
                        sender = new JGroupSender(LibraryManager.clusterName, LibraryManager.peerName);
                    }

                    sender.open();

                    //Get the list of peers connected
                    listPeers = sender.getListPeers();

                    Book myBook = new Book();

                    //If a library has been selected
                    if(comboBoxLocation.getSelectedIndex() > 0)
                    {
                        myBook.setPeerUid(listPeers.get(comboBoxLocation.getSelectedIndex() - 1).getUid());
                    }

                    String conditionString = "";
                    String typeValue = (String)comboBoxType.getSelectedItem();

                    //Create the conditions to the request
                    if(typeValue.equals("All criteria") || typeValue.equals("Title"))
                    {
                        if(!conditionString.isEmpty())
                        {
                            conditionString += " OR ";
                        }
                        conditionString += " title LIKE '%"+textFieldSearch.getText()+"%'";
                    }
                    if(typeValue.equals("All criteria") || typeValue.equals("Summary"))
                    {
                        if(!conditionString.isEmpty())
                        {
                            conditionString += " OR ";
                        }
                        conditionString += " summary LIKE '%"+textFieldSearch.getText()+"%'";
                    }
                    if(typeValue.equals("All criteria") || typeValue.equals("Author"))
                    {
                        if(!conditionString.isEmpty())
                        {
                            conditionString += " OR ";
                        }
                        conditionString += " book_id IN (SELECT book_id FROM `Author` WHERE name LIKE '%"+textFieldSearch.getText()+"%')";
                    }
                    if(typeValue.equals("All criteria") || typeValue.equals("Genre"))
                    {
                        if(!conditionString.isEmpty())
                        {
                            conditionString += " OR ";
                        }
                        conditionString += " book_id IN (SELECT book_id FROM `Link_Book_Genre` WHERE genre_id IN (SELECT genre_id FROM `Genre` WHERE name LIKE '%"+textFieldSearch.getText()+"%'))";
                    }
                    if(typeValue.equals("All criteria") || typeValue.equals("Characters"))
                    {
                        if(!conditionString.isEmpty())
                        {
                            conditionString += " OR ";
                        }
                        conditionString += " book_id IN (SELECT book_id FROM `Character` WHERE character_name LIKE '%"+textFieldSearch.getText()+"%')";
                    }

                    System.out.println(conditionString);
                    List<String> conditionList = new ArrayList<String>();
                    conditionList.add(conditionString);

                    ArrayList<Book> listBooks = new ArrayList<Book>();

                    //Get the results
                    List<Book> listEntity = sender.listAll(myBook, conditionList, "title ASC");
                    for(IEntity entity : listEntity)
                    {
                        Book aBook = (Book)entity;
                        aBook = (Book) sender.loadArray(aBook, "author", "name ASC");
                        listBooks.add(aBook);
                    }
                    bookDataModel.setListBooks(listBooks);
                    bookDataModel.reloadData();

                    sender.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //Create the listener for the Add Button
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                //Open the book window
                JFrame frame = new JFrame("Add a new book");
                frame.setContentPane(new BookWindowGUI().getPanelForm());
                frame.setLocationRelativeTo(null);
                frame.pack();
                frame.setVisible(true);

            }
        });


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableBooks.getSelectedRow();
                if(indexRow != -1)
                {
                    //Open the window for books
                    JFrame frame = new JFrame(bookDataModel.getBook(indexRow).title);
                    frame.setContentPane(new BookWindowGUI(bookDataModel.getBook(indexRow)).getPanelForm());
                    frame.setLocationRelativeTo(null);
                    frame.pack();
                    frame.setVisible(true);
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no book");
                }
            }
        });

        //Create the listener for the delete button
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                //If one book has been selected
                int indexRow = tableBooks.getSelectedRow();
                if(indexRow != -1)
                {
                    try {
                        DistributableSenderInterface sender = null;

                        //For SQLSpaces module
                        if(LibraryManager.middlewaremodule == 1)
                        {
                            sender = new SqlSpacesSender(LibraryManager.clusterName, LibraryManager.peerName, LibraryManager.ipAddress, LibraryManager.port);
                        }
                        //For JGroups module
                        else if(LibraryManager.middlewaremodule == 2)
                        {
                            sender = new JGroupSender(LibraryManager.clusterName, LibraryManager.peerName);
                        }

                        sender.open();

                        Book bookToDelete;
                        bookToDelete = bookDataModel.getBook(indexRow);

                        //Delete the list of authors
                        bookToDelete = sender.loadArray(bookToDelete, "author", null);

                        if(bookToDelete.author != null)
                        {
                            for(int i = 0; i < bookToDelete.author.length; i++)
                            {
                                sender.delete(bookToDelete.author[i]);
                            }
                        }

                        //Delete the links to genres
                        bookToDelete = sender.loadArray(bookToDelete, "linkedGenre", null);

                        if(bookToDelete.linkedGenre != null)
                        {
                            for(int i = 0; i < bookToDelete.linkedGenre.length; i++)
                            {
                                sender.delete(bookToDelete.linkedGenre[i]);
                            }
                        }

                        //Delete the list of characters
                        bookToDelete = sender.loadArray(bookToDelete, "character", null);

                        if(bookToDelete.character != null)
                        {
                            for(int i = 0; i < bookToDelete.character.length; i++)
                            {
                                sender.delete(bookToDelete.character[i]);
                            }
                        }

                        //Delete the book
                        sender.delete(bookToDelete);

                        sender.close();

                        JOptionPane.showMessageDialog(null, "Book deleted");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no book");
                }
            }
        });
    }

    private void createUIComponents() {

        bookDataModel = new BookDataModel();

        tableBooks = new JTable(bookDataModel);
        tableBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        comboBoxLocation = new JComboBox();

        comboBoxType = new JComboBox();

        try {

            DistributableSenderInterface sender = null;

            //For SQLSpaces module
            if(LibraryManager.middlewaremodule == 1)
            {
                sender = new SqlSpacesSender(LibraryManager.clusterName, LibraryManager.peerName, LibraryManager.ipAddress, LibraryManager.port);
            }
            //For JGroups module
            else if(LibraryManager.middlewaremodule == 2)
            {
                sender = new JGroupSender(LibraryManager.clusterName, LibraryManager.peerName);
            }

            sender.open();

            listPeers = sender.getListPeers();

            comboBoxType.removeAllItems();
            comboBoxType.addItem("All criteria");
            comboBoxType.addItem("Title");
            comboBoxType.addItem("Summary");
            comboBoxType.addItem("Author");
            comboBoxType.addItem("Genre");
            comboBoxType.addItem("Characters");

            comboBoxLocation.removeAllItems();

            comboBoxLocation.addItem("All libraries");

            for(Peer peer : listPeers)
            {
                comboBoxLocation.addItem(peer.getName());
            }

            sender.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

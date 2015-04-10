package org.ddbstoolkit.demo.client.view;


import javax.swing.*;

import org.ddbstoolkit.demo.client.model.AuthorDataModel;
import org.ddbstoolkit.demo.client.model.CharacterDataModel;
import org.ddbstoolkit.demo.client.model.GenreDataModel;
import org.ddbstoolkit.demo.model.Author;
import org.ddbstoolkit.demo.model.Book;
import org.ddbstoolkit.demo.model.Genre;
import org.ddbstoolkit.demo.model.Link_Book_Genre;
import org.ddbstoolkit.toolkit.core.DistributableSenderInterface;
import org.ddbstoolkit.toolkit.core.IEntity;
import org.ddbstoolkit.toolkit.core.Peer;
import org.ddbstoolkit.toolkit.modules.datastore.jena.DistributedSPARQLManager;
import org.ddbstoolkit.toolkit.modules.middleware.jgroups.JGroupSender;
import org.ddbstoolkit.toolkit.modules.middleware.sqlspaces.SqlSpacesSender;
import org.ddbstoolkit.demo.model.Character;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Window to manage information about a book
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class BookWindowGUI extends JFrame {
    private JFormattedTextField formattedTextFieldTitle;
    private JTextArea textAreaSummary;
    private JLabel labelTitle;
    private JLabel labelSummary;
    private JPanel panelAuthors;
    private JTable tableAuthor;
    private JButton buttonAddGenre;
    private JButton buttonUpdateGenre;
    private JButton buttonDeleteGenre;
    private JButton buttonAddCharacter;
    private JButton buttonUpdateCharacter;
    private JButton deleteButton2;
    private JButton autofillButton;
    private JButton validateButton;
    private JLabel labelAuthor;
    private JPanel panelTopAuthors;
    private JButton buttonAddAuthor;
    private JButton buttonUpdateAuthor;
    private JButton buttonDeleteAuthor;
    private JPanel panelGenre;
    private JPanel panelTopGenre;
    private JPanel panelForm;
    private JLabel labelGenre;
    private JLabel labelCharacters;
    private JPanel panelCharacters;
    private JPanel panelCommands;
    private JTable tableGenre;
    private JPanel panelTopCharacters;
    private JTable tableCharacters;
    private JButton buttonDeleteCharacter;
    private JLabel labelLibrary;
    private JComboBox comboBoxLibrary;

    /**
     * Model for the table of authors
     */
    private AuthorDataModel authorDataModel;

    /**
     * Model for the table of genres
     */
    private GenreDataModel genreDataModel;

    /**
     * Model for the table of characters
     */
    private CharacterDataModel characterDataModel;

    /**
     * Array of peers connected
     */
    private List<Peer> listPeers = new ArrayList<Peer>();

    /**
     * Book to update
     */
    private final Book bookToUpdate;

    /**
     * Default constructor to add a book
     */
    public BookWindowGUI() {

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

            //Load the list of peers connected
            listPeers = sender.getListPeers();

            comboBoxLibrary.removeAllItems();

            for(Peer peer : listPeers)
            {
                comboBoxLibrary.addItem(peer.getName());
            }

            sender.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Add the listeners for add and update actions
        addCommonListeners();

        //Create the listener for add action
        validateButton.addActionListener(new ActionListener() {
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

                    Book bookToAdd = new Book();
                    bookToAdd.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                    bookToAdd.title = formattedTextFieldTitle.getText();
                    bookToAdd.summary = textAreaSummary.getText();

                    //Add the book
                    sender.add(bookToAdd);

                    bookToAdd = (Book) sender.readLastElement(bookToAdd);

                    //Add the authors linked to the book
                    for (Author author : authorDataModel.getListAuthors()) {
                        author.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                        author.book_id = bookToAdd.book_id;
                        sender.add(author);
                    }

                    //Add the genres
                    for (Genre genre : genreDataModel.getListGenres()) {
                        genre.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();

                        List<String> conditionList = new ArrayList<String>();
                        conditionList.add("name = '" + genre.name + "'");

                        List<Genre> listGenre = sender.listAll(genre, conditionList, null);

                        //If the genre didn't exist, the genre is added
                        if (listGenre == null || listGenre.size() == 0) {
                            sender.add(genre);
                            genre = (Genre) sender.readLastElement(genre);
                        } else {
                            genre = (Genre) listGenre.get(0);
                        }

                        //Add the link to the genre
                        Link_Book_Genre newLink = new Link_Book_Genre();
                        newLink.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                        newLink.book_id = bookToAdd.book_id;
                        newLink.genre_id = genre.genre_id;
                        sender.add(newLink);

                    }

                    //Add the characters
                    for (Character character : characterDataModel.getListCharacters()) {
                        character.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                        character.book_id = bookToAdd.book_id;
                        sender.add(character);
                    }

                    sender.close();

                    JOptionPane.showMessageDialog(null, "Book added");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        bookToUpdate = new Book();
    }

    /**
     * Default constructor for updating a book
     */
    public BookWindowGUI(Book bookToUpdate) {

        this.bookToUpdate = bookToUpdate;

        final Book myBook = bookToUpdate;

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

            comboBoxLibrary.removeAllItems();

            for(Peer peer : listPeers)
            {
                comboBoxLibrary.addItem(peer.getName());
            }

            //Update the form with the existing informations
            formattedTextFieldTitle.setText(bookToUpdate.title);
            textAreaSummary.setText(bookToUpdate.summary);

            for(Peer myPeer : listPeers)
            {
                if(myPeer.getUid().equals(bookToUpdate.peerUid))
                {
                    comboBoxLibrary.setSelectedItem(myPeer.getName());
                    break;
                }

            }

            bookToUpdate = sender.loadArray(bookToUpdate, "author", "name ASC");
            authorDataModel.setListAuthors(bookToUpdate.author);
            authorDataModel.reloadDataFromMySQLDatabase();

            bookToUpdate = sender.loadArray(bookToUpdate, "linkedGenre", null);
            if(bookToUpdate.linkedGenre != null)
            {
                Genre[] listGenre = new Genre[bookToUpdate.linkedGenre.length];
                for(int i = 0; i < bookToUpdate.linkedGenre.length; i++)
                {
                    Genre myGenre = new Genre();
                    myGenre.peerUid = bookToUpdate.peerUid;
                    myGenre.genre_id = bookToUpdate.linkedGenre[i].genre_id;
                    listGenre[i] = (Genre) sender.read(myGenre);
                }

                genreDataModel.setListGenres(listGenre);
                genreDataModel.reloadDataFromMySQLDatabase();
            }

            bookToUpdate = sender.loadArray(bookToUpdate, "character", "character_name ASC");
            characterDataModel.setListCharacters(bookToUpdate.character);
            characterDataModel.reloadDataFromMySQLDatabase();

            sender.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Add the listeners for actions for add and update action
        addCommonListeners();

        validateButton.addActionListener(new ActionListener() {
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

                    Book bookToModify = myBook;

                    //Book is updated on the same library
                    if(bookToModify.peerUid.equals(listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid()))
                    {
                        bookToModify.title = formattedTextFieldTitle.getText();
                        bookToModify.summary = textAreaSummary.getText();

                        //Update the book
                        sender.update(bookToModify);

                        //Delete the list of authors
                        bookToModify = (Book)sender.loadArray(bookToModify, "author", null);

                        if(bookToModify.author != null)
                        {
                            for(int i = 0; i < bookToModify.author.length; i++)
                            {
                                 sender.delete(bookToModify.author[i]);
                            }
                        }

                        //Add the authors linked to the book
                        for(Author author : authorDataModel.getListAuthors())
                        {
                            author.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                            author.book_id = bookToModify.book_id;
                            sender.add(author);
                        }

                        //Delete the links to the genre
                        bookToModify = sender.loadArray(bookToModify, "linkedGenre", null);

                        if(bookToModify.linkedGenre != null)
                        {
                            for(int i = 0; i < bookToModify.linkedGenre.length; i++)
                            {
                                sender.delete(bookToModify.linkedGenre[i]);
                            }
                        }

                        //Add the links to the genres
                        for(Genre genre : genreDataModel.getListGenres())
                        {
                            genre.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();

                            List<String> conditionList = new ArrayList<String>();
                            conditionList.add("name = '"+genre.name+"'");

                            List<Genre> listGenre = sender.listAll(genre, conditionList, null);

                            //If no genre
                            if(listGenre == null || listGenre.size() == 0)
                            {
                                sender.add(genre);
                                genre = sender.readLastElement(genre);
                            }
                            else
                            {
                                genre = listGenre.get(0);
                            }

                            //Add the link to the entity
                            Link_Book_Genre newLink = new Link_Book_Genre();
                            newLink.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                            newLink.book_id = bookToModify.book_id;
                            newLink.genre_id = genre.genre_id;
                            sender.add(newLink);

                        }

                        //Delete the list of authors
                        bookToModify = sender.loadArray(bookToModify, "character", null);

                        if(bookToModify.character != null)
                        {
                            for(int i = 0; i < bookToModify.character.length; i++)
                            {
                                sender.delete(bookToModify.character[i]);
                            }
                        }

                        //Add the new characters
                        for(Character character : characterDataModel.getListCharacters())
                        {
                            character.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                            character.book_id = bookToModify.book_id;
                            sender.add(character);
                        }

                    }
                    //Book is moved to another library
                    else
                    {
                        Book oldBook = bookToModify;

                        Book newBook = new Book();
                        newBook.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                        newBook.title = formattedTextFieldTitle.getText();
                        newBook.summary = textAreaSummary.getText();

                        //Add the book
                        sender.add(newBook);

                        newBook = sender.readLastElement(newBook);

                        //Delete the list of authors in the current library
                        oldBook = sender.loadArray(oldBook, "author", null);

                        if(oldBook.author != null)
                        {
                            for(int i = 0; i < oldBook.author.length; i++)
                            {
                                sender.delete(oldBook.author[i]);
                            }
                        }

                        //Add the authors linked to the book
                        for(Author author : authorDataModel.getListAuthors())
                        {
                            author.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                            author.book_id = newBook.book_id;
                            sender.add(author);
                        }

                        //Delete the links of genre in the current library
                        oldBook = sender.loadArray(oldBook, "linkedGenre", null);

                        if(oldBook.linkedGenre != null)
                        {
                            for(int i = 0; i < oldBook.linkedGenre.length; i++)
                            {
                                sender.delete(oldBook.linkedGenre[i]);
                            }
                        }

                        //Add the genres
                        for(Genre genre : genreDataModel.getListGenres())
                        {
                            genre.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();

                            List<String> conditionList = new ArrayList<String>();
                            conditionList.add("name = '"+genre.name+"'");

                            List<Genre> listGenre = sender.listAll(genre, conditionList, null);

                            //If no genre, add the genre
                            if(listGenre == null || listGenre.size() == 0)
                            {
                                sender.add(genre);
                                genre = sender.readLastElement(genre);
                            }
                            else
                            {
                                genre = listGenre.get(0);
                            }

                            //Add the link to the entity
                            Link_Book_Genre newLink = new Link_Book_Genre();
                            newLink.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                            newLink.book_id = newBook.book_id;
                            newLink.genre_id = genre.genre_id;
                            sender.add(newLink);

                        }

                        //Delete the list of characters to the current library
                        oldBook = sender.loadArray(oldBook, "character", null);

                        if(oldBook.character != null)
                        {
                            for(int i = 0; i < oldBook.character.length; i++)
                            {
                                sender.delete(oldBook.character[i]);
                            }
                        }

                        //Add the characters
                        for(Character character : characterDataModel.getListCharacters())
                        {
                            character.peerUid = listPeers.get(comboBoxLibrary.getSelectedIndex()).getUid();
                            character.book_id = newBook.book_id;
                            sender.add(character);
                        }

                        //Delete the book
                        sender.delete(oldBook);
                    }


                    sender.close();

                    JOptionPane.showMessageDialog(null, "Book updated");
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });
    }

    /**
     * Add the listeners for common button for adding and updating action
     */
    private void addCommonListeners()
    {

        //Create a listener for the Autofill button
        autofillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                try {

                    DistributedSPARQLManager manager = new DistributedSPARQLManager();

                    //1st try : Exact match (Much faster)
                    List<String> listCondition = listCondition = new ArrayList<String>();
                    listCondition.add(DistributedSPARQLManager.getObjectVariable(new Book())+" fb:type.object.name '"+formattedTextFieldTitle.getText()+"'@en");
                    listCondition.add("FILTER ( lang(?title) =  'en' )");
                    listCondition.add("FILTER ( lang(?summary) = 'en' )");

                    List<Book> listBook = manager.listAll(new Book(), listCondition, null);

                    //If the first matching didn't succeed
                    if(listBook.size() == 0)
                    {
                        //2nd try : regex matching (Quite slow)
                        listCondition = new ArrayList<String>();
                        listCondition.add(DistributedSPARQLManager.getObjectVariable(new Book())+" fb:type.object.name ?nameToFind");
                        listCondition.add("FILTER regex(str(?nameToFind), '"+formattedTextFieldTitle.getText()+"', 'i')");
                        listCondition.add("FILTER ( lang(?title) =  'en' )");
                        listCondition.add("FILTER ( lang(?summary) = 'en' )");

                        listBook = manager.listAll(new Book(), listCondition, null);
                    }

                    //No book found
                    if(listBook.size() == 0)
                    {
                        JOptionPane.showMessageDialog(null, "No book with the name "+formattedTextFieldTitle.getText());
                    }
                    //One book found
                    else if(listBook.size() == 1)
                    {
                        Book foundBook = listBook.get(0);

                        String substring = "@en";

                        int indexSubString = foundBook.title.indexOf(substring);

                        if(indexSubString != -1)
                        {
                            foundBook.title = foundBook.title.substring(0, indexSubString);
                        }

                        indexSubString = foundBook.summary.indexOf(substring);

                        if(indexSubString != -1)
                        {
                            foundBook.summary = foundBook.summary.substring(0, indexSubString);
                        }

                        formattedTextFieldTitle.setText(foundBook.title);
                        textAreaSummary.setText(foundBook.summary);

                        foundBook = manager.loadArray(foundBook, "author", "name ASC");
                        authorDataModel.setListAuthors(foundBook.author);
                        authorDataModel.reloadDataFromEndpoint();

                        foundBook = manager.loadArray(foundBook, "genre", "name ASC");
                        genreDataModel.setListGenres(foundBook.genre);
                        genreDataModel.reloadDataFromEndpoint();

                        foundBook = manager.loadArray(foundBook, "character", "character_name ASC");
                        characterDataModel.setListCharacters(foundBook.character);
                        characterDataModel.reloadDataFromEndpoint();
                    }
                    //Multiple books found
                    else
                    {
                        String[] choices = new String[listBook.size()];

                        //Create the list of the choice
                        for(int i = 0; i < listBook.size(); i++)
                        {
                            Book foundBook = (Book) listBook.get(i);

                            String substring = "@en";

                            int indexSubString = foundBook.title.indexOf(substring);

                            if(indexSubString != -1)
                            {
                                choices[i] = i+"-"+foundBook.title.substring(0, indexSubString);
                            }
                            else
                            {
                                choices[i] = i+"-"+foundBook.title;
                            }

                        }

                        String choiceString = (String)JOptionPane.showInputDialog(null,
                                "Found multiple books for "+formattedTextFieldTitle.getText(),
                                "Search results",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                choices,
                                choices[0]);

                        if(choiceString != null)
                        {
                            //Get the index of the book
                            int i = 0;
                            while(!choiceString.equals(choices[i]))
                            {
                                i++;
                            }

                            Book foundBook = (Book) listBook.get(i);

                            String substring = "@en";

                            int indexSubString = foundBook.title.indexOf(substring);

                            if(indexSubString != -1)
                            {
                                foundBook.title = foundBook.title.substring(0, indexSubString);
                            }

                            indexSubString = foundBook.summary.indexOf(substring);

                            if(indexSubString != -1)
                            {
                                foundBook.summary = foundBook.summary.substring(0, indexSubString);
                            }

                            formattedTextFieldTitle.setText(foundBook.title);
                            textAreaSummary.setText(foundBook.summary);

                            foundBook = manager.loadArray(foundBook, "author", "name ASC");
                            authorDataModel.setListAuthors(foundBook.author);
                            authorDataModel.reloadDataFromEndpoint();

                            foundBook = manager.loadArray(foundBook, "genre", "name ASC");
                            genreDataModel.setListGenres(foundBook.genre);
                            genreDataModel.reloadDataFromEndpoint();

                            foundBook = manager.loadArray(foundBook, "character", "character_name ASC");
                            characterDataModel.setListCharacters(foundBook.character);
                            characterDataModel.reloadDataFromEndpoint();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Create listeners on the table Author
        buttonAddAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String authorName = JOptionPane.showInputDialog(null, "Enter a name of an author : ",
                        "", 1);
                Author myAuthor = new Author();
                myAuthor.name = authorName;
                if(authorName != null)
                {
                    authorDataModel.addAuthor(myAuthor);
                    authorDataModel.reloadData();
                }
            }
        });
        buttonUpdateAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableAuthor.getSelectedRow();
                if(indexRow != -1)
                {
                    Author myAuthor = authorDataModel.getAuthor(indexRow);
                    String authorName = JOptionPane.showInputDialog(null, "Enter a name of an author : ", myAuthor.name);

                    if(authorName != null)
                    {
                        myAuthor.name = authorName;
                        authorDataModel.reloadData();
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no author");
                }
            }
        });
        buttonDeleteAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableAuthor.getSelectedRow();
                if(indexRow != -1)
                {
                    Author myAuthor = authorDataModel.getAuthor(indexRow);
                    authorDataModel.removeAuthor(myAuthor);
                    authorDataModel.reloadData();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no author");
                }
            }
        });

        //Create listeners for genres
        buttonAddGenre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String genreName = JOptionPane.showInputDialog(null, "Enter a name of a genre : ",
                        "", 1);
                Genre myGenre = new Genre();
                myGenre.name = genreName;
                if(genreName != null)
                {
                    genreDataModel.addGenre(myGenre);
                    genreDataModel.reloadData();
                }
            }
        });
        buttonUpdateGenre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableGenre.getSelectedRow();
                if(indexRow != -1)
                {
                    Genre myGenre = genreDataModel.getGenre(indexRow);
                    String genreName = JOptionPane.showInputDialog(null, "Enter a name of a genre : ", myGenre.name);

                    if(genreName != null)
                    {
                        myGenre.name = genreName;
                        genreDataModel.reloadData();
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no genre");
                }
            }
        });
        buttonDeleteGenre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableGenre.getSelectedRow();
                if(indexRow != -1)
                {
                    Genre myGenre = genreDataModel.getGenre(indexRow);
                    genreDataModel.removeGenre(myGenre);
                    genreDataModel.reloadData();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no author");
                }
            }
        });

        //Create listeners for Characters
        buttonAddCharacter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                String characterName = JOptionPane.showInputDialog(null, "Enter a name of a character : ",
                        "", 1);
                Character myCharacter = new Character();
                myCharacter.character_name = characterName;
                if(characterName != null)
                {
                    characterDataModel.addCharacter(myCharacter);
                    characterDataModel.reloadData();
                }
            }
        });
        buttonUpdateCharacter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableCharacters.getSelectedRow();
                if(indexRow != -1)
                {
                    Character myCharacter = characterDataModel.getCharacter(indexRow);
                    String characterName = JOptionPane.showInputDialog(null, "Enter a name of a character : ", myCharacter.character_name);

                    if(characterName != null)
                    {
                        myCharacter.character_name = characterName;
                        characterDataModel.reloadData();
                    }

                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no character");
                }
            }
        });
        buttonDeleteCharacter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                int indexRow = tableCharacters.getSelectedRow();
                if(indexRow != -1)
                {
                    Character myCharacter = characterDataModel.getCharacter(indexRow);
                    characterDataModel.removeCharacter(myCharacter);
                    characterDataModel.reloadData();
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You have selected no character");
                }
            }
        });
    }

    public JPanel getPanelForm() {
        return panelForm;
    }


    private void createUIComponents() {

        authorDataModel = new AuthorDataModel();

        tableAuthor = new JTable(authorDataModel);
        tableAuthor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        genreDataModel = new GenreDataModel();

        tableGenre = new JTable(genreDataModel);
        tableGenre.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        characterDataModel = new CharacterDataModel();

        tableCharacters = new JTable(characterDataModel);
        tableCharacters.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        comboBoxLibrary = new JComboBox();
    }
}

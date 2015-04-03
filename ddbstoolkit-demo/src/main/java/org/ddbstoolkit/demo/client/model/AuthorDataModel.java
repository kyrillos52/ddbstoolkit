package org.ddbstoolkit.demo.client.model;


import javax.swing.table.DefaultTableModel;

import org.ddbstoolkit.demo.model.Author;

import java.util.ArrayList;

/**
 * TableModel of authors
 * User: Cyril GRANDJEAN
 * Date: 27/06/2012
 * Time: 14:16
 *
 * @version Creation of the class
 */
public class AuthorDataModel extends DefaultTableModel {

    /**
     * List of authors
     */
    private Author[] listAuthors;

    /**
     * List of filtered authors
     */
    private ArrayList<Author> listAuthorsFiltered = new ArrayList<Author>();

    /**
     * Constructor
     */
    public AuthorDataModel() {
    }

    /**
     * Get access to the list of authors
     * @return list of filtered authors
     */
    public ArrayList<Author> getListAuthors() {
        if(listAuthorsFiltered == null)
        {
            listAuthorsFiltered = new ArrayList<Author>();
        }
        return listAuthorsFiltered;
    }

    /**
     * Add an author to the collection
     * @param myAuthor author to add
     */
    public void addAuthor(Author myAuthor)
    {
        listAuthorsFiltered.add(myAuthor);
    }

    /**
     * Remove an author from the collection
     * @param myAuthor author to remove
     */
    public void removeAuthor(Author myAuthor)
    {
        listAuthorsFiltered.remove(myAuthor);
    }

    /**
     * Set an author to a position
     * @param row index in the collection
     * @param myAuthor author to update
     * @return
     */
    public Author setAuthor(int row, Author myAuthor) {
        return listAuthorsFiltered.set(row, myAuthor);
    }

    /**
     * Get an author in a selected index
     * @param row index
     * @return
     */
    public Author getAuthor(int row) {
        return listAuthorsFiltered.get(row);
    }

    public void setListAuthors(Author[] listAuthors) {
        this.listAuthors = listAuthors;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public String getColumnName(int arg0) {

        String name;

        switch (arg0) {
            case 0:
                name = "Name";
                break;
            default:
                name = "";
        }

        return name;
    }

    @Override
    public int getRowCount() {
        return getListAuthors().size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object book;

        switch (column) {
            case 0:
                book = getAuthor(row).name;
                break;
            default:
                book = null;
        }

        return book;
    }

    /**
     * Reload data from the remote endpoint
     */
    public void reloadDataFromEndpoint() {

        String substring = "@en";
        listAuthorsFiltered = new ArrayList<Author>();

        for(int i = 0; i < listAuthors.length; i++)
        {
            int indexSubString = listAuthors[i].name.indexOf(substring);
            //If data found
            if(indexSubString != -1)
            {
                 Author myAuthor = listAuthors[i];
                 myAuthor.name = listAuthors[i].name.substring(0, indexSubString);
                 listAuthorsFiltered.add(myAuthor);
            }
        }

        reloadData();
    }

    /**
     * Reload data from the MySQL database
     */
    public void reloadDataFromMySQLDatabase() {

        listAuthorsFiltered = new ArrayList<Author>();

        for(int i = 0; i < listAuthors.length; i++)
        {
            Author myAuthor = listAuthors[i];
            listAuthorsFiltered.add(myAuthor);
        }

        reloadData();
    }


    /**
     * Reload data from author
     */
    public void reloadData() {

        this.fireTableDataChanged();
    }
}

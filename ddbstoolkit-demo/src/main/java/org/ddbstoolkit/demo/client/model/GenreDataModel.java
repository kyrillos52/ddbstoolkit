package org.ddbstoolkit.demo.client.model;

import javax.swing.table.DefaultTableModel;

import org.ddbstoolkit.demo.model.Genre;

import java.util.ArrayList;

/**
 * TableModel of Genres
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
@SuppressWarnings("serial")
public class GenreDataModel extends DefaultTableModel {

    /**
     * List of genres
     */
    private Genre[] listGenres;

    /**
     * List of filtered genres
     */
    private ArrayList<Genre> listGenreFiltered = new ArrayList<Genre>();

    /**
     * Constructor
     */
    public GenreDataModel() {
    }

    public void addGenre(Genre myGenre)
    {
        listGenreFiltered.add(myGenre);
    }

    public void removeGenre(Genre myGenre)
    {
        listGenreFiltered.remove(myGenre);
    }

    public Genre setGenre(int row, Genre myGenre) {
        return listGenreFiltered.set(row, myGenre);
    }

    /**
     * Get access to the list of genres
     * @return list of genres
     */
    public ArrayList<Genre> getListGenres() {
        if(listGenreFiltered == null)
        {
            listGenreFiltered = new ArrayList<Genre>();
        }
        return listGenreFiltered;
    }

    public Genre getGenre(int row) {
        return listGenreFiltered.get(row);
    }

    public void setListGenres(Genre[] listGenre) {
        this.listGenres = listGenre;
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
        return getListGenres().size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object book;

        switch (column) {
            case 0:
                book = getGenre(row).name;
                break;
            default:
                book = null;
        }

        return book;
    }

    /**
     * Reload data from the remote Endpoint
     */
    public void reloadDataFromEndpoint() {

        listGenreFiltered = new ArrayList<Genre>();
        String substring = "@en";

        for(int i = 0; i < listGenres.length; i++)
        {
            int indexSubString = listGenres[i].name.indexOf(substring);
            //If data found
            if(indexSubString != -1)
            {
                Genre myGenre = listGenres[i];
                myGenre.name = listGenres[i].name.substring(0, indexSubString);
                listGenreFiltered.add(myGenre);
            }
        }
        reloadData();
    }

    /**
     * Reload data from the MySQL database
     */
    public void reloadDataFromMySQLDatabase() {

        listGenreFiltered = new ArrayList<Genre>();

        for(int i = 0; i < listGenres.length; i++)
        {
            Genre myGenre = listGenres[i];
            listGenreFiltered.add(myGenre);
        }
        reloadData();
    }

    /**
     * Reload data
     */
    public void reloadData() {

        this.fireTableDataChanged();
    }
}

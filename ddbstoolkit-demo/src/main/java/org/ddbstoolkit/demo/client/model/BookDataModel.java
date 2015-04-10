package org.ddbstoolkit.demo.client.model;

import javax.swing.table.DefaultTableModel;

import org.ddbstoolkit.demo.model.Book;

import java.util.ArrayList;

/**
 * TableModel of Books
 * @author Cyril GRANDJEAN
 * @version 1.0 Creation of the class
 */
public class BookDataModel extends DefaultTableModel {

    /**
     * List of books
     */
    private ArrayList<Book> listBooks;

    /**
     * Constructor
     */
    public BookDataModel() {
    }

    /**
     * Get access to the list of books
     * @return list of books
     */
    public ArrayList<Book> getListBooks() {
        if(listBooks == null)
        {
            listBooks = new ArrayList<Book>();
        }
        return listBooks;
    }

    public Book getBook(int row) {
        return listBooks.get(row);
    }

    public void setListBooks(ArrayList<Book> listBooks) {
        this.listBooks = listBooks;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int arg0) {

        String name;

        switch (arg0) {
            case 0:
                name = "Title";
                break;
            case 1:
                name = "Authors";
                break;
            default:
                name = "";
        }

        return name;
    }

    @Override
    public int getRowCount() {
        return getListBooks().size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object book;

        switch (column) {
            case 0:
                book = getBook(row).title;
                break;
            case 1:
                String value = "";
                Book aBook = getBook(row);
                for(int i = 0; i < aBook.author.length; i++)
                {
                    value += aBook.author[i].name;
                }
                book = value;
                break;
            default:
                book = null;
        }

        return book;
    }

    /**
     * Reload data
     */
    public void reloadData() {

        this.fireTableDataChanged();
    }
}

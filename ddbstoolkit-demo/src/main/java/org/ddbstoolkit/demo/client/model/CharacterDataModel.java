package org.ddbstoolkit.demo.client.model;

import org.ddbstoolkit.demo.model.Character;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/**
 * TableModel of characters
 * User: Cyril GRANDJEAN
 * Date: 27/06/2012
 * Time: 14:17
 *
 * @version Creation of the class
 */
public class CharacterDataModel extends DefaultTableModel {

    /**
     * List of characters
     */
    private Character[] listCharacters;

    /**
     * List of genre filtered
     */
    private ArrayList<Character> listCharactersFiltered = new ArrayList<Character>();

    /**
     * Constructor
     */
    public CharacterDataModel() {
    }

    /**
     * Get access to the list of characters
     * @return list of characters
     */
    public ArrayList<Character> getListCharacters() {
        if(listCharactersFiltered == null)
        {
            listCharactersFiltered = new ArrayList<Character>();
        }
        return listCharactersFiltered;
    }

    public Character getCharacter(int row) {
        return listCharactersFiltered.get(row);
    }

    public void setListCharacters(Character[] listCharacters) {
        this.listCharacters = listCharacters;
    }

    public void addCharacter(Character myCharacter)
    {
        listCharactersFiltered.add(myCharacter);
    }

    public void removeCharacter(Character myCharacter)
    {
        listCharactersFiltered.remove(myCharacter);
    }

    public Character setCharacter(int row, Character myCharacter) {
        return listCharactersFiltered.set(row, myCharacter);
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
        return getListCharacters().size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Object book;

        switch (column) {
            case 0:
                book = getCharacter(row).character_name;
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

        listCharactersFiltered = new ArrayList<Character>();
        String substring = "@en";

        for(int i = 0; i < listCharacters.length; i++)
        {
            int indexSubString = listCharacters[i].character_name.indexOf(substring);
            //If data found
            if(indexSubString != -1)
            {
                Character myCharacter = listCharacters[i];
                myCharacter.character_name = listCharacters[i].character_name.substring(0, indexSubString);
                listCharactersFiltered.add(myCharacter);
            }
        }

        reloadData();
    }

    /**
     * Reload data from the MySQL database
     */
    public void reloadDataFromMySQLDatabase() {

        listCharactersFiltered = new ArrayList<Character>();

        for(int i = 0; i < listCharacters.length; i++)
        {
            Character myCharacter = listCharacters[i];
            listCharactersFiltered.add(myCharacter);
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

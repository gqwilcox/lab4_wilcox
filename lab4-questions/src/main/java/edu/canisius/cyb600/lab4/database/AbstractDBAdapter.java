package edu.canisius.cyb600.lab4.database;

import edu.canisius.cyb600.lab4.dataobjects.Actor;
import edu.canisius.cyb600.lab4.dataobjects.Category;
import edu.canisius.cyb600.lab4.dataobjects.Film;

import java.sql.Connection;
import java.util.List;

/**
 * Abstract DB Adapter
 */
public abstract class AbstractDBAdapter {
    Connection conn;

    public AbstractDBAdapter(Connection conn) {
        this.conn = conn;
    }

    public abstract List<String> getCategories();

    public abstract List<Film> checkFilmLength(int length);

    public abstract List<Actor> checkFirstLetterFirstName(char firstLetter);

    public abstract List<Film> getFilmCat(Category category);

    public abstract List<Actor> insertActorsByLastName(List<Actor> actors);

    //SELECTS

    //INSERTS

    //JOIN


}

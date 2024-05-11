package edu.canisius.cyb600.lab4.database;

import edu.canisius.cyb600.lab4.dataobjects.Actor;
import edu.canisius.cyb600.lab4.dataobjects.Category;
import edu.canisius.cyb600.lab4.dataobjects.Film;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Posgres Implementation of the db adapter.
 */
public class PostgresDBAdapter extends AbstractDBAdapter {

    public PostgresDBAdapter(Connection conn) {
        super(conn);
    }

    @Override
    public List<String> getCategories() {
        try (Statement statement = conn.createStatement()) {
            //This statement is easy
            //Select * from actor is saying "Return all Fields for all rows in films". Because there
            //is no "where clause", all rows are returned
            ResultSet results = statement.executeQuery("Select DISTINCT NAME from CATEGORY");
            //Initialize an empty List to hold the return set of films.
            List<String> categories = new ArrayList<>();
            //Loop through all the results and create a new Film object to hold all its information
            while (results.next()) {
                String categoryName = results.getString("NAME");
                categories.add(categoryName);
            }
            //Return all the films.
            return categories;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();

    }

    @Override
    public List<Film> checkFilmLength(int length) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM FILM WHERE LENGTH > ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, length);
            ResultSet results = statement.executeQuery();

            while (results.next()) {
                Film film = new Film();
                film.setFilmId(results.getInt("FILM_ID"));
                film.setTitle(results.getString("TITLE"));
                film.setDescription(results.getString("DESCRIPTION"));
                film.setReleaseYear(results.getString("RELEASE_YEAR"));
                film.setLanguageId(results.getInt("LANGUAGE_ID"));
                film.setRentalDuration(results.getInt("RENTAL_DURATION"));
                film.setRentalRate(results.getDouble("RENTAL_RATE"));
                film.setLength(results.getInt("LENGTH"));
                film.setReplacementCost(results.getDouble("REPLACEMENT_COST"));
                film.setRating(results.getString("RATING"));
                film.setSpecialFeatures(results.getString("SPECIAL_FEATURES"));
                film.setLastUpdate(results.getDate("LAST_UPDATE"));
                films.add(film);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }

        return films;
    }

    @Override
    public List<Actor> checkFirstLetterFirstName(char firstLetter) {
        try (Statement statement = conn.createStatement()) {

            String sqlQuery = "SELECT * FROM actor WHERE LOWER(LEFT(FIRST_NAME, 1)) = '" + Character.toLowerCase(firstLetter) + "'";
            ResultSet results = statement.executeQuery(sqlQuery);

            List<Actor> actors = new ArrayList<>();
            while (results.next()) {
                Actor actor = new Actor();
                actor.setActorId(results.getInt("ACTOR_ID"));
                actor.setFirstName(results.getString("FIRST_NAME"));
                actor.setLastName(results.getString("LAST_NAME"));
                actor.setLastUpdate(results.getDate("LAST_UPDATE"));

                actors.add(actor);
            }
            return actors;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Film> getFilmCat(Category category) {
        String sql = "SELECT * " +
                "FROM film " +
                "JOIN film_category ON film.film_id = film_category.film_id " +
                "JOIN category ON film_category.category_id = category.category_id " +
                "WHERE category.category_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, category.getCategoryId());

            ResultSet results = statement.executeQuery();
            List<Film> films = new ArrayList<>();
            while (results.next()) {
                Film film = new Film();
                film.setFilmId(results.getInt("film_id"));
                film.setTitle(results.getString("title"));
                film.setDescription(results.getString("description"));
                film.setReleaseYear(results.getString("release_year"));
                film.setLanguageId(results.getInt("language_id"));
                film.setRentalDuration(results.getInt("rental_duration"));
                film.setRentalRate(results.getDouble("rental_rate"));
                film.setLength(results.getInt("length"));
                film.setReplacementCost(results.getDouble("replacement_cost"));
                film.setRating(results.getString("rating"));
                film.setSpecialFeatures(results.getString("special_features"));
                film.setLastUpdate(results.getDate("last_update"));

                films.add(film);
            }
            return films;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Actor> insertActorsByLastName(List<Actor> actors) {
        String sql = "INSERT INTO ACTOR (first_name, last_name) VALUES (?, ?) RETURNING ACTOR_ID, LAST_UPDATE";
        List<Actor> insertedActors = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (Actor actor : actors) {
                if (actor.getLastName().length() % 2 != 0) {
                    statement.setString(1, actor.getFirstName());
                    statement.setString(2, actor.getLastName());
                    try (ResultSet results = statement.executeQuery()) {
                        if (results.next()) {
                            Actor insertedActor = new Actor();
                            insertedActor.setActorId(results.getInt("ACTOR_ID"));
                            insertedActor.setLastUpdate(results.getDate("LAST_UPDATE"));
                            insertedActor.setFirstName(actor.getFirstName()); // Set first name from original actor object
                            insertedActor.setLastName(actor.getLastName());   // Set last name from original actor object
                            insertedActors.add(insertedActor);
                            System.out.println("Inserted actor: " + insertedActor.getFirstName() + " " + insertedActor.getLastName());
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }

        return insertedActors;
    }


}

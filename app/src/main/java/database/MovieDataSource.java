package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import models.Movie;

/**
 * Created by kristian on 15-4-7.
 */
public class MovieDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_MOVIE_TITLE, MySQLiteHelper.COLUMN_MOVIE_PROGRESS};
    private Context con;

    public MovieDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        this.con = context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Movie createMovie(String movieTitle, String movieProgress) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_MOVIE_TITLE, movieTitle);
        values.put(MySQLiteHelper.COLUMN_MOVIE_PROGRESS, movieProgress);
        long insertId = database.insert(MySQLiteHelper.TABLE_MOVIES, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_MOVIES,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Movie newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public void deleteMovie(Movie movie) {
        long id = movie.getId();
//        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_MOVIES, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public ArrayList<Movie> getAllMovie() {
        ArrayList<Movie> userList = new ArrayList<Movie>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_MOVIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Movie movie = cursorToUser(cursor);
            userList.add(movie);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return userList;
    }

    private Movie cursorToUser(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(cursor.getLong(0));
        movie.setMovieTitle(cursor.getString(1));
        movie.setMovieProgress(Float.parseFloat(cursor.getString(2)));
        return movie;
    }

    /**
     * Remove all users and groups from database.
     */
    public void removeAll() {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
//        SQLiteDatabase db = helper.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        database.delete(MySQLiteHelper.TABLE_MOVIES, null, null);
    }
}
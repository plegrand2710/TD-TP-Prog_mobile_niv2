package com.TD1.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBAdapter {
    private static final String DATABASE_NAME = "GameScoresDB.db";
    private static final String TABLE_SCORES = "scores";
    private static final String KEY_ID = "id";
    private static final String KEY_PSEUDO = "pseudo";
    private static final String KEY_SCORE = "score";
    private Connection connection;

    public DBAdapter() {
        try {
            FileHandle dbFile = Gdx.files.local(DATABASE_NAME);
            String url = "jdbc:sqlite:" + dbFile.file().getAbsolutePath();
            Gdx.app.log("applicationGameSnak", "Database path: " + dbFile.file().getAbsolutePath());

            connection = DriverManager.getConnection(url);
            Gdx.app.log("applicationGameSnak", "Database connected successfully.");

            createTable();
        } catch (SQLException e) {
            Gdx.app.error("applicationGameSnak", "Error initializing database: " + e.getMessage(), e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_SCORES + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_PSEUDO + " TEXT NOT NULL, " +
            KEY_SCORE + " INTEGER NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertScore(String pseudo, int score) {
        String sql = "INSERT INTO " + TABLE_SCORES + " (" + KEY_PSEUDO + ", " + KEY_SCORE + ") VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pseudo);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Array<String> getAllScores() {
        Array<String> scoresList = new Array<>();
        String sql = "SELECT " + KEY_PSEUDO + ", " + KEY_SCORE + " FROM " + TABLE_SCORES + " ORDER BY " + KEY_SCORE + " DESC";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String pseudo = rs.getString(KEY_PSEUDO);
                int score = rs.getInt(KEY_SCORE);
                scoresList.add(pseudo + " - " + score);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scoresList;
    }

    public ArrayList<String> getTop3Scores() {
        ArrayList<String> topScores = new ArrayList<>();
        String sql = "SELECT " + KEY_PSEUDO + ", " + KEY_SCORE + " FROM " + TABLE_SCORES + " ORDER BY " + KEY_SCORE + " DESC LIMIT 3";

        Gdx.app.log("applicationGameSnak", "Executing SQL: " + sql);

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String pseudo = rs.getString(KEY_PSEUDO);
                int score = rs.getInt(KEY_SCORE);
                topScores.add(pseudo + " - " + score);
            }
            Gdx.app.log("applicationGameSnak", "Scores retrieved: " + topScores);
        } catch (SQLException e) {
            Gdx.app.error("applicationGameSnak", "Error fetching scores: " + e.getMessage(), e);
        }
        return topScores;
    }

    public void updateScore(int id, String pseudo, int score) {
        String sql = "UPDATE " + TABLE_SCORES + " SET " + KEY_PSEUDO + " = ?, " + KEY_SCORE + " = ? WHERE " + KEY_ID + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, pseudo);
            pstmt.setInt(2, score);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteScore(int id) {
        String sql = "DELETE FROM " + TABLE_SCORES + " WHERE " + KEY_ID + " = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAllScores() {
        String sql = "DELETE FROM " + TABLE_SCORES;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.TD1.Pauline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoresFileAdapter {
    private static final String SCORE_FILE_NAME = "GameScores.txt";

    public ScoresFileAdapter() {
        FileHandle file = Gdx.files.local(SCORE_FILE_NAME);
        if (!file.exists()) {
            file.writeString("", false);
        }
    }

    public void insertScore(String pseudo, int score) {
        FileHandle file = Gdx.files.local(SCORE_FILE_NAME);
        String line = pseudo + " - " + score;
        file.writeString(line + "\n", true);
    }

    public ArrayList<String> getAllScores() {
        ArrayList<ScoreEntry> entries = getAllScoreEntries();
        Collections.sort(entries, new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry o1, ScoreEntry o2) {
                return Integer.compare(o2.score, o1.score);
            }
        });
        ArrayList<String> scoresList = new ArrayList<>();
        for (ScoreEntry e : entries) {
            scoresList.add(e.pseudo + " - " + e.score);
        }
        return scoresList;
    }

    public ArrayList<String> getTop3Scores() {
        ArrayList<ScoreEntry> entries = getAllScoreEntries();
        Collections.sort(entries, new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry o1, ScoreEntry o2) {
                return Integer.compare(o2.score, o1.score);
            }
        });
        ArrayList<String> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, entries.size()); i++) {
            ScoreEntry e = entries.get(i);
            top3.add(e.pseudo + " - " + e.score);
        }
        return top3;
    }

    public void deleteAllScores() {
        FileHandle file = Gdx.files.local(SCORE_FILE_NAME);
        file.writeString("", false);
    }

    private ArrayList<ScoreEntry> getAllScoreEntries() {
        ArrayList<ScoreEntry> entries = new ArrayList<>();
        FileHandle file = Gdx.files.local(SCORE_FILE_NAME);
        String content = file.readString();
        String[] lines = content.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("-");
            if (parts.length >= 2) {
                String pseudo = parts[0].trim();
                try {
                    int score = Integer.parseInt(parts[1].trim());
                    entries.add(new ScoreEntry(pseudo, score));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return entries;
    }

    private static class ScoreEntry {
        String pseudo;
        int score;

        ScoreEntry(String pseudo, int score) {
            this.pseudo = pseudo;
            this.score = score;
        }
    }
}

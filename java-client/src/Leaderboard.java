import java.io.*;
import java.util.*;
import java.nio.file.*;

// Leaderboard system for the rhythm boxing game.
// Manages player scores and stores them in a file.
public class Leaderboard {
    private static final String LEADERBOARD_FILE = "text/leaderboard.txt";
    private static final int MAX_ENTRIES = 10; // Maximum number of entries to keep
    private List<PlayerScore> scores;

    // Inner class to represent a player score entry
    public static class PlayerScore implements Comparable<PlayerScore> {
        private String playerName;
        private DifficultyManager.Difficulty levelReached;
        private int points;

        public PlayerScore(String playerName, DifficultyManager.Difficulty levelReached, int points) {
            this.playerName = playerName;
            this.levelReached = levelReached;
            this.points = points;
        }

        @Override
        public int compareTo(PlayerScore other) {
            // Primary sort by points (descending)
            if (other.points != this.points) {
                return Integer.compare(other.points, this.points);
            }
            // Secondary sort by level (higher difficulty first)
            if (this.levelReached != other.levelReached) {
                return other.levelReached.ordinal() - this.levelReached.ordinal();
            }
            // Finally sort by name (alphabetical)
            return this.playerName.compareTo(other.playerName);
        }

        @Override
        public String toString() {
            return String.format("%-20s %-10s %d", playerName, levelReached, points);
        }

        // Getters
        public String getPlayerName() {
            return playerName;
        }

        public DifficultyManager.Difficulty getLevelReached() {
            return levelReached;
        }

        public int getPoints() {
            return points;
        }
    }

    // Constructor - loads existing leaderboard if available
    public Leaderboard() {
        scores = new ArrayList<>();
        loadLeaderboard();
    }

    // Add a new score to the leaderboard
    public boolean addScore(String playerName, DifficultyManager.Difficulty levelReached, int points) {
        PlayerScore newScore = new PlayerScore(playerName, levelReached, points);
        scores.add(newScore);

        // Sort the scores
        Collections.sort(scores);

        // Trim to max entries
        if (scores.size() > MAX_ENTRIES) {
            scores = new ArrayList<>(scores.subList(0, MAX_ENTRIES));
        }

        // Save to file
        saveLeaderboard();

        // Return true if score made it to the leaderboard
        return scores.contains(newScore);
    }

    // Load leaderboard from file
    private void loadLeaderboard() {
        try {
            if (!Files.exists(Paths.get(LEADERBOARD_FILE))) {
                return; // File doesn't exist yet
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(LEADERBOARD_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String name = parts[0];
                        DifficultyManager.Difficulty level = DifficultyManager.Difficulty.valueOf(parts[1]);
                        int pts = Integer.parseInt(parts[2]);
                        scores.add(new PlayerScore(name, level, pts));
                    }
                }
            }

            // Sort after loading
            Collections.sort(scores);

        } catch (IOException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing difficulty level: " + e.getMessage());
        }
    }

    // Save leaderboard to file
    private void saveLeaderboard() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (PlayerScore score : scores) {
                writer.write(score.getPlayerName() + "," +
                             score.getLevelReached() + "," +
                             score.getPoints());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    // Check if a score would place on the leaderboard
    public boolean isHighScore(int points) {
        return scores.size() < MAX_ENTRIES || points > scores.get(scores.size() - 1).getPoints();
    }

    // Get current leaderboard
    public List<PlayerScore> getLeaderboard() {
        return new ArrayList<>(scores);
    }

    // Display the leaderboard to console
    public void displayLeaderboard() {
        System.out.println("\n============== RHYTHM BOXING LEADERBOARD ===============\n");
        System.out.println(String.format("%-4s %-20s %-10s %s", "RANK", "PLAYER", "LEVEL", "POINTS"));
        System.out.println("--------------------------------------------------------");

        int rank = 1;
        for (PlayerScore score : scores) {
            System.out.println(String.format("%-4d %s", rank++, score.toString()));
        }

        if (scores.isEmpty()) {
            System.out.println("No scores recorded yet!");
        }

        System.out.println("--------------------------------------------------------");
    }
}

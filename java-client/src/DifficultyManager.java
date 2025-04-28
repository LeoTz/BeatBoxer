import java.util.HashMap;
import java.util.Map;

public class DifficultyManager {
    // Difficulty levels
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    private Difficulty currentDifficulty;
    private Map<Difficulty, Integer> thresholds;
    private PointSystem pointSystem;
    
    // Constructor
    public DifficultyManager(PointSystem pointSystem) {
        this.pointSystem = pointSystem;
        this.currentDifficulty = Difficulty.EASY; // Start at easy
        
        // Initialize thresholds for difficulty level
        this.thresholds = new HashMap<>();
        thresholds.put(Difficulty.EASY, 150);    // Need 150 points to advance from EASY
        thresholds.put(Difficulty.MEDIUM, 450);  // Need 450 points to advance from MEDIUM
    }
    
    // Check if player can advance to next difficulty
    public boolean canAdvanceToNextLevel() {
        Integer threshold = thresholds.get(currentDifficulty); // Get the current threshold
        return threshold != null && pointSystem.getTotalPoints() >= threshold; // Compare with player's total points
    }
    
    // Move to next difficulty level if conditions are met
    public boolean advanceToNextLevel() {
        if (!canAdvanceToNextLevel()) {
            return false; // Do nothing if player doesn't meet the threshold
        }
        
        switch (currentDifficulty) {
            case EASY:
                currentDifficulty = Difficulty.MEDIUM; // Move to MEDIUM
                break;
            case MEDIUM:
                currentDifficulty = Difficulty.HARD; // Move to HARD
                break;
            case HARD:
                return false; // Already at highest difficulty
            default:
                return false;
        }
        return true; // Advancement succeeded
    }
    
    // Get the current difficulty level
    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }
    
    // Get parameters specific to the current difficulty level
    public DifficultyParams getDifficultyParams() {
        switch (currentDifficulty) {
            case EASY:
                return new DifficultyParams(1.0f, 20, 4000, 6000); // Easy settings
            case MEDIUM:
                return new DifficultyParams(1.2f, 30, 3000, 4500); // Medium settings
            case HARD:
                return new DifficultyParams(1.4f, 40, 1000, 2500); // Hard settings
            default:
                return new DifficultyParams(1.0f, 20, 4000, 6000); // Default fallback
        }
    }
    
    // Inner class for storing difficulty-specific parameters
    public static class DifficultyParams {
        private float pointMultiplier; // Score multiplier
        private int missPenalty; // Points deducted for missing a beat
        private int minInterval; // Minimum beat interval in ms
        private int maxInterval; // Maximum beat interval in ms
        
        public DifficultyParams(float pointMultiplier, int missPenalty, int minInterval, int maxInterval) {
            this.pointMultiplier = pointMultiplier;
            this.missPenalty = missPenalty;
            this.minInterval = minInterval;
            this.maxInterval = maxInterval;
        }
        
        public float getPointMultiplier() {
            return pointMultiplier;
        }
        
        public int getMissPenalty() {
            return missPenalty;
        }
        
        public int getMinInterval() {
            return minInterval;
        }
        
        public int getMaxInterval() {
            return maxInterval;
        }
    }
}

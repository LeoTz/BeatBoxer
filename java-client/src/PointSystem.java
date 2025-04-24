public class PointSystem {
    // Penalty for off-beat punch
    private int missPenaltyPoints = 20;
    
    // Difficulty multiplier
    private float difficultyMultiplier = 1.0f;
    
    // Stats tracking
    private int totalPoints = 0;
    private int totalPunches = 0;
    private int missedPunches = 0;
    
    // Set difficulty multiplier
    public void setDifficultyMultiplier(float multiplier) {
        this.difficultyMultiplier = multiplier;
    }
    
    // Set miss penalty
    public void setMissPenalty(int penalty) {
        this.missPenaltyPoints = penalty;
    }

    // Add points for a successful punch (on beat)
    public int addPunchPoints(int speed) {
        totalPunches++;
        
        // Calculate points based on speed
        int pointsEarned = calculatePoints(speed);
        
        // Apply difficulty multiplier
        pointsEarned = Math.round(pointsEarned * difficultyMultiplier);
        
        // Update total points
        totalPoints += pointsEarned;
        
        return pointsEarned;
    }
    
    // Calculate points based on punch speed
    private int calculatePoints(int speed) {
        float normalizedSpeed = (speed / 127.0f) * 50;
        return Math.round(normalizedSpeed);
    }
    
    // Deduct points for a missed punch (off-beat)
    public int missedPunch() {
        totalPunches++;
        missedPunches++;

        // Deduct penalty points
        totalPoints -= missPenaltyPoints;
        
        // Prevent negative score
        if (totalPoints < 0) totalPoints = 0;

        return -missPenaltyPoints;
    }
    
    // Reset all points and stats
    public void resetPoints() {
        totalPoints = 0;
        totalPunches = 0;
        missedPunches = 0;
    }
    
    // Get total points
    public int getTotalPoints() {
        return totalPoints;
    }
    
    // Get total punches
    public int getTotalPunches() {
        return totalPunches;
    }
    
    // Get missed punches
    public int getMissedPunches() {
        return missedPunches;
    }
    
    // Calculate average points per punch
    public float getAveragePointsPerPunch() {
        if (totalPunches == 0) return 0;
        return (float) totalPoints / totalPunches;
    }
}

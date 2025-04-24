import jssc.SerialPortException;

public class Driver {

    public static void main(String[] args) {
        try {
            // Display game title
            System.out.println("========================================================");
            System.out.println("            BEAT BOXER - THREE LEVEL GAME");
            System.out.println("========================================================");

            // Display game rules
            System.out.println("\n--------------------- GAME RULES -----------------------\n");
            System.out.println("1. Punch the bag in sync with the music beat.");
            System.out.println("2. Each punch is scored based on:");
            System.out.println("   - Speed (how fast you punch)");
            System.out.println("   - Timing (whether close to beat)");
            System.out.println("3. Missing a beat results in a penalty.");
            System.out.println("4. Perform well to level up from EASY to MEDIUM to HARD.");
            System.out.println("5. Your score will be recorded in the leaderboard.");
            System.out.println("--------------------------------------------------------\n");

            // Create the GameManager which manages all components and states
            GameManager gameManager = new GameManager();

            // Display leaderboard before starting the game
            gameManager.getLeaderboard().displayLeaderboard();

            // Start with EASY level
            gameManager.startGame();

            // Let EASY level run for 30 seconds
            Thread.sleep(30000);

            // End EASY level
            gameManager.endLevel();

            // Wait for user to continue (e.g., press Enter or choose next)
            while (gameManager.isWaitingForUser());

            // Check if player advanced to MEDIUM level
            if (gameManager.getDifficultyManager().getCurrentDifficulty() == DifficultyManager.Difficulty.MEDIUM) {
                // Start MEDIUM level
                gameManager.startGame();
                Thread.sleep(30000); // Let MEDIUM level run for 30 seconds
                gameManager.endLevel(); // End MEDIUM level
            }

            while (gameManager.isWaitingForUser());

            // Check if player advanced to HARD level
            if (gameManager.getDifficultyManager().getCurrentDifficulty() == DifficultyManager.Difficulty.HARD) {
                // Start HARD level
                gameManager.startGame();
                Thread.sleep(30000); // Let HARD level run for 30 seconds
                gameManager.endLevel(); // End HARD level
            }

            // Game is complete
            System.out.println("\nGame complete!");

        } catch (SerialPortException e) {
            System.err.println("Serial port error: " + e.getMessage());
            e.printStackTrace(); // Debug info
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
            e.printStackTrace(); // Debug info
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace(); // Debug info
        }
    }
}

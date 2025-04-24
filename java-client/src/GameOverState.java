import java.util.Scanner;

class GameOverState implements GameState {
    private GameManager gameManager;
    private Leaderboard leaderboard;

    public GameOverState(GameManager gameManager) {
        this.gameManager = gameManager;
        this.leaderboard = gameManager.getLeaderboard(); // Use the existing leaderboard instance
    }

    @Override
    public void start() {
        System.out.println("\n--- GAME OVER ---");

        int finalScore = gameManager.getPointSystem().getTotalPoints();
        DifficultyManager.Difficulty levelReached = gameManager.getDifficultyManager().getCurrentDifficulty();

        System.out.println("Final score: " + finalScore);
        System.out.println("Difficulty reached: " + levelReached);

        // Check for high score
        if (leaderboard.isHighScore(finalScore)) {
            System.out.println("\n🎉 Congratulations! You've achieved a high score!");
            System.out.print("Enter your name: ");

            Scanner scanner = new Scanner(System.in);
            String playerName = scanner.nextLine().trim();

            if (playerName.isEmpty()) {
                playerName = "Anonymous";
            }

            leaderboard.addScore(playerName, levelReached, finalScore);

            scanner.close();
        }

        // Display the leaderboard
        leaderboard.displayLeaderboard();

        System.out.println("\nThank you for playing!");

        // Shutdown beat-producing background threads
        if (gameManager.getBeatProducer() instanceof ConcreteSubject) {
            ((ConcreteSubject) gameManager.getBeatProducer()).shutdown();
        }
    }

    @Override
    public void end() {
    }
}
import java.util.Scanner;
import jssc.SerialPortException;

public class GameManager {
    // Game states
    private GameState currentState;
    private GameState easyState;
    private GameState mediumState;
    private GameState hardState;
    private GameState gameOverState;

    // Core components
    private PointSystem pointSystem;
    private DifficultyManager difficultyManager;
    private BeatProducer beatProducer;
    private ArduinoCommunicator arduinoCommunicator;
    private Leaderboard leaderboard;

    // User interaction flag
    private boolean waitingForUser = false;

    // Constructor initializes game components and states
    public GameManager() throws SerialPortException {
        this.pointSystem = new PointSystem();
        this.difficultyManager = new DifficultyManager(pointSystem);
        this.beatProducer = new BeatProducer();
        this.arduinoCommunicator = new ArduinoCommunicator(beatProducer, pointSystem);
        this.leaderboard = new Leaderboard();

        // Initialize game states
        this.easyState = new EasyState(this);
        this.mediumState = new MediumState(this);
        this.hardState = new HardState(this);
        this.gameOverState = new GameOverState(this);

        // Set initial state to EASY
        this.currentState = easyState;
    }

    // Start the current level
    public void startGame() throws SerialPortException, InterruptedException {
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

        // Wait for user to press Enter
        System.out.print("\nPress Enter to start the ");
        if (currentState == easyState) System.out.println("Easy level.");
        else if (currentState == mediumState) System.out.println("Medium level.");
        else if (currentState == hardState) System.out.println("Hard level.");
        scanner.nextLine();

        // Countdown before starting the level
        System.out.println("\nGet ready!");
        for (int i = 5; i > 0; i--) {
            System.out.println(i + "...");
            Thread.sleep(1000);
        }
        System.out.println("Go!\n");

        // Small delay before starting
        Thread.sleep(500);

        // Start the current state
        currentState.start();

        // Allow time for background threads
        Thread.sleep(1000);
    }

    // End current level and evaluate next steps
    public void endLevel() throws SerialPortException, InterruptedException {
        currentState.end();

        if (difficultyManager.canAdvanceToNextLevel()) {
            offerNextLevel();
        } else {
            setGameOverState();
        }
    }

    // Ask the player if they want to continue to the next level
    private void offerNextLevel() throws SerialPortException, InterruptedException {
        waitingForUser = true;

        System.out.println("\nCongratulations! You've scored " + pointSystem.getTotalPoints() + " points!");
        System.out.println("Would you like to proceed to the next level? (yes/no)");

        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("yes") || input.equals("y")) {
            difficultyManager.advanceToNextLevel();
            setStateForCurrentDifficulty();
        } else {
            setGameOverState();
        }

        waitingForUser = false;
    }

    // Set the current game state based on the difficulty level
    private void setStateForCurrentDifficulty() {
        switch (difficultyManager.getCurrentDifficulty()) {
            case EASY:
                currentState = easyState;
                break;
            case MEDIUM:
                currentState = mediumState;
                break;
            case HARD:
                currentState = hardState;
                break;
            default:
                currentState = easyState;
        }
    }

    // Set the game to Game Over state
    private void setGameOverState() throws SerialPortException, InterruptedException {
        currentState = gameOverState;
        currentState.start();
    }

    // Component getters
    public PointSystem getPointSystem() {
        return pointSystem;
    }

    public DifficultyManager getDifficultyManager() {
        return difficultyManager;
    }

    public BeatProducer getBeatProducer() {
        return beatProducer;
    }

    public ArduinoCommunicator getArduinoCommunicator() {
        return arduinoCommunicator;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public boolean isWaitingForUser() {
        return waitingForUser;
    }
}

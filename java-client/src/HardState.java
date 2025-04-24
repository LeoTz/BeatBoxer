import jssc.SerialPortException;

public class HardState implements GameState {
    private final GameManager gameManager;

    public HardState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void start() throws SerialPortException, InterruptedException {
        System.out.println("\n--- STARTING HARD LEVEL ---");

        // Fetch hard difficulty parameters
        DifficultyManager.DifficultyParams params = 
            gameManager.getDifficultyManager().getDifficultyParams();

        // Apply difficulty settings
        gameManager.getPointSystem().setDifficultyMultiplier(params.getPointMultiplier());
        gameManager.getPointSystem().setMissPenalty(params.getMissPenalty());
        gameManager.getBeatProducer().setMinInterval(params.getMinInterval());
        gameManager.getBeatProducer().setMaxInterval(params.getMaxInterval());

        // Start beat and punch detection
        gameManager.getBeatProducer().startLevel();
        gameManager.getArduinoCommunicator().startPunchDetection();
    }

    @Override
    public void end() {
        gameManager.getBeatProducer().endLevel();
        gameManager.getArduinoCommunicator().stopPunchDetection();

        try {
            Thread.sleep(200); // Brief pause to ensure a clean shutdown
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n--- ENDING HARD LEVEL ---");
    }
}

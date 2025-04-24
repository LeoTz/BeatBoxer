// Easy level state

import jssc.SerialPortException;

public class EasyState implements GameState {
    private GameManager gameManager;

    // Constructor to initialize GameManager
    public EasyState(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    // Start the EASY level
    @Override
    public void start() throws SerialPortException, InterruptedException {
        System.out.println("\n--- STARTING EASY LEVEL ---");

        // Get difficulty parameters for EASY level
        DifficultyManager.DifficultyParams params = 
            gameManager.getDifficultyManager().getDifficultyParams();

        // Configure point system based on difficulty
        gameManager.getPointSystem().setDifficultyMultiplier(params.getPointMultiplier());
        gameManager.getPointSystem().setMissPenalty(params.getMissPenalty());

        // Configure beat producer with EASY timing intervals
        gameManager.getBeatProducer().setMinInterval(params.getMinInterval());
        gameManager.getBeatProducer().setMaxInterval(params.getMaxInterval());

        // Start producing beats
        gameManager.getBeatProducer().startLevel();

        // Start detecting punches from Arduino
        gameManager.getArduinoCommunicator().startPunchDetection();
    }

    // End the EASY level
    @Override
    public void end() {
        // Stop beat production and punch detection
        gameManager.getBeatProducer().endLevel();
        gameManager.getArduinoCommunicator().stopPunchDetection();

        // Give some time for components to fully stop
        try {
            Thread.sleep(200); // small delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n--- ENDING EASY LEVEL ---");
    }
}

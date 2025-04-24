// Medium level state

import jssc.SerialPortException;

public class MediumState implements GameState {
    private GameManager gameManager;
    
    public MediumState(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    @Override
    public void start() throws SerialPortException, InterruptedException {
        System.out.println("\n--- STARTING MEDIUM LEVEL ---");
        
        // Configure components for medium difficulty
        DifficultyManager.DifficultyParams params = 
            gameManager.getDifficultyManager().getDifficultyParams();
        
        // Set difficulty-specific parameters
        gameManager.getPointSystem().setDifficultyMultiplier(params.getPointMultiplier());
        gameManager.getPointSystem().setMissPenalty(params.getMissPenalty());
        gameManager.getBeatProducer().setMinInterval(params.getMinInterval());
        gameManager.getBeatProducer().setMaxInterval(params.getMaxInterval());
        
        // Start the level
        gameManager.getBeatProducer().startLevel();
        gameManager.getArduinoCommunicator().startPunchDetection();
    }
    
    @Override
    public void end() {
        gameManager.getBeatProducer().endLevel();
        gameManager.getArduinoCommunicator().stopPunchDetection();
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } // wait for some time for everything to end
        
        System.out.println("\n--- ENDING MEDIUM LEVEL ---");
    }
}

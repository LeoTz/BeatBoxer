import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeatProducer extends ConcreteSubject implements Runnable {
    private final String DRUM_SAMPLE_PATH = "sounds/drum_beat.wav"; // Path to the drum beat sound file
    
    private ScheduledExecutorService scheduler;
    private Random random = new Random();
    private volatile boolean isRunning = false;
    private Clip audioClip;
    
    // Difficulty configuration: beat intervals in milliseconds
    private int minInterval; // Minimum interval between beats
    private int maxInterval; // Maximum interval between beats
    
    /**
     * Constructor - initializes the beat producer with default difficulty settings.
     * Loads the drum beat audio sample.
     */
    public BeatProducer() {
        super();
        // Set default beat interval (easy difficulty)
        this.minInterval = 2000; // 2 seconds
        this.maxInterval = 4000; // 4 seconds
        
        // Load the audio clip for the beat sound
        loadAudioClip();
    }
    
    /**
     * Loads the drum beat sound clip from the specified file path.
     */
    private void loadAudioClip() {
        try {
            File audioFile = new File(DRUM_SAMPLE_PATH);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading drum sample: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Starts the level by beginning random beat generation.
     * If already running, it first shuts down the previous scheduler.
     */
    public void startLevel() {
        if (scheduler != null && !scheduler.isTerminated()) {
            endLevel();
        }
        
        // Start the beat generation
        isRunning = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.execute(this);
        
        System.out.println("Beat production started with intervals " + minInterval + "-" + maxInterval + "ms\n");
    }
    
    /**
     * Ends the current level by stopping the beat generator thread.
     */
    public void endLevel() {
        isRunning = false;
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                // Allow up to 2 seconds for the scheduler to terminate gracefully
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                scheduler.shutdownNow();
            }
        }
    }
    
    /**
     * Main loop for beat generation.
     * Waits for a random interval between beats, then notifies observers and plays the sound.
     */
    @Override
    public void run() {
        try {
            // Initial delay before starting beats
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Beat detector interrupted during startup");
            return;
        }
        
        while (isRunning) {
            try {
                // Compute next interval randomly within min and max bounds
                int nextInterval = minInterval + random.nextInt(maxInterval - minInterval);
                
                // Notify all observers (e.g., ArduinoCommunicator)
                notifyObservers();
                
                // Play drum beat sound
                playBeat();
                
                // Wait for next beat
                Thread.sleep(nextInterval);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error in beat detector: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Plays the drum beat by resetting and starting the audio clip.
     */
    private void playBeat() {
        if (audioClip != null) {
            audioClip.setFramePosition(0); // Reset to the beginning of the clip
            audioClip.start(); // Play clip
        }
    }
    
    // Setters for beat interval range, useful for adjusting difficulty
    public void setMinInterval(int minInt) {
        this.minInterval = minInt;
    }
    
    public void setMaxInterval(int maxInt) {
        this.maxInterval = maxInt;
    }
}

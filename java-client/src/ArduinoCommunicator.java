import jssc.SerialPortException;

public class ArduinoCommunicator extends ConcreteObserver implements Runnable {
    private SerialPortHandle sph;
    private volatile boolean isRunning = false;
    private PointSystem pointSystem;
    private boolean isConnected = false;
    private Thread communicationThread; // Manages the thread handling communication with Arduino
    
    private int speed = 0;
    
    // Constructor that accepts a Subject and PointSystem
    public ArduinoCommunicator(Subject subject, PointSystem pointSystem) throws SerialPortException {
        super(subject);
        this.sph = new SerialPortHandle("COM14"); // Open Serial Port
        this.pointSystem = pointSystem;
        this.isConnected = true;
    }
    
    // Alternative constructor that creates a new PointSystem if none is provided
    public ArduinoCommunicator(Subject subject) throws SerialPortException {
        this(subject, new PointSystem());
    }

    // Starts punch detection after the game begins
    public void startPunchDetection() throws SerialPortException, InterruptedException {
        if (!isConnected) {
            System.err.println("Cannot start punch detection: Arduino not connected");
            return;
        }
        
        Thread.sleep(500); // Short delay to stabilize communication

        startPunchDetectionThread(); // Launch punch detection in a separate thread
    }

    // Sends a signal to Arduino when a beat is detected
    @Override
    public void update() {
        try {
            if (isConnected) {
                sph.writeByte((byte) 0x01); // Code for Beat detected: 0000 0001
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    // Launches punch detection in a new thread
    public void startPunchDetectionThread() {
        if (!isConnected) {
            System.err.println("Cannot start punch detection: Arduino not connected");
            return;
        }
        
        isRunning = true; // Flag to keep the thread running
        
        // Only create and start the thread if it's not already running
        if (communicationThread == null || !communicationThread.isAlive()) {
            communicationThread = new Thread(this, "ArduinoCommunicationThread");
            communicationThread.start();
        }
    }
    
    /**
     * Contains the punch detection loop
     * Executed when the communication thread starts
     */
    @Override
    public void run() {
        speed = 0; // Reset speed at thread start
        
        while (isRunning && isConnected) {
            try {
                int response = sph.readByte() & 0xFF; // Read one byte from Arduino

                // Parse the response byte
                boolean isMissedBeat = (response & 0b10000000) != 0; // Bit 7 (MSB)
                int value = response & 0b01111111; // Lower 7 bits represent speed
                
                if (isMissedBeat) {
                    int penalty = pointSystem.missedPunch();
                    System.out.println("Missed beat! Penalty: " + penalty +
                                      " | Total: " + pointSystem.getTotalPoints());
                    speed = 0; // Reset after miss
                    continue;
                }
                
                speed = value; // Assign speed value
                
                if (speed > 0) {
                    int points = pointSystem.addPunchPoints(speed);
                    System.out.println("Punch on beat! Speed: " + speed +
                                       " | Points: " + points +
                                       " | Total: " + pointSystem.getTotalPoints());
                    speed = 0; // Reset after successful punch
                }

                Thread.sleep(50); // Prevent busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interrupt status
                break;
            } catch (Exception e) {
                System.err.println("Arduino communication error: " + e.getMessage());
                isRunning = false; // Stop on error
            }
        }
    }

    /**
     * Stops punch detection and signals the Arduino to stop
     */
    public void stopPunchDetection() {
        if (!isConnected) {
            System.err.println("Cannot stop punch detection: Arduino not connected");
            return;
        }
        
        stopPunchDetectionThread(); // Stop the detection thread
    }
    
    /**
     * Gracefully stops the punch detection thread
     */
    public void stopPunchDetectionThread() {
        isRunning = false; // Signal thread to stop
        
        if (communicationThread != null && communicationThread.isAlive()) {
            communicationThread.interrupt(); // Interrupt the thread
            
            try {
                communicationThread.join(1000); // Wait for the thread to finish
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for communication thread to stop");
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
            communicationThread = null; // Clean up thread reference
        }
    }
    
    /**
     * Closes the serial connection and releases resources
     */
    public void close() {
        stopPunchDetectionThread(); // Ensure thread is stopped
        isConnected = false; // Mark device as disconnected
    }
}

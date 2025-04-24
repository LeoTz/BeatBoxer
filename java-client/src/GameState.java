// State interface

import jssc.SerialPortException;

public interface GameState {
    void start() throws SerialPortException, InterruptedException;
    void end();
}
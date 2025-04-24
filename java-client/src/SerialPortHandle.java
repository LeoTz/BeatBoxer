import jssc.SerialPort;
import jssc.SerialPortException;

// Class for handling serial port communication using the jSSC library.
class SerialPortHandle {
    SerialPort sp; // SerialPort object for communication
    String path; // Path of the serial port

    // Constructor to initialize and open the serial port.
    public SerialPortHandle(String path) {
        super(); // Calls the parent class constructor
        this.sp = new SerialPort(path); // Create a new SerialPort instance.
        this.path = path;
        try {
            sp.openPort(); // Open the serial port.
            sp.setParams(9600, 8, 1, 0); // Set serial parameters: Baud rate 9600, 8 data bits, 1 stop bit, no parity.

            // Flush garbage data on initial open
            while (sp.getInputBufferBytesCount() > 0) {
                sp.readBytes(); // Read and discard available bytes.
            }
        } catch (SerialPortException e) {
            e.printStackTrace(); // Print error details if serial port fails to open.
        }
    }

    // Reads a line of text from the serial port.
    public String readLine() {
        StringBuilder string = new StringBuilder(); // Buffer to store incoming data.

        while (true) {
            try {
                byte[] buffer = sp.readBytes(1); // Read one byte at a time.

                if (buffer == null || buffer.length == 0)
                    continue; // If no data is available, continue loop.

                char c = (char) buffer[0]; // Convert byte to character.

                // Check for end of line characters (newline '\n' or carriage return '\r').
                if (c == '\n' || c == '\r') {
                    if (string.length() == 0) {
                        continue; // Skip empty lines
                    } else {
                        break; // return non-empty line
                    }
                }

                string.append(c); // Append character to buffer.
            } catch (SerialPortException e) {
                e.printStackTrace(); // Print error if reading fails.
                break;
            }
        }
        return string.toString().trim(); // Return the received line after trimming whitespace.
    }
    
    // Writes a single byte to the serial port.
    public void writeByte(byte b) throws SerialPortException {
        sp.writeBytes(new byte[] { b }); // Convert the byte to an array and write it to the serial port.
    }

    // Reads a single byte from the serial port.
    @SuppressWarnings("deprecation")
	public byte readByte() throws SerialPortException {
        byte[] buffer = sp.readBytes(1); // Read 1 byte from the serial port (blocking read).
        
        if (buffer != null && buffer.length > 0) {
            return buffer[0]; // Return the received byte.
        } else {
            // Throw an exception if no data is received.
            throw new SerialPortException(path, "readByte", "No data received");
        }
    }


    // Sends a string over the serial port, followed by a newline character.
    public void printLine(String s) {
        byte byteArray[] = s.getBytes(); // Convert string to byte array.
        try {
            sp.writeBytes(byteArray); // Send the string as bytes.
            sp.writeByte((byte) '\n'); // Append a newline character.
        } catch (SerialPortException e1) {
            e1.printStackTrace(); // Print error details if sending fails.
        }
    }
}

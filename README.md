# Beat Boxer

An interactive rhythm-based boxing game that combines Java software with Arduino hardware to create an immersive boxing experience.

## Description

Beat Boxer is a game where players throw real punches detected by Arduino sensors, timed with music beats. The application combines physical exercise with rhythm gaming for an engaging workout experience.

## Prerequisites

- Eclipse IDE for Java development
- Arduino IDE
- Arduino Board
- jSSC library for serial communication

## Installation

### Java Setup (Eclipse)

1. **Clone the repository**
   ```bash
   git clone https://github.com/LeoTz/BeatBoxer.git
   ```

2. **Import into Eclipse**
   - Open Eclipse IDE
   - Go to File > Import > Existing Projects into Workspace
   - Select the cloned repository folder
   - Click Finish

3. **Configure Build Path**
   - Right-click project > Build Path > Configure Build Path
   - Under Libraries tab, add the `jSSC.jar` file from the lib/ folder

4. **Run the Application**
   - Open `Driver.java`
   - Right-click > Run As > Java Application

### Arduino Setup

1. **Open Arduino Code**
   - Launch Arduino IDE
   - Open the main sketch file in the arduino/ folder

2. **Configure Arduino**
   - Select your board from Tools > Board
   - Select the correct port from Tools > Port

3. **Upload Code**
   - Connect Arduino via USB
   - Click the Upload button

## Project Structure

```
beat-boxer/
├── src/              # Java source files
├── lib/              # External libraries (including jSSC.jar)
├── arduino/          # Arduino code files
├── sounds/           # Music and sound effects
└── text/             # Game text and configuration files
```

## Troubleshooting

- **Port Connection Issues**: Make sure the correct COM port is selected in both Arduino IDE and the Java application
- **Missing Libraries**: Verify jSSC.jar is properly added to the build path
- **Communication Errors**: Ensure no other program is using the same serial port

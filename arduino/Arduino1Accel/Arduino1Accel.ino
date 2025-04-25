#include <SoftwareSerial.h>

const int groundpin = 18;  // Ground
const int powerpin = 19;   // Power
const int xpin = A3;       // Accelerometer X-axis

const int LED_PIN = LED_BUILTIN;
const int BUZZER_PIN = 8;  // Buzzer on pin 8

const unsigned long PUNCH_WINDOW_MS = 300;  // Punch detection window
const int threshold = 400;      // Threshold for punch detection
const int maxExpected = 1200;   // Max expected value

unsigned long lastBeatTime = 0;
bool beatActive = false;

SoftwareSerial mySerial(10, 11); // RX, TX

void setup() {
  mySerial.begin(9600);

  pinMode(groundpin, OUTPUT);
  pinMode(powerpin, OUTPUT);
  digitalWrite(groundpin, LOW);
  digitalWrite(powerpin, HIGH);

  pinMode(LED_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  digitalWrite(BUZZER_PIN, LOW);  // Make sure buzzer is off at start
}

void loop() {
  // === Listen for Beat Signal ===
  if (mySerial.available()) {
    byte command = mySerial.read();

    if (command == 0x01) { // Beat received
      // Flash LED
      digitalWrite(LED_PIN, HIGH);
      delay(50);
      digitalWrite(LED_PIN, LOW);

      lastBeatTime = millis();
      beatActive = true;

      // Get ready to measure the punch
      int minVal = 1023;
      int maxVal = 0;
      unsigned long startTime = millis();
      
      // Read initial values but don't use them yet
      for (int i = 0; i < 3; i++) {
        analogRead(xpin);
        delay(1);
      }
      
      // Measure the punch over the time window
      while (millis() - startTime < PUNCH_WINDOW_MS) {
        int currentX = analogRead(xpin);
        
        // Track min and max values to calculate the total swing
        minVal = min(minVal, currentX);
        maxVal = max(maxVal, currentX);
        
        delay(2);  // Small delay between readings
      }
      
      // Calculate the total swing range - this represents punch force
      int punchStrength = maxVal - minVal;
      
      if (punchStrength > threshold) {
        // Map the punch strength to a speed value (1-127)
        byte speed = map(punchStrength, threshold, maxExpected, 1, 127);
        speed = constrain(speed, 1, 127);
        byte encoded = 0b00000000 | speed; // Bit 7 (MSB) remains 0 (on-beat)
        mySerial.write(encoded);
      } else {
        mySerial.write(0b10000000); // Missed beat
        
        // Beat missed – buzz!
        tone(BUZZER_PIN, 1000);
        delay(200);  // Buzz for 200ms
        noTone(BUZZER_PIN);

        
      }
    }
  }
}

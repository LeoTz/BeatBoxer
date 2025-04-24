#include <SoftwareSerial.h>

const int LED_PIN_1 = 2;         // LED for visual feedback
const int LED_PIN_2 = 3;

SoftwareSerial mySerial(10, 11); // RX, TX

void setup() {
  mySerial.begin(9600);

  pinMode(LED_PIN_1, OUTPUT);
  pinMode(LED_PIN_2, OUTPUT);
}

void loop() {
  // --- Handle incoming beat signal (LED command) ---
  if (mySerial.available() > 0) {
    byte command = mySerial.read();
    if (command == 0x01) {
      digitalWrite(LED_PIN_1, HIGH);
      digitalWrite(LED_PIN_2, HIGH);
      delay(200);
      digitalWrite(LED_PIN_1, LOW);
      digitalWrite(LED_PIN_2, LOW);
    }
  }
}

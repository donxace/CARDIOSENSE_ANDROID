#include <WiFiS3.h>
#include <Wire.h>
#include <hd44780.h>
#include <hd44780ioClass/hd44780_I2Cexp.h>  // for LCD

// ==================== LCD & Heart Monitor ====================
hd44780_I2Cexp lcd(0x27);

const int sensorPin = A0;
const int ledPins[7] = {2, 3, 4, 5, 6, 7, 8};
const int heartbeatLED = 13;
const int buzzerPin = 9;

int threshold = 535;
unsigned long refractoryPeriod = 500;

int beatCount = 0;
bool printedOnce = false;
bool heartbeatDetectedPrev = false;

unsigned long startTime = 0;
unsigned long lastBeatTime = 0;

// LED & buzzer timers
unsigned long ledBuzzerStart = 0;
unsigned long ledSequenceStart = 0;
unsigned long ledDuration = 50;
unsigned long ledStepDelay = 30;
int ledStep = 0;
bool sequenceActive = false;
bool ledBuzzerActive = false;

// RR intervals for HRV
unsigned long rrIntervals[100];
int rrCount = 0;

// ==================== Wi-Fi Server ====================
WiFiServer server(80);
char ssid[] = "HUAWEI-3j74";
char pass[] = "dxp2jzb9";

WiFiClient client;
unsigned long lastPing = 0;
bool monitorActive = false; // initially off

// ==================== SETUP ====================
void setup() {
  Serial.begin(115200);
  Serial.println("Connecting to WiFi...");
  WiFi.begin(ssid, pass);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  while(WiFi.localIP() == INADDR_NONE)
    {
      Serial.println(".");
    }

  Serial.println("\nConnected, IP: ");
  Serial.println(WiFi.localIP());

  server.begin();

  // LCD setup
  lcd.init();
  lcd.backlight();
  lcd.setCursor(0,0); lcd.print("Heart Monitor");
  lcd.setCursor(0,1); lcd.print("Beat: 0");

  // Pin setup
  pinMode(heartbeatLED, OUTPUT);
  pinMode(buzzerPin, OUTPUT);
  digitalWrite(heartbeatLED, LOW);
  digitalWrite(buzzerPin, LOW);

  for (int i=0; i<7; i++) {
    pinMode(ledPins[i], OUTPUT);
    digitalWrite(ledPins[i], LOW);
  }

  startTime = millis();

  
}





// ==================== LOOP ====================
void loop() {
  runHeartController();
}

unsigned long lastRRTime = 0;
int rrInterval = 800; // initial RR interval

void printRRInterval() {
  if (!monitorActive) return;

  unsigned long currentTime = millis();

  // Stop after 10 seconds
  if (currentTime - startTime >= 60000) { // 10000 ms = 10 seconds
    Serial.println("Monitoring finished after 10 seconds.");
    monitorActive = false;
  }

  // Only print if the interval has passed
  if (currentTime - lastRRTime >= rrInterval) {
    rrInterval = random(1000, 1200); // simulate next RR interval

    Serial.print("RR Interval (ms): ");
    Serial.println(rrInterval);

    client.println(rrInterval); // send to server

    lastRRTime = currentTime; // reset timer
  }
}

void runHeartController() {

  if (!client || !client.connected()) {
    client = server.available();   // ✅ NO new variable

    if (client) {
      Serial.println("Client connected");
      client.println("CONNECTED");
    }
  }

  

  // Read commands non-blocking
  if (client && client.connected() && client.available()) {
    String command = client.readStringUntil('\n');
    command.trim();
    command.replace("\r", "");
    command.toUpperCase();

    Serial.print("Received command: [");
    Serial.print(command);
    Serial.println("]");

    if (command == "ON") {
      startTime = millis();
      printedOnce = false;
      beatCount = 0;
      rrCount = 0;
      lastBeatTime = 0;
      monitorActive = true;
      client.println("Heart monitor started");
    }
    else if (command == "OFF") {
      monitorActive = false;
      digitalWrite(heartbeatLED, LOW);
      digitalWrite(buzzerPin, LOW);
      client.println("Monitor OFF");
    }
    else {
      client.println("Unknown command: " + command);
    }
  }

  // Send keep-alive every 1 second
  if (client && client.connected() && millis() - lastPing >= 1000) {
    client.println("ALIVE");
    lastPing = millis();
  }

  // Run heart monitor continuously
  if (monitorActive) {
    //heart(client);
    printRRInterval();
  }
}
/*
// ==================== HEART MONITOR FUNCTION ====================
void heart(WiFiClient &client) {
  unsigned long currentTime = millis();
  int sensorValue = analogRead(sensorPin);
  unsigned long elapsed = currentTime - startTime;

  if (elapsed < 18000) { // run for 1 minute
    bool heartbeatDetected = sensorValue > threshold;

    // Detect rising edge + refractory period
    if (heartbeatDetected && !heartbeatDetectedPrev && (currentTime - lastBeatTime > refractoryPeriod)) {
      beatCount++;
      unsigned long rr = (lastBeatTime > 0) ? (currentTime - lastBeatTime) : 0;
      lastBeatTime = currentTime;

      // Store RR interval
      if (rr > 0 && rrCount < 100) {
        rrIntervals[rrCount++] = rr;
        Serial.print("RR Interval #"); Serial.print(rrCount); Serial.print(": "); Serial.println(rr);

        if (client && client.connected()) {
          client.println(rr);
          client.flush();
          Serial.println("Sent RR interval to client");
        }
      }

      // Update LCD
      lcd.setCursor(6, 1);
      lcd.print(beatCount); lcd.print("   ");

      // LED & buzzer
      digitalWrite(heartbeatLED, HIGH);
      digitalWrite(buzzerPin, HIGH);
      ledBuzzerStart = currentTime;
      ledBuzzerActive = true;

      // LED sequence animation
      sequenceActive = true;
      ledStep = 0;
      ledSequenceStart = currentTime;
      digitalWrite(ledPins[ledStep], HIGH);

      // Calculate HRV every 5 beats
      if (rrCount >= 5) {
        float hrv = calculateHRV(rrIntervals, rrCount);
        Serial.print("HRV (RMSSD): "); Serial.println(hrv);
        lcd.setCursor(0,0);
        lcd.print("HRV: "); lcd.print(hrv,1); lcd.print(" ms   ");
      }
    }

    heartbeatDetectedPrev = heartbeatDetected;

    // Turn off heartbeat LED & buzzer
    if (ledBuzzerActive && currentTime - ledBuzzerStart >= ledDuration) {
      digitalWrite(heartbeatLED, LOW);
      digitalWrite(buzzerPin, LOW);
      ledBuzzerActive = false;
    }

    // Animate LED sequence
    if (sequenceActive && currentTime - ledSequenceStart >= ledStepDelay) {
      if (ledStep > 0) digitalWrite(ledPins[ledStep-1], LOW);
      ledStep++;
      if (ledStep < 7) {
        digitalWrite(ledPins[ledStep], HIGH);
        ledSequenceStart = currentTime;
      } else {
        digitalWrite(ledPins[6], LOW);
        sequenceActive = false;
      }
    }
  } else if (!printedOnce) {
    // 1-minute summary
    lcd.clear();
    lcd.setCursor(0,0); lcd.print("Total Count: "); lcd.print(beatCount);
    lcd.setCursor(0,1); lcd.print("                ");

    Serial.println("\n===== SESSION SUMMARY =====");
    Serial.print("Total Beats in 1 Min: "); Serial.println(beatCount);

    if (rrCount >= 5) {
      float finalHRV = calculateHRV(rrIntervals, rrCount);
      Serial.print("Final HRV (RMSSD): "); Serial.println(finalHRV);
    }

    Serial.println("===========================");
    printedOnce = true;
  }
}

// ==================== HRV CALCULATION ====================
float calculateHRV(unsigned long rr[], int n) {
  if (n < 2) return 0;

  double sumSqDiff = 0;
  for (int i=0; i<n-1; i++) {
    double diff = (double)rr[i+1] - (double)rr[i];
    sumSqDiff += diff*diff;
  }

  double meanSqDiff = sumSqDiff / (n-1);
  return sqrt(meanSqDiff);
}
*/

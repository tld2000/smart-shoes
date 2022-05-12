#include <SPI.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>
#include <SoftwareSerial.h>

// Define Global Vars |
//                    V

// Force Sensor Pins
#define FSR_PIN_1 A0
#define FSR_PIN_2 A1

// General BMP Sensor Pins
#define BMP_SCL 10
#define BMP_SDO 12
#define BMP_SDA 11

// Specific BMP Sensor Pins
#define BMP_CS1 9
#define BMP_CS2 8

// BT Module Pins
#define TX_PIN = 
#define RX_PIN = 

// BMP Modules
Adafruit_BMP280 bmp1(BMP_CS1, BMP_SDA, BMP_SDO, BMP_SCL);
Adafruit_BMP280 bmp2(BMP_CS2, BMP_SDA, BMP_SDO, BMP_SCL);

// BT Module
SoftwareSerial bt_module(RX_PIN, TX_PIN)

// Baseline Calibration Values
int fsr_baseline_1;
int fsr_baseline_2;

void setup() {
  // Init serial comm.
  Serial.begin(9600);

  // Init bluetooth
  bt_module.begin(9600);

  // Init bmp1
  if (!bmp1.begin()) {  
    Serial.println("WARNING -> bmp1 not initialized correctly");
  }

  // Init bmp2
  if (!bmp2.begin()) {  
    Serial.println("WARNING -> bmp2 not initialized correctly");
  }
}

void loop() {
  if(bt_module.available()) {
    char msg = bt_module.read();
    if (msg == 'c') {
      calibrate();
    }
    else {
      Serial.print("WARNING -> Unknown message from bt_module: ");
      Serial.print(msg);
      Serial.print("\n");
    }
  }

  // Read from all four sensors
  int fsrreading1 = analogRead(FSR_PIN_1);
  int fsrreading2 = analogRead(FSR_PIN_2);
  float bmpreading1 = bmp1.readPressure();
  float bmpreading2 = bmp2.readPressure();

  bt_module.write(fsr_baseline_1);

  Serial.print(abs(fsr_baseline_1 - fsrreading1));
  Serial.print(" , ");

  Serial.println(abs(fsr_baseline_2 - fsrreading2));
  delay(100);
}

void calibrate() {
  fsr_1_baseline = analogRead(FSR_PIN_1);
  fsr_2_baseline = analogRead(FSR_PIN_2);
  bmp_1_baseline = bmp1.readPressure();
  bmp_2_baseline = bmp2.readPressure();
}
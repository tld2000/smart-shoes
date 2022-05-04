




#include <SoftwareSerial.h>

SoftwareSerial BTserial(10, 11); // CONNECT BT RX PIN TO ARDUINO 11 PIN | CONNECT BT TX PIN TO ARDUINO 10 PIN

void setup()
{
 Serial.begin(9600);
 BTserial.begin(9600);
}

void loop()
{
 if (BTserial.available())
 {
   byte x = BTserial.read();
   Serial.write(x);
 }

 if (Serial.available())
 {
   byte y = Serial.read();
   BTserial.write(y);
 }
}

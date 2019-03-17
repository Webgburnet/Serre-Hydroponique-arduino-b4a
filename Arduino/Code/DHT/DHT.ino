#include <DHT.h>

#define DHTTYPE DHT11

#define DHTPIN 2

DHT dht(DHTPIN, DHTTYPE);


void setup() 
{
  Serial.begin(115200);
  Serial.println("Début setup");
  Serial.println("Début Initialisation du capteur DHT11");
  dht.begin();
  Serial.println("Fin Initialisation du capteur DHT11");

  delay(1000);
  
  Serial.println("Fin setup");
}

void loop() {
  
  float h = dht.readHumidity();
  float t = dht.readTemperature();


  Serial.println("\nDebug Serial");
  Serial.print("Temperature (DHT11) : "); 
  Serial.print(t);
  Serial.println(" °C");
  Serial.print("HUMIDITE DE L'AIR (DHT11) : "); 
  Serial.print(h);
  Serial.println(" %RH");
}


#define pin_capteur_sol A0

void setup() 
{
  Serial.begin(115200);
  Serial.println("Début setup");

  delay(1000);
  
  Serial.println("Fin setup");
}

void loop() {
  
 
  capteur_sol = analogRead(pin_capteur_sol);

  Serial.print("Humidite du sol : " );
  Serial.println(capteur_sol);
}


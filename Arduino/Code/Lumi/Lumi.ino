#define pin_luminosite A0
int luminosite=0;

void setup() 
{
  Serial.begin(115200);
  Serial.println("Début setup");
  
  Serial.println("Fin setup");
}

void loop() {
	luminosite = analogRead(pin_luminosite);
	luminosite = map(value, 0, 800, 0, 10);
	delay(100);
}


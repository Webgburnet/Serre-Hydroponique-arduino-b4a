#define pin_moteur 3
#define pin_button 4

boolean etat_button = 0;
boolean etat_save_button=0;
boolean etat_moteur = 0;

void setup() 
{
  Serial.begin(115200);
  Serial.println("Début setup");
  Serial.println("Début Initialisation du capteur DHT11");
 
  pinMode(pin_moteur,OUTPUT);
  digitalWrite(pin_moteur,LOW);
  
  pinMode(pin_button,INPUT);

  delay(1000);
  
  Serial.println("Fin setup");
}

void loop() {
  
  etat_button = digitalRead(pin_button);

  if(etat_button!= etat_save_button)
  {
    if(etat_button)
    {
      if(etat_moteur==1)
      {
        etat_moteur=0;
//        Serial.println("etat_moteur=0");
      }
      else
      {
        etat_moteur=1;
//        Serial.println("etat_moteur=1");
      }
    }
    etat_save_button=etat_button;
  }
  digitalWrite(pin_moteur, etat_moteur);  
}


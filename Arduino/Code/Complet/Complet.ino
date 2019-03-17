#include <DHT.h>
#include <Wire.h>
#include <MutichannelGasSensor.h>
#include <rgb_lcd.h>

#define DHTTYPE DHT11

#define DHTPIN 2
#define pin_capteur_sol A0
#define pin_moteur 3
#define pin_button 4

DHT dht(DHTPIN, DHTTYPE);

rgb_lcd lcd;
const int colorR = 255;
const int colorG = 255;
const int colorB = 255;

int capteur_sol = 0;
boolean etat_button = 0;
boolean etat_save_button=0;
boolean etat_moteur = 0;

unsigned long date_courante = 0;
unsigned long interval = 2000;
byte i = 0;
boolean premier_ecran = false;
boolean deuxieme_ecran = false;
boolean troisieme_ecran = false;
boolean quatrieme_ecran = false;


void setup() 
{
  Serial.begin(115200);
  Serial.println("Début setup");
  Serial.println("Début Initialisation du capteur DHT11");
  dht.begin();
  Serial.println("Fin Initialisation du capteur DHT11");

//  gas.begin(0x04);
//  gas.powerOn();
//  Serial.print("Version du Firmware : ");
//  Serial.println(gas.getVersion());

  pinMode(pin_moteur,OUTPUT);
  digitalWrite(pin_moteur,LOW);
  
  pinMode(pin_button,INPUT);

  Serial.println("LPO Henri Brisson");
  Serial.println("18100 Vierzon");
  
  lcd.begin(16, 2);
  lcd.setRGB(colorR, colorG, colorB);
  lcd.setCursor(0, 0);
  lcd.print("Henri Brisson");
  lcd.setCursor(0, 1);
  lcd.print("18100 Vierzon");
  
  delay(1000);
  
  Serial.println("Fin setup");
}

void loop() {
  
  float h = dht.readHumidity();
  float t = dht.readTemperature();

//  float co;
//  float no2;
//  co = gas.measure_CO();   
//  no2 = gas.measure_NO2();
  capteur_sol = analogRead(pin_capteur_sol);

  Serial.println("\nDebug Serial");
  Serial.print("Temperature (DHT11) : "); 
  Serial.print(t);
  Serial.println(" °C");
  Serial.print("HUMIDITE DE L'AIR (DHT11) : "); 
  Serial.print(h);
  Serial.println(" %RH");
  Serial.print("Concentration de CO : ");
//  Serial.print(co);
  Serial.println(" ppm");
  Serial.print("Concentration de NO2 : ");
//  Serial.print(no2);
  Serial.println(" ppm");
  Serial.print("Humidite du sol : " );
  Serial.println(capteur_sol);
  Serial.print("Etat_moteur :");
  Serial.println(etat_moteur);
  Serial.println("Fin Debug Serial \n");

  if (millis() - date_courante > interval)
  {
    i++;

    if (i == 1)
    {
      premier_ecran = true;
    }
    if (i == 2)
    {
      deuxieme_ecran = true;
    }
    if (i == 3)
    {
      troisieme_ecran = true;
    }
    if (i == 4)
    {
      quatrieme_ecran = true;
      i = 0;
    }

    date_courante += interval; //millis();
  }

  if (premier_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Composteur");
    lcd.setCursor(0, 1);
    lcd.print("Projet 2017-2018");
    premier_ecran = false;
  }

  if (deuxieme_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("T ext=");
    lcd.setCursor(6, 0);
    lcd.print(t);
    lcd.setCursor(11, 0);
    lcd.print((char)223);
    lcd.print("C");
    lcd.setCursor(0, 1);
    lcd.print("H ext=");
    lcd.setCursor(6, 1);
    lcd.print(h);
    lcd.setCursor(11, 1);
    lcd.print("%RH");
    deuxieme_ecran = false;
  }
  if (troisieme_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("CO=");
    lcd.setCursor(3, 0);
    lcd.print(t);
    lcd.setCursor(8, 0);
    lcd.print("ppm");
    lcd.setCursor(0, 1);
    lcd.print("NO2=");
    lcd.setCursor(4, 1);
    lcd.print(h);
    lcd.setCursor(9, 1);
    lcd.print("ppm");
    troisieme_ecran = false;
  }
  if (quatrieme_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("H sol=");
    lcd.setCursor(6, 0);
    lcd.print(capteur_sol);
    lcd.setCursor(0, 1);
    lcd.print("Etat moteur=");
    lcd.setCursor(12, 1);
    lcd.print(etat_moteur);   
    quatrieme_ecran = false;
  }
  
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


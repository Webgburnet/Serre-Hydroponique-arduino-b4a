/*
 * Programme Final:  | Serre Hydroponique |
 * 
 * Mise à jours: 20/05/2019
 * Version: 3.0
 */



// Librairie Wifi Shield
#include <WiFi.h>
#include <WiFiUdp.h>
#include <SPI.h>

// Librairie NFC
#include <PN532_I2C.h>
#include <PN532.h>
#include <NfcAdapter.h>

// Library du temperature et humidity sensor
#include "DHT.h"

// Librairie du lcd display
#include "rgb_lcd.h"

// Library de la clock
#include "DS1307.h"
DS1307 clock;

// Library du light sensor
#include <math.h>
#define LIGHT_SENSOR A1 // Définition de a pin du light sensor
const int thresholdvalue=10;         // Définir la vaeur du light sensor et récupérer la valeur
float Rsensor; // Résistance du capteur

// Capteur de température et humidité
#define DHTPIN 8 //pin du capteur
#define DHTTYPE DHT11 //type de capteur
DHT dht(DHTPIN, DHTTYPE); 

// NFC
String const myUID = "04 64 D2 A2 9E 33 80"; // Placer l'ID de la carte
PN532_I2C pn532_i2c(Wire);
NfcAdapter nfc = NfcAdapter(pn532_i2c);

// Composants
int ventilateur = A9;
int chauffage = A8;
int aimant = A10;
int eclairage = A11;
int brumisateur = A12;
int pompe = A13;
 
// LCD display
rgb_lcd lcd; //Définition de la couleur du LCD Display
const int colorR = 0;
const int colorG = 0;
const int colorB = 100;

unsigned long date_courante = 0;
unsigned long interval = 2000;
byte i = 0;
boolean premier_ecran = false;
boolean deuxieme_ecran = false;
boolean troisieme_ecran = false;
boolean quatrieme_ecran = false;

#define pin_humidity A0 // Broche du moisture sensor

//Shield Wifi
int status = WL_IDLE_STATUS;
char ssid[] = "Sti-2k18-SIN";         // Nom du réseau wifi
char pass[] = "Sti-2k18-Sin";       // votre mot de passe réseau wifi(utilisez pour WPA ou comme clé pour WEP)
int keyIndex = 0;                 // votre numéro d'index de clé de réseau (nécessaire uniquement pour WEP)
unsigned int localPort = 5500;        // port local sur lequel écouter
WiFiUDP Udp; // Déclaration du type de protocol employé qui sera ici du UDP


void setup() {
Serial.println("Début Setup");
    Serial.begin(9600);
    Serial.println("NDEF Reader");

    // Déclaration des composants
    pinMode(chauffage, OUTPUT); // Déclaration du chauffage
    pinMode(ventilateur, OUTPUT); // Déclaration du ventilateur
    pinMode(aimant, OUTPUT); // Déclaration de l'électroaimant
    pinMode(eclairage, OUTPUT); // Déclaration de la Lampe
    pinMode(brumisateur, OUTPUT); // Déclaration du Brumisateur
    pinMode(pompe, OUTPUT); // Déclaration de la Pompe à eau

    // Initialisation des composants
    digitalWrite(brumisateur, HIGH); // Ecriture du  Brumisateur sur LOW
    digitalWrite(ventilateur, HIGH); // Ecriture du ventilateur sur LOW
    digitalWrite(aimant, LOW); // Ecriture de l'électroaimant sur ON
    digitalWrite(eclairage, HIGH); // Ecriture de la Lampe sur LOW
    digitalWrite(pompe, LOW); // Ecriture de la Pompe sur ON
    digitalWrite(chauffage, HIGH); // Ecriture du chauffage sur LOW

    nfc.begin();
    lcd.clear();
    dht.begin();
    

    //clock
    clock.begin();
    clock.fillByYMD(2019,03,26); // Jan 19,2013
    clock.fillByHMS(8,15,0); // 13:35.0"
    clock.fillDayOfWeek(SAT); // Défnition de la date
    clock.setTime(); // Définition de l'heure

    // Configuration du LCD
    lcd.begin(16, 2);
    lcd.setRGB(colorR, colorG, colorB); // Définir lcd en RGB

    // Initialisation du Wifi
    status = WiFi.begin(ssid,pass);
    printWifiStatus();
    Udp.begin(localPort);
    Serial.println("Fin Setup");

}

void loop() {

  // Protocole Udp
  int Size=Udp.parsePacket();
  char message[Size];
  String conv_message_to_string;
  Serial.println("loop");
  printTime();
 
  // Capteur humidity & temperature 
  float h = dht.readHumidity(); // Lire la valeur de l'humidité
  float t = dht.readTemperature(); // Lire la valeur de la température
  float humidite_sol=analogRead(pin_humidity);

  // Light sensor
  int sensorValue = analogRead(LIGHT_SENSOR);
  Rsensor = (float)(1023-sensorValue)*10/sensorValue; //résistance du capteur

// Lire l'humidité sur l'écran lcd
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
      i = 0;
    }
    date_courante += interval; //millis();
  }

  if (premier_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print(" Serre ");
    lcd.setCursor(0, 1);
    lcd.print("Projet 2018-2019");
    premier_ecran = false;
  }

  if (deuxieme_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("T=");
    lcd.setCursor(2, 0);
    lcd.print(t);
    lcd.setCursor(11, 0);
    lcd.print((char)223);
    lcd.print("C");
    lcd.setCursor(0, 1);
    lcd.print("H=");
    lcd.setCursor(2, 1);
    lcd.print(h);
    lcd.setCursor(11, 1);
    lcd.print("%RH");
    deuxieme_ecran = false;
  }
  if (troisieme_ecran)
  {
    lcd.clear();
    lcd.setCursor(0, 0);
    lcd.print("Hsol=");
    lcd.setCursor(5, 0);
    lcd.print(humidite_sol);
    lcd.setCursor(12, 0);
    lcd.print("");
    lcd.setCursor(0, 1);
    lcd.print("Lum=");
    lcd.setCursor(4, 1);
    lcd.print(Rsensor);
    lcd.setCursor(9, 1);
    lcd.print("Lumen");
    troisieme_ecran = false;
  }
  
  char cles;

   if(Serial.available()>0)
   {
    cles = Serial.read();
   }

  // CODE DES REGLEMENTATIONS
  if (/*t > 21 || */cles=='t'){
    Serial.println("t>21");
      digitalWrite(ventilateur, LOW);
      digitalWrite(chauffage, HIGH);
      cles=0;
  }

  if (/*t < 18 || */cles=='r'){
      Serial.println("t<18");
      digitalWrite(chauffage, LOW);
      digitalWrite(ventilateur, HIGH);
      digitalWrite(brumisateur, HIGH);
      cles=0;
  }

  if (/*humidite_sol < 300 || */cles=='s'){
    Serial.println("sol<300");
    digitalWrite(pompe, LOW);
    digitalWrite(chauffage, HIGH);
    cles=0;
  }

  if (/*humidite_sol > 700 || */cles=='q'){
    Serial.println("sol>700");
      digitalWrite(chauffage, LOW);
      digitalWrite(pompe, HIGH);
      cles=0;
  }

  if (/*h < 35 || */cles=='h'){
    Serial.println("h<35");
    digitalWrite(brumisateur, LOW);
    digitalWrite(chauffage, HIGH);
    cles=0;
  }

  if (/*h > 70 ||*/cles=='g'){
    Serial.println("h>70");
    digitalWrite(brumisateur, HIGH);
    digitalWrite(chauffage, LOW);
    cles=0;
    
  }

  if (/*clock.month == 6 || clock.month == 7 || clock.month == 8 || clock.month == 9 || */cles=='e')
  {
    if (/*(clock.hour < 12 && Rsensor < 10000)|| */cles=='e')
    {
      Serial.println("eclairage high");
       digitalWrite(eclairage, LOW);
       cles=0;
    } 
    else 
    {
      Serial.println("eclairage low");
       digitalWrite(eclairage, HIGH);
       cles=0;
    }
  } 
  else 
  {
    if (/*clock.month == 12 || clock.month == 1 || clock.month == 2 || clock.month == 3 || */cles=='f')
    {
      if (/*(clock.hour < 12 && Rsensor < 10000) || */cles=='f')
      {
        Serial.println("eclairage high");
       digitalWrite(eclairage, LOW);
       cles=0;
      } 
      else 
      {
        Serial.println("eclairage low");
       digitalWrite(eclairage, HIGH);
       cles=0;
      }
    }
    }

    if(Size)
    {
      // lit le paquet dans packetBufffer
      Udp.read(message, Size);
      int cases = 0;
      while (cases != Size)
      {
      conv_message_to_string = conv_message_to_string + message[cases];
      cases = cases+1;
      }

      // Nous viendrons vérifier que la trame a été reçu
      Serial.print("Paquet reçu de taille : ");
      Serial.println(Size);
      Serial.print("Adresse IP de  ");
      IPAddress remote = Udp.remoteIP();
      Serial.print(remote);
      Serial.print(", sur le port ");
      Serial.println(Udp.remotePort());
      Serial.println("Msg UDP:");
      Serial.println(conv_message_to_string);

    // Il s'agit de la partie d'affichage des valeurs des capteurs
    if(conv_message_to_string=="Acquer")
    {
      Serial.print("Le message est : ");
      Serial.print("Capteur actualisé");

      delay(1);
      
      Udp.beginPacket(Udp.remoteIP(), Udp.remotePort());
      
      Udp.write("Humidite ambiante = ");
      Udp.print(h);
      Udp.write(" %");
      Udp.write("\n");
      Udp.write("Temperature = ");
      Udp.print(t);
      Udp.write(" C");
      Udp.write("\n");
      Udp.write("Humidite sol = ");
      Udp.print(humidite_sol);
      Udp.write("\n");
      Udp.write("Luminosite = ");
      Udp.print(sensorValue);
      Udp.write(" Lux");
      Udp.endPacket();
       }
    if(conv_message_to_string=="VentiOFF"){
      digitalWrite(ventilateur, HIGH);
    }
    if(conv_message_to_string=="VentiON"){
      digitalWrite(ventilateur, LOW);
    }
    if(conv_message_to_string=="BrumiOFF"){
      digitalWrite(brumisateur, HIGH);
    }
    if(conv_message_to_string=="BrumiON"){
      digitalWrite(brumisateur, LOW);
    }
    if(conv_message_to_string=="VanneOFF"){
      digitalWrite(pompe, HIGH);
    }
    if(conv_message_to_string=="VanneON"){
      digitalWrite(pompe, LOW);
    }
    if(conv_message_to_string=="AimantOFF"){
      digitalWrite(aimant, HIGH);
    }
    if(conv_message_to_string=="AimantON"){
      digitalWrite(aimant, LOW);
    }
    if(conv_message_to_string=="EclairageON"){
      digitalWrite(eclairage, LOW);
    }
    if(conv_message_to_string=="EclairageOFF"){
      digitalWrite(eclairage, HIGH);
    }
    if(conv_message_to_string=="ChauffageON"){
      digitalWrite(chauffage, LOW);
    }
    if(conv_message_to_string=="ChauffageOFF"){
      digitalWrite(chauffage, HIGH);
    }
    }

    // Partie NFC
    Serial.println("\nScan a NFC tag\n");
    if (nfc.tagPresent())
    {
        NfcTag tag = nfc.read();
        tag.print();

        String scannedUID = tag.getUidString(); // Lecture de l'UID du tag

        if( scannedUID == myUID) // Comparaison entre l'ID lue et l'ID acceptée
        {
          // La carte a été acceptée
          Serial.println("Bienvenue");
          Serial.println();

          // Désactivation du courant dans l'électro-aimant pour faire ouvrir la Serre
          digitalWrite(aimant, HIGH);
          
          // Allumer la led verte
          Serial.println("TAG Verifier - Acces Autorise !");
          Serial.println();
          lcd.clear();
          lcd.print("Acces Autorise !");
          delay(5000);

          lcd.clear();
          digitalWrite(aimant, LOW);
          
        }else{
          
          Serial.println("TAG Inconnu - Acces refuse !");
          Serial.println();
          lcd.clear();
          lcd.print("Acces Refuse !");

          digitalWrite(aimant, HIGH);
          delay(5000);

          lcd.clear();

        }
    }
}

// Configuration de l'Horloge
void printTime()
{
//    clock.getTime();
//    Serial.print(clock.hour, DEC);
//    Serial.print(":");
//    Serial.print(clock.minute, DEC);
//    Serial.print(":");
//    Serial.print(clock.second, DEC);
//    Serial.print("  ");
//    Serial.print(clock.month, DEC);
//    Serial.print("/");
//    Serial.print(clock.dayOfMonth, DEC);
//    Serial.print("/");
//    Serial.print(clock.year, DEC);
//    Serial.print(" ");
//    
//    
//    switch (clock.dayOfWeek) // Définition des jours de la semaine
//    {
//        case MON:
//        Serial.print("MON");
//        break;
//        case TUE:
//        Serial.print("TUE");
//        break;
//        case WED:
//        Serial.print("WED");
//        break;
//        case THU:
//        Serial.print("THU");
//        break;
//        case FRI:
//        Serial.print("FRI");
//        break;
//        case SAT:
//        Serial.print("SAT");
//        break;
//        case SUN:
//        Serial.print("SUN");
//        break;
//    }
//    Serial.println(" "); //Afficher la date sur le moniteur série
}

// Configuration du Wifi
void printWifiStatus() {
  // affiche le SSID du réseau auquel vous êtes connecté :
  Serial.print("SSID : ");
  Serial.println(WiFi.SSID());

  // affiche l'adresse IP du Shield WiFi :
  IPAddress ip(192,168,2,234); // = WiFi.localIP();
  WiFi.config(ip);
  Serial.print("Addresse IP : ");
  Serial.println(ip);

  // affiche la longeur du signal reçu :
  long rssi = WiFi.RSSI();
  Serial.print("longeur du Signal (RSSI) : ");
  Serial.print(rssi);
  Serial.println(" dBm");
}

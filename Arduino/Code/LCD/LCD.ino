#include <rgb_lcd.h>

rgb_lcd lcd;
const int colorR = 255;
const int colorG = 255;
const int colorB = 255;

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
    lcd.print("var");
    lcd.setCursor(11, 0);
    lcd.print((char)223);
    lcd.print("C");
    lcd.setCursor(0, 1);
    lcd.print("H ext=");
    lcd.setCursor(6, 1);
    lcd.print("var");
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
    lcd.print("var");
    lcd.setCursor(8, 0);
    lcd.print("ppm");
    lcd.setCursor(0, 1);
    lcd.print("NO2=");
    lcd.setCursor(4, 1);
    lcd.print("var");
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
    lcd.print("var");
    lcd.setCursor(0, 1);
    lcd.print("Etat moteur=");
    lcd.setCursor(12, 1);
    lcd.print("var");   
    quatrieme_ecran = false;
  }
}


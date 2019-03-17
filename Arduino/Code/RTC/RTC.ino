#include <Wire.h>
#include "DS1307.h"

DS1307 clock;
void setup()
{
    Serial.begin(9600);
    clock.begin();
    clock.fillByYMD(2013,1,19);//Jan 19,2013
    clock.fillByHMS(15,28,30);//15:28 30"
    clock.fillDayOfWeek(SAM);//Samedi
    clock.setTime();
}
void loop()
{
    printTime();
}

void printTime()
{
    clock.getTime();
    Serial.print(clock.hour, DEC);
    Serial.print(":");
    Serial.print(clock.minute, DEC);
    Serial.print(":");
    Serial.print(clock.second, DEC);
    Serial.print("  ");
    Serial.print(clock.month, DEC);
    Serial.print("/");
    Serial.print(clock.dayOfMonth, DEC);
    Serial.print("/");
    Serial.print(clock.year+2000, DEC);
    Serial.print(" ");
    Serial.print(clock.dayOfMonth);
    Serial.print("*");
    switch (clock.dayOfWeek)
    {
        case LUN:
        Serial.print("Lundi");
        break;
        case MAR:
        Serial.print("Mardi");
        break;
        case MER:
        Serial.print("Mercredi");
        break;
        case JEU:
        Serial.print("Jeudi");
        break;
        case VEN:
        Serial.print("Vendredi");
        break;
        case SAM:
        Serial.print("Samedi");
        break;
        case DIM:
        Serial.print("Dimanche");
        break;
    }
    Serial.println(" ");
}
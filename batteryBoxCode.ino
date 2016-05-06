#inlcude <LiquidCrystal.h>

LiquidCrystal lcd (12, 11, 10, 5, 4, 3, 2)

double batt1 = analogRead(A0);
double batt2 = analogRead(A1);
double batt3 = analogRead(A2);
double batt4 = analogRead(A3);
int displayOn = 1;

const int relay1 = n/a;
const int relay2 = n/a;
const int relay3 = n/a;
const int relay4 = n/a;

const int updateBatteries = n/a;
const int screenOff = n/a;
const int autoUpdate = n/a;

void setup() {
  pinMode(relay1, OUTPUT);
  pinMode(relay2, OUTPUT);
  pinMode(relay3, OUTPUT);
  pinMode(relay4, OUTPUT);

  pinMode(refreshScreen, INPUT);
  pinMode(screenOff, INPUT);
  pinMode(autoUpdateMode, INPUT);
  
  lcd.begin(16, 2);
  lcd.print("Sagan-o-Matic V1");
  delay(500);
}

void loop() {  
  if (refreshScreen == HIGH) {
   updateScreen();
  }
  else if (screenOff == HIGH) {
   displayToggle();
  }
  else if (autoUpdateMode == HIGH) {
    autoUpdate();
  }
}

//-------------------Functions---------------------//

void updateVoltage() {
  double divisor = 1;
  
  batt1 = analogRead(A0)/divisor;
  batt2 = analogRead(A1)/divisor;
  batt3 = analogRead(A2)/divisor;
  batt4 = analogRead(A3)/divisor;

  if(batt1 >= 14.6)
    digitalWrite(relay1, LOW);
  else
    digitalWrite(relay1, HIGH);
  if(batt2 >= 14.6)
    digitalWrite(relay2, LOW);
  else
    digitalWrite(relay2, HIGH);

  if(batt3 >= 14.6)
    digitalWrite(relay3, LOW);
  else
    digitalWrite(relay3, HIGH);

    if(batt4 >= 14.6)
    digitalWrite(relay4, LOW);
  else
    digitalWrite(relay4, HIGH);
    
//  batt1 = batt1/divisor;
//  batt2 = batt2/divisor;
//  batt3 = batt3/divisor;
//  batt4 = batt4/divisor;
}

 void updateScreen() {
  updateVoltage();
  
  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("B1:");
  lcd.setCursor(3,0);
  lcd.print(batt1);
  
  lcd.setCursor(8,0);
  lcd.print("B2:");
  lcd.setCursor(11,0);
  lcd.print(batt2);
  
  lcd.setCursor(0,1);
  lcd.print("B3:");
  lcd.setCursor(3,1);
  lcd.print(batt3);
  
  lcd.setCursor(8,1);
  lcd.print("B4:");
  lcd.setCursor(11,1);
  lcd.print(batt4);
}

void displayToggle() {
  if (displayOn == 1) {
    lcd.noDisplay();
    displayOn = 0;
    }
  else {
    lcd.display();
    displayOn = 1;
    }
}

void autoUpdate() {
  delay(50);
  
  while(refreshScreen == LOW || autoUpdateMode == LOW) {
    updateScreen();
    delay(90000);
  }
}


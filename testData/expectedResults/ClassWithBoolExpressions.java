public class ClassWithBoolExpressions {
ClassWithBoolExpressions() {
}
public int simpleBoolExpressions() {
boolean boolOne;
int intOne;
int intTwo;
intOne = (int) 6;
intTwo = (int) 8728;
boolOne = (intOne >= intTwo) & (intTwo > 3 && intOne < 67);
intOne = intTwo > 34 ? 99 : 1;
return intTwo > 34 ? 99 : 1;
}
public void ifExpression() {
int intOne;
int intTwo;
int intThree;
boolean boolOne;
intOne = 5;
intTwo = (int) 45;
if ((intOne > intTwo) & (intOne > 433) && intTwo > 46) {
boolOne = intOne > 56;
intThree = (int) 324;
} else {
if (intTwo >= 354) {
intTwo = (int) 999;
}
intTwo = (int) 99;
}
if (intOne > 1111) {
intTwo = (int) 11;
} else {
if (intOne == 2222) {
intTwo = (int) 22;
} else {
intTwo = (int) 33;
}
}
intThree = (int) 55;
}
}

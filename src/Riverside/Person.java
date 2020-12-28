package Riverside;

import processing.core.PVector;

public class Person {
    float x;
    float y;

    public Person(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(PVector speed){
        x += speed.x;
        y += speed.y;
    }


}

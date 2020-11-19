package MASTest;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;


public class Box extends PApplet {

    PVector loc;

    public Box(PVector b_loc) {
        loc = b_loc;

    }


/*
    public void paint(PApplet app){
        app.pushStyle();
        app.strokeWeight(1);
        app.stroke(0);
        app.fill(255, 0, 0);
        app.rect(loc.x, loc.y, Main.box_size, Main.box_size);
        app.popStyle();
    }
 */

    public ArrayList<Box> boxes(double a,double b,int x){
        ArrayList<Box>box =new ArrayList<>();

        return box;
    }

}

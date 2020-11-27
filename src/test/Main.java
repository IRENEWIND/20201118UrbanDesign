package test;

import processing.core.PApplet;
import processing.core.PVector;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.util.ArrayList;



public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("test.Main");
    }

//    ArrayList<WB_PolyLine> myPlan = new ArrayList<WB_PolyLine>();

    public void settings() {
        size(400, 400);
    }

    public void setup() {
//        myPlan =  DXFImport.getDXFPolyLine("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/0plan.dxf", "RIVER");
        PVector a = new PVector(0,0);
        if(a == new PVector(0,0)){
            println("true");
        }
    }

    public void draw() {

    }


}




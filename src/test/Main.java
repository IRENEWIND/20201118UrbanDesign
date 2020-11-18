package test;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;

import java.awt.*;
import java.util.ArrayList;


public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("test.Main");
    }

    ArrayList<WB_PolyLine> polys = DXFImport.getDXFPolyLine("F:a.dxf","river");

    public void settings() {
        size(400, 400);
    }

    public void setup() {

    }

    public void draw() {

    }


}




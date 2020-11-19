package DrawPolygonsByZ;

import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;
import java.util.ArrayList;

public class Test extends PApplet {
    public static void main(String[] args) {
        PApplet.main("DrawPolygons.Test");
    }

    public void settings() {
        size(1000, 1000, P3D);
    }

    double Length;
    double Height;
    double Dis;

    Box box;
    WB_Render render;


    public void setup() {
        box = new Box(this);
        render = new WB_Render(this);
        Length = 500;
        Height = 300;
        Dis = 20;
    }

    public void draw() {
        background(255);
        fill(255, 0, 0);
        stroke(0);
        ArrayList<WB_Polygon> DividedBoxes = box.divideBox(Length, Height, Dis);
        for (WB_Polygon ply : DividedBoxes)
            render.drawPolygonEdges(ply);
    }

}

package Box;


import processing.core.PApplet;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;

public class Main extends PApplet {

    double Length;
    double Height;
    double Dis;

    Box box;
    WB_Render render;

    public static void main(String[] args) {
        PApplet.main("Box.Main");
    }

    public void settings() {
        size(500, 600, P3D);
    }

    public void setup() {
        render = new WB_Render(this);
        Length = 300;
        Height = 300;
        Dis = 20;

        box = new Box(this);

    }

    public void draw() {
        background(255);
        drawBasicBox();
    }


    //绘制基础的网格
    void drawBasicBox(){
        fill(255);
        stroke(0);
        ArrayList<WB_Polygon> BasicBoxes = box.divideBox(Length, Height, Dis);
        for (WB_Polygon ply : BasicBoxes) {
            render.drawPolygonEdges(ply);
        }
    }

}

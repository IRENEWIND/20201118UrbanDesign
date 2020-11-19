package MASTest;

import processing.core.PApplet;
import processing.core.PVector;
import wblut.geom.WB_RandomDisk;
import wblut.processing.WB_Render3D;

import java.util.ArrayList;


public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("MASTest.Main");
    }


    static int box_size = 4;
    ArrayList<Box> basic_boxes;

    public void settings() {
        size(600, 800);
    }

    public void setup() {
        basic_boxes = new ArrayList<Box>();

        for (int i = 0; i < width; i += box_size) {
            for (int j = 0; j < height; j += box_size) {
                PVector t_box_loc = new PVector(i, j);
                basic_boxes.add(new Box(t_box_loc));

            }
        }
    }

    public void draw() {
        background(80, 80, 89);

        for (Box t_box : basic_boxes) {

            t_box.paint(this);
        }
    }
}

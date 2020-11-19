package DrawPolygonsByZ;

import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import java.util.ArrayList;

public class Box {
    PApplet app;

    public Box(PApplet app) {
        this.app=app;
    }

    public ArrayList<WB_Polygon> divideBox(double length, double height, double dis) {
        ArrayList<WB_Polygon> Boxpolys = new ArrayList<>();

        int Numl = (int) (length / dis);
        int Numh = (int) (height / dis);
        for (int i = 0; i < Numl; i++) {
            for (int j = 0; j < Numh; j++) {
                WB_Point pt = new WB_Point(i * length / Numl, j * height / Numh);
                WB_Point pt1 = new WB_Point((i + 1) * length / Numl, j * height / Numh);
                WB_Point pt2 = new WB_Point((i + 1) * length / Numl, (j + 1) * height / Numh);
                WB_Point pt3 = new WB_Point(i * length / Numl, (j + 1) * height / Numh);
                WB_Polygon ply = new WB_Polygon(pt, pt1, pt2, pt3);
                Boxpolys.add(ply);
            }
        }
        return Boxpolys; 
    }
}

package Riverside;

import processing.core.PVector;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;

import java.util.ArrayList;

public class GridCollection {
    ArrayList<Grid> grids;

    public GridCollection(ArrayList<Grid> grids) {
        this.grids = grids;
    }


    public PVector getPersonRiverFactor(WB_PolyLine river, float x, float y) {
        PVector speed_river = new PVector(0,0);
        float river_radius = 300; //河岸影响范围40m
        float dis = Method.closestDis(river,x,y);
        if(river_radius > dis){
            WB_Point tpoint = Method.closestPointPL(river,x,y);
            PVector t = new PVector(tpoint.xf() -x, tpoint.yf() -y); //向河边的向量
            speed_river = t.normalize().mult(dis/river_radius).mult(dis/river_radius);
        }
        return speed_river;
    }


    public PVector getPersonSquareFactor(ArrayList<Grid> l_squre_center, float x, float y) {
        PVector speed_square = new PVector(0,0);
        float l_square_radius = 500; //影响50m
        for (int k = 0; k < l_squre_center.size(); k++) {
            Grid t = l_squre_center.get(k);
            float dis = Method.getDistance(t.x, t.y, x,y);
            if(l_square_radius > dis){
                PVector a = new PVector(t.x-x, t.y-y);
                speed_square = a.normalize().mult(dis/l_square_radius).mult(dis/l_square_radius);
            }
        }
        return speed_square;
    }

    public PVector getPersonsSquareFactor(ArrayList<Grid> s_squre_center, float x, float y) {
        PVector speed_square = new PVector(0,0);
        float l_square_radius = 50; //影响15m
        for (int k = 0; k < s_squre_center.size(); k++) {
            Grid t = s_squre_center.get(k);
            float dis = Method.getDistance(t.x, t.y, x,y);
            if(l_square_radius > dis){
                PVector a = new PVector(t.x-x, t.y-y);
                speed_square = a.normalize().mult(dis/l_square_radius).mult(dis/l_square_radius);
            }
        }
        return speed_square;

    }
}

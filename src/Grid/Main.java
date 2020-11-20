package Grid;

import DrawPolygonsByZ.Box;
import processing.core.PApplet;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Main extends PApplet {
    GridCollection testGrids;
    int l_grid = 10;

    public static void main(String[] args) {
        processing.core.PApplet.main("Grid.Main");
    }

    public void settings() {
        size(400, 700, P3D);
    }

    public void setup() {
        testGrids = new GridCollection(new ArrayList<Grid>());
        testGrids = testGrids.setInitial(width, height, l_grid);//初始化网格
        testGrids = testGrids.setRiver();  //设置初始河道
        testGrids = testGrids.setLake();   //设置初始湖泊
        testGrids = testGrids.setRoad();   //设置初始交通主干道
        draw_grids();
    }

    public void draw() {

    }

    //绘制网格
    void draw_grids() {
        ArrayList<Grid> grids_draw = testGrids.getArray();
        for(Grid t_grid : grids_draw){
            strokeWeight(1);
            stroke(0);
//            noStroke();
            fill(200);
            if (t_grid.property == 0) fill(200);
            else if (t_grid.property == 1) fill(0, 180, 180);
            else if (t_grid.property == 2) fill(0, 180, 220);
            else if (t_grid.property == 3) fill(120);
            rect(t_grid.x, t_grid.y, t_grid.l, t_grid.l);
        }
    }

}
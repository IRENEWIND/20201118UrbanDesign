package Grid;

import javafx.beans.property.Property;
import processing.core.PApplet;
import processing.core.PImage;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Main extends PApplet {
    static int W = 600;
    static int H = 1000;
    static int l_grid = 5;
    static int Nx = W/l_grid;
    static int Ny = H/l_grid;

    PImage origin;
    PImage origin_r;
    PImage origin_lake;
    PImage origin_road;


    GridCollection testGrids;

    public static void main(String[] args) {
        processing.core.PApplet.main("Grid.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        frameRate(1);

        origin = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin.jpg");
        origin_r = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin_r.png");
        origin_lake = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin_lake.png");
        origin_road = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin_road.png");


        gridsSetInitial();
//        testGrids = testGrids.setInitial(width, height, l_grid);//初始化网格
//        testGrids = testGrids.setPic(origin);

//        testGrids = testGrids.setRiver();  //设置初始河道
//        testGrids = testGrids.setLake();   //设置初始湖泊
//        testGrids = testGrids.setRoad();   //设置初始交通主干道
        testGrids.changeRiverFactor();  //改变河流影响因子
    }

    public void draw() {
        draw_grids();

    }

    //鼠标敲击事件
    public void mousePressed(){
        testGrids.setLSquareCenter(mouseX, mouseY);  //改变广场点的属性和影响因子
    }

    //绘制网格
    void draw_grids() {
        ArrayList<Grid> grids_draw = testGrids.grids;
        for(Grid t_grid : grids_draw){
//            strokeWeight(1);
//            stroke(0);
            noStroke();
            fill(200);
            //映射效果展示
            if(t_grid.property == 100){
                fill(255*t_grid.ls_factor, 0, 0);
            }
            if (t_grid.property == 100) fill(255);
            else if (t_grid.property == 1) fill(0, 180, 180);
            else if (t_grid.property == 2) fill(0, 180, 220);
            else if (t_grid.property == 3) fill(170,170,170);
            else if (t_grid.property == 20) fill(255,200,0);
            else if (t_grid.property == 21) fill(255,172,0);
            rect(t_grid.x, t_grid.y, t_grid.l, t_grid.l);
        }
    }

    void gridsSetInitial() {
        ArrayList<Grid> t_grids = new ArrayList<Grid>();
        for (int y = 0; y < height; y += l_grid) {
            for (int x = 0; x < width; x += l_grid) {
                int t_property = 100;
                int r_col = origin_r.get(x, y);
                int lack_col = origin_lake.get(x, y);
                int road_col = origin_road.get(x,y);

                if (r_col < color(255)) t_property = 1;
                if(lack_col < color(255))t_property = 2;
                if(road_col < color(255)) t_property = 3;

                Grid t = new Grid(x, y, l_grid, t_property);
                t_grids.add(t);
            }
        }
        testGrids = new GridCollection(t_grids);
    }


}
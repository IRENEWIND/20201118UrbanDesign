package Grid;

import javafx.beans.property.Property;
import processing.core.PApplet;
import processing.core.PImage;
import Grid.Grid;
import Grid.LSquareCenter;
import wblut.geom.WB_Polygon;
import wblut.processing.WB_Render;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Main extends PApplet {
    static int W = 600;
    static int H = 1000;
    static int l_grid = 5;
    static int Nx = W / l_grid;
    static int Ny = H / l_grid;

    PImage origin;
    PImage origin_r;
    PImage origin_lake;
    PImage origin_road;


    GridCollection testGrids;

    Boolean ifMoveLSquare = false; //是否移动大广场的中心点，开始否
    Boolean ifDrawOLSellipse = false; //是否绘制广场周边的圆，开始否

    public static void main(String[] args) {
        processing.core.PApplet.main("Grid.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        frameRate(30);

        origin = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin.jpg");
        origin_r = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin_r.png");
        origin_lake = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin_lake.png");
        origin_road = loadImage("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/origin_road.png");

        gridsSetInitial();
        testGrids.changeRiverFactor();  //改变河流影响因子
    }

    public void draw() {
        //是否移动其他大广场位置
        if (ifMoveLSquare) {
            testGrids.moveOLSC();
        }
        //是否绘制广场周边的外圆
        if (ifDrawOLSellipse) {
            draw_l_square_ellipse();
        }

        draw_grids();
        if (keyPressed) {
            //产生新的大广场,会移动
            if (key == 'g' || key == 'G') {
                testGrids.deleteOLSC();
                testGrids.genOLSC();
                ifMoveLSquare = true;
                ifDrawOLSellipse = true;
            }

            //固定移动广场的位置，之后不再绘制
            if (key == 't' || key == 'T') {
                ifMoveLSquare = false;
                testGrids.chooseOLSC();
            }

            //产生大广场位置
            if (key == 'v' || key == 'V') {
                testGrids.genOLSCSurround();
                ifDrawOLSellipse = false;
            }
        }
    }


    //鼠标敲击事件
    public void mousePressed() {
        testGrids.setLSquareCenter(mouseX, mouseY);
    }

    //绘制网格
    void draw_grids() {
        ArrayList<Grid> grids_draw = testGrids.grids;
        for (Grid t_grid : grids_draw) {
            noStroke();
            fill(200);

            if (t_grid.property == 100) fill(255);
            else if (t_grid.property == 1) fill(0, 180, 180);
            else if (t_grid.property == 2) fill(0, 180, 220);
            else if (t_grid.property == 3) fill(170, 170, 170);
            else if (t_grid.property == 20 || t_grid.property == 22) fill(255, 200, 0);
            else if (t_grid.property == 21) fill(255, 172, 0);
            rect(t_grid.x, t_grid.y, t_grid.l, t_grid.l);
        }

    }

    //绘制小广场的影响边界
    void draw_l_square_ellipse() {
        ArrayList<LSquareCenter> ts = testGrids.getOLSC();
        for (int k = 0; k < ts.size(); k++) {
            fill(0);
//            noStroke();
            LSquareCenter t = ts.get(k);
            ellipse(t.x, t.y, 5, 5);

            stroke(0);
            noFill();
            ellipse(t.x, t.y, t.radius * l_grid, t.radius * l_grid);
        }
    }


    //根据图片设置最初的网格
    void gridsSetInitial() {
        ArrayList<Grid> t_grids = new ArrayList<Grid>();
        for (int y = 0; y < height; y += l_grid) {
            for (int x = 0; x < width; x += l_grid) {
                int t_property = 100;
                int r_col = origin_r.get(x, y);
                int lack_col = origin_lake.get(x, y);
                int road_col = origin_road.get(x, y);

                if (r_col < color(255)) t_property = 1;
                if (lack_col < color(255)) t_property = 2;
                if (road_col < color(255)) t_property = 3;

                Grid t = new Grid(x, y, l_grid, t_property);
                t_grids.add(t);
            }
        }
        testGrids = new GridCollection(t_grids);
    }

}
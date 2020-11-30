package testGrid;


import hep.aida.ref.Test2;
import testGrid.Grid;
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
    static int Nx = W / l_grid;
    static int Ny = H / l_grid;


    GridCollection testGrids;
    Boolean ifMoveLSquare = false; //是否移动大广场的中心点，开始是


    public static void main(String[] args) {
        processing.core.PApplet.main("testGrid.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        frameRate(20);

        testGrids = new GridCollection(new ArrayList<Grid>());
        testGrids = testGrids.setInitial(width, height, l_grid);//初始化网格
        testGrids = testGrids.setRiver();  //设置初始河道
//        testGrids = testGrids.setLake();   //设置初始湖泊
//        testGrids = testGrids.setRoad();   //设置初始交通主干道
        testGrids.changeRiverFactor();  //改变河流影响因子
        testGrids.changeRiverFlowField();  //改变河流力场

    }

    public void draw() {

        //是否移动其他大广场位置
        if (ifMoveLSquare) {
            testGrids.moveOLSC();
        }
        draw_grids();
        draw_l_square_ellipse();


        if (keyPressed) {

            //产生新的大广场,会移动
            if (key == 'g' || key == 'G') {
                testGrids.genOLSC();
                ifMoveLSquare = true;
            }

            //固定移动广场的位置，之后不再绘制
            if (key == 't' || key == 'T') {
                ifMoveLSquare = false;
                testGrids.chooseOLSC();
            }

            if(key == 'v' || key == 'V'){
                testGrids.genOLSCSurround();
                println(2);
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
//            strokeWeight(1);
//            stroke(0);
            noStroke();
            fill(200);
//            映射效果展示
//            if (t_grid.property == 100) {
//                fill(255 * t_grid.r_factor, 0, 0);
//            }
            if (t_grid.property == 100) fill(255);
            else if (t_grid.property == 1) fill(0, 180, 180);
            else if (t_grid.property == 2) fill(0, 180, 220);
            else if (t_grid.property == 3) fill(170, 170, 170);
            else if (t_grid.property == 20) fill(255, 200, 0);
            else if (t_grid.property == 21) fill(255, 172, 0);
            rect(t_grid.x, t_grid.y, t_grid.l, t_grid.l);
        }
    }

    //绘制小广场的影响边界
    void draw_l_square_ellipse() {
        ArrayList<LSquareCenter> ts = testGrids.getOLSC();
        for (int k = 0; k < ts.size(); k++) {
            fill(0);
            noStroke();
            LSquareCenter t = ts.get(k);
            ellipse(t.x, t.y, 5, 5);

            stroke(0);
            noFill();
            ellipse(t.x, t.y, t.radius * l_grid, t.radius * l_grid);
        }
    }


}




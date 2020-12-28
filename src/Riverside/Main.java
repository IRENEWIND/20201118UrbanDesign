package Riverside;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import wblut.geom.WB_Curve;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.nurbs.WB_Bezier;
import wblut.processing.WB_Render;

import java.util.ArrayList;

public class Main extends PApplet {
    WB_Render render;

    static int W = 1800;  //每个像素0.5m
    static int H = 400;
    static int l_grid = 1;
    static int Nx = W / l_grid;
    static int Ny = H / l_grid;

    GridCollection testGrids;
    ArrayList<Grid> L_squre_center = new ArrayList<Grid>();
    ArrayList<Grid> s_squre_center = new ArrayList<Grid>();
    int s_squre_num = 25;
    int l_square_num = 3;
    int all_square_num = s_squre_num + l_square_num;

    WB_PolyLine river_line = new WB_PolyLine(new WB_Point(0, 300), new WB_Point(W / 4, 320), new WB_Point(W / 2, 330), new WB_Point(W, 300));

    ArrayList<Person> people = new ArrayList<Person>();
    int peopleNum = 400;


    public static void main(String[] args) {
        processing.core.PApplet.main("Riverside.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        frameRate(120);
        render = new WB_Render(this);
        rectMode(CENTER);

        gridsSetInitial();
//        peopleSetInitial();

    }


    public void draw() {
        fill(0);
        stroke(0);
//        render.drawPolyLine(river_line); //绘制河岸线


//        peopleMove();
//        peopleDraw();


    }

    public void mousePressed() {
        drawGrids();
    }


    ///************人群相关************************

    //生成初始的人群
    void peopleSetInitial() {
        for (int k = 0; k < peopleNum; k++) {
            peopleNew();
        }
    }

    //产生新的个体
    void peopleNew() {
        int randomNum = (int) (Math.random() * 7); //产生随机人群的选项
        Person t;
        if (randomNum == 0) {
            t = new Person(0, 150);
        } else if (randomNum == 1) {
            t = new Person(0, 250);
        } else if (randomNum == 2) {
            t = new Person(W, 150);
        } else if (randomNum == 3) {
            t = new Person(W, 250);
        } else if (randomNum == 4) {
            t = new Person(300, 100);
        } else if (randomNum == 5) {
            t = new Person(900, 100);
        } else if (randomNum == 6) {
            t = new Person(1300, 100);
        } else if (randomNum == 6) {
            t = new Person(1700, 100);
        } else {
            t = new Person(0, 0);
        }
        people.add(t);
    }

    //人群运动
    void peopleMove() {
        for (int k = 0; k < people.size(); k++) {
            Person t = people.get(k);
            //判断t的位置，如果不合适就产生新的
            if (t.x < -10 || t.x > 10 + W || t.y < -10) {
                people.remove(k);
                peopleNew();
            } else {
                float theta = (float) Math.random() * TWO_PI;
                PVector speed_random = new PVector(cos(theta), sin(theta));
                PVector speed_river = testGrids.getPersonRiverFactor(river_line, t.x, t.y);
                PVector speed_square = testGrids.getPersonSquareFactor(L_squre_center, t.x, t.y);
                PVector speed_s_square = testGrids.getPersonsSquareFactor(s_squre_center,t.x, t.y);
                PVector speed_all = speed_random.mult((float) 0.1).add(speed_river.mult((float) 0.3)).add(speed_square.mult((float) 0.4).add(speed_s_square.mult((float)0.2))  );
                t.update(speed_all);
            }
        }
    }

    //绘制人群
    void peopleDraw() {
        for (int k = 0; k < people.size(); k++) {
            Person t = people.get(k);
            noStroke();
            fill(0);
            ellipse(t.x, t.y, 1, 1);
        }
    }

    //****************人群相关*****************/


    //设置最初的网格,主要是湖泊,广场
    void gridsSetInitial() {
        ArrayList<Grid> t_grids = new ArrayList<Grid>();
        for (int y = 0; y < height; y += l_grid) {
            for (int x = 0; x < width; x += l_grid) {
                int t_property = 100;

                //设定湖泊
                if (y > 300) {
                    t_property = 1;
                }

                //设定主要广场中心
                if ((x == 50 && y == 280) || (x == 950 && y == 130) || (x == 1700 && y == 280)) {
                    t_property = 20;
                    Grid s = new Grid(x, y, l_grid, t_property);
                    L_squre_center.add(s);
                }

                Grid t = new Grid(x, y, l_grid, t_property);
                t_grids.add(t);
            }
        }
        testGrids = new GridCollection(t_grids);

        //产生其他广场
        genSmallSquare();
    }

    void genSmallSquare() {
        for (int k = 0; k < s_squre_num; k++) {
            int randomx = (int)(Math.random() * W);
            int randomy = (int)(Math.random()*200 + 100);
            int t_property = 22;
            Grid s = new Grid(randomx, randomy, l_grid, t_property);
            s_squre_center.add(s);
        }
    }

    //绘制网格
    void drawGrids() {
        ArrayList<Grid> grids_draw = testGrids.grids;
        for (Grid t_grid : grids_draw) {
            noStroke();
            fill(200);

            if (t_grid.property == 100) fill(255);
            else if (t_grid.property == 1) fill(0, 180, 180);
            else if (t_grid.property == 2) fill(0, 180, 220);
            else if (t_grid.property == 3) fill(170, 170, 170);
            else if (t_grid.property == 20) fill(255, 200, 0);
            else if (t_grid.property == 21) fill(255, 172, 0);
            rect(t_grid.x, t_grid.y, t_grid.l, t_grid.l);
        }

        //绘制广场中心点
        for (int k = 0; k < L_squre_center.size(); k++) {
            Grid t = L_squre_center.get(k);
            noStroke();
            fill(255, 200, 0);
            rect(t.x, t.j, 10, 10);
        }
        //绘制小广场中心点s
        for (int k = 0; k < s_squre_center.size(); k++) {
            Grid t = s_squre_center.get(k);
            noStroke();
            fill(255, 200, 0);
            rect(t.x, t.j, 5, 5);
        }

    }


}

package Riverside;

import com.triplescape.doapamine.Person;
import gzf.gui.CameraController;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import wblut.geom.*;
import wblut.nurbs.WB_Bezier;
import wblut.processing.WB_Render;

import java.util.ArrayList;
import java.util.Iterator;

public class Main extends PApplet {
    WB_Render render;
    CameraController cam1;

    static int W = 600;  //每个像素0.5m
    static int H = 900;

    ArrayList<WB_Polygon> edge = new ArrayList<WB_Polygon>(); //最外围边线
    ArrayList<WB_Polygon> river_edge = new ArrayList<WB_Polygon>();  //河流边界
    ArrayList<WB_Polygon> lake_edge = new ArrayList<WB_Polygon>();  //河流边界

    public static void main(String[] args) {
        processing.core.PApplet.main("Riverside.Main");
    }

    public void settings() {
        size(W, H, "processing.opengl.PGraphics3D");
    }

    public void setup() {
        render = new WB_Render(this);
        this.cam1 = new CameraController(this);
        this.cam1.top();
        edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0edge");  //导入最外面的边界
        river_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0river");//导入河流的边界
        lake_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0lake");//导入河流的边界
    }


    public void draw() {
        background(255);
        translate(width/2, height/2);

        drawEdgeAndRiver();

    }

    public void mousePressed() {

    }


    void drawEdgeAndRiver(){
        stroke(0);
        noFill();
        Iterator var1 = edge.iterator();
        while(var1.hasNext()){
            WB_Polygon the_edge = (WB_Polygon) var1.next();
            render.drawPolygonEdges(the_edge);
        }

        fill(0, 180, 180);
        stroke(0, 180, 180);
        Iterator var2 = river_edge.iterator();
        while(var2.hasNext()){
            WB_Polygon the_river_edge = (WB_Polygon) var2.next();
            render.drawPolygonEdges(the_river_edge);
        }

        fill(0, 180, 180);
        stroke(0, 180, 180);
        Iterator var3 = lake_edge.iterator();
        while(var3.hasNext()){
            WB_Polygon the_lake_edge = (WB_Polygon) var3.next();
            render.drawPolygonEdges(the_lake_edge);
        }
    }


}

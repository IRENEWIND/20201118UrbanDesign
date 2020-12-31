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

    WB_AABB boundary;
    ArrayList<WB_Polygon> edge = new ArrayList<WB_Polygon>(); //最外围边线
    ArrayList<WB_Polygon> river_edge = new ArrayList<WB_Polygon>();  //河流边界
    ArrayList<WB_PolyLine> river_center = new ArrayList<>();
    ArrayList<WB_Polygon> river_s_edge = new ArrayList<WB_Polygon>();  //小河流边界
    ArrayList<WB_Polygon> lake_edge = new ArrayList<WB_Polygon>();  //湖泊边界
    ArrayList<WB_Polygon> block_edge = new ArrayList<>();
    ArrayList<WB_Point> origin_square = new ArrayList<>();

    ArrayList<LSquare> l_squares = new ArrayList<>();
    int l_square_radius = 400;  //两个公园相距400m
    int l_square_num = 5;

    float river_factor_radius = 5000;

    public static void main(String[] args) {
        processing.core.PApplet.main("Riverside.Main");
    }

    public void settings() {
        size(W, H, "processing.opengl.PGraphics3D");
    }

    public void setup() {
        frameRate(60);
        initDXF();
    }


    public void draw() {
        background(255);
        translate(width/2, height/2);
        drawDXF();
        moveLSquares();
        drawLSquares();

        if (keyPressed) {
            //产生新的大广场,会移动
            if (key == 'g' || key == 'G') {
                genLSquares();
            }
            //删减大广场
            if (key == 't' || key == 'T') {
                removeLSquares();
            }
        }
    }


    public void mousePressed() {

    }

    private void genLSquares() {
        for (int k = 0; k < l_square_num; k++) {
//            float x = random((float)boundary.getMinX(),(float)boundary.getCenterX());
//            float y = random((float)boundary.getMinY(),(float)boundary.getMaxY()-300);
            float x = random((float)boundary.getMinX()+400,(float)boundary.getCenterX());
            float y = random((float)boundary.getMinY()+200, (float)boundary.getCenterY()+1200);
            if(Method.ifContain(edge,x,y)){
                l_squares.add(new LSquare(x, y, l_square_radius));
                k++;
            }
            k--;
        }
    }

    private void drawLSquares() {
        for (int k = 0; k < l_squares.size(); k++) {
            LSquare t = l_squares.get(k);
            stroke(255, 200, 0);
            ellipse(t.x, t.y, l_square_radius, l_square_radius);
            strokeWeight(5);
            ellipse(t.x, t.y, 5, 5);
            strokeWeight(1);
        }
        for (int h = 0; h < origin_square.size(); h++) {
            WB_Point t = origin_square.get(h);
            stroke(255, 200, 0);
            ellipse(t.xf(), t.yf(), l_square_radius, l_square_radius);
            strokeWeight(5);
            ellipse(t.xf(), t.yf(), 5, 5);
            strokeWeight(1);
        }
    }

    private void moveLSquares(){
        for (int k = 0; k < l_squares.size(); k++) {
            LSquare p = l_squares.get(k);

            PVector all = new PVector();
            PVector attractions = new PVector();
            PVector river_factor = new PVector();
            PVector river_push = new PVector();
            //计算小球间的力
            for(LSquare q : l_squares){
                if(p.distance(q) < l_square_radius ){
                    PVector attraction = new PVector(p.x-q.x, p.y-q.y);
                    attraction.normalize();
                    attraction.mult((l_square_radius-p.distance(q))/l_square_radius);
                    attractions.add(attraction);
                }
            }
            for(WB_Point q : origin_square){
                if(p.distance(q) < l_square_radius){
                    PVector attraction = new PVector(p.x-q.xf(), p.y-q.yf());
                    attraction.normalize();
                    attraction.mult(1-p.distance(q)/l_square_radius);
                    attractions.add(attraction);
                }
            }
            //计算河流对其拉力
            float r_min_distance = Method.closestPointdis(river_edge, p.x, p.y);
            if (r_min_distance < river_factor_radius){
                WB_Point t_point = Method.closestPoint(river_edge, p.x, p.y);
                river_factor = new PVector(t_point.xf()-p.x, t_point.yf()-p.y);
                river_factor.normalize();
                river_factor.mult(1-r_min_distance/river_factor_radius);
            }
            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (r_min_distance_center<20){
                l_squares.remove(k);
            }else if(r_min_distance_center<50) {
                WB_Point t_point = Method.closestPointPL(river_center, p.x, p.y);
                river_push = new PVector(p.x-t_point.xf(), p.y-t_point.yf());
                river_push.normalize();
                river_factor.mult(1-r_min_distance/50);
            }

            all.add(attractions.mult(10)).add(river_factor.mult(5)).add(river_push.mult(10));
            p.update(all);
        }
    }

    private void removeLSquares(){
        for (int k = 0; k < l_squares.size(); k++) {
            LSquare p = l_squares.get(k);

            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (r_min_distance_center>200){
                l_squares.remove(k);
            }
        }
    }


    private void initDXF() {
        render = new WB_Render(this);
        this.cam1 = new CameraController(this);
        this.cam1.top();
        boundary = DXFImport.getBoundary("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0edge");
        edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0edge");  //导入最外面的边界
        river_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0river");//导入河流的边界
        river_center = DXFImport.getDXFPolyLine("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0river_center");//导入河流的边界
        river_s_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0river_s");//导入河流的边界
        lake_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0lake");//导入湖泊的边界
        block_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0block");//导入地块的边界
        origin_square = DXFImport.getDXFPoints("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "1origin_square");
    }

    void drawDXF(){

        stroke(0);
        fill(180); //为了道路
        Iterator var1 = edge.iterator();
        while(var1.hasNext()){
            WB_Polygon the_edge = (WB_Polygon) var1.next();
            render.drawPolygonEdges(the_edge);
        }

        stroke(180);
        fill(255);
        Iterator var4 = block_edge.iterator();
        while(var4.hasNext()){
            WB_Polygon the_block_edge = (WB_Polygon) var4.next();
            render.drawPolygonEdges(the_block_edge);
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
        Iterator var6 = river_s_edge.iterator();
        while(var6.hasNext()){
            WB_Polygon the_s_river_edge = (WB_Polygon) var6.next();
            render.drawPolygonEdges(the_s_river_edge);
        }

        fill(0, 180, 180);
        stroke(0, 180, 180);
        Iterator var3 = lake_edge.iterator();
        while(var3.hasNext()){
            WB_Polygon the_lake_edge = (WB_Polygon) var3.next();
            render.drawPolygonEdges(the_lake_edge);
        }

        noFill();
        strokeWeight(5);
        stroke(255, 200, 0);
        Iterator var5 = origin_square.iterator();
        while(var5.hasNext()){
            WB_Point the_origin_square = (WB_Point) var5.next();
            render.drawPoint(the_origin_square);
        }
        strokeWeight(1);

    }


}

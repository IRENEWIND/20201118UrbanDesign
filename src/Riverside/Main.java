package Riverside;

import com.triplescape.doapamine.Person;
import gzf.gui.CameraController;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import wblut.geom.*;
import wblut.nurbs.WB_Bezier;
import wblut.processing.WB_Render;

import java.security.cert.TrustAnchor;
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

    ArrayList<WB_Polygon> area_public = new ArrayList<>();
    ArrayList<WB_Polygon> area_life = new ArrayList<>();
    ArrayList<WB_Polygon> area_ecology = new ArrayList<>();


    ArrayList<Square> l_squares = new ArrayList<>();
    ArrayList<Square> l_squares_origin = new ArrayList<>();
    ArrayList<Square> l_squares_all = new ArrayList<>();
    int l_square_radius = 400;  //两个公园相距400m
    int l_square_num = 5;   //每次产生广场数目
    float river_factor_radius = 5000;   //河流对大广场吸引力
    Boolean ifMoveLSquare = false; //是否移动大广场的中心点，开始否
    Boolean ifLSquare = false;      //是否编辑广场


    ArrayList<Square> s_squares = new ArrayList<>();
    int s_square_radius = 100;  //两个公园相距400m
    int s_square_num = 1;   //每次产生广场数目
    Boolean ifMoveSSquare = false; //是否移动小广场的中心点，开始否
    Boolean ifSSquare = false;     //是否编辑小广场



    public static void main(String[] args) {
        processing.core.PApplet.main("Riverside.Main");
    }

    public void settings() {
        size(W, H, "processing.opengl.PGraphics3D");
    }

    public void setup() {
        frameRate(60);
        initDXF();
        genLSquaresOrigin();  //得到最初的广场点
    }

    public void draw() {
        background(255);
        translate(width / 2, height / 2);
        drawDXF();

        //控制东西生成
        if (keyPressed) {
            //生成广场
            if (key == 'L' || key == 'l') {
                ifLSquare = true;
            }
            //生成小节点
            if(key == 's' || key == 'S'){
                ifSSquare = true;
            }
        }

        /************广场***********/
        if(!ifLSquare){
            drawLSquaresCenter();
        }

        if(ifLSquare) {
            drawLSquares();

            //是否移动其他大广场位置
            if (ifMoveLSquare) {
                moveLSquares();
            }

            if (keyPressed) {
                //产生新的大广场,会移动
                if (key == 'g' || key == 'G') {
                    genLSquares();
                    ifMoveLSquare = true;
                }
                //删减大广场
                if (key == 'd' || key == 'D') {
                    removeLSquares();
                }
                //移动大广场
                if (key == 'm' || key == 'M') {
                    ifMoveLSquare = true;
                }
                //固定大广场
                if (key == 'f' || key == 'F') {
                    ifMoveLSquare = false;
                }
                //存储大广场
                if (key == 'a' || key == 'A') {
                    ifLSquare = false;
                    ifMoveLSquare = false;

                    //将已有大广场放入一个集合
                    for (Square p : l_squares) {
                        l_squares_all.add(p);
                    }
                    for(Square p : l_squares_origin){
                        l_squares_all.add(p);
                    }
                }
            }
        }

        /************小节点***********/

        if(!ifSSquare){
            drawSSquaresCenter();
        }

        if(ifSSquare){
            drawSSquares();

            //是否移动其他大广场位置
            if (ifMoveSSquare) {
                moveSSquares();
            }

            if (keyPressed) {
                //产生新的大广场,会移动
                if (key == 'g' || key == 'G') {
                    genSSquares();
                    ifMoveSSquare = true;
                }
                //删减大广场
                if (key == 'd' || key == 'D') {
                    removeSSquares();
                }
                //移动大广场
                if (key == 'm' || key == 'M') {
                    ifMoveSSquare = true;
                }
                //固定大广场
                if (key == 'f' || key == 'F') {
                    ifMoveSSquare = false;
                }
                //存储大广场
                if (key == 'a' || key == 'A') {
                    ifSSquare = false;
                    ifMoveSSquare = false;
                }
            }

        }

    }


    /*********************小广场*********************/

    private void genSSquares() {
        //每个大节点附近产生新泡泡
        for(Square p: l_squares_all){
            for (int k = 0; k < s_square_num; k++) {
                float x = p.x;
                float y = p.y;
                Square t = new Square(x, y, s_square_radius);
                s_squares.add(t);
            }
        }
    }

    private void removeSSquares() {

    }

    private void moveSSquares() {
        for (int k = 0; k < s_squares.size(); k++) {
            Square p = s_squares.get(k);

            PVector all = new PVector();
            PVector attractions = new PVector();
            PVector river_factor = new PVector();
            PVector river_push = new PVector();

            //计算小球间的力
            for (Square q : s_squares) {
                if (p.distance(q) < s_square_radius) {
                    PVector attraction = new PVector(p.x - q.x, p.y - q.y);
                    attraction.normalize();
                    attraction.add(new PVector(random(-1, 1), random(-1, 1)));
                    attraction.mult((s_square_radius - p.distance(q)) / s_square_radius);
                    attractions.add(attraction);
                }
            }

            //计算河流对其拉力
            float r_min_distance = Method.closestPointdis(river_edge, p.x, p.y);
            if (r_min_distance < (river_factor_radius/100)) {
                WB_Point t_point = Method.closestPoint(river_edge, p.x, p.y);
                river_factor = new PVector(t_point.xf() - p.x, t_point.yf() - p.y);
                river_factor.normalize();
                river_factor.mult(1 - r_min_distance / river_factor_radius);
            }

            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (r_min_distance_center < 10) {
            } else if (r_min_distance_center < (s_square_num/10)) {
                WB_Point t_point = Method.closestPointPL(river_center, p.x, p.y);
                river_push = new PVector(p.x - t_point.xf(), p.y - t_point.yf());
                river_push.normalize();
                river_factor.mult(1 - r_min_distance_center / 50);
            }

            all.add(attractions.mult((float) 0.5)).add(river_factor.mult((float) 0.01)).add(river_push.mult((float) 0.4)).mult(5);
            p.update(all);
        }
    }

    private void drawSSquares() {
        for (int k = 0; k < s_squares.size(); k++) {
            Square t = s_squares.get(k);
            stroke(150, 0, 255);
            ellipse(t.x, t.y, s_square_radius, s_square_radius);
            strokeWeight(3);
            ellipse(t.x, t.y, 2, 2);
            strokeWeight(1);
        }
    }

    private void drawSSquaresCenter(){
        for (int k = 0; k < s_squares.size(); k++) {
            Square t = s_squares.get(k);
            stroke(150, 0, 255);
            strokeWeight(3);
            ellipse(t.x, t.y, 2, 2);
            strokeWeight(1);
        }
    }

    /*********************大广场*********************/

    private void genLSquaresOrigin(){
        for(WB_Point t : origin_square){
            float x = t.xf();
            float y = t.yf();
            Square p = new Square(x, y, l_square_radius);
            l_squares_origin.add(p);
        }
    }

    private void genLSquares() {
        for (int k = 0; k < l_square_num; k++) {
            float x = 0;
            float y = 0;
            int random_num = (int) (random(1) + 0.5);
            if (random_num == 0) {
//                x = random((float) boundary.getMinX(), (float) boundary.getCenterX());
                x = 0;
                y = random((float) boundary.getMinY(), (float) boundary.getCenterY());
            } else if (random_num == 1) {
                x = random((float) boundary.getMinX(), (float) boundary.getCenterX());
                y = random((float) boundary.getCenterY(), (float) boundary.getMaxY());
            }


            if (Method.ifContain(edge, x, y)) {
                l_squares.add(new Square(x, y, l_square_radius));
                k++;
            }
            k--;
        }
    }

    private void moveLSquares() {
        for (int k = 0; k < l_squares.size(); k++) {
            Square p = l_squares.get(k);

            PVector all = new PVector();
            PVector attractions = new PVector();
            PVector river_factor = new PVector();
            PVector river_push = new PVector();
            //计算小球间的力
            for (Square q : l_squares) {
                if (p.distance(q) < l_square_radius) {
                    PVector attraction = new PVector(p.x - q.x, p.y - q.y);
                    attraction.normalize();
                    attraction.add(new PVector(random(-1, 1), random(-1, 1)));
                    attraction.mult((l_square_radius - p.distance(q)) / l_square_radius);
                    attractions.add(attraction);
                }
            }
            for (WB_Point q : origin_square) {
                if (p.distance(q) < l_square_radius) {
                    PVector attraction = new PVector(p.x - q.xf(), p.y - q.yf());
                    attraction.normalize();
                    attraction.add(new PVector(random(-1, 1), random(-1, 1)));
                    attraction.mult(1 - p.distance(q) / l_square_radius);
                    attractions.add(attraction);
                }
            }
            //计算河流对其拉力
            float r_min_distance = Method.closestPointdis(river_edge, p.x, p.y);
            if (r_min_distance < river_factor_radius) {
                WB_Point t_point = Method.closestPoint(river_edge, p.x, p.y);
                river_factor = new PVector(t_point.xf() - p.x, t_point.yf() - p.y);
                river_factor.normalize();
                river_factor.mult(1 - r_min_distance / river_factor_radius);
            }
            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (r_min_distance_center < 10) {
//                l_squares.remove(k);
            } else if (r_min_distance_center < l_square_radius/10) {
                WB_Point t_point = Method.closestPointPL(river_center, p.x, p.y);
                river_push = new PVector(p.x - t_point.xf(), p.y - t_point.yf());
                river_push.normalize();
                river_factor.mult(1 - r_min_distance_center / 50);
            }

            all.add(attractions.mult((float) 0.6)).add(river_factor.mult((float) 0.2)).add(river_push.mult((float) 0.2)).mult(20);
            p.update(all);
        }
    }

    private void removeLSquares() {
        for (int k = 0; k < l_squares.size(); k++) {
            Square p = l_squares.get(k);
            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (r_min_distance_center > 200) {
                l_squares.remove(k);
            }
        }
    }

    private void drawLSquares() {
        for (int k = 0; k < l_squares.size(); k++) {
            Square t = l_squares.get(k);
            stroke(255, 200, 0);
            ellipse(t.x, t.y, l_square_radius, l_square_radius);
            strokeWeight(5);
            ellipse(t.x, t.y, 5, 5);
            strokeWeight(1);
        }
        for (int h = 0; h < l_squares_origin.size(); h++) {
            Square t = l_squares_origin.get(h);
            stroke(255, 200, 0);
            ellipse(t.x, t.y, l_square_radius, l_square_radius);
            strokeWeight(5);
            ellipse(t.x, t.y, 5, 5);
            strokeWeight(1);
        }
    }

    private void drawLSquaresCenter(){
        for (int k = 0; k < l_squares.size(); k++) {
            Square t = l_squares.get(k);
            stroke(255, 200, 0);
            strokeWeight(5);
            ellipse(t.x, t.y, 5, 5);
            strokeWeight(1);
        }
        for (int h = 0; h < l_squares_origin.size(); h++) {
            Square t = l_squares_origin.get(h);
            stroke(255, 200, 0);
            strokeWeight(5);
            ellipse(t.x, t.y, 5, 5);
            strokeWeight(1);
        }
    }

    /*********************DXF*********************/

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
        area_public = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_public");
        area_life = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_life");;
        area_ecology = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_ecology");;
    }

    void drawDXF() {
        stroke(0);
        fill(180); //为了道路
        Iterator var1 = edge.iterator();
        while (var1.hasNext()) {
            WB_Polygon the_edge = (WB_Polygon) var1.next();
            render.drawPolygonEdges(the_edge);
        }

        stroke(180);
        fill(255);
        Iterator var4 = block_edge.iterator();
        while (var4.hasNext()) {
            WB_Polygon the_block_edge = (WB_Polygon) var4.next();
            render.drawPolygonEdges(the_block_edge);
        }

        fill(0, 180, 180);
        stroke(0, 180, 180);
        Iterator var2 = river_edge.iterator();
        while (var2.hasNext()) {
            WB_Polygon the_river_edge = (WB_Polygon) var2.next();
            render.drawPolygonEdges(the_river_edge);
        }

        fill(0, 180, 180);
        stroke(0, 180, 180);
        Iterator var6 = river_s_edge.iterator();
        while (var6.hasNext()) {
            WB_Polygon the_s_river_edge = (WB_Polygon) var6.next();
            render.drawPolygonEdges(the_s_river_edge);
        }

        fill(0, 180, 180);
        stroke(0, 180, 180);
        Iterator var3 = lake_edge.iterator();
        while (var3.hasNext()) {
            WB_Polygon the_lake_edge = (WB_Polygon) var3.next();
            render.drawPolygonEdges(the_lake_edge);
        }

        noFill();
        strokeWeight(5);
        stroke(255, 200, 0);
        Iterator var5 = origin_square.iterator();
        while (var5.hasNext()) {
            WB_Point the_origin_square = (WB_Point) var5.next();
            render.drawPoint(the_origin_square);
        }
        strokeWeight(1);

        stroke(0);
        noFill();
        Iterator var11 = area_public.iterator();
        while (var11.hasNext()) {
            WB_Polygon the_area_public = (WB_Polygon) var11.next();
            render.drawPolygonEdges(the_area_public);
        }

        stroke(0);
        noFill();
        Iterator var12 = area_life.iterator();
        while (var12.hasNext()) {
            WB_Polygon the_area_life = (WB_Polygon) var12.next();
            render.drawPolygonEdges(the_area_life);
        }

        stroke(0);
        noFill();
        Iterator var13 = area_ecology.iterator();
        while (var13.hasNext()) {
            WB_Polygon the_area_ecology = (WB_Polygon) var13.next();
            render.drawPolygonEdges(the_area_ecology);
        }

    }


}

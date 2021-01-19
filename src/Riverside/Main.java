package Riverside;

import Riverside.CurveAttractors;
import gzf.gui.CameraController;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.Rect;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import wblut.geom.*;
import wblut.nurbs.WB_Bezier;
import wblut.processing.WB_Render;

import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Iterator;

public class Main extends PApplet {
    WB_Render render;
    CameraController cam1;

    static int W = 1800;  //每个像素0.5m
    static int H = 1000;

    WB_AABB boundary;
    ArrayList<WB_Polygon> edge = new ArrayList<WB_Polygon>(); //最外围边线
    ArrayList<WB_Polygon> river_edge = new ArrayList<WB_Polygon>();  //河流边界
    ArrayList<WB_PolyLine> river_center = new ArrayList<>();
//    ArrayList<WB_Polygon> river_s_edge = new ArrayList<WB_Polygon>();  //小河流边界
    ArrayList<WB_Polygon> lake_edge = new ArrayList<WB_Polygon>();  //湖泊边界
    ArrayList<WB_Polygon> block_edge = new ArrayList<>();
    ArrayList<WB_Point> origin_square = new ArrayList<>();


    ArrayList<WB_Polygon> area_all = new ArrayList<>();
//    ArrayList<WB_Polygon> area_public = new ArrayList<>();
//    ArrayList<WB_Polygon> area_life = new ArrayList<>();
//    ArrayList<WB_Polygon> area_ecology = new ArrayList<>();
    ArrayList<WB_Polygon> area_down = new ArrayList<>();
    ArrayList<WB_Polygon> area_up = new ArrayList<>();

    ArrayList<WB_Polygon> _commercial = new ArrayList<>();
    ArrayList<WB_Polygon> _landscape = new ArrayList<>();
    ArrayList<WB_Polygon> _public = new ArrayList<>();
    ArrayList<WB_Polygon> _resident = new ArrayList<>();

    ArrayList<Square> l_squares = new ArrayList<>();
    ArrayList<Square> l_squares_origin = new ArrayList<>();
    ArrayList<Square> l_squares_all = new ArrayList<>();
    int l_square_radius = 500;  //两个公园相距400m
    int l_square_num = 5;   //每次产生广场数目
    float river_factor_radius = 5000;   //河流对大广场吸引力
    Boolean ifMoveLSquare = false; //是否移动大广场的中心点，开始否
    Boolean ifLSquare = false;      //是否编辑广场


    ArrayList<Square> s_squares = new ArrayList<>();
    int s_square_radius = 120;  //两个公园相距400m
    int s_square_num = 1;   //每次产生广场数目
    Boolean ifMoveSSquare = false; //是否移动小广场的中心点，开始否
    Boolean ifSSquare = false;     //是否编辑小广场


    Boolean ifPath = false;         //是否编辑道路
    Boolean ifgen = true;
    Boolean ifPathMove = true;
    Boolean ifq = true;
    float cruveStandardLength = 400;
    int curve_num;
    int curve_steps = 30;  //是实际的1/2
    ArrayList<WB_Point> my_nodes = new ArrayList<>();
    ArrayList<VerletParticle2D> nodes = new ArrayList<VerletParticle2D>(); //节点
    ArrayList<CurveAttractors> curves = new ArrayList<CurveAttractors>(); //曲线及曲线上的点
    VerletPhysics2D physics = new VerletPhysics2D();  //点的物理引擎
    ArrayList<VerletPhysics2D> curve_springs = new ArrayList<VerletPhysics2D>();  //曲线的物理引擎
    ArrayList<VerletPhysics2D> curve_out_springs = new ArrayList<VerletPhysics2D>(); //间隔点的物理引擎，防止折角过大

    int curve_num_up;
    ArrayList<WB_Point> my_nodes_up = new ArrayList<>();
    ArrayList<VerletParticle2D> nodes_up = new ArrayList<VerletParticle2D>(); //节点
    ArrayList<CurveAttractors> curves_up = new ArrayList<CurveAttractors>(); //曲线及曲线上的点
    VerletPhysics2D physics_up = new VerletPhysics2D();  //点的物理引擎
    ArrayList<VerletPhysics2D> curve_springs_up = new ArrayList<VerletPhysics2D>();  //曲线的物理引擎
    ArrayList<VerletPhysics2D> curve_out_springs_up = new ArrayList<VerletPhysics2D>(); //间隔点的物理引擎，防止折角过大


    boolean record;


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
                l_squares_all.clear();
            }
            //生成小节点
            if (key == 'k' || key == 'K') {
                ifSSquare = true;
            }
            //将所有节点汇聚到一个集合里面
            if (key == 'p' || key == 'P') {
                ifPath = true;
            }
        }

        /************道路***********/
        if (ifPath) {
            if (!ifgen) {
                if(ifPathMove) {
                    physics.update();
                    physics_up.update();

                    for (int k = 0; k < curve_springs.size(); k++) {
                        VerletPhysics2D a = curve_springs.get(k);
                        a.update();
                    }
                    for (int k = 0; k < curve_out_springs.size(); k++) {
                        VerletPhysics2D a = curve_out_springs.get(k);
                        a.update();
                    }
                    for (int k = 0; k < curve_springs_up.size(); k++) {
                        VerletPhysics2D a = curve_springs_up.get(k);
                        a.update();
                    }
                    for (int k = 0; k < curve_out_springs_up.size(); k++) {
                        VerletPhysics2D a = curve_out_springs_up.get(k);
                        a.update();
                    }
                }

//                drawNodes();
//                drawAttractors();
                drawSprings();


                if (keyPressed) {
                    if (key == 'f' || key == 'F'){
                        ifPathMove = false;
                    }
                    if (key == 'm' || key == 'M'){
                        ifPathMove = true;
                    }
                }

            }

            if (ifgen) {
                if (keyPressed) {
                    //集合所有点
                    if (key == 'q' || key == 'Q') {
                        if (ifq) {
                            genNodes();
                            ifq = false;
                        }
                    }
                    //产生道路
                    if (key == 'w' || key == 'W') {
                        ifgen = false;
                        curve_num = (int) (my_nodes.size() / 2);
                        curve_num_up = (int) (my_nodes_up.size() / 2);
                        initPhysics();
                    }
                }
            }
        }

        /************小节点***********/
        if (ifSSquare) {
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

        if (!ifSSquare) {
            drawSSquaresCenter();
        }

        /************广场***********/
        if (ifLSquare) {
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
                    for (Square p : l_squares_origin) {
                        l_squares_all.add(p);
                    }
                    //删除不合适的

                }
            }
        }

        if (!ifLSquare) {
            drawLSquaresCenter();
        }
    }


    /*********************路径生成*********************/

    void initPhysics() {
        physics.setWorldBounds(new Rect((float) boundary.getMinX(), (float) boundary.getMinY(), (float) (boundary.getMaxX() - boundary.getMinX()), (float) (boundary.getMaxY() - boundary.getMinX())));
        physics_up.setWorldBounds(new Rect((float) boundary.getMinX(), (float) boundary.getMinY(), (float) (boundary.getMaxX() - boundary.getMinX()), (float) (boundary.getMaxY() - boundary.getMinX())));
        generateNewNodes();
        generateNewNodes_up();
        generateCruveAndPoints();
        generateCruveAndPoints_up();
    }

    private void genNodes() {
//        ArrayList<WB_Point> points = Method.genPointsinPolygon(boundary, area_all, 10);
        ArrayList<WB_Point> points = new ArrayList<>();
        for (Square p : l_squares_all) {
            float x = p.x;
            float y = p.y;
            WB_Point t = new WB_Point(x, y);
            points.add(t);
        }
        for (Square p : s_squares) {
            float x = p.x;
            float y = p.y;
            WB_Point t = new WB_Point(x, y);
            points.add(t);
        }

        ArrayList<WB_Point> points_down = new ArrayList<>();
        for (WB_Point p : points){
            if(Method.ifContain(area_down,p.xf(), p.yf())){
                points_down.add(p);
            }
        }

        ArrayList<WB_Point> points_up = new ArrayList<>();
        for (WB_Point p : points){
            if(Method.ifContain(area_up,p.xf(), p.yf())){
                points_up.add(p);
            }
        }

        for (WB_Point p : points_down) {
            my_nodes.add(p);
        }

        for (WB_Point p : points_up) {
            my_nodes_up.add(p);
        }

    }

    //产生新node
    void generateNewNodes() {
        for (int k = 0; k < my_nodes.size(); k++) {
            float x = my_nodes.get(k).xf();
            float y = my_nodes.get(k).yf();
            VerletParticle2D t = new VerletParticle2D(x, y);
            nodes.add(t);
        }
    }

    void generateNewNodes_up() {
        for (int k = 0; k < my_nodes_up.size(); k++) {
            float x = my_nodes_up.get(k).xf();
            float y = my_nodes_up.get(k).yf();
            VerletParticle2D t = new VerletParticle2D(x, y);
            nodes_up.add(t);
        }
    }



    private void generateCruveAndPoints() {
        ArrayList<WB_Point> nodes_matching_a = new ArrayList<>();
        ArrayList<WB_Point> nodes_matching_c = new ArrayList<>();

        //挑出两个锚点
        ArrayList<WB_Point> nodes_matching = new ArrayList<>();
        for (WB_Point t : my_nodes) {
            nodes_matching.add(t);
        }

        for (int k = 0; k < 2 * curve_num; k++) {
            if (k < curve_num) {
                nodes_matching_a.add(nodes_matching.get(k));
            } else if (k < 2 * curve_num && k >= curve_num) {
                nodes_matching_c.add(nodes_matching.get(k));
            }
        }

        //更换nodes_matching_c元素顺序,得到比较合适的连接点

        float min_distance_deviation_all = 0; //计算现在的总距离
        for (int k = 0; k < curve_num; k++) {
            WB_Point p = nodes_matching_a.get(k);
            WB_Point q = nodes_matching_c.get(k);
            min_distance_deviation_all += abs(Method.getDistance(p, q) - cruveStandardLength);
        }

        for (int k = 0; k < curve_num; k++) {
            for (int h = 0; h < curve_num; h++) {
                if (h != k) {
                    WB_Point a1 = nodes_matching_a.get(k);
                    WB_Point c1 = nodes_matching_c.get(k);
                    WB_Point a2 = nodes_matching_a.get(h);
                    WB_Point c2 = nodes_matching_c.get(h);
                    float length_origin = abs(Method.getDistance(a1, c1) - cruveStandardLength) + abs(Method.getDistance(a2, c2) - cruveStandardLength);
                    float length_now = abs(Method.getDistance(a1, c2) - cruveStandardLength) + abs(Method.getDistance(a2, c1) - cruveStandardLength);
                    if (length_now < length_origin) {
                        nodes_matching_c.set(k, c2);
                        nodes_matching_c.set(h, c1);
                    }
                }
            }
        }


        //产生第三点b，连接得到曲线
        for (int h = 0; h < curve_num; h++) {
            VerletPhysics2D curve_attractors_physics = new VerletPhysics2D();  //初始曲线上每个球斥力集合
            VerletPhysics2D curve_out_attractors_physics = new VerletPhysics2D();  //初始曲线上每个球斥力集合

            WB_Point a = nodes_matching_a.get(h);
            WB_Point c = nodes_matching_c.get(h);
            float minx = min(a.xf(), c.xf());
            float maxx = max(a.xf(), c.xf());
            float miny = min(a.yf(), c.yf());
            float maxy = max(a.yf(), c.yf());
            WB_Point b = new WB_Point(random(minx, maxx), random(miny, maxy));

            //生成曲线上的点和弹簧
            CurveAttractors curve_points = new CurveAttractors();
            for (int k = 0; k <= curve_steps * 2; k++) {
                float t;
                float x;
                float y;
                if (k < curve_steps) {
                    t = k / (float) curve_steps;
                    x = curvePoint(a.xf(), a.xf(), b.xf(), c.xf(), t);
                    y = curvePoint(a.yf(), a.yf(), b.yf(), c.yf(), t);

                } else {
                    t = (k - curve_steps) / (float) curve_steps;
                    x = curvePoint(a.xf(), b.xf(), c.xf(), c.xf(), t);
                    y = curvePoint(a.yf(), b.yf(), c.yf(), c.yf(), t);
                }
                VerletParticle2D p = new VerletParticle2D(x, y);
                physics.addParticle(p);  //将点加入物理系统
                if (k == 0 || k == curve_steps * 2) p.lock(); //将弹簧两个定点固定

                if (k > 0) {
                    VerletParticle2D q = curve_points.attractors.get(k - 1);
                    VerletSpring2D s = new VerletSpring2D(p, q, (float) (p.distanceTo(q) * 0.9), (float) 0.001);//相邻点形成弹簧
                    curve_attractors_physics.addSpring(s);
                    if (k > 1) {
                        VerletParticle2D r = curve_points.attractors.get(k - 2);
                        VerletSpring2D u = new VerletSpring2D(p, r, (float) (p.distanceTo(r) * 1), (float) 0.001); //间隔点形成弹簧
                        curve_out_attractors_physics.addSpring(u);
                    }
                    physics.addBehavior(new AttractionBehavior2D(p, (float) (p.distanceTo(q) * 0.8), (float) 0.1)); //所有点之间产生和其他点的引力
                }
                curve_points.add(p);
            }
            curve_springs.add(curve_attractors_physics);
            curve_out_springs.add(curve_out_attractors_physics);

            curves.add(curve_points);

        }
    }

    private void generateCruveAndPoints_up() {
        ArrayList<WB_Point> nodes_matching_a = new ArrayList<>();
        ArrayList<WB_Point> nodes_matching_c = new ArrayList<>();

        //挑出两个锚点
        ArrayList<WB_Point> nodes_matching = new ArrayList<>();
        for (WB_Point t : my_nodes_up) {
            nodes_matching.add(t);
        }

        for (int k = 0; k < 2 * curve_num_up; k++) {
            if (k < curve_num_up) {
                nodes_matching_a.add(nodes_matching.get(k));
            } else if (k < 2 * curve_num_up && k >= curve_num_up) {
                nodes_matching_c.add(nodes_matching.get(k));
            }
        }

        //更换nodes_matching_c元素顺序,得到比较合适的连接点

        float min_distance_deviation_all = 0; //计算现在的总距离
        for (int k = 0; k < curve_num_up; k++) {
            WB_Point p = nodes_matching_a.get(k);
            WB_Point q = nodes_matching_c.get(k);
            min_distance_deviation_all += abs(Method.getDistance(p, q) - cruveStandardLength);
        }

        for (int k = 0; k < curve_num_up; k++) {
            for (int h = 0; h < curve_num_up; h++) {
                if (h != k) {
                    WB_Point a1 = nodes_matching_a.get(k);
                    WB_Point c1 = nodes_matching_c.get(k);
                    WB_Point a2 = nodes_matching_a.get(h);
                    WB_Point c2 = nodes_matching_c.get(h);
                    float length_origin = abs(Method.getDistance(a1, c1) - cruveStandardLength) + abs(Method.getDistance(a2, c2) - cruveStandardLength);
                    float length_now = abs(Method.getDistance(a1, c2) - cruveStandardLength) + abs(Method.getDistance(a2, c1) - cruveStandardLength);
                    if (length_now < length_origin) {
                        nodes_matching_c.set(k, c2);
                        nodes_matching_c.set(h, c1);
                    }
                }
            }
        }


        //产生第三点b，连接得到曲线
        for (int h = 0; h < curve_num_up; h++) {
            VerletPhysics2D curve_attractors_physics = new VerletPhysics2D();  //初始曲线上每个球斥力集合
            VerletPhysics2D curve_out_attractors_physics = new VerletPhysics2D();  //初始曲线上每个球斥力集合

            WB_Point a = nodes_matching_a.get(h);
            WB_Point c = nodes_matching_c.get(h);
            float minx = min(a.xf(), c.xf());
            float maxx = max(a.xf(), c.xf());
            float miny = min(a.yf(), c.yf());
            float maxy = max(a.yf(), c.yf());
            WB_Point b = new WB_Point(random(minx, maxx), random(miny, maxy));

            //生成曲线上的点和弹簧
            CurveAttractors curve_points = new CurveAttractors();
            for (int k = 0; k <= curve_steps * 2; k++) {
                float t;
                float x;
                float y;
                if (k < curve_steps) {
                    t = k / (float) curve_steps;
                    x = curvePoint(a.xf(), a.xf(), b.xf(), c.xf(), t);
                    y = curvePoint(a.yf(), a.yf(), b.yf(), c.yf(), t);

                } else {
                    t = (k - curve_steps) / (float) curve_steps;
                    x = curvePoint(a.xf(), b.xf(), c.xf(), c.xf(), t);
                    y = curvePoint(a.yf(), b.yf(), c.yf(), c.yf(), t);
                }
                VerletParticle2D p = new VerletParticle2D(x, y);
                physics_up.addParticle(p);  //将点加入物理系统
                if (k == 0 || k == curve_steps * 2) p.lock(); //将弹簧两个定点固定

                if (k > 0) {
                    VerletParticle2D q = curve_points.attractors.get(k - 1);
                    VerletSpring2D s = new VerletSpring2D(p, q, (float) (p.distanceTo(q) * 0.9), (float) 0.001);//相邻点形成弹簧
                    curve_attractors_physics.addSpring(s);
                    if (k > 1) {
                        VerletParticle2D r = curve_points.attractors.get(k - 2);
                        VerletSpring2D u = new VerletSpring2D(p, r, (float) (p.distanceTo(r) * 1), (float) 0.001); //间隔点形成弹簧
                        curve_out_attractors_physics.addSpring(u);
                    }
                    physics_up.addBehavior(new AttractionBehavior2D(p, (float) (p.distanceTo(q) * 0.8), (float) 0.1)); //所有点之间产生和其他点的引力
                }
                curve_points.add(p);
            }
            curve_springs_up.add(curve_attractors_physics);
            curve_out_springs_up.add(curve_out_attractors_physics);
            curves_up.add(curve_points);

        }
    }

    //绘制node
    void drawNodes() {
        noStroke();
        fill(255, 0, 0);
        for (int k = 0; k < nodes_up.size(); k++) {
            VerletParticle2D t = nodes_up.get(k);
            ellipse(t.x, t.y, 10, 10);
        }
    }

    //绘制attractors
    void drawAttractors() {
        fill(0, 255, 0);
        for (int k = 0; k < curves_up.size(); k++) {
            CurveAttractors a = curves_up.get(k);
            for (int h = 0; h < a.attractors.size(); h++) {
                VerletParticle2D t = a.attractors.get(h);
                ellipse(t.x, t.y, 5, 5);
            }
        }
    }

    //绘制弹簧
    private void drawSprings() {
        stroke(255, 0, 255);
        for (int k = 0; k < curve_springs.size(); k++) {
            VerletPhysics2D a = curve_springs.get(k);
            for (int h = 0; h < a.springs.size(); h++) {
                VerletSpring2D s = a.springs.get(h);
                line(s.a.x, s.a.y, s.b.x, s.b.y);
            }
        }

        stroke(255, 0, 255);
        for (int k = 0; k < curve_springs_up.size(); k++) {
            VerletPhysics2D a = curve_springs_up.get(k);
            for (int h = 0; h < a.springs.size(); h++) {
                VerletSpring2D s = a.springs.get(h);
                line(s.a.x, s.a.y, s.b.x, s.b.y);
            }
        }
    }


    /*********************小广场*********************/

    private void genSSquares() {
        //每个大节点附近产生新泡泡
        for (Square p : l_squares_all) {
            for (int k = 0; k < s_square_num; k++) {
                float x = p.x;
                float y = p.y;
                Square t = new Square(x, y, s_square_radius);
                s_squares.add(t);
            }
        }
    }

    private void removeSSquares() {
        for (int k = s_squares.size() - 1; k >= 0; k--) {
            Square p = s_squares.get(k);
            Boolean ifdelete = true;

            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (!Method.ifContain(area_all,p.x,p.y)) {
                    s_squares.remove(k);
                    ifdelete = false;
            }
            //删除lake内部的点
            if (ifdelete) {
                if (Method.ifContain(lake_edge, p.x, p.y)) {
                    s_squares.remove(k);
                    ifdelete = false;
                }
            }
            if (ifdelete) {
                if (Method.ifContain(river_edge, p.x, p.y)) {
                    s_squares.remove(k);
                    ifdelete = false;
                }
            }
            if (ifdelete) {
                if (!Method.ifContain(block_edge, p.x, p.y)) {
                    s_squares.remove(k);
                    ifdelete = false;
                }
            }
        }
    }

    private void moveSSquares() {
        for (int k = 0; k < s_squares.size(); k++) {
            Square p = s_squares.get(k);

            PVector all = new PVector();
            PVector attractions = new PVector();
            PVector river_factor = new PVector();
            PVector river_pushout = new PVector();
            PVector river_push = new PVector();
//            PVector river_s_push = new PVector();
            PVector lake_push = new PVector();

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

            for(Square q:l_squares_all){
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
            if (r_min_distance < (river_factor_radius / 1000)) {
                WB_Point t_point = Method.closestPoint(river_edge, p.x, p.y);
                river_factor = new PVector(t_point.xf() - p.x, t_point.yf() - p.y);
                river_factor.normalize();
                river_factor.mult(1 - r_min_distance / river_factor_radius);
            }

            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (r_min_distance_center < 10) {
            } else if (r_min_distance_center < (s_square_radius / 2)) {
                WB_Point t_point = Method.closestPointPL(river_center, p.x, p.y);
                river_pushout = new PVector(p.x - t_point.xf(), p.y - t_point.yf());
                river_pushout.normalize();
                river_pushout.mult(1 - r_min_distance_center / (s_square_radius / 2));
            }
            all.add(attractions.mult((float) 0.6)).add(river_factor.mult((float) 0.2)).add(river_push.mult((float) 0.1).add(lake_push)).mult(3);
            p.update(all);

            //河流斥力
            if (Method.ifContain(river_edge, p.x, p.y)) {
                WB_Point t_point = Method.closestPoint(river_edge, p.x, p.y);
                river_push = new PVector(t_point.xf() - p.x, t_point.yf() - p.y);
                river_push.normalize();
            }

            //湖泊斥力
            if (Method.ifContain(lake_edge, p.x, p.y)) {
                WB_Point t_point = Method.closestPoint(lake_edge, p.x, p.y);
                lake_push = new PVector(t_point.xf() - p.x, t_point.yf() - p.y);
                lake_push.normalize();
            }

            all.add(attractions.mult((float) 0.5)).add(river_factor.mult((float) 0.3)).add(river_pushout.mult((float) 0.2).add(river_push.mult(3)).add(lake_push.mult(3))).mult(3);
            p.update(all);

        }
    }

    private void drawSSquares() {
        for (int k = 0; k < s_squares.size(); k++) {
            Square t = s_squares.get(k);
            strokeWeight(3);
            noFill();
            stroke(150, 0, 255);
            ellipse(t.x, t.y, s_square_radius, s_square_radius);
            strokeWeight(3);
            fill(150, 0, 255);
            ellipse(t.x, t.y, 6, 6);
            strokeWeight(3);
            noFill();
        }
    }

    private void drawSSquaresCenter() {
        for (int k = 0; k < s_squares.size(); k++) {
            Square t = s_squares.get(k);
            fill(150, 0, 255);
            stroke(150, 0, 255);
            strokeWeight(3);
            ellipse(t.x, t.y, 6, 6);
            strokeWeight(3);
            noFill();
        }
    }

    /*********************大广场*********************/

    private void genLSquaresOrigin() {
        for (WB_Point t : origin_square) {
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
            int random_num = (int) (random(1) + 0.3);
            if (random_num == 0) {
                x = (float) boundary.getCenterX() + 300;
                y = random((float) boundary.getMinY(), (float) boundary.getCenterY() + 600);
            } else if (random_num == 1) {
                x = random((float) boundary.getMinX(), (float) boundary.getCenterX());
                y = random((float) boundary.getCenterY() + 800,(float) boundary.getCenterY() + 1000);
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
            PVector river_pushout = new PVector();
            PVector river_s_push = new PVector();
            PVector lake_push = new PVector();

            //计算小球间的力
            for (Square q : l_squares) {
                if (p.distance(q) < l_square_radius) {
                    PVector attraction = new PVector(p.x - q.x, p.y - q.y);
                    attraction.normalize();
                    attraction.add((new PVector(random(-1, 1), random(-1, 1))).mult((float) 0.8));
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
            if (r_min_distance_center < l_square_radius / 10) {
                WB_Point t_point = Method.closestPointPL(river_center, p.x, p.y);
                river_pushout = new PVector(p.x - t_point.xf(), p.y - t_point.yf());
                river_pushout.normalize();
                river_pushout.mult(1 - r_min_distance_center / (l_square_radius / 10));
            }

            //湖泊斥力
            if (Method.ifContain(lake_edge, p.x, p.y)) {
                WB_Point t_point = Method.closestPoint(lake_edge, p.x, p.y);
                lake_push = new PVector(t_point.xf() - p.x, t_point.yf() - p.y);
                lake_push.normalize();
            }


            all.add(attractions.mult((float) 0.6)).add(river_factor.mult((float) 0.3)).add(river_pushout.mult((float) 0.2).add(lake_push.mult(3))).mult(10);
            p.update(all);
        }
    }

    private void removeLSquares() {
        for (int k = l_squares.size() - 1; k >= 0; k--) {
            Square p = l_squares.get(k);
            Boolean ifdelete = true;

            //计算河流对其斥力
            float r_min_distance_center = Method.closestPointdisPL(river_center, p.x, p.y);
            if (ifdelete) {
                if (r_min_distance_center > 300) {
                    l_squares.remove(k);
                    ifdelete = false;
                }
            }
            //删除lake内部的点
            if (ifdelete) {
                if (Method.ifContain(lake_edge, p.x, p.y)) {
                    l_squares.remove(k);
                    ifdelete = false;
                }
            }

            if (ifdelete) {
                if (!Method.ifContain(area_all, p.x, p.y)) {
                    l_squares.remove(k);
                    ifdelete = false;
                }
            }
            if (ifdelete) {
                if (!Method.ifContain(block_edge, p.x, p.y)) {
                    l_squares.remove(k);
                    ifdelete = false;
                }
            }
        }
    }

    private void drawLSquares() {
        for (int k = 0; k < l_squares.size(); k++) {
            noFill();
            Square t = l_squares.get(k);
            strokeWeight(3);
            stroke(255, 200, 0);
            ellipse(t.x, t.y, l_square_radius, l_square_radius);
            fill(255, 200, 0);
            strokeWeight(3);
            ellipse(t.x, t.y, 10, 10);
            strokeWeight(3);
            noFill();
        }
        for (int h = 0; h < l_squares_origin.size(); h++) {
            Square t = l_squares_origin.get(h);
            strokeWeight(3);
            stroke(255, 200, 0);
            ellipse(t.x, t.y, l_square_radius, l_square_radius);
            fill(255, 200, 0);
            strokeWeight(3);
            ellipse(t.x, t.y, 10, 10);
            strokeWeight(1);
            noFill();
        }
    }

    private void drawLSquaresCenter() {
        for (int k = 0; k < l_squares_all.size(); k++) {
            Square t = l_squares_all.get(k);
            fill(255, 200, 0);
            stroke(255, 200, 0);
            strokeWeight(3);
            ellipse(t.x, t.y, 10, 10);
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
//        river_s_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0river_s");//导入河流的边界
        lake_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0lake");//导入湖泊的边界
        block_edge = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "0block");//导入地块的边界
        origin_square = DXFImport.getDXFPoints("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "1origin_square");
//        area_public = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_public");
//        area_life = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_life");
//        area_ecology = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_ecology");
        area_all = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_all");
        area_down = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_down");
        area_up = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "2area_up");

        _commercial = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "3_commercial");
        _landscape = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "3_landscape");
        _public = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "3_public");
        _resident = DXFImport.getDXFPolygons("D:/computer/idealC/idealc_workplace/20201118UrbanDesign/others/00.dxf", "3_resident");
    }

    void drawDXF() {
        stroke(0);
        fill(140); //为了道路
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

        fill(255,0,0,70);
        stroke(255, 0, 0);
        Iterator var10 = _commercial.iterator();
        while (var10.hasNext()) {
            WB_Polygon the_commercial = (WB_Polygon) var10.next();
            render.drawPolygonEdges(the_commercial);
        }

        fill(0,255,255,100);
        stroke(0, 255, 255);
        Iterator var12 = _public.iterator();
        while (var12.hasNext()) {
            WB_Polygon the_public = (WB_Polygon) var12.next();
            render.drawPolygonEdges(the_public);
        }

        fill(255,255,0,70);
        stroke(255, 255, 0);
        Iterator var13 = _resident.iterator();
        while (var13.hasNext()) {
            WB_Polygon the_resident = (WB_Polygon) var13.next();
            render.drawPolygonEdges(the_resident);
        }

        fill(0,255,0,70);
        stroke(0, 255, 0);
        Iterator var14 = _landscape.iterator();
        while (var14.hasNext()) {
            WB_Polygon the_landscape = (WB_Polygon) var14.next();
            render.drawPolygonEdges(the_landscape);
        }
        noFill();


    }


}

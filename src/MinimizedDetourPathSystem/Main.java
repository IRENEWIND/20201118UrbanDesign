package MinimizedDetourPathSystem;

import processing.core.PApplet;
import toxi.geom.Rect;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;

import java.awt.*;
import java.util.ArrayList;

public class Main extends PApplet {
    static int W = 600;
    static int H = 600;

    ArrayList<VerletParticle2D> nodes = new ArrayList<VerletParticle2D>(); //节点
    ArrayList<VerletParticle2D> random_nodes = new ArrayList<VerletParticle2D>();//随机节点
    ArrayList<CurveAttractors> curves = new ArrayList<CurveAttractors>();


    int node_num = 20;
    int random_node_num = node_num / 2;

    int curve_steps = 50;  //是实际的1/2

    float delta = 10;

    VerletPhysics2D physics = new VerletPhysics2D();


    public static void main(String[] args) {
        processing.core.PApplet.main("MinimizedDetourPathSystem.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        background(255);
        initPhysics();
    }

    public void draw() {
        physics.update();
        background(255);
        drawNodes();
        drawAttractors();
        drawSprings();
    }




    void initPhysics() {
        physics.setWorldBounds(new Rect(0, 0, W, H));
        generateNewNodes();
        generateRandomNodes();
        generateCruveAndPoints();
    }


///*****************Node相关**********

    //产生新node
    void generateNewNodes() {
        for (int k = 0; k < node_num; k++) {
            float x = random(W);
            float y = random(H);
            VerletParticle2D t = new VerletParticle2D(x, y);
//            t.lock();
            nodes.add(t);
        }
    }

    private void generateRandomNodes() {
        for (int k = 0; k < random_node_num; k++) {
            float x = random(W);
            float y = random(H);
            VerletParticle2D t = new VerletParticle2D(x, y);
            random_nodes.add(t);
        }
    }

    private void generateCruveAndPoints() {
        for (int h = 0; h < random_nodes.size(); h++) {
            Point b = new Point((int) random_nodes.get(h).x, (int) random_nodes.get(h).y);
            Point a = new Point((int) nodes.get(2 * h).x, (int) nodes.get(2 * h).y);
            Point c = new Point((int) nodes.get(2 * h + 1).x, (int) nodes.get(2 * h + 1).y);
            curve(a.x, a.y, a.x, a.y, b.x, b.y, c.x, c.y);
            curve(a.x, a.y, b.x, b.y, c.x, c.y, c.x, c.y);

            //生成曲线上的点和弹簧
            CurveAttractors curve_points = new CurveAttractors();
            for (int k = 0; k <= curve_steps * 2; k++) {
                float t;
                float x;
                float y;
                if (k < curve_steps) {
                    t = k / (float) curve_steps;
                    x = curvePoint(a.x, a.x, b.x, c.x, t);
                    y = curvePoint(a.y, a.y, b.y, c.y, t);

                } else {
                    t = (k-curve_steps) / (float) curve_steps;
                    x = curvePoint(a.x, b.x, c.x, c.x, t);
                    y = curvePoint(a.y, b.y, c.y, c.y, t);
                }
                VerletParticle2D p = new VerletParticle2D(x, y);
                physics.addParticle(p);  //将点加入物理系统
                if(k==0||k==curve_steps*2)p.lock(); //将弹簧两个定点固定
                physics.addBehavior(new AttractionBehavior2D(p, (float) (delta * 3), (float)0.001)); //产生和其他东西的斥力
                physics.addBehavior(new AttractionBehavior2D(p, (float) (delta * 0.03), (float)-1)); //产生和其他东西的斥力
                if(k>0){
                    VerletParticle2D q=curve_points.attractors.get(k-1);
                    VerletSpring2D s=new VerletSpring2D(p,q,(float)(p.distanceTo(q)/1),(float) 1);
                    physics.addSpring(s);
                }
                curve_points.add(p);
            }

            curves.add(curve_points);
        }
    }


    //绘制node
    void drawNodes() {
        noStroke();
        fill(0);
        for (int k = 0; k < nodes.size(); k++) {
            VerletParticle2D t = nodes.get(k);
            ellipse(t.x, t.y, 5, 5);
        }
    }

    //绘制attractors
    void drawAttractors() {
        for (int k = 0; k < curves.size(); k++) {
            CurveAttractors a = curves.get(k);
            for (int h = 0; h < a.attractors.size(); h++) {
                VerletParticle2D t = a.attractors.get(h);
                ellipse(t.x, t.y, 3, 3);
            }
        }
    }

    //绘制弹簧
    private void drawSprings() {
        stroke(255, 0,255);
        for(VerletSpring2D s : physics.springs){
            line(s.a.x, s.a.y, s.b.x, s.b.y);
        }
    }

    //*****************Node相关**************/

}



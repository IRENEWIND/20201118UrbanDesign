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
    ArrayList<CurveAttractors> curves = new ArrayList<CurveAttractors>(); //曲线及曲线上的点
    VerletPhysics2D physics = new VerletPhysics2D();  //点的物理引擎
    ArrayList<VerletPhysics2D> curve_springs = new ArrayList<VerletPhysics2D>();  //曲线的物理引擎
    ArrayList<VerletPhysics2D> curve_out_springs = new ArrayList<VerletPhysics2D>(); //间隔点的物理引擎，防止折角过大

    int node_num = 50;//节点总数
    int random_node_num = node_num / 2;

    int curve_steps = 50;  //是实际的1/2


    public static void main(String[] args) {
        processing.core.PApplet.main("MinimizedDetourPathSystem.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        frameRate(60);
        background(255);
        initPhysics();
    }

    public void draw() {
        physics.update();
        for (int k = 0; k < curve_out_springs.size(); k++) {
            VerletPhysics2D a = curve_out_springs.get(k);
            a.update();
        }
        for (int k = 0; k < curve_springs.size(); k++) {
            VerletPhysics2D a = curve_springs.get(k);
            a.update();
        }

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



    //产生新node
    void generateNewNodes() {
        for (int k = 0; k < node_num; k++) {
            float random_number = random(1);
            float x,y;
            if(random_number<0.5) {
                x = ((int) (random(1) + 0.5)) * W;
                y = random(H);
            }else{
                x = random(W);
                y = ((int) (random(1) + 0.5)) * H;
            }
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
            VerletPhysics2D curve_attractors_physics = new VerletPhysics2D();  //初始曲线上每个球斥力集合
            VerletPhysics2D curve_out_attractors_physics = new VerletPhysics2D();  //初始曲线上每个球斥力集合

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


                if(k>0){
                    VerletParticle2D q=curve_points.attractors.get(k-1);
                    VerletSpring2D s=new VerletSpring2D(p,q,(float)(p.distanceTo(q)*0.9),(float) 0.001);//相邻点形成弹簧
                    curve_attractors_physics.addSpring(s);
                    if(k>1){
                        VerletParticle2D r = curve_points.attractors.get(k-2);
                        VerletSpring2D u = new VerletSpring2D(p,r,(float)(p.distanceTo(r)*1),(float) 0.001); //间隔点形成弹簧
                        curve_out_attractors_physics.addSpring(u);
                    }
                    physics.addBehavior(new AttractionBehavior2D(p, (float) (p.distanceTo(q)*0.8), (float)0.01)); //所有点之间产生和其他点的引力
                }
                curve_points.add(p);
            }
            curve_springs.add(curve_attractors_physics);
            curve_out_springs.add(curve_out_attractors_physics);

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
        for (int k = 0; k < curve_springs.size(); k++) {
            VerletPhysics2D a = curve_springs.get(k);
            for (int h = 0; h < a.springs.size(); h++) {
                VerletSpring2D s = a.springs.get(h);
                line(s.a.x, s.a.y ,s.b.x ,s.b.y);
            }
        }
    }



}



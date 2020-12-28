package testSplAndBall;

import cern.clhep.PhysicalConstants;
import processing.core.PApplet;
import toxi.geom.Rect;
import toxi.geom.Vec2D;
import toxi.physics2d.VerletParticle2D;
import toxi.physics2d.VerletPhysics2D;
import toxi.physics2d.VerletSpring2D;
import toxi.physics2d.behaviors.AttractionBehavior2D;
import toxi.physics2d.behaviors.GravityBehavior2D;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    static int W = 600;
    static int H = 600;

    int string_res = 100; //线上点的数量
    int ball_res = 60;
    int ball_radius = 80;

    VerletPhysics2D physics;
    VerletParticle2D selectedParticle;


    public static void main(String[] args) {
        processing.core.PApplet.main("testSplAndBall.Main");
    }

    public void settings() {
        size(W, H, P3D);
    }

    public void setup() {
        background(255);
        initPhysics();
    }

    public void draw() {
        background(255);
        physics.update();
        stroke(0);
        for(VerletSpring2D s : physics.springs){
            line(s.a.x, s.a.y, s.b.x, s.b.y);
        }

        fill(0);
        noStroke();
        for(VerletParticle2D t : physics.particles){
            ellipse(t.x, t.y, 5,5);
        }

    }

    //初始化
    void initPhysics() {
        physics = new VerletPhysics2D();
        // set screen bounds as bounds for physics sim
        physics.setWorldBounds(new Rect(0, 0, width, height));
        // add gravity along positive Y axis
        physics.addBehavior(new GravityBehavior2D(new Vec2D((float)0, (float)0.1)));
        // compute spacing for string particles
        float delta = (float) width / (string_res - 1);
        for (int i = 0; i < string_res; i++) {
            // create particles along X axis
            VerletParticle2D p = new VerletParticle2D(i * delta, H/2);
            physics.addParticle(p);
            // define a repulsion field around each particle
            // this is used to push the ball away
            physics.addBehavior(new AttractionBehavior2D(p, (float)(delta * 1.5), -20));
            // connect each particle to its previous neighbour
            if (i > 0) {
                VerletParticle2D q = physics.particles.get(i - 1);
                VerletSpring2D s = new VerletSpring2D(p, q, (float)(delta * 0.5), (float)0.1);
                physics.addSpring(s);
            }
        }
        // lock 1st & last particles
        physics.particles.get(0).lock();
        physics.particles.get(physics.particles.size() - 1).lock();

        // create ball
        // first create a particle as the ball centre
        VerletParticle2D c = new VerletParticle2D(W/2, 100);
        physics.addParticle(c);
        // list to store all ball perimeter particles
        List<VerletParticle2D> cparts = new ArrayList<VerletParticle2D>();
        for (int i = 0; i < ball_res; i++) {
            // create a rotation vector, scale it to the radius and move relative to ball center
            Vec2D pos = Vec2D.fromTheta(i * TWO_PI / ball_res).scaleSelf(ball_radius).addSelf(c);
            // create particle and add to lists
            VerletParticle2D p = new VerletParticle2D(pos);
            cparts.add(p);
            physics.addParticle(p);
            // connect to ball center for extra stability
            physics.addSpring(new VerletSpring2D(c, p, ball_radius, (float)0.01));
            // also connect all perimeter particles sequentially
            if (i > 0) {
                VerletParticle2D q = cparts.get(i - 1);
                physics.addSpring(new VerletSpring2D(p, q, p.distanceTo(q), 1));
            }
        }
        // finally close ball perimeter by connecting first & last particle
        VerletParticle2D p = cparts.get(0);
        VerletParticle2D q = cparts.get(ball_res - 1);
        physics.addSpring(new VerletSpring2D(p, q, p.distanceTo(q), 1));
    }


}

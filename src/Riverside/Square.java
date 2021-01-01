package Riverside;

import processing.core.PVector;
import wblut.geom.WB_Point;

import static Riverside.Main.W;
import static Riverside.Main.H;
import static processing.core.PApplet.sqrt;

public class Square {
    float x; //横向坐标
    float y; //纵向
    int radius; //影响范围
    PVector speed;

    public Square(float x, float y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = new PVector(-1, -1);
    }

    public void update(PVector a){
            speed = a;
            x += speed.x;
            y += speed.y;
    }

    public float distance(Square q){
        float distance = sqrt((this.x-q.x)*(this.x-q.x) + (this.y-q.y)*(this.y-q.y));
        return distance;
    }

    public float distance(WB_Point q){
        float distance = sqrt((this.x-q.xf())*(this.x-q.xf()) + (this.y-q.yf())*(this.y-q.yf()));
        return distance;
    }


}

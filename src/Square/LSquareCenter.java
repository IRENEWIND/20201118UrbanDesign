package Square;

import processing.core.PVector;

import static Square.Main.W;
import static Square.Main.H;
import static Square.Main.l_grid;

public class LSquareCenter {
    int x; //横向坐标
    int y; //纵向
    int i;
    int j;
    int radius; //影响范围
    PVector speed;

    public LSquareCenter(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = new PVector(-1,-1);

        this.i = x/l_grid;
        this.j = y/l_grid;
    }

    public void update(PVector a){
        if(x>10 && x<W-10 && y>10 && y<H-10) {
            speed = a;
            x += speed.x;
            y += speed.y;
            i = x / l_grid;
            j = y / l_grid;
        }else{
            speed = new PVector(0,0);
        }
    }

}

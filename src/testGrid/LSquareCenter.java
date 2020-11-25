package testGrid;

import processing.core.PVector;

import static testGrid.Main.l_grid;

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
        this.speed = new PVector(0,0);

        this.i = x/l_grid;
        this.j = y/l_grid;
    }

    public void update(PVector a){
        speed = a;
        x += speed.x;
        y += speed.y;
        i = x/l_grid;
        j = y/l_grid;

    }

}

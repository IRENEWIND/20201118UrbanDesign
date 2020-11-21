package Grid;

import java.util.ArrayList;

public class Grid {
    int property; //属性：0无,1河流,2湖泊,3原有道路，4原有建筑,20大广场,21小广场，22绿地
    int x;
    int y;
    int l;

    public Grid(int x, int y, int l_grid, int property) {
        this.property = property;
        this.x = x;
        this.y = y;
        this.l = l_grid;
    }

    public void findGrid(int mx, int my){

    }

}

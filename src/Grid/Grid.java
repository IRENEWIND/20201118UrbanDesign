package Grid;

import java.util.ArrayList;

public class Grid {
    int property; //属性：0无,1河流,2湖泊,3原有道路，4原有建筑,10大广场,11小广场，12绿地
    int x;
    int y;
    int l;

    public Grid(int x, int y, int l_grid, int property) {
        this.property = property;
        this.x = x;
        this.y = y;
        this.l = l_grid;
    }

}

package Grid;

import java.util.ArrayList;

import static Grid.Main.Nx;

public class Grid {
    int property; //属性：100无,1河流,2湖泊,3原有道路，4原有建筑,20大广场,21小广场，22绿地
    int x; //坐标x
    int y;  //坐标y
    int l;  //各自宽度
//    int n;  //在数组中的标号
//    int i;  //横向的数
//    int j;  //纵向的数

    public Grid(int x, int y, int l_grid, int property) {
        this.property = property;
        this.x = x;
        this.y = y;
        this.l = l_grid;
//        this.i = x/l;
//        this.j = y/l;
//        this.n = (y/l) * Nx + x/l;
    }




}

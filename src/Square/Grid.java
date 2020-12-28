package Square;

import processing.core.PVector;

import static Square.Main.Nx;

public class Grid {
    int property; //属性：100无,1河流,2湖泊,3原有道路，4原有建筑,20大广场中点,21大广场其他点，22其他大广场中心点
    int x; //坐标x
    int y;  //坐标y
    int l;  //各自宽度
    int n;  //在数组中的标号
    int i;  //横向的数
    int j;  //纵向的数

    //    double l_factor; //landscape 风景因子
    float r_factor;  //河流影响因子，每个格子受到河流影响的值
    float ls_factor;  //大广场影响因子
    float r_ls_factor; //河流和广场影响因子加权重

    PVector river_flow_field;

    public Grid(int x, int y, int l_grid, int property) {
        this.property = property;
        this.x = x;
        this.y = y;
        this.l = l_grid;
        this.i = x/this.l;
        this.j = y/this.l;
        this.n = j* Nx + i;

        this.r_factor = 0;
        this.ls_factor = 0;
        this.r_ls_factor = (float) (0.5*r_factor + 0.5*ls_factor);

        river_flow_field = new PVector(0,0);
    }

    public void update_r_ls_factor(){
        r_ls_factor = (float) (0.3*r_factor + 0.7*ls_factor);
    }

}

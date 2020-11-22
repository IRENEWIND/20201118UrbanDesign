package Grid;

import javafx.beans.property.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import static Grid.Main.*;

//改变属性，内容用change
//设置初始值，河流、道路用set，手动生成大广场set
//自动生成用gen

public class GridCollection {
    ArrayList<Grid> grids;

    public GridCollection(ArrayList<Grid> grids) {
        this.grids = grids;
    }

    //根据坐标定义大广场的位置,确定中心点,改变广场影响因子
    public void setLSquareCenter(int mx, int my) {
        //找寻i,j
        int i = mx / l_grid;
        int j = my / l_grid;
        changeGridProperty(i, j, 20);
//        changeLSquareFactor();
//        genLSquareSurround();
    }

    //根据大广场中心,计算影响因子，产生大广场周边的广场21
    public void genLSquareSurround(){
        changeR_LS_Factor();

        int n_square = 20;
        Grid[] t_grids = new Grid[n_square];
        t_grids = mathMaxFactors(n_square);//找出周边的20个最大的值
        for (int k = 0; k < n_square; k++) {
            int n = t_grids[k].n;
            changeGridProperty(n, 21);
        }

    }


    //改变每个格点的河流影响因子,影响10个格点，幂衰减
    public void changeRiverFactor() {
        int factor_grid = 10; //影响周边10个格点
        for (int k = 0; k < grids.size(); k++) {
            Grid m = grids.get(k);
            for (int h = 0; h < grids.size(); h++) {
                Grid n = grids.get(h);
                if (n.property == 1) {
                    float dist_mn = dist(m.x, m.y, n.x, n.y);
                    float dist_max = (float) factor_grid * l_grid;
                    if (dist_mn < dist_max) { //影响控制在10个单元格长度
                        float t_factor = map(dist_mn * dist_mn, 0, dist_max * dist_max, 1, 0);
                        m.r_factor = max(m.r_factor, t_factor);
                    }
                }
            }
            grids.set(k, m);
        }
    }

    //改变格点的大广场影响因子
    public void changeLSquareFactor() {
        int factor_grid = 15; //影响周边20个格点
            for (int k = 0; k < grids.size(); k++) {
                Grid n = grids.get(k);
                if (n.property == 20) {
                    int i = n.i;
                    int j = n.j;
                    for (int p = i - factor_grid; p <= i + factor_grid; p++) {
                        for (int q = j - factor_grid; q <= j + factor_grid; q++) {
                            Grid m = grids.get(p*Nx+q);
                            float dist_pq = sqrt((p - i) * (p - i) + (q - j) * (q - j)) * l_grid;
                            float dist_max = sqrt(2)*factor_grid * l_grid;
                            float t_factor = map(dist_pq * dist_pq, 0, dist_max * dist_max, 1, 0);
                            m.ls_factor = max(m.ls_factor, t_factor);
                            grids.set(q*Nx+p, m);
                        }
                    }
                }
            }
        }

    //改变河流大广场合并影响因子
    public void changeR_LS_Factor() {
        for (int k = 0; k < grids.size(); k++) {
            Grid n = grids.get(k);
            n.change_r_ls_factor();
            grids.set(k, n);
        }
    }


    //根据i,j找到Grid，对grid更改属性property
    public void changeGridProperty(int i, int j, int new_property) {
        int ti = i;
        int tj = j;
        int x = grids.get((j * Nx + i)).x;
        int y = grids.get((j * Nx + i)).y;
        grids.set((j * Nx + i), new Grid(x, y, l_grid, new_property));
    }

    //根据n找到Grid，对grid更改属性property
    public void changeGridProperty(int n, int new_property) {
        int x = grids.get(n).x;
        int y = grids.get(n).y;
        grids.set(n, new Grid(x, y, l_grid, new_property));
    }

    //初始化网格
    public GridCollection setInitial(int w, int h, int l_grid) {
        GridCollection new_grids;
        ArrayList<Grid> t_grids = new ArrayList<Grid>();
        for (int j = 0; j < h; j += l_grid) {
            for (int i = 0; i < w; i += l_grid) {
                Grid t = new Grid(i, j, l_grid, 100);
                t_grids.add(t);
            }
        }
        new_grids = new GridCollection(t_grids);
        return new_grids;
    }

    //设置初始河流
    public GridCollection setRiver() {
        GridCollection t;
        ArrayList<Grid> new_grids = new ArrayList<Grid>();
        for (Grid t_grid : grids) {
            if (t_grid.x > 150 && t_grid.x < 210 && t_grid.y > 150) {
                t_grid.property = 1;
            } else if (t_grid.x < 210 && t_grid.y > 150 && t_grid.y < 200) {
                t_grid.property = 1;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }

    //设置初始湖泊
    public GridCollection setLake() {
        GridCollection t;
        ArrayList<Grid> new_grids = new ArrayList<Grid>();
        for (Grid t_grid : grids) {
            //绘制三个湖泊位置
            if (t_grid.x > 250 && t_grid.x < 380 && t_grid.y > 260 && t_grid.y < 380) {
                t_grid.property = 2;
            } else if (t_grid.x > 250 && t_grid.x < 380 && t_grid.y > 430 && t_grid.y < 500) {
                t_grid.property = 2;
            } else if (t_grid.x > 280 && t_grid.x < 380 && t_grid.y > 560 && t_grid.y < 630) {
                t_grid.property = 2;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }

    //设置初始道路
    public GridCollection setRoad() {
        GridCollection t;
        ArrayList<Grid> new_grids = new ArrayList<Grid>();
        for (Grid t_grid : grids) {
            //绘制2条主干道
            if (t_grid.y > 390 && t_grid.y < 420) {
                t_grid.property = 3;
            } else if (t_grid.y > 510 && t_grid.y < 540) {
                t_grid.property = 3;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }


    //找出最大的r_ls_factor中n个元素，返回新数组
    public Grid[] mathMaxFactors(int n){
        Grid[] t_grids = new Grid[n];
        for (int k = 0; k < grids.size(); k++) {
            Grid m = grids.get(k);
            if (m.property == 100) {
                if (k < n) {  //在n个以内先放入集合中
                    t_grids[k] = m;
                } else if (m.r_ls_factor != 0) {
                    float min_factor = m.r_ls_factor;
                    int removeN = n;
                    for (int h = 0; h < n; h++) {
                        if (t_grids[h].r_ls_factor < min_factor) {
                            min_factor = t_grids[h].r_ls_factor;
                            removeN = h;
                        }
                        if (removeN != n) {
                            t_grids[removeN] = m;
                        }
                    }
                }
            }
        }
        return t_grids;
    }

}

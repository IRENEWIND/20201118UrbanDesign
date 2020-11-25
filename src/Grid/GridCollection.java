package Grid;

import javafx.beans.property.Property;
import org.apache.batik.dom.xbl.NodeXBL;
import processing.core.PApplet;
import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import static Grid.Main.*;

//改变属性，内容用change
//设置初始值，河流、道路用set，手动生成大广场set
//自动生成用gen
//计算用math

public class GridCollection {

//大广场参数
    int r_factor = 10;  //大广场中心点影响的范围
    int n_square = 15;  //大广场周边取的格子数


    ArrayList<Grid> grids;

    public GridCollection(ArrayList<Grid> grids) {
        this.grids = grids;
    }

    //根据坐标定义大广场的位置,确定中心点,改变广场影响因子,并产生周边
    public void setLSquareCenter(int mx, int my) {
        //找寻i,j
        int i = mx / l_grid;
        int j = my / l_grid;
        Grid t = grids.get(j * Nx + i);
        //在河流附近生成点，远的地方不行
        if (t.r_factor > 0.7) {
            if (t.property == 100) {
                changeGridProperty(i, j, 20);
                changeLSquareFactor(i, j);
                changeR_LS_Factor();
                genLSquareSurround(i, j, r_factor, n_square);
            }
        }
    }

    //根据中心点产生其他大广场坐标
    public void genLSquareCenter() {

    }


    //根据大广场中心(i,j),在一定范围r_factor计算影响因子，产生大广场周边的广场21
    public void genLSquareSurround(int i, int j, int r_factor, int n_square) {
        Grid[] t_grids;
        t_grids = mathMaxFactors(i, j, r_factor, n_square);//找出周边的n_square个最大的值
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

    //根据广场中心点改变格点的大广场影响因子，根据格点(i,j)
    public void changeLSquareFactor(int i, int j) {
        int factor_grid = 10; //影响周边格点
        for (int p = i - factor_grid; p <= i + factor_grid; p++) {
            for (int q = j - factor_grid; q <= j + factor_grid; q++) {
                if (p >= 0 && p < Nx && q >= 0 && q < Ny) {
                    Grid m = grids.get(q * Nx + p);
                    float dist_pq = sqrt((p - i) * (p - i) + (q - j) * (q - j)) * l_grid;
                    float dist_max = sqrt(2) * factor_grid * l_grid;
                    float t_factor = map(dist_pq * dist_pq, 0, dist_max * dist_max, 1, 0);
                    m.ls_factor = max(m.ls_factor, t_factor);
                    grids.set(q * Nx + p, m);
                }
            }
        }
    }


    //改变河流大广场合并影响因子
    public void changeR_LS_Factor() {
        for (int k = 0; k < grids.size(); k++) {
            Grid n = grids.get(k);
            n.update_r_ls_factor();
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

    //找出一定范围（广场影响范围）最大的r_ls_factor中n个元素，返回新数组
    public Grid[] mathMaxFactors(int i, int j, int r_factor, int n) {
        Grid[] t_grids = new Grid[n];
        int k = 0;
        for (int p = i - r_factor; p <= i + r_factor; p++) {
            for (int q = j - r_factor; q <= j + r_factor; q++) {
                if (p >= 0 && p < Nx && q >= 0 && q < Ny) {
                    Grid m = grids.get(q * Nx + p);
                    if (m.property == 100) {
                        if (k < n) {  //在n个以内先放入集合中
                            t_grids[k] = m;
                            k++;
                        } else if (m.r_ls_factor != 0) {
                            float min_factor = m.r_ls_factor;
                            int removeN = n;
                            for (int h = 0; h < n; h++) {
                                if (t_grids[h].r_ls_factor < min_factor) {
                                    min_factor = t_grids[h].r_ls_factor;
                                    removeN = h;
                                }
                            }
                            if (removeN != n) {
                                t_grids[removeN] = m;
                            }
                        }
                    }
                }
            }
        }
        return t_grids;
    }

}

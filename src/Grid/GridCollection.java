package Grid;

import javafx.beans.property.Property;
import org.apache.batik.dom.xbl.NodeXBL;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import Grid.Grid;
import Grid.LSquareCenter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import static Grid.Main.*;
import static Grid.Main.H;
import static Grid.Main.Nx;
import static Grid.Main.Ny;
import static Grid.Main.W;
import static Grid.Main.l_grid;

//改变属性，内容用change
//设置初始值，河流、道路用set，手动生成大广场set
//自动生成用gen
//计算用math

public class GridCollection {
    ArrayList<Grid> grids;

    public GridCollection(ArrayList<Grid> grids) {
        this.grids = grids;
    }


    //大广场参数
    int R_factor = 10;  //大广场中心点影响的范围,产生中心点周边广场块块使用
    int n_square = 20;  //大广场周边取的格子数

    ArrayList<LSquareCenter> origin_l_square_centers = new ArrayList<LSquareCenter>();
    ArrayList<LSquareCenter> other_l_square_centers = new ArrayList<LSquareCenter>();
    int l_square_num = 10;   //产生其他大广场的数量 每次
    int l_square_radius = 20;  //大广场影响半径，生成新大广场使用


    /**********手动广场生成***********/

    //根据坐标定义大广场的位置,确定中心点,改变广场影响因子,并产生周边
    public void setLSquareCenter(int mx, int my) {
        //找寻i,j
        int i = mx / l_grid;
        int j = my / l_grid;
        Grid t = grids.get(j * Nx + i);
        //在河流附近生成点，远的地方不行
        if (t.r_factor > 0.98) {
            if (t.property == 100) {
                origin_l_square_centers.add(new LSquareCenter(i * l_grid, j * l_grid, l_square_radius)); //加入初始广场值
                changeGridProperty(i, j, 20);
                changeLSquareFactor(i, j);
                changeR_LS_Factor();
//                genLSquareSurround(i, j, R_factor, n_square);
            }
        }
    }

    //根据大广场中心(i,j),在一定范围R_factor计算影响因子，产生大广场周边的广场21
    public void genLSquareSurround(int i, int j, int R_factor, int n_square) {
        Grid[] t_grids;
        t_grids = mathMaxFactors(i, j, R_factor, n_square);//找出周边的n_square个最大的值
        for (int k = 0; k < n_square; k++) {
            int n = t_grids[k].n;
            changeGridProperty(n, 21);
        }

    }


    //找出一定范围（广场影响范围）最大的r_ls_factor中n个元素，返回新数组
    public Grid[] mathMaxFactors(int i, int j, int R_factor, int n) {
        Grid[] t_grids = new Grid[n];
        int k = 0;
        for (int p = i - R_factor; p <= i + R_factor; p++) {
            for (int q = j - R_factor; q <= j + R_factor; q++) {
                if (p >= 0 && p < Nx && q >= 0 && q < Ny) {
                    Grid m = grids.get(q * Nx + p);
                    if (m.property != 1 && m.property != 2 && m.property != 3 && m.property != 20 && m.property != 22) {
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


    /**********OLSC其他大广场的智能体逐步生成***********/
    //根据中心点产生其他广场格子坐标 otherLSquareCenter
    // 产生最初的球球，得到河流的点
    public void genOLSC() {
        //产生最初的几个圆
        for (int k = 0; k < l_square_num; k++) {
//            LSquareCenter t = new LSquareCenter((int) (W * Math.random()), (int) (H * Math.random()), l_square_radius);
            LSquareCenter t = new LSquareCenter((int) (W * Math.random()), (int) (H * Math.random()), l_square_radius);
            int ti = t.i;
            int tj = t.j;
            Grid q = grids.get(Nx * t.j + t.i);
            if(q.r_factor > 0.4) {
                other_l_square_centers.add(t);
            }
        }
    }


    //移动广场中心点，根据河流对应力场
    public void moveOLSC() {
        for (int k = 0; k < other_l_square_centers.size(); k++) {
            LSquareCenter t = other_l_square_centers.get(k);
            Grid rt = grids.get(Nx * t.j + t.i); //找到广场对应的力场点

            PVector r_factor = rt.river_flow_field;

            //检测t和其他圆的距离得到新力场
            PVector ball_factor = new PVector(0, 0);

            for (int h = 0; h < other_l_square_centers.size() + origin_l_square_centers.size(); h++) {
                if (h != k) {
                    LSquareCenter s;
                    if (h < other_l_square_centers.size()) {
                        s = other_l_square_centers.get(h);
                    } else {
                        s = origin_l_square_centers.get(h - other_l_square_centers.size());  //和原广场比较
                    }
                    float d = dist(t.x, t.y, s.x, s.y);  //两个点之间的距离
                    if (d < 0.7 * l_square_radius * l_grid) {
                        ball_factor = new PVector(t.x - s.x, t.y - s.y);
                        ball_factor.normalize();
                        break;
                    }
                }
            }

            PVector all = r_factor.add(ball_factor.mult(1));
            t.update(all);

            //先检测小球是否在河流影响因子外部而且speed为0，如果是，删掉这个要素，增加一个新的
            if ((t.speed.equals(new PVector(0, 0)) && rt.r_factor < 0.5)) {
                other_l_square_centers.set(k, new LSquareCenter((int) (W * Math.random()), (int) (H * Math.random()), l_square_radius));
            } else {
                other_l_square_centers.set(k, t);
            }
        }
    }

    //选择其他的广场中心,删除离河远的点
    public void chooseOLSC() {
        for (int k = other_l_square_centers.size() - 1; k >= 0; k--) {
            LSquareCenter t = other_l_square_centers.get(k);
            int ti = t.i;
            int tj = t.j;
            Grid s = grids.get(tj * Nx + ti);
            if (s.r_factor < 0.99 || s.property == 2) {
                other_l_square_centers.remove(k);
            }
        }
    }

    //生成广场中心点周边
    public void genOLSCSurround() {
        for (int h = 0; h < other_l_square_centers.size(); h++) {
            LSquareCenter t = other_l_square_centers.get(h);
            int i = t.i;
            int j = t.j;
            changeGridProperty(i, j, 22);
            changeLSquareFactor(i, j);
            changeR_LS_Factor();
            genLSquareSurround(i, j, R_factor, n_square);
        }
        for (int l = 0; l < origin_l_square_centers.size(); l++) {
            LSquareCenter t = origin_l_square_centers.get(l);
            int i = t.i;
            int j = t.j;
            genLSquareSurround(i, j, R_factor, n_square);
        }
    }

    //为了重新绘制选点，删除已有的周边点
    public void deleteOLSC() {
        for (int k = 0; k < grids.size(); k++) {
            Grid t = grids.get(k);
            if (t.property == 21 || t.property == 22) {
                t.property = 100;
            }
            grids.set(k, t);
        }

    }

    //向外界传输生成广场的集合，方便绘图
    public ArrayList<LSquareCenter> getOLSC() {
        ArrayList<LSquareCenter> ts = new ArrayList<LSquareCenter>();
        ts.addAll(other_l_square_centers);
        ts.addAll(origin_l_square_centers);
        return ts;
    }


    /**********改变影响因子***********/
    //改变每个格点的河流影响因子,影响10个格点，幂衰减
    public void changeRiverFactor() {
        int factor_grid = 50; //影响周边10个格点
        for (int k = 0; k < grids.size(); k++) {
            Grid m = grids.get(k);
            float dist_max = (float) factor_grid * l_grid;
            float dist_mn = dist_max;
//            if (m.property != 1) {
                for (int h = 0; h < grids.size(); h++) {
                    Grid n = grids.get(h);
                    if (n.property == 1) {
                        float dist_t = dist(m.x, m.y, n.x, n.y);
                        if (dist_t < dist_mn) { //影响控制在10个单元格长度
                            dist_mn = dist_t;
                        }
                    }
                }
//            }
            float t_factor = map(dist_mn * dist_mn, 0, dist_max * dist_max, 1, 0);
//            float t_factor = (float)Math.log10(dist_mn) + 1;
//                m.r_factor = max(m.r_factor, t_factor);
            m.r_factor = t_factor;
            grids.set(k, m);
        }


    }

    //根据坐标r_factor改变点的river_flow_field方向，产生力的作用
    public void changeRiverFlowField() {
        for (int j = 0; j < Ny; j++) {
            for (int i = 0; i < Nx; i++) {
                Grid m = grids.get(Nx * j + i);
                Grid n = m; //目标点
                float r_factor_max = m.r_factor;
                for (int k = j - 1; k <= j + 1; k++) {
                    for (int l = i - 1; l <= i + 1; l++) {
                        if (l >= 0 && l < Nx && k >= 0 && k < Ny) {
                            Grid t = grids.get(Nx * k + l);
                            if (t.r_factor >= r_factor_max) {
                                r_factor_max = t.r_factor;
                                n = t;
                            }
                        }
                    }
                }
                //如果在河里就往外给一个向量
                if (m.property == 1) {
                    m.river_flow_field = new PVector((n.i - m.i) * 3, (n.j - m.j) * 3);
                } else {
                    m.river_flow_field = new PVector(n.i - m.i, n.j - m.j);
                }
                grids.set(j * Nx + i, m);
            }
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


    /**********改变属性property***********/
//根据i,j找到Grid，对grid更改属性property, 河流不变
    public void changeGridProperty(int i, int j, int new_property) {
        int ti = i;
        int tj = j;
        Grid t = grids.get(j * Nx + i);
        if (t.property != 1) {
            t.property = new_property;
        }
        grids.set((j * Nx + i), t);
    }

    //根据n找到Grid，对grid更改属性property
    public void changeGridProperty(int n, int new_property) {
        Grid t = grids.get(n);
        t.property = new_property;
        grids.set(n, t);
    }


    /**********设置初始化***********/
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
            if (t_grid.x > 300 && t_grid.x < 340 && t_grid.y > 150) {
                t_grid.property = 1;
            } else if (t_grid.x < 340 && t_grid.y > 190 && t_grid.y < 220) {
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
            if (t_grid.x > 380 && t_grid.x < 500 && t_grid.y > 360 && t_grid.y < 480) {
                t_grid.property = 2;
            } else if (t_grid.x > 380 && t_grid.x < 510 && t_grid.y > 560 && t_grid.y < 650) {
                t_grid.property = 2;
            } else if (t_grid.x > 410 && t_grid.x < 480 && t_grid.y > 700 && t_grid.y < 830) {
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
            if (t_grid.y > 510 && t_grid.y < 540) {
                t_grid.property = 3;
            } else if (t_grid.y > 660 && t_grid.y < 690) {
                t_grid.property = 3;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }


}



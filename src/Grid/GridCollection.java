package Grid;

import javafx.beans.property.Property;

import java.util.ArrayList;
import java.util.Collection;

import static Grid.Main.*;

public class GridCollection {
    ArrayList<Grid> grids;

    public GridCollection(ArrayList<Grid> grids) {
        this.grids = grids;
    }


    //根据坐标定义大广场的位置,确定中心店
    public void setLSquare(int mx, int my) {
        int i = mx / l_grid;
        int j = my / l_grid;
        int area_gn = 0; //广场格子数
        int max_gn =0; //最大格子数
        changeGridProperty(i, j, 20);

        for (int k = i - 1; k <= i + 1; k++) {
            for (int h = j - 1; h <= j + 1; h++) {
                if(area_gn<5) {
                    if (grids.get(h * Nx + k).property > 10) {
                        changeGridProperty(k, h, 20);
                        area_gn += 1;
                    }
                }
            }
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

}

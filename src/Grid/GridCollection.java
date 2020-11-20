package Grid;

import java.util.ArrayList;
import java.util.Collection;

public class GridCollection {
    ArrayList<Grid> grids;

    public GridCollection(ArrayList<Grid> grids) {
        this.grids = grids;
    }

    //导出grids数组
    public ArrayList<Grid> getArray(){
        return grids;
    }


    public GridCollection geneLSquare(){
        GridCollection new_grids;
        ArrayList<Grid> t_grids = new ArrayList<Grid>();

        new_grids = new GridCollection(t_grids);
        return new_grids;
    }

    //初始化网格
    public GridCollection setInitial(int w, int h, int l_grid){
        GridCollection new_grids;
        ArrayList<Grid> t_grids = new ArrayList<Grid>();
        for (int j = 0; j < h; j += l_grid) {
            for (int i = 0; i < w; i += l_grid) {
                Grid t = new Grid(i, j, l_grid, 0);
                t_grids.add(t);
            }
        }
        new_grids = new GridCollection(t_grids);
        return new_grids;
    }

    //设置初始河流
    public GridCollection  setRiver(){
        GridCollection t;
        ArrayList<Grid> new_grids = new ArrayList<Grid>();
        for(Grid t_grid : grids){
            if(t_grid.x > 150 && t_grid.x < 210 && t_grid.y>150){
                t_grid.property = 1;
            }else if(t_grid.x<210 && t_grid.y > 150 && t_grid.y < 200){
                t_grid.property = 1;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }

    //设置初始湖泊
    public GridCollection  setLake(){
        GridCollection t;
        ArrayList<Grid> new_grids = new ArrayList<Grid>();
        for(Grid t_grid : grids){
            //绘制三个湖泊位置
            if(t_grid.x > 250 && t_grid.x < 380 && t_grid.y > 260 && t_grid.y < 380){
                t_grid.property = 2;
            }else if(t_grid.x > 250 && t_grid.x < 380 && t_grid.y > 430 && t_grid.y < 500){
                t_grid.property = 2;
            }else if(t_grid.x > 280 && t_grid.x < 380 && t_grid.y > 560 && t_grid.y < 630){
                t_grid.property = 2;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }


    public GridCollection setRoad() {
        GridCollection t;
        ArrayList<Grid> new_grids = new ArrayList<Grid>();
        for(Grid t_grid : grids){
            //绘制2条主干道
            if(t_grid.y > 390 && t_grid.y < 420){
                t_grid.property = 3;
            }else if(t_grid.y > 510 && t_grid.y < 540){
                t_grid.property = 3;
            }
            new_grids.add(t_grid);
        }
        grids = new_grids;
        t = new GridCollection(grids);
        return t;
    }

}

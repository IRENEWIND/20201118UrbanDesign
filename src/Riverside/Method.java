package Riverside;

import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;

import java.util.ArrayList;

public class Method {


    public static float getDistance(float x1, float y1, float x2, float y2) {
        float dis;
        dis = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return dis;
    }

    public static WB_Point closestPointPL(WB_PolyLine polyline, float x, float y) {
        WB_Point cPnt;
        WB_Point thePnt = new WB_Point();
        float minDis = 10000;
        WB_Point presentPnt = new WB_Point(x, y);
        ArrayList<WB_Point> pts = new ArrayList<>();
        //存储多段线的所有点
        for (int i = 0; i < polyline.getNumberOfPoints() - 1; i++)
            pts.add(polyline.getPoint(i));
        //寻找离每个线段的最近点——getClosestPoint2D将超出线段的点的最近点赋予端点的值
        for (int i = 0; i < pts.size() - 1; i++) {
            cPnt = WB_GeometryOp.getClosestPoint2D(presentPnt, polyline.getSegment(i));
            float presentDis = getDistance(x, y, cPnt.xf(), cPnt.yf());
            if (presentDis < minDis) {
                minDis = presentDis;
                thePnt = cPnt;
            }
        }
        return thePnt;
    }

    public static float closestDis(WB_PolyLine polyline, float x, float y) {
        WB_Point cPnt;
        WB_Point thePnt = new WB_Point();
        float minDis = 10000;
        WB_Point presentPnt = new WB_Point(x, y);
        ArrayList<WB_Point> pts = new ArrayList<>();
        //存储多段线的所有点
        for (int i = 0; i < polyline.getNumberOfPoints() - 1; i++)
            pts.add(polyline.getPoint(i));
        //寻找离每个线段的最近点——getClosestPoint2D将超出线段的点的最近点赋予端点的值
        for (int i = 0; i < pts.size() - 1; i++) {
            cPnt = WB_GeometryOp.getClosestPoint2D(presentPnt, polyline.getSegment(i));
            float presentDis = getDistance(x, y, cPnt.xf(), cPnt.yf());
            if (presentDis < minDis) {
                minDis = presentDis;
                thePnt = cPnt;
            }
        }
        return minDis;
    }




}

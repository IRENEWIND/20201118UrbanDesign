package Riverside;

import javafx.scene.shape.Polyline;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;

import java.util.ArrayList;
import java.util.Iterator;

public class Method {

    public void Method(){

    }


    //polygon和（x,y)是否包含
    public static boolean ifContain(ArrayList<WB_Polygon> polygons, float x, float y) {
        int nCross = 0;
        for (int k = 0; k < polygons.size(); k++) {
            WB_Polygon polygon = polygons.get(k);

            for(int i = 0; i < polygon.getNumberOfPoints() - 1; ++i) {
                WB_Point p1 = polygon.getPoint(i);
                WB_Point p2 = polygon.getPoint(i + 1);
                if (p1.yf() != p2.yf() && y >= Math.min(p1.yf(), p2.yf()) && y < Math.max(p1.yf(), p2.yf())) {
                    double crossx = (double)((y - p1.yf()) * (p2.xf() - p1.xf()) / (p2.yf() - p1.yf()) + p1.xf());
                    if (crossx > (double)x) {
                        ++nCross;
                    }
                }
            }

        }
        return nCross % 2 == 1;
    }

    //得到点与点的距离
    public static float getDistance(float x1, float y1, float x2, float y2) {
        double dis1 = Math.pow((double)(x1 - x2), 2.0D);
        double dis2 = Math.pow((double)(y1 - y2), 2.0D);
        return (float)Math.sqrt(dis1 + dis2);
    }

    public static float getDistance(WB_Point a, WB_Point b) {
        double dis1 = Math.pow((double)(a.xf() - b.xf()), 2.0D);
        double dis2 = Math.pow((double)(a.yf() - b.yf()), 2.0D);
        return (float)Math.sqrt(dis1 + dis2);
    }

    /********************************计算最近点*************************************/

    //得到polyline和（x,y)的最近点
    public static WB_Point closestPoint(WB_PolyLine polyline, float x, float y) {
        WB_Point thePnt = new WB_Point();
        float minDis = 1.0E9F;
        WB_Point presentPnt = new WB_Point((double)x, (double)y);
        ArrayList<WB_Point> pts = new ArrayList();

        int i;
        for(i = 0; i < polyline.getNumberOfPoints() - 1; ++i) {
            pts.add(polyline.getPoint(i));
        }

        for(i = 0; i < pts.size() - 1; ++i) {
            WB_Point cPnt = WB_GeometryOp.getClosestPoint2D(presentPnt, polyline.getSegment(i));
            float presentDis = getDistance(x, y, cPnt.xf(), cPnt.yf());
            if (presentDis < minDis) {
                minDis = presentDis;
                thePnt = cPnt;
            }
        }
        return thePnt;
    }
    public static WB_Point closestPointPL(ArrayList<WB_PolyLine> polylines, float x, float y) {
        WB_Point thePnt = new WB_Point();
        float minDis = 1.0E9F;

        for(WB_PolyLine t_polyline : polylines){
            float min = closestPointdis(t_polyline, x, y);
            if(min<minDis) {
                minDis = min;
                thePnt = closestPoint(t_polyline, x, y);
            }
        }
        return thePnt;
    }
    //得到polygon和（x,y)的最近点
    public static WB_Point closestPoint(WB_Polygon polygon, float x, float y) {
        WB_Point thePnt = new WB_Point();
        float minDis = 1.0E9F;
        WB_Point presentPnt = new WB_Point((double)x, (double)y);
        ArrayList<WB_Point> pts = new ArrayList();

        int i;
        for(i = 0; i < polygon.getNumberOfPoints() - 1; ++i) {
            pts.add(polygon.getPoint(i));
        }

        for(i = 0; i < pts.size() - 1; ++i) {
            WB_Point cPnt = WB_GeometryOp.getClosestPoint2D(presentPnt, polygon.getSegment(i));
            float presentDis = getDistance(x, y, cPnt.xf(), cPnt.yf());
            if (presentDis < minDis) {
                minDis = presentDis;
                thePnt = cPnt;
            }
        }
        return thePnt;
    }
    public static WB_Point closestPoint(ArrayList<WB_Polygon> polygons, float x, float y) {
        WB_Point thePnt = new WB_Point();
        float minDis = 1.0E9F;

        for(WB_Polygon t_polygon : polygons){
            float min = closestPointdis(t_polygon, x, y);
            if(min<minDis) {
                minDis = min;
                thePnt = closestPoint(t_polygon, x, y);
            }
        }
        return thePnt;
    }

    //得到polyline和（x,y)的最近点的距离
    public static float closestPointdis(WB_PolyLine polyline, float x, float y) {
        WB_Point thePnt = new WB_Point();
        float minDis = 1.0E9F;
        WB_Point presentPnt = new WB_Point((double)x, (double)y);
        ArrayList<WB_Point> pts = new ArrayList();

        int i;
        for(i = 0; i < polyline.getNumberOfPoints() - 1; ++i) {
            pts.add(polyline.getPoint(i));
        }

        for(i = 0; i < pts.size() - 1; ++i) {
            WB_Point cPnt = WB_GeometryOp.getClosestPoint2D(presentPnt, polyline.getSegment(i));
            float presentDis = getDistance(x, y, cPnt.xf(), cPnt.yf());
            if (presentDis < minDis) {
                minDis = presentDis;
                thePnt = cPnt;
            }
        }
        return minDis;
    }
    public static float closestPointdisPL(ArrayList<WB_PolyLine> polylines, float x, float y) {
        float minDis = 1.0E9F;
        for(WB_PolyLine t_polyline : polylines){
            float min = closestPointdis(t_polyline, x, y);
            if(min<minDis) minDis = min;
        }
        return minDis;
    }
    //得到polygon和（x,y)的最近点的距离
    public static float closestPointdis(WB_Polygon polygon, float x, float y){
        WB_Point thePnt = new WB_Point();
        float minDis = 1.0E9F;
        WB_Point presentPnt = new WB_Point((double)x, (double)y);
        ArrayList<WB_Point> pts = new ArrayList();

        int i;
        for(i = 0; i < polygon.getNumberOfPoints() - 1; ++i) {
            pts.add(polygon.getPoint(i));
        }

        for(i = 0; i < pts.size() - 1; ++i) {
            WB_Point cPnt = WB_GeometryOp.getClosestPoint2D(presentPnt, polygon.getSegment(i));
            float presentDis = getDistance(x, y, cPnt.xf(), cPnt.yf());
            if (presentDis < minDis) {
                minDis = presentDis;
                thePnt = cPnt;
            }
        }
        return minDis;
    }
    public static float closestPointdis(ArrayList<WB_Polygon> polygons, float x, float y) {
        float minDis = 1.0E9F;
        for(WB_Polygon t_polygon : polygons){
            float min = closestPointdis(t_polygon, x, y);
            if(min<minDis) minDis = min;
        }
        return minDis;
    }

    /********************************计算最近点*************************************/


    public static ArrayList<WB_PolyLine> changePolygonToPolyline(ArrayList<WB_Polygon> polygons){
        ArrayList<WB_PolyLine> cs = new ArrayList<>();
        for(WB_Polygon a : polygons) {
            ArrayList<WB_Point> bs = new ArrayList<>();
            for (int k = 0; k < a.getNumberOfPoints(); k++) {
                bs.add(a.getPoint(k));
            }
            WB_PolyLine c = new WB_PolyLine(bs);
            cs.add(c);
        }
        return cs;
    }

}

package test;

import jdk.dynalink.beans.StaticClass;
import org.kabeja.dxf.*;
import org.kabeja.dxf.helpers.Point;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;
import processing.core.PApplet;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hao Hua     whitegreen@163.com
 * <p>
 * http://labaaa.org
 */

public class DXFImport extends PApplet {

    //读取 PolyLines
    //
    public static double[][][] polylines_layer(String filename, String layerName) { // read polylines from the one layer
        Parser parser = ParserBuilder.createDefaultParser();
        DXFDocument doc = null;
        try {
            parser.parse(filename, DXFParser.DEFAULT_ENCODING);
            doc = parser.getDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DXFLWPolyline(doc.getDXFLayer(layerName));
    }

    public static double[][][] DXFLWPolyline(DXFLayer layer) {
        List<DXFLWPolyline> pls = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LWPOLYLINE);
        double[][][] polyLines = new double[pls.size()][][];
        for (int i = 0; i < polyLines.length; i++) {
            DXFLWPolyline pl = pls.get(i);
            int n = pl.getVertexCount();
            double[][] poly = new double[n][];
            for (int j = 0; j < n; j++) {
                DXFVertex v = pl.getVertex(j);
                poly[j] = new double[]{v.getX(), v.getY()};
            }
            polyLines[i] = poly;
        }
        return polyLines;
    }


    public static double[][][] polygons_layer(String filename, String layerName) {// read polygons from the one layer
        return null;
    }

    //读取 Lines
    //

    public static double[][][] Lines_layer(String filename, String layerName) { // read polylines from the one layer
        Parser parser = ParserBuilder.createDefaultParser();
        DXFDocument doc = null;
        try {
            parser.parse(filename, DXFParser.DEFAULT_ENCODING);
            doc = parser.getDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DXFLine(doc.getDXFLayer(layerName));
    }

    public static double[][][] DXFLine(DXFLayer layer) {
        List<DXFLine> pls = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);
        double[][][] Lines = new double[pls.size()][][];
        for (int i = 0; i < Lines.length; i++) {
            DXFLine pl = pls.get(i);
            int n = 2;
            double[][] poly = new double[n][];
            for (int j = 0; j < n; j++) {
                Point v;
                if (j == 0) {
                    v = pl.getStartPoint();
//                    println(v+"1");
                } else {
                    v = pl.getEndPoint();
//                    println(v+"2");
                }
                poly[j] = new double[]{v.getX(), v.getY()};
            }
            Lines[i] = poly;
        }
        return Lines;
    }

    //dxf获取线条
    public static ArrayList<WB_PolyLine> getDXFPolyLine(String filename, String LayerName) {
        double[][][] polys = DXFImport.polylines_layer(filename, LayerName);
        ArrayList<WB_PolyLine> polyLines = new ArrayList<>();

        for (double[][] poly : polys) {
            WB_Point[] pts = new WB_Point[poly.length];
            for (int j = 0; j < poly.length; ++j) {
                pts[j] = new WB_Point(poly[j]);
            }
            polyLines.add(new WB_PolyLine(pts));
        }
        return polyLines;
    }
}

package org.panaggelica.inspector_routes.processors;

import lombok.extern.slf4j.Slf4j;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterVisitor;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.model.oati.Inspector;
import org.panaggelica.inspector_routes.model.oati.Inspectorate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FeatureProcessorImpl implements FeatureProcessor {

    private static final Geometry exemplar = new Point(null, new PrecisionModel(), 4326);
    private static final int FEATURE_THRESHOLD = 4;
    private static final FeatureDistanceMetric distanceMetric = new FeatureDistanceMetric();

    private Geometry voronoiDiagram;
    private FeatureCollection thisFeatures;
    private Map<Inspectorate, FeatureCollection> featured = new HashMap<>();

    @Override
    public void process(FeatureCollection featureCollection, List<Inspectorate> inspectorates, RoutingOptions options) {

        thisFeatures = featureCollection;

        VoronoiDiagramBuilder voronoi = new VoronoiDiagramBuilder();

        Collection c = new ArrayList();
        for (Inspectorate inspectorate : inspectorates) {
            Point point = inspectorate.getParsedLocation();
            c.add(point.getCoordinate());
        }

        voronoi.setSites(c);
        voronoiDiagram = voronoi.getDiagram(exemplar.getFactory());

        log.info("voronoi: {}", voronoiDiagram);

        // todo: intersect features with polygons
        int i = 0;
        for (Inspectorate inspectorate : inspectorates) {
            featured.put(inspectorate, featureCollection.subCollection(new Filter() {
                @Override
                public boolean evaluate(Object object) {
                    return false;
                }

                @Override
                public Object accept(FilterVisitor visitor, Object extraData) {
                    return null;
                }
            }));
        }
    }

    @Override
    public List<Point> getCoords(Inspectorate inspectorate, Inspector inspector, RoutingOptions options) {
        FeatureCollection featureCollection = featured.get(inspectorate);
        List<Point> res = new ArrayList<>();
        try (FeatureIterator featureIterator = featureCollection.features()) {
            while (featureIterator.hasNext()) {
                SimpleFeature feature = (SimpleFeature) featureIterator.next();
                res.addAll(getFeaturePoints(feature, options));
            }
        }
        return res;
    }

    private Point getFeatureCentroid(SimpleFeature feature) {
        return ((Geometry) feature.getDefaultGeometry()).getCentroid();
    }

    private double getFeatureArea(SimpleFeature feature) {
        return ((Geometry) feature.getDefaultGeometry()).getArea();
    }

    private List<Point> getFeaturePoints(SimpleFeature feature, RoutingOptions options) {
        Object defaultGeometry = feature.getDefaultGeometry();
        Class geometryClass = defaultGeometry.getClass();

        if (MultiLineString.class.isAssignableFrom(geometryClass)) {
            return pointFromMLS((MultiLineString) defaultGeometry);
        } else if (LineString.class.isAssignableFrom(geometryClass)) {
            return pointFromLS((LineString) defaultGeometry);
        } else if (MultiPolygon.class.isAssignableFrom(geometryClass)) {
            return pointFromMP((MultiPolygon) defaultGeometry);
        } else if (Polygon.class.isAssignableFrom(geometryClass)) {
            return pointFromP((Polygon) defaultGeometry);
        } else if (Point.class.isAssignableFrom(geometryClass)) {
            return List.of((org.locationtech.jts.geom.Point) defaultGeometry);

        } else {
            log.error("Unknown class: {}", geometryClass);
            return Collections.emptyList();
        }

    }

    private List<Point> pointFromLS(LineString lineString) {
//        log.info("LS: {}", lineString);
        return List.of(lineString.getStartPoint(), lineString.getEndPoint());
    }

    private List<Point> pointFromMLS(MultiLineString multiLineString) {
//        log.info("MLS: {}", multiLineString);
        List<Point> res = new ArrayList<>();
        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            LineString ls = (LineString) multiLineString.getGeometryN(i);
            res.addAll(pointFromLS(ls));
        }
        return res;
    }

    private List<Point> pointFromP(Polygon polygon) {
//        log.info("P: {}", polygon);

        return List.of(polygon.getCoordinates())
                .stream().map(c -> GeometryFactory.createPointFromInternalCoord(c, exemplar))
                .collect(Collectors.toList());

    }

    private List<Point> pointFromMP(MultiPolygon multiPolygon) {
//        log.info("MP: {}", multiPolygon);
        List<Point> res = new ArrayList<>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon p = (Polygon) multiPolygon.getGeometryN(i);
            res.addAll(pointFromP(p));
        }
        return res;
    }


}

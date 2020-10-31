package org.panaggelica.inspector_routes.processors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FeatureProcessorImpl implements FeatureProcessor {

    private static final Geometry exemplar = new Point(null, new PrecisionModel(), 4326);
    private static final int FEATURE_THRESHOLD = 4;
    private static final FeatureDistanceMetric distanceMetric = new FeatureDistanceMetric();

    @SneakyThrows
    @Override
    public List<Point> process(FeatureCollection features, RoutingOptions options) {

//        PDBSCANClusterer clusterer = new PDBSCANClusterer(features, 4, 0.005, distanceMetric);
//        ArrayList<SimpleFeature> arr = clusterer.performClustering();
//
//        log.info("clustered: {}", arr);

        List<Point> res = new ArrayList<>();
        int featureThreshold = 0;

        try (FeatureIterator iterator = features.features()) {
            while (iterator.hasNext() && featureThreshold < FEATURE_THRESHOLD) {
                SimpleFeature feature = (SimpleFeature) iterator.next();
                res.addAll(getPoints(feature, options));
                featureThreshold++;
            }
        }
        return res;
    }

    private List<Point> getPoints(SimpleFeature feature, RoutingOptions options) {
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

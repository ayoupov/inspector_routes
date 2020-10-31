package org.panaggelica.inspector_routes.processors;

import lombok.extern.slf4j.Slf4j;
import org.geotools.data.DataUtilities;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.triangulate.VoronoiDiagramBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.model.oati.Inspector;
import org.panaggelica.inspector_routes.model.oati.Inspectorate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@Component
@Slf4j
public class FeatureProcessorImpl implements FeatureProcessor {

    private static final Geometry exemplar = new Point(null, new PrecisionModel(), 4326);
    private static final int FEATURE_THRESHOLD = 4;
    private static final FeatureDistanceMetric distanceMetric = new FeatureDistanceMetric();

    private Geometry voronoiDiagram;
    private FeatureCollection thisFeatures;
    private final Map<Inspectorate, FeatureCollection> featured;
    private final Map<String, Boolean> routedFeatures;

    public FeatureProcessorImpl() {
        featured = new HashMap<>();
        routedFeatures = new HashMap<>();
    }

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

        HashMap<Inspectorate, Geometry> zones = new HashMap<>();
        inspectorates.forEach(ins -> {
            for (int i = 0; i < voronoiDiagram.getNumGeometries(); i++) {
                Geometry zone = voronoiDiagram.getGeometryN(i);
                if (zone.contains(ins.getParsedLocation())) {
                    zones.put(ins, zone);
                    // вычисляем, какие фичи попадают в заявленные зоны
                    // todo: проверка на попадание фич в несколько зон
                    List<SimpleFeature> thisZoneList = new ArrayList<>();
                    try (FeatureIterator featureIterator = featureCollection.features()) {
                        while (featureIterator.hasNext()) {

                            SimpleFeature feature = (SimpleFeature) featureIterator.next();
                            if (zone.intersects((Geometry) feature.getDefaultGeometry()))
                                thisZoneList.add(feature);
                        }

                    }
                    featured.put(ins, DataUtilities.collection(thisZoneList));
                }
            }
        });

        boolean voronoiCheck = new HashSet<>(zones.values()).size() == zones.keySet().size();
        log.info("voronoi check " + (voronoiCheck ? "passed" : "failed"));

        log.info("balanced as {}", featured
                .entrySet().stream().collect(Collectors.toMap(
                        e -> e.getKey().getId(), e -> e.getValue().size()
                )));
    }

    private static final int MAX_OBJECTS = 4;

    @Override
    public List<Point> getCoords(Inspectorate inspectorate, Inspector inspector, RoutingOptions options) {
        FeatureCollection featureCollection = featured.get(inspectorate);
        List<Point> res = new ArrayList<>();
        int i = 0;
        try (FeatureIterator featureIterator = featureCollection.features()) {
            while (featureIterator.hasNext() && i < MAX_OBJECTS) {

                SimpleFeature feature = (SimpleFeature) featureIterator.next();

                final String featureID = feature.getID();
                if (!TRUE.equals(routedFeatures.get(featureID))) {
                    res.addAll(getFeaturePoints(feature, options));
                    routedFeatures.put(featureID, true);
                    i++;
                }

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
        return List.of(polygon.getCentroid());
//        return List.of(polygon.getCoordinates())
//                .stream().map(c -> GeometryFactory.createPointFromInternalCoord(c, exemplar))
//                .collect(Collectors.toList());

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

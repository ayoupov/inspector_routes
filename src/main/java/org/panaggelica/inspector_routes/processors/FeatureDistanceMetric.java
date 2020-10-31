package org.panaggelica.inspector_routes.processors;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureDistanceMetric implements DistanceMetric<SimpleFeature> {

    @Override
    public double calculateDistance(SimpleFeature val1, SimpleFeature val2) throws DBSCANClusteringException {
        Geometry v1 = (Geometry) val1.getDefaultGeometry();
        Geometry v2 = (Geometry) val2.getDefaultGeometry();
        return v1.distance(v2);
    }
}

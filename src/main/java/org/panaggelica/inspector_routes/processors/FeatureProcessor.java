package org.panaggelica.inspector_routes.processors;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.geotools.feature.FeatureCollection;
import org.locationtech.jts.geom.Point;
import org.panaggelica.inspector_routes.model.RoutingOptions;

import java.util.List;

public interface FeatureProcessor {
    List<Point> process(FeatureCollection features, RoutingOptions options) throws DBSCANClusteringException;
}

package org.panaggelica.inspector_routes.processors;

import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.geotools.feature.FeatureCollection;
import org.locationtech.jts.geom.Point;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.model.oati.Inspector;
import org.panaggelica.inspector_routes.model.oati.Inspectorate;

import java.util.List;

public interface FeatureProcessor {
    void process(FeatureCollection features, List<Inspectorate> inspectorates, RoutingOptions options) throws DBSCANClusteringException;

    List<Point> getCoords(Inspectorate inspectorate, Inspector inspector, RoutingOptions options);
}

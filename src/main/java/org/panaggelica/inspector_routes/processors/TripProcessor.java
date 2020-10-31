package org.panaggelica.inspector_routes.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.geotools.feature.FeatureCollection;
import org.panaggelica.inspector_routes.model.InspectorTrip;
import org.panaggelica.inspector_routes.model.oati.Inspectorates;
import org.panaggelica.inspector_routes.model.RoutingOptions;

import java.util.List;

public interface TripProcessor {

    InspectorTrip getTrip(FeatureCollection geojson, List<Inspectorates> inspetorates, RoutingOptions options) throws JsonProcessingException, DBSCANClusteringException;
}

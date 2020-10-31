package org.panaggelica.inspector_routes.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.geotools.feature.FeatureCollection;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.model.oati.Inspectorate;
import org.panaggelica.inspector_routes.model.response.Response;

import java.util.List;

public interface TripProcessor {

    Response getTrip(FeatureCollection geojson, List<Inspectorate> inspetorates, RoutingOptions options) throws JsonProcessingException, DBSCANClusteringException;
}

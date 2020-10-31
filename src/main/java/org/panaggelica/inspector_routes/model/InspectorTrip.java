package org.panaggelica.inspector_routes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.panaggelica.inspector_routes.model.osrm.OSRMTripResponse;
import org.panaggelica.inspector_routes.util.IOUtil;

import java.io.OutputStream;

@Data
@Slf4j
public class InspectorTrip {

    private final OSRMTripResponse response;

    @SneakyThrows
    public InspectorTrip(OSRMTripResponse osrmTripResponse) {
        this.response = osrmTripResponse;
        Geometry route = null;
        if (response.isOk())
            route = response.getTrips().get(0).getParsedGeometry();
        if (route != null) {
            log.info("route: {}", route);
        }

    }
}

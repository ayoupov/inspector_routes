package org.panaggelica.inspector_routes.model.response;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.panaggelica.inspector_routes.model.oati.Inspector;
import org.panaggelica.inspector_routes.model.osrm.OSRMTripResponse;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
public class Response {

    Map<Integer, Trip> trips;

    @SneakyThrows
    public Response(Map<Inspector, OSRMTripResponse> inspectorTrips) {
        Map<Integer, Trip> trips = inspectorTrips.entrySet().stream()
                .collect(Collectors.toMap(
                        entry ->
                                entry.getKey().getId(),
                        entry ->
                        {
                            Geometry route = null;
                            OSRMTripResponse osrmTripResponse = entry.getValue();
                            if (osrmTripResponse.isOk())
                                route = osrmTripResponse.getTrips().get(0).getParsedGeometry();
                            if (route != null) {
                                log.info("route: {}", route);
                            }
                            return new Trip(route);
                        }
                ));
        setTrips(trips);
    }
}

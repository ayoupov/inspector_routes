package org.panaggelica.inspector_routes.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.geotools.feature.FeatureCollection;
import org.locationtech.jts.geom.Point;
import org.panaggelica.inspector_routes.model.InspectorTrip;
import org.panaggelica.inspector_routes.model.oati.Inspectorates;
import org.panaggelica.inspector_routes.model.osrm.OSRMTripResponse;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TripProcessorImpl implements TripProcessor {

    @Autowired
    FeatureProcessor featureProcessor;

    @Value("${osrm.server}")
    String osrmPath;

    ObjectMapper objectMapper = IOUtil.objectMapper;

    @Override
    public InspectorTrip getTrip(FeatureCollection features, List<Inspectorates> inspectorates, RoutingOptions options) throws JsonProcessingException, DBSCANClusteringException {
        int size = features.size();
        log.info("all features size {} ", size);

        // combine `features` in compact batch with `options`
        List<Point> points = featureProcessor.process(features, options);

        String coords = points.stream()
                .map(Point::getCoordinate)
                .map(c -> c.x + "," + c.y).collect(Collectors.joining(";"));

        log.info("coords: {}", coords);

        // do connection to osrm

        WebClient client = WebClient.create(osrmPath);
        String tripURI = buildTripURI(options, coords);
        log.info("tripURI: {}", tripURI);

        String response = client
                .get()
                .uri(URI.create(tripURI))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        OSRMTripResponse osrmTripResponse = objectMapper.readValue(response, OSRMTripResponse.class);

        log.info("response: {}", response);

        // wait

        InspectorTrip trip = new InspectorTrip(osrmTripResponse);

        return trip;
    }

    private String buildTripURI(RoutingOptions options, String coords) {
        String uri = osrmPath + "/trip/v1/driving/" + coords + "?geometries=geojson"; // fixme: use options
        return uri;
    }

}

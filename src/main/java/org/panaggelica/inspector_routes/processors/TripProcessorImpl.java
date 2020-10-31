package org.panaggelica.inspector_routes.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.geotools.feature.FeatureCollection;
import org.locationtech.jts.geom.Point;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.model.oati.Inspector;
import org.panaggelica.inspector_routes.model.oati.Inspectorate;
import org.panaggelica.inspector_routes.model.osrm.OSRMTripResponse;
import org.panaggelica.inspector_routes.model.response.Response;
import org.panaggelica.inspector_routes.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TripProcessorImpl implements TripProcessor {

    @Autowired
    FeatureProcessor featureProcessor;

    @Value("${osrm.server}")
    String osrmPath;

    ObjectMapper objectMapper = IOUtil.objectMapper;

    WebClient client;

    @PostConstruct
    void init() {
        client = WebClient.create(osrmPath);
    }

    @Override
    public Response getTrip(FeatureCollection features, List<Inspectorate> inspectorates, RoutingOptions options) throws JsonProcessingException, DBSCANClusteringException {
        int size = features.size();
        log.info("all features size {} ", size);

        // combine `features` in compact batch with `options`
        featureProcessor.process(features, inspectorates, options);

        Map<Inspector, OSRMTripResponse> responseMap = new LinkedHashMap<>();

        for (Inspectorate inspectorate : inspectorates) {
            List<Inspector> inspectors = inspectorate.getInspectors();
            for (Inspector inspector : inspectors) {

                List<Point> points = featureProcessor.getCoords(inspectorate, inspector, options);
                if (points.isEmpty())
                    continue;
                String coords = points.stream()
                        .map(Point::getCoordinate)
                        .map(c -> c.x + "," + c.y).collect(Collectors.joining(";"));
                log.info("coords: {}", coords);

                // do connection to osrm
                OSRMTripResponse osrmTripResponse = getOSRMTrip(options, coords);
                responseMap.put(inspector, osrmTripResponse);
            }
        }

        Response response = new Response(responseMap);

        return response;
    }

    @SneakyThrows
    private OSRMTripResponse getOSRMTrip(RoutingOptions options, String coords) {
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

        return osrmTripResponse;
    }

    private String buildTripURI(RoutingOptions options, String coords) {
        String uri = osrmPath + "/trip/v1/driving/" + coords + "?geometries=geojson"; // fixme: use options
        return uri;
    }

}

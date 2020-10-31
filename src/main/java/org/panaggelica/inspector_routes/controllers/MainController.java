package org.panaggelica.inspector_routes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.panaggelica.inspector_routes.model.InspectorTrip;
import org.panaggelica.inspector_routes.model.oati.Inspectorates;
import org.panaggelica.inspector_routes.model.RoutingOptions;
import org.panaggelica.inspector_routes.processors.TripProcessor;
import org.panaggelica.inspector_routes.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/")
@Slf4j
public class MainController {

    @Autowired
    private TripProcessor tripProcessor;

    ObjectMapper objectMapper = IOUtil.objectMapper;

    @SneakyThrows
    @PostMapping(path = "/trip", produces = MediaType.APPLICATION_JSON_VALUE)
    public InspectorTrip getTrip(@RequestBody ObjectNode objectNode) throws IOException {
        String objectsJSON = objectNode.get("objects").toString();
        String inspectoratesJSON = objectNode.get("inspectorates").toString();
        String optionsJSON = objectNode.get("options").toString();

        FeatureJSON io = new FeatureJSON();
        FeatureCollection objects = io.readFeatureCollection(objectsJSON);

        List<Inspectorates> inspectorates = objectMapper.readValue(inspectoratesJSON, List.class);

        RoutingOptions options = objectMapper.readValue(optionsJSON, RoutingOptions.class);

        log.info("objects: {}", objects);
        log.info("inspectorates: {}", inspectorates);
        log.info("options: {}", options);

        return tripProcessor.getTrip(objects, inspectorates, options);
    }

}

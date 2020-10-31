package org.panaggelica.inspector_routes.model.oati;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import lombok.SneakyThrows;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class Inspectorate {

    int id;

    @Nullable
    String name;

    ArrayNode location;

    List<Inspector> inspectors;

    @SneakyThrows
    @JsonIgnore
    public Point getParsedLocation() {
        return new Point(
                new Coordinate(location.get(0).doubleValue(), location.get(1).doubleValue()),
                new PrecisionModel(), 4326);

    }
}

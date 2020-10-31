package org.panaggelica.inspector_routes.model.response;

import lombok.Data;
import org.locationtech.jts.geom.Geometry;

@Data
public class Trip {

    private Geometry route;

    public Trip(Geometry route) {

        this.route = route;
    }
}

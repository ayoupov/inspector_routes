package org.panaggelica.inspector_routes.model.osrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class OSRMTripResponse {
    // {"code":"Ok",
    String code;
    // "trips":
    List<OSRMTrip> trips;
    List<OSRMWaypoint> waypoints;
    // []}

    @JsonIgnore
    public boolean isOk() {
        return code != null && code.toLowerCase().equals("ok");
    }

}

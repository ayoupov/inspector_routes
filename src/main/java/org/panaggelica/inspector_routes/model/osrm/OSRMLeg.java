package org.panaggelica.inspector_routes.model.osrm;

import lombok.Data;

import java.util.List;

@Data
public class OSRMLeg {
    //"steps":[],"distance":6612.9,"duration":4768.6,"summary":"","weight":4768.6
    List<OSRMStep> steps;
    double distance;
    double duration;
    String summary;
    double weight;
}

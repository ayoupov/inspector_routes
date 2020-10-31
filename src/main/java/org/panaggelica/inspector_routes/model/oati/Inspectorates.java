package org.panaggelica.inspector_routes.model.oati;

import lombok.Data;
import org.opengis.geometry.primitive.Point;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
public class Inspectorates {

    int id;

    @Nullable
    String name;

    Point location;

    List<Inspector> inspectors;
}

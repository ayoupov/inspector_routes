package org.panaggelica.inspector_routes.model.oati;

import lombok.Data;
import org.panaggelica.inspector_routes.model.PreferredMode;
import org.springframework.lang.Nullable;

@Data
public class Inspector {

    int id;

    @Nullable
    String name;

    PreferredMode preferredMode;
}

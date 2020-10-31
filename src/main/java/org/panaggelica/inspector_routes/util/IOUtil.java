package org.panaggelica.inspector_routes.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.n52.jackson.datatype.jts.JtsModule;

public class IOUtil {

    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JtsModule());
    }
}

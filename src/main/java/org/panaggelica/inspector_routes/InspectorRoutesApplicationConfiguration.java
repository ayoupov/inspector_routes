package org.panaggelica.inspector_routes;

import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InspectorRoutesApplicationConfiguration {

    @Bean
    public JtsModule getJtsModule() {
        return new JtsModule();
    }
}

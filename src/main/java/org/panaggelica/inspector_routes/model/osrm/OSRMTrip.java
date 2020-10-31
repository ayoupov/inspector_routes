package org.panaggelica.inspector_routes.model.osrm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.SneakyThrows;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.locationtech.jts.geom.Geometry;

import java.util.List;

@Data
public class OSRMTrip {

    @JsonIgnore
    FeatureJSON io = new FeatureJSON();

    // {"geometry":
    // {"coordinates":[[37.679575,55.768616],[37.679576,55.768616],[37.685328,55.775322],[37.705905,55.787026],[37.709901,55.793243],[37.71575,55.795803],[37.749064,55.802056],[37.753145,55.805878],[37.764128,55.808798],[37.760617,55.813164],[37.760751,55.813208],[37.76963,55.816932],[37.786296,55.820504],[37.808417,55.823293],[37.808419,55.823305],[37.76824,55.825229],[37.763918,55.826834],[37.761022,55.825655],[37.753395,55.828014],[37.712662,55.826404],[37.695671,55.832699],[37.691833,55.831522],[37.681125,55.834909],[37.663266,55.835027],[37.657144,55.836577],[37.64679,55.842709],[37.635987,55.845221],[37.633988,55.849076],[37.626123,55.853417],[37.624991,55.857882],[37.58712,55.869862],[37.571121,55.869397],[37.558356,55.87369],[37.553641,55.87292],[37.547994,55.875035],[37.547999,55.875036],[37.541882,55.886248],[37.522121,55.89408],[37.52212,55.894097],[37.531911,55.88942],[37.529247,55.870745],[37.531853,55.867115],[37.524727,55.86114],[37.531083,55.858461],[37.530548,55.853882],[37.542109,55.844674],[37.542138,55.844678],[37.555053,55.847227],[37.546844,55.852291],[37.546804,55.852316],[37.513407,55.866353],[37.505342,55.861982],[37.492285,55.859699],[37.492279,55.859698],[37.493632,55.855433],[37.48894,55.850976],[37.488793,55.847622],[37.492285,55.842013],[37.487499,55.839447],[37.487543,55.839447],[37.504586,55.837008],[37.504977,55.831051],[37.504999,55.831022],[37.50339,55.832976],[37.492456,55.8323],[37.496049,55.82673],[37.493808,55.825592],[37.499316,55.818068],[37.49935,55.818023],[37.507589,55.807805],[37.51128,55.806869],[37.517472,55.808357],[37.517476,55.808362],[37.523603,55.810142],[37.540052,55.805828],[37.540057,55.80583],[37.523497,55.795415],[37.519244,55.795006],[37.518762,55.793479],[37.52356,55.792622],[37.523607,55.792553],[37.522027,55.789247],[37.524238,55.786322],[37.521072,55.784682],[37.521072,55.784682],[37.530606,55.782263],[37.533535,55.782863],[37.533772,55.782814],[37.533887,55.782831],[37.533895,55.78283],[37.543559,55.779897],[37.545392,55.780626],[37.550616,55.777675],[37.550638,55.777681],[37.559559,55.779488],[37.56828,55.784904],[37.57126,55.789744],[37.569427,55.791911],[37.569425,55.791923],[37.587058,55.785274],[37.585555,55.776375],[37.623672,55.752683],[37.631687,55.725731],[37.631691,55.725731],[37.631049,55.727625],[37.652426,55.740733],[37.656156,55.750745],[37.661833,55.751322],[37.670132,55.755792],[37.676952,55.768445],[37.679575,55.768616]],"type":"LineString"}
    // ,"legs":[
    // {"steps":[],"distance":0.1,"duration":0.1,"summary":"","weight":0.1},{"steps":[],"distance":8577.8,"duration":6176.3,"summary":"","weight":6176.3},{"steps":[],"distance":22,"duration":15.9,"summary":"","weight":15.9},{"steps":[],"distance":3336,"duration":2405.8,"summary":"","weight":2405.8},{"steps":[],"distance":1.3,"duration":1,"summary":"","weight":1},{"steps":[],"distance":19962.9,"duration":14389.7,"summary":"","weight":14389.7},{"steps":[],"distance":0.3,"duration":0.3,"summary":"","weight":0.3},{"steps":[],"distance":3138,"duration":2261.2,"summary":"","weight":2261.2},{"steps":[],"distance":1.9,"duration":1.4,"summary":"","weight":1.4},{"steps":[],"distance":6885.9,"duration":4960.1,"summary":"","weight":4960.1},{"steps":[],"distance":1.9,"duration":1.3,"summary":"","weight":1.3},{"steps":[],"distance":1676,"duration":1210.5,"summary":"","weight":1210.5},{"steps":[],"distance":3.7,"duration":2.7,"summary":"","weight":2.7},{"steps":[],"distance":4192.3,"duration":3024.4,"summary":"","weight":3024.4},{"steps":[],"distance":0.4,"duration":0.3,"summary":"","weight":0.3},{"steps":[],"distance":2759.4,"duration":1992.8,"summary":"","weight":1992.8},{"steps":[],"distance":2.7,"duration":2,"summary":"","weight":2},{"steps":[],"distance":1863.2,"duration":1341.9,"summary":"","weight":1341.9},{"steps":[],"distance":3.5,"duration":2.5,"summary":"","weight":2.5},{"steps":[],"distance":3004.7,"duration":2163.4,"summary":"","weight":2163.4},{"steps":[],"distance":5.4,"duration":3.9,"summary":"","weight":3.9},{"steps":[],"distance":2127.3,"duration":1532,"summary":"","weight":1532},{"steps":[],"distance":0.6,"duration":0.4,"summary":"","weight":0.4},{"steps":[],"distance":1656.7,"duration":1192.9,"summary":"","weight":1192.9},{"steps":[],"distance":0.4,"duration":0.3,"summary":"","weight":0.3},{"steps":[],"distance":2534.1,"duration":1826.3,"summary":"","weight":1826.3},{"steps":[],"distance":11.3,"duration":8.2,"summary":"","weight":8.2},{"steps":[],"distance":1046.5,"duration":755.4,"summary":"","weight":755.4},{"steps":[],"distance":0,"duration":0,"summary":"","weight":0},{"steps":[],"distance":887.5,"duration":639.2,"summary":"","weight":639.2},{"steps":[],"distance":26.1,"duration":19,"summary":"","weight":19},{"steps":[],"distance":8.9,"duration":6.4,"summary":"","weight":6.4},{"steps":[],"distance":0.5,"duration":0.4,"summary":"","weight":0.4},{"steps":[],"distance":1395.4,"duration":1004.5,"summary":"","weight":1004.5},{"steps":[],"distance":1.7,"duration":1.3,"summary":"","weight":1.3},{"steps":[],"distance":2681.9,"duration":1934.5,"summary":"","weight":1934.5},{"steps":[],"distance":1.3,"duration":1,"summary":"","weight":1},{"steps":[],"distance":9508.7,"duration":6848.5,"summary":"","weight":6848.5},{"steps":[],"distance":0.3,"duration":0.2,"summary":"","weight":0.2},{"steps":[],"distance":6613.1,"duration":4768.7,"summary":"","weight":4768.7}],"distance":83941.7,"duration":60496.7,"weight_name":"duration","weight":60496.7}],
    // "waypoints":[]
    ObjectNode geometry;
    List<OSRMLeg> legs;
    //"distance":28490.6,"duration":20541.8,"weight_name":"duration","weight":20541.8
    double distance;
    double duration;
    String weight_name;
    double weight;

    @SneakyThrows
    @JsonIgnore
    public Geometry getParsedGeometry() {
        GeometryJSON io = new GeometryJSON();
        return io.read(geometry.toString());
    }
}

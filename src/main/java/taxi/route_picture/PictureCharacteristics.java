package taxi.route_picture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PictureCharacteristics {
    private Integer zoom;
    private float longitudePerZoom;
    private float latitudePerZoom;
    private GeoPoint center;
}

package taxi.route_picture;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeoPoint {

    private float latitude;
    private float longitude;

    @Override
    public String toString() {
        return longitude + "," + latitude;
    }
}

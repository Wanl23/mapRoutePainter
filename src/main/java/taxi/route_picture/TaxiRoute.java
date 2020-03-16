package taxi.route_picture;

import lombok.Data;

import java.util.ArrayList;
import java.util.Comparator;

@Data
public class TaxiRoute {
    private ArrayList<GeoPoint> geoPoints = new ArrayList<>();

    private float minLongitude;
    private float maxLongitude;

    private float minLatitude;
    private float maxLatitude;

    private GeoPoint center;

    public TaxiRoute(ArrayList<GeoPoint> geoPoints) {
        this.geoPoints = geoPoints;
        this.minLongitude = geoPoints.stream().min(Comparator.
                comparing(GeoPoint::getLongitude)).get().getLongitude();
        this.maxLongitude = geoPoints.stream().max(Comparator.
                comparing(GeoPoint::getLongitude)).get().getLongitude();
        this.minLatitude = geoPoints.stream().min(Comparator.
                comparing(GeoPoint::getLatitude)).get().getLatitude();
        this.maxLatitude = geoPoints.stream().max(Comparator.
                comparing(GeoPoint::getLatitude)).get().getLatitude();
        float centerLat = maxLatitude - ((maxLatitude - minLatitude) / 2);
        float centerLong = maxLongitude - ((maxLongitude - minLongitude) / 2);
        center = new GeoPoint(centerLat, centerLong);
    }
}

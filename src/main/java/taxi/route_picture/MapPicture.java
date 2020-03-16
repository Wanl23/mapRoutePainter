package taxi.route_picture;

import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MapPicture {

    private static int picture_width = 1242;
    private static int picture_height = 1080;

    //18 60.636670 - 60.640000 - 60.643330 00.003330
    //18 56.828420 - 56.830000 - 56.831580 00.001580

    //17 w 60.635 - 60.640000 - 60.645 00.006660
    //17 h 56.827 - 56.830000 - 56.833 00.003160

    //16 w 60.629 - 60.640000 - 60.651 00.013320
    //16 h 56.824 - 56.830000 - 56.836 00.006320

    //15 w 60.618 - 60.640000 - 60.662 00.026640
    //15 h 56.817 - 56.830000 - 56.843 00.012640

    //14 w 60.596 - 60.640000 - 60.685 00.053280
    //14 h 56.802 - 56.830000 - 56.857 00.025280

    //13 w 60.553 - 60.640000 - 60.727 00.106560
    //13 h 56.774 - 56.830000 - 56.884 00.050560

    //12 w 60.470 - 60.640000 - 60.810 00.213120
    //12 h 56.720 - 56.830000 - 56.940 00.101020

    //11 w 60.300 - 60.640000 - 60.980
    //11 h 56.610 - 56.830000 - 57.050

    //10 w 59.070 - 60.640000 - 61.310
    //10 h 56.390 - 56.830000 - 57.270

    //10 w 59.070 - 60.640000 - 61.310
    //10 h 56.390 - 56.830000 - 57.270

    public static void get2gisMap(String center, String zoom) throws IOException {
        String gisUrl = "http://static.maps.2gis.com/1.0?";
        UriComponentsBuilder urlBuilder = UriComponentsBuilder
                .fromUriString(gisUrl)
                .queryParam("center", center)
                .queryParam("zoom", zoom)
                .queryParam("size", picture_width + "," + picture_height);
        URL url = new URL(urlBuilder.toUriString());
        BufferedImage img = ImageIO.read(url);
        File file = new File("downloaded.png");
        ImageIO.write(img, "png", file);
    }

    public static void main(String[] args) throws IOException {
        GeoPoint geoPoint1 = new GeoPoint(56.830463f, 60.640659f);
        GeoPoint geoPoint2 = new GeoPoint(56.830648f, 60.64068f);
        GeoPoint geoPoint3 = new GeoPoint(56.830792f, 60.636049f);
        GeoPoint geoPoint4 = new GeoPoint(56.830143f, 60.630207f);
        GeoPoint geoPoint5 = new GeoPoint(56.828699f, 60.616924f);
        GeoPoint geoPoint6 = new GeoPoint(56.835361f, 60.613892f);

        ArrayList<GeoPoint> geoPoints = new ArrayList<>();

        geoPoints.add(geoPoint1);
        geoPoints.add(geoPoint2);
        geoPoints.add(geoPoint3);
        geoPoints.add(geoPoint4);
        geoPoints.add(geoPoint5);
        geoPoints.add(geoPoint6);

        TaxiRoute taxiRoute = new TaxiRoute(geoPoints);

        System.out.println("getCenter " + taxiRoute.getCenter());
        System.out.println("getMaxLatitude " + taxiRoute.getMaxLatitude());
        System.out.println("getMaxLongitude " + taxiRoute.getMaxLongitude());
        System.out.println("getMinLatitude " + taxiRoute.getMinLatitude());
        System.out.println("getMinLongitude " + taxiRoute.getMinLongitude());

        Integer zoom = getZoom(taxiRoute);
        System.out.println("zoom " + zoom);

        get2gisMap(taxiRoute.getCenter().toString(), zoom.toString());
    }

    public static int getZoom(TaxiRoute taxiRoute) {
        int maxZoom = 18;
        float marginInPicture = 0.9f;
        float longitudePerZoom = 00.003330f;
        float latitudePerZoom = 00.001580f;

        GeoPoint center = taxiRoute.getCenter();

        float routMaxLat = taxiRoute.getMaxLatitude();
        float routMinLat = taxiRoute.getMinLatitude();

        float routMaxLong = taxiRoute.getMaxLongitude();
        float routMinLong = taxiRoute.getMinLongitude();

        for (int i = maxZoom; i > 0; i--) {
            float zoomMaxLat = center.getLatitude() + latitudePerZoom * marginInPicture;
            float zoomMinLat = center.getLatitude() - latitudePerZoom * marginInPicture;

            boolean positionLatFitZoomLat = zoomMaxLat > routMaxLat && zoomMinLat < routMinLat;

            float zoomMaxLong = center.getLongitude() + longitudePerZoom * marginInPicture;
            float zoomMinLong = center.getLongitude() - longitudePerZoom * marginInPicture;

            boolean positionLongFitZoomLong = zoomMaxLong > routMaxLong && zoomMinLong < routMinLong;

            if (positionLatFitZoomLat && positionLongFitZoomLong) {
                return i;
            }
            latitudePerZoom*=2;
            longitudePerZoom*=2;
        }
        return 1;
    }
}

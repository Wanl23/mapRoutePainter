package taxi.route_picture;

import ij.ImagePlus;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MapPicture {

    private static int picture_width = 1242;
    private static int picture_height = 1080;

    public static void main(String[] args) throws IOException {

        process(takeGeo());
    }

    private static TaxiRoute takeGeo() {
        GeoPoint geoPoint1 = new GeoPoint(56.830463f, 60.640659f);
        GeoPoint geoPoint2 = new GeoPoint(56.830648f, 60.64068f);
        GeoPoint geoPoint3 = new GeoPoint(56.830792f, 60.636049f);
        GeoPoint geoPoint4 = new GeoPoint(56.830143f, 60.630207f);
        GeoPoint geoPoint5 = new GeoPoint(56.828699f, 60.616924f);
        GeoPoint geoPoint6 = new GeoPoint(56.835361f, 60.613892f);

        GeoPoint geoPoint7 = new GeoPoint(56.834105f, 60.600256f);
        GeoPoint geoPoint8 = new GeoPoint(56.837809f, 60.598804f);
        GeoPoint geoPoint9 = new GeoPoint(56.841301f, 60.631734f);
        GeoPoint geoPoint10 = new GeoPoint(56.830766f, 60.636141f);
        ArrayList<GeoPoint> geoPoints = new ArrayList<>();
        geoPoints.add(geoPoint1);
        geoPoints.add(geoPoint2);
        geoPoints.add(geoPoint3);
        geoPoints.add(geoPoint4);
        geoPoints.add(geoPoint5);
        geoPoints.add(geoPoint6);
        geoPoints.add(geoPoint7);
        geoPoints.add(geoPoint8);
        geoPoints.add(geoPoint9);
        geoPoints.add(geoPoint10);

        return new TaxiRoute(geoPoints);
    }

    private static void process(TaxiRoute taxiRoute) throws IOException {
        PictureCharacteristics pictureCharacteristics = getPictureCharactersFromRoute(taxiRoute);
        BufferedImage bufferedImage = get2gisMap(taxiRoute.getCenter().toString(), pictureCharacteristics.getZoom().toString());

        ArrayList<GeoPoint> geoPoints = taxiRoute.getGeoPoints();
        for (int i = 0; i < geoPoints.size() - 1; i++) {
            PixelCoords pixelCoords1 = convertCoordinates(geoPoints.get(i), pictureCharacteristics);
            PixelCoords pixelCoords2 = convertCoordinates(geoPoints.get(i + 1), pictureCharacteristics);
            bufferedImage = print(bufferedImage, pixelCoords1, pixelCoords2);
        }
        File file = new File("new.png");
        ImageIO.write(bufferedImage, "png", file);
    }

    private static BufferedImage get2gisMap(String center, String zoom) throws IOException {
        String gisUrl = "http://static.maps.2gis.com/1.0?";
        UriComponentsBuilder urlBuilder = UriComponentsBuilder
                .fromUriString(gisUrl)
                .queryParam("center", center)
                .queryParam("zoom", zoom)
                .queryParam("size", picture_width + "," + picture_height);
        URL url = new URL(urlBuilder.toUriString());
        return ImageIO.read(url);
    }

    private static PixelCoords convertCoordinates(GeoPoint geoPoint, PictureCharacteristics pictureCharacteristics) throws IOException {
        GeoPoint center = pictureCharacteristics.getCenter();
        float latitudePerZoom = pictureCharacteristics.getLatitudePerZoom();
        float longitudePerZoom = pictureCharacteristics.getLongitudePerZoom();
        float pixelLatCenter = picture_height / 2;
        float pixelLongCenter = picture_width / 2;
        float coefficientLat = latitudePerZoom / picture_height;
        float coefficientLong = longitudePerZoom / picture_width;

        float lon = ((geoPoint.getLongitude() - center.getLongitude()) / coefficientLong);
        if (lon > 0) lon += pixelLongCenter;
        else if (lon == 0) lon = pixelLongCenter;
        else lon = pixelLongCenter - lon * -1;

        float lat = ((center.getLatitude() - geoPoint.getLatitude()) / coefficientLat);
        if (lat > 0) lat += pixelLatCenter;
        else if (lat == 0 ) lat = pixelLatCenter;
        else lat = picture_height - pixelLatCenter - lat * -1;

        return new PixelCoords(lon, lat);
    }

    private static PictureCharacteristics getPictureCharactersFromRoute(TaxiRoute taxiRoute) {
        int maxZoom = 18;
        float marginInPicture = 0.9f;
        float longitudeFromCenterToLine = 00.003330f;
        float latitudeFromCenterToLine = 00.001580f;

        GeoPoint center = taxiRoute.getCenter();

        float routMaxLat = taxiRoute.getMaxLatitude();
        float routMinLat = taxiRoute.getMinLatitude();

        float routMaxLong = taxiRoute.getMaxLongitude();
        float routMinLong = taxiRoute.getMinLongitude();

        for (int i = maxZoom; i > 0; i--) {
            float zoomMaxLong = center.getLongitude() + longitudeFromCenterToLine * marginInPicture;
            float zoomMinLong = center.getLongitude() - longitudeFromCenterToLine * marginInPicture;

            boolean positionLongFitZoomLong = zoomMaxLong > routMaxLong && zoomMinLong < routMinLong;

            float zoomMaxLat = center.getLatitude() + latitudeFromCenterToLine * marginInPicture;
            float zoomMinLat = center.getLatitude() - latitudeFromCenterToLine * marginInPicture;

            boolean positionLatFitZoomLat = zoomMaxLat > routMaxLat && zoomMinLat < routMinLat;

            if (positionLongFitZoomLong && positionLatFitZoomLat) {
                return new PictureCharacteristics(i,
                        longitudeFromCenterToLine * 2,
                        latitudeFromCenterToLine * 2, center);
            }
            longitudeFromCenterToLine *= 2;
            latitudeFromCenterToLine *= 2;
        }
        return new PictureCharacteristics(1,
                longitudeFromCenterToLine * 2,
                latitudeFromCenterToLine * 2, center);
    }

    private static BufferedImage print(BufferedImage bufferedImage, PixelCoords pixelCoords1, PixelCoords pixelCoords2) {
        Graphics2D graphics = (Graphics2D)bufferedImage.getGraphics();
        graphics.setColor(Color.BLUE);
        graphics.setStroke(new BasicStroke(10));
        graphics.drawLine(pixelCoords1.x, pixelCoords1.y, pixelCoords2.x, pixelCoords2.y);
        return bufferedImage;
    }
}

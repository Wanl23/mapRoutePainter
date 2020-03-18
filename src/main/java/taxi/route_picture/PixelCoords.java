package taxi.route_picture;

import lombok.Data;

@Data
public class PixelCoords {
    int x;
    int y;

    public PixelCoords(float x, float y) {
        this.x = (int) x;
        this.y = (int) y;
    }
}

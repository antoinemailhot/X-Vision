package xvision.model;

import java.awt.Rectangle;

public class Crop {
    private final Rectangle zone;

    public Crop(Rectangle rect) {
        this.zone = rect != null ? new Rectangle(rect) : null;
    }

    public Rectangle getZone() {
        return zone != null ? new Rectangle(zone) : null;
    }

    @Override
    public String toString() {
        return zone != null ? "Crop" + zone.toString() : "Crop(null)";
    }
}

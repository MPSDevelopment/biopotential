package vis;

public class Viewport {
    public Viewport(int x0, int x1, int targetW, int targetH,
                    double padding) {
        this.x0 = x0;
        this.y0 = 0;
        this.x1 = x1;
        this.y1 = 0;
        this.targetW = targetW;
        this.targetH = targetH;
        this.padding = padding;
        this.onlyX = true;
    }

    public Viewport(int x0, int y0,
                    int x1, int y1,
                    int targetW, int targetH,
                    double padding) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.targetW = targetW;
        this.targetH = targetH;
        this.padding = padding;
        this.onlyX = false;
    }

    public int getX0() {
        return x0;
    }
    public int getY0() throws ViewportException {
        if (onlyX) {
            throw new ViewportException("Viewport::getY0 on X-only viewport");
        }
        return y0;
    }
    public int getX1() {
        return x1;
    }
    public int getY1() throws ViewportException {
        if (onlyX) {
            throw new ViewportException("Viewport::getY1 on X-only viewport");
        }
        return y1;
    }
    public int getTargetW() {
        return targetW;
    }
    public int getTargetH() {
        return targetH;
    }
    public double getPadding() {
        return padding;
    }

    private final int x0;
    private final int x1;
    private final int y0;
    private final int y1;
    private final int targetH;
    private final int targetW;
    private final double padding;
    private final boolean onlyX;
}

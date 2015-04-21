package org.yolo.etienne.strobbe.transfertchaleur.tools;

/**
 * @author Etienne Strobbe
 */
public class Tuples<X, Y> {
    private X x;
    private Y y;

    public Tuples(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public Y getY() {
        return y;
    }

    public X getX() {
        return x;
    }
}

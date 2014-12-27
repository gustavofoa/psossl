package br.inpe.psossl.model;

public class Container {

    private final double w;
    private final double h;

    public Container(double w, double h) {
            super();
            this.w = w;
            this.h = h;
    }

    public double getWidth() {
            return w;
    }

    public double getHeight() {
            return h;
    }

    public boolean validateParams() {
        if(getHeight() <= 0 || getWidth() <= 0)
            return false;
        return true;
    }

}

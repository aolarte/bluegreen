package com.andresolarte.bluegreen.model;

public class Versions {
    private int blue;
    private  int green;

    public Versions() {

    }

    public Versions(int blue, int green) {
        this.blue = blue;
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Versions{");
        sb.append("blue=").append(blue);
        sb.append(", green=").append(green);
        sb.append('}');
        return sb.toString();
    }
}

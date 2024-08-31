package com.sd38.gymtiger.model;

public class ShippingCalculator {
    private double weight;
    private double distance;

    public ShippingCalculator(double weight, double distance) {
        this.weight = weight;
        this.distance = distance;
    }

    public double calculateShipCost() {
        double ratePerKg = 20000.0;
        double ratePerKm = 1200.0;
        double baseCost = 30000.0;
        double weightCost = weight*ratePerKg;
        double disCost = distance * ratePerKm;
        double shipCost = weightCost + disCost;
        return shipCost;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static void main(String[] args) {
        ShippingCalculator ship = new ShippingCalculator(1.0, 50.0);
        double cost = ship.calculateShipCost();
        System.out.println("The ship cost is " + cost + "VND");
    }
}

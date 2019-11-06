package com.example.taxiapp;

import android.location.Location;

public class TaxiManager {

    private Location destinationLocation;

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public float returnDistanceInMeters(Location currentLocation) {

        if (currentLocation != null && destinationLocation != null) {
            return currentLocation.distanceTo(destinationLocation);
        } else {
            return -100.0f;
        }


    }

    public String returnDistanceInMiles(Location currentLocation, int meterPerMile) {

        int miles = (int) (returnDistanceInMeters(currentLocation) / meterPerMile);

        if (miles == 1) {
            return "1 mile ";
        } else if (miles > 1) {
            return miles + "Miles";
        } else {
            return "NO Mile";
        }

    }

    public String timeToReachDestination(Location currentLocation, float milesPerHour, int metersPerMile) {

        float distanceInMeters = returnDistanceInMeters(currentLocation);

        float timeLeft = distanceInMeters / (milesPerHour * metersPerMile);

        int timeLeftHour = (int) timeLeft;

        int minutesLeft = (int)((timeLeft - timeLeftHour) * 60);

        return "Time to reach : " + timeLeftHour + ":"+minutesLeft;

    }

}

package pw.react.backend.utils;

import static java.lang.Math.*;

public class Utils {
    public static Boolean roleAdminOrUser(String userRole){
        if (userRole == null)
            return false;
        return userRole.equalsIgnoreCase("admin") || userRole.equalsIgnoreCase("user");
    }

    public static Boolean roleUser(String userRole){
        if (userRole == null)
            return false;
        return userRole.equalsIgnoreCase("user");
    }

    public static Boolean roleAdmin(String userRole){
        if (userRole == null)
            return false;
        return userRole.equalsIgnoreCase("admin");
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in kilometers (approximately)
        double earthRadius = 6371;

        // Convert latitude and longitude to radians
        lat1 = toRadians(lat1);
        lon1 = toRadians(lon1);
        lat2 = toRadians(lat2);
        lon2 = toRadians(lon2);

        // Calculate the differences
        double dLon = lon2 - lon1;
        double dLat = lat2 - lat1;

        // Apply Haversine formula
        double a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2) * sin(dLon / 2);
        double c = 2 * atan2(sqrt(a), sqrt(1 - a));

        // Calculate the distance
        return earthRadius * c;
    }
}

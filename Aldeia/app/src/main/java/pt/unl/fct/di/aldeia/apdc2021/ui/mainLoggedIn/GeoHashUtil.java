package pt.unl.fct.di.aldeia.apdc2021.ui.mainLoggedIn;

import ch.hsr.geohash.GeoHash;

public class GeoHashUtil {
    public static final int LP_HASH_LENGTH = 3;
    public static final int HP_HASH_LENGTH = 12;

    public static String convertCoordsToGeoHashHighPrecision(double latitude, double longitude) {
        return GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, HP_HASH_LENGTH);
    }

    public static String convertCoordsToGeoHashLowPrecision(double latitude, double longitude) {
        return GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, LP_HASH_LENGTH);
    }
}
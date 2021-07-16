package voluntier.util;

import ch.hsr.geohash.GeoHash;
import voluntier.exceptions.IllegalCoordinatesException;
//import ch.hsr.geohash.WGS84Point;
//import ch.hsr.geohash.queries.GeoHashCircleQuery;

//import com.spatial4j.core.io.GeohashUtils;

public class GeoHashUtil {
	public static final int LP_HASH_LENGTH = 3;
	public static final int HP_HASH_LENGTH = 12;
	
	public static void checkValidCoords(double latitude, double longitude) throws IllegalCoordinatesException {
		if(!isValidCoords(latitude, longitude))
			throw new IllegalCoordinatesException("Coordinates out of range");
	}
	
	public static boolean isValidCoords(double latitude, double longitude) {
		return latitude >= -90 && latitude <= 90 && longitude >= -180 || longitude <= 180;
	}

	public static String convertCoordsToGeoHashHighPrecision(double latitude, double longitude) throws IllegalCoordinatesException {
		checkValidCoords(latitude, longitude);
		//return GeohashUtils.encodeLatLon(latitude, longitude, GeohashUtils.MAX_PRECISION);
		return GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, HP_HASH_LENGTH);
	}
	
	public static String convertCoordsToGeoHashLowPrecision(double latitude, double longitude) throws IllegalCoordinatesException {
		checkValidCoords(latitude, longitude);
		return GeoHash.geoHashStringWithCharacterPrecision(latitude, longitude, LP_HASH_LENGTH);
		/*GeoHash.withCharacterPrecision(latitude, longitude, HASH_LENGTH ).toBase32();
		
		List<GeoHash> geohashes =  new GeoHashCircleQuery(new WGS84Point(latitude, longitude), radius).getSearchHashes();
		List<Long> res = new LinkedList<>();
		geohashes.forEach(geohash -> res.add(geohash.longValue()));
		
		return res;*/
		//radius /= 11;
		/*int precision = GeohashUtils.lookupHashLenForWidthHeight(radius, radius);
		LOG.severe("" + precision);
		String hash = GeohashUtils.encodeLatLon(latitude, longitude, Math.max(Math.min(precision, 24), 1));
		LOG.severe(hash);
		return hash;*/
	}
	
}

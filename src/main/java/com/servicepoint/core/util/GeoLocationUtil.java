package com.servicepoint.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeoLocationUtil {
    private static final Logger logger = LoggerFactory.getLogger(GeoLocationUtil.class);


    public static LocationResult getLocationFromIp(String ip) throws Exception {
        String apiUrl = "https://ipapi.co/" + ip + "/json/";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            // Log the raw JSON response for debugging
            logger.debug("GeoLocation API Response: {}", response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.toString());

            double latitude = json.has("latitude") ? json.get("latitude").asDouble() : 0.0;
            double longitude = json.has("longitude") ? json.get("longitude").asDouble() : 0.0;
            String city = json.has("city") ? json.get("city").asText() : "";
            String region = json.has("region") ? json.get("region").asText() : "";
            String country = json.has("country_name") ? json.get("country_name").asText() : "";

            return new LocationResult(latitude, longitude, city, region, country);
        }
    }

    public static class LocationResult {
        private double latitude;
        private double longitude;
        private String city;
        private String region;
        private String country;

        public LocationResult(double latitude, double longitude, String city, String region, String country) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.city = city;
            this.region = region;
            this.country = country;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCity() { return city; }
        public String getRegion() { return region; }
        public String getCountry() { return country; }
    }
}

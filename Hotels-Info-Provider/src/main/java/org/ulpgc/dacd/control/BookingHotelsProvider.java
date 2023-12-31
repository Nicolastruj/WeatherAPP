package org.ulpgc.dacd.control;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ulpgc.dacd.model.Hotel;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class BookingHotelsProvider implements HotelsProvider {

    @Override
    public List<Hotel> getHotels(String apiKey, String apiHost, String checkinDate, String checkoutDate,
                                 String adultsNumber, String childrensNumber, String childrensAge, String roomNumber,
                                 String islandName) throws MyHotelException {
        try {
            String destId = getDestId(islandName, apiKey, apiHost);
            System.out.println(destId);

            List<String> jsonHotelList = searchHotels(destId, checkinDate, checkoutDate, adultsNumber, childrensNumber, childrensAge, roomNumber);

            return convertJsonListToHotelList(jsonHotelList, checkinDate, checkoutDate);
        } catch (Exception e) {
            throw new MyHotelException("Error in call processing", e);
        }
    }
    public static String getDestId(String islandName, String apiKey, String apiHost) throws Exception {
        String name = islandName;
        String locale = "es";
        String url = "https://booking-com.p.rapidapi.com/v1/hotels/locations";
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("locale", locale);
        url = buildUrlWithParams1(url, params);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("X-RapidAPI-Key", apiKey);
        request.setHeader("X-RapidAPI-Host", apiHost);
        HttpResponse response = client.execute(request);
        String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
        return extractDestId(responseBody);
    }

    private static String buildUrlWithParams1(String baseUrl, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        if (!params.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Eliminar el último "&"
        }
        return urlBuilder.toString();
    }

    private static String extractDestId(String responseBody) {
        JsonArray jsonArray = JsonParser.parseString(responseBody).getAsJsonArray();

        if (jsonArray.size() > 0) {
            JsonObject firstObject = jsonArray.get(0).getAsJsonObject();
            if (firstObject.has("dest_id")) {
                return firstObject.get("dest_id").getAsString();
            }
        }
        return null;
    }
    public List<String> searchHotels(String destId, String checkinDate, String checkoutDate, String adultsNumber, String childrensNumber, String childrensAge, String roomNumber) {
        try {
            String apiKey = "77f0f6e079msh3c7c501786eb1d0p1f2305jsndf8f225650c8";
            String apiHost = "booking-com.p.rapidapi.com";//TODO que reciba la apikey y apihost como parametros
            String url = buildSearchUrl(destId, checkinDate, checkoutDate, adultsNumber, childrensNumber, childrensAge, roomNumber);
            HttpGet request = createHttpGetRequest(url, apiKey, apiHost);
            HttpResponse response = executeHttpRequest(request);
            String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());
            return processHotelSearchResponse(responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildSearchUrl(String destId, String checkinDate, String checkoutDate, String adultsNumber, String childrensNumber, String childrensAge, String roomNumber) {
        String baseUrl = "https://booking-com.p.rapidapi.com/v1/hotels/search";
        Map<String, String> params = new HashMap<>();
        params.put("units", "metric");
        params.put("dest_id", destId);
        params.put("dest_type", "region");
        params.put("room_number", roomNumber);
        params.put("checkin_date", checkinDate);
        params.put("checkout_date", checkoutDate);
        params.put("order_by", "popularity");
        params.put("locale", "es");
        params.put("adults_number", adultsNumber);
        params.put("filter_by_currency", "EUR");
        params.put("children_number", childrensNumber);
        params.put("children_ages", childrensAge);
        params.put("include_adjacency", "true");
        return buildUrlWithParams2(baseUrl, params);
    }

    private HttpGet createHttpGetRequest(String url, String apiKey, String apiHost) {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.setHeader("X-RapidAPI-Key", apiKey);
        request.setHeader("X-RapidAPI-Host", apiHost);
        return request;
    }

    private HttpResponse executeHttpRequest(HttpGet request) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        return client.execute(request);
    }

    private static List<String> processHotelSearchResponse(String responseBody) {
        List<String> hotelList = new ArrayList<>();
        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

        if (jsonResponse.has("result")) {
            JsonArray resultsArray = jsonResponse.getAsJsonArray("result");
            for (int i = 0; i < resultsArray.size(); i++) {
                JsonObject hotelObject = resultsArray.get(i).getAsJsonObject();
                hotelList.add(hotelObject.toString());
            }
        }

        return hotelList;
    }

    private static String buildUrlWithParams2(String baseUrl, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);

        if (!params.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1); // Eliminar el último "&"
        }

        return urlBuilder.toString();
    }
    public static List<Hotel> convertJsonListToHotelList(List<String> jsonList, String checkInDate, String checkOutDate) {
        List<Hotel> hotelList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String jsonString : jsonList) {
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                Hotel hotel = createHotelFromJsonNode(jsonNode, checkInDate, checkOutDate);
                hotelList.add(hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return hotelList;
    }

    private static Hotel createHotelFromJsonNode(JsonNode jsonNode, String checkIn, String checkOut) {
        String id = extractId(jsonNode);
        String name = extractName(jsonNode);
        String location = extractLocation(jsonNode);
        double totalPriceAfterTaxesAndDiscount = extractTotalPrice(jsonNode);
        double pricePerNightAfterTaxesAndDiscount = extractPricePerNight(jsonNode);
        double discountPercentageForOnlineBooking = extractDiscountPercentage(jsonNode);
        String review = extractReview(jsonNode);
        String reviewNumber = extractReviewNumber(jsonNode);
        String distanceToCenter = extractDistanceToCenter(jsonNode);
        String starsNumber = extractStarsNumber(jsonNode);
        boolean freeCancellation = extractFreeCancellation(jsonNode);
        List<String> services = extractServices(jsonNode);
        Instant ts = Instant.now();

        return new Hotel(id, name, location, totalPriceAfterTaxesAndDiscount,
                pricePerNightAfterTaxesAndDiscount, discountPercentageForOnlineBooking,
                review, reviewNumber, distanceToCenter, starsNumber, freeCancellation, services, checkIn, checkOut, ts, "Hotels-Info-Provider");
    }

    private static String extractId(JsonNode jsonNode) {
        return jsonNode.path("id").asText("");
    }

    private static String extractName(JsonNode jsonNode) {
        return jsonNode.path("hotel_name").asText("");
    }

    private static String extractLocation(JsonNode jsonNode) {
        return jsonNode.path("city").asText("");
    }

    private static double extractTotalPrice(JsonNode jsonNode) {
        JsonNode priceNode = jsonNode.path("composite_price_breakdown");
        return priceNode.path("gross_amount").asDouble(0.0);
    }

    private static double extractPricePerNight(JsonNode jsonNode) {
        JsonNode priceNode = jsonNode.path("composite_price_breakdown");
        return priceNode.path("gross_amount_per_night").asDouble(0.0);
    }

    private static double extractDiscountPercentage(JsonNode jsonNode) {
        JsonNode priceNode = jsonNode.path("composite_price_breakdown");
        JsonNode benefitsNode = priceNode.path("benefits");
        return benefitsNode.path(2).path("item_amount").path("value").asDouble(0.0);
    }

    private static String extractReview(JsonNode jsonNode) {
        return jsonNode.path("review_score_word").asText("");
    }

    private static String extractReviewNumber(JsonNode jsonNode) {
        return jsonNode.path("review_nr").asText("");
    }

    private static String extractDistanceToCenter(JsonNode jsonNode) {
        return jsonNode.path("distance_to_cc_formatted").asText("");
    }

    private static String extractStarsNumber(JsonNode jsonNode) {
        return jsonNode.path("starsNumber").asText("");
    }

    private static boolean extractFreeCancellation(JsonNode jsonNode) {
        return jsonNode.path("is_free_cancellable").asInt(0) == 1;
    }

    private static List<String> extractServices(JsonNode jsonNode) {
        JsonNode facilitiesNode = jsonNode.path("hotel_facilities");
        List<String> services = new ArrayList<>();
        if (facilitiesNode != null && facilitiesNode.isArray()) {
            for (JsonNode facility : facilitiesNode) {
                services.add(facility.asText(""));
            }
        }
        return services;
    }
}
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingHotelsProvider implements HotelsProvider {

    @Override
    public List<Hotel> getHotels(String islandName) {
        try {
            String destId = getDestId("GranCanaria");
            System.out.println(destId);
            List<String> jsonHotelList = searchHotels(destId, "2024-02-15", "2024-02-20", "1", "1");
            for (String hotel : jsonHotelList){
                System.out.println("Hotel Info: " + hotel);
            }
            List<Hotel> hotelList = convertJsonListToHotelList(jsonHotelList);
            return hotelList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String getDestId(String islandName) throws Exception {
        String apiKey = "77f0f6e079msh3c7c501786eb1d0p1f2305jsndf8f225650c8";
        String apiHost = "booking-com.p.rapidapi.com";
        String name = islandName;
        String locale = "es";

        // Construir la URL con parámetros
        String url = "https://booking-com.p.rapidapi.com/v1/hotels/locations";
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("locale", locale);
        url = buildUrlWithParams1(url, params);

        // Crear la solicitud HTTP
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // Configurar las cabeceras
        request.setHeader("X-RapidAPI-Key", apiKey);
        request.setHeader("X-RapidAPI-Host", apiHost);

        // Realizar la solicitud
        HttpResponse response = client.execute(request);

        String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());

        // Extraer dest_id del cuerpo de la respuesta
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
    public List<String> searchHotels(String destId, String checkinDate, String checkoutDate, String adultsNumber, String roomNumber) {
        try {
            String apiKey = "77f0f6e079msh3c7c501786eb1d0p1f2305jsndf8f225650c8";
            String apiHost = "booking-com.p.rapidapi.com";

            // Construir la URL con parámetros
            String url = "https://booking-com.p.rapidapi.com/v1/hotels/search";
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
            params.put("include_adjacency", "true");
            url = buildUrlWithParams2(url, params);

            // Crear la solicitud HTTP
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            // Configurar las cabeceras
            request.setHeader("X-RapidAPI-Key", apiKey);
            request.setHeader("X-RapidAPI-Host", apiHost);

            // Realizar la solicitud
            HttpResponse response = client.execute(request);
            String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity());

            // Procesar la respuesta y construir la lista de strings
            return processHotelSearchResponse(responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public static List<Hotel> convertJsonListToHotelList(List<String> jsonList) {
        List<Hotel> hotelList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String jsonString : jsonList) {
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                // Extract relevant information from jsonNode and create a Hotel object
                Hotel hotel = createHotelFromJsonNode(jsonNode);
                hotelList.add(hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return hotelList;
    }

    private static Hotel createHotelFromJsonNode(JsonNode jsonNode) {
        // Extract relevant fields from jsonNode and create a Hotel object
        String id = jsonNode.path("id").asText("");
        String name = jsonNode.path("hotel_name").asText("");
        String location = jsonNode.path("city").asText("");

        JsonNode priceNode = jsonNode.path("composite_price_breakdown");
        double totalPriceAfterTaxesAndDiscount = priceNode.path("gross_amount").asDouble(0.0);
        double pricePerNightAfterTaxesAndDiscount = priceNode.path("gross_amount_per_night").asDouble(0.0);

        JsonNode benefitsNode = priceNode.path("benefits");
        double discountPercentageForOnlineBooking = benefitsNode.path(2).path("item_amount").path("value").asDouble(0.0);

        String review = jsonNode.path("review_score_word").asText("");
        String reviewNumber = jsonNode.path("review_nr").asText("");
        String distanceToCenter = jsonNode.path("distance_to_cc_formatted").asText("");
        String starsNumber = jsonNode.path("starsNumber").asText("");
        boolean freeCancellation = jsonNode.path("is_free_cancellable").asInt(0) == 1;

        // Extract services from the "hotel_facilities" field
        JsonNode facilitiesNode = jsonNode.path("hotel_facilities");
        List<String> services = new ArrayList<>();
        if (facilitiesNode != null && facilitiesNode.isArray()) {
            for (JsonNode facility : facilitiesNode) {
                services.add(facility.asText(""));
            }
        }

        return new Hotel(id, name, location, totalPriceAfterTaxesAndDiscount,
                pricePerNightAfterTaxesAndDiscount, discountPercentageForOnlineBooking,
                review, reviewNumber, distanceToCenter, starsNumber, freeCancellation, services);
    }
}
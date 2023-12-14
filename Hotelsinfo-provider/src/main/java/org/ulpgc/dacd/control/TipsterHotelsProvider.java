package org.ulpgc.dacd.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TipsterHotelsProvider implements HotelsProvider {

    public List<JsonObject> getHotels() {
        try {
            // Construir la URL y los parámetros de la solicitud
            String apiUrl = buildApiUrl();
            Map<String, String> params = buildApiParams();

            // Configurar y realizar la solicitud
            Connection.Response response = makeRequest(apiUrl, params);

            // Procesar y devolver la respuesta como lista de JsonObject
            return processResponse(response);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildApiUrl() {
        return "https://hotels-com-provider.p.rapidapi.com/v2/hotels/search";
    }

    private Map<String, String> buildApiParams() {
        Map<String, String> params = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese region_id: ");
        params.put("region_id", scanner.nextLine());
        System.out.print("Ingrese checkin_date (Formato: yyyy-MM-dd): ");
        params.put("checkin_date", scanner.nextLine());
        System.out.print("Ingrese checkout_date (Formato: yyyy-MM-dd): ");
        params.put("checkout_date", scanner.nextLine());
        System.out.print("Ingrese adults_number: ");
        params.put("adults_number", scanner.nextLine());
        params.put("locale", "es_ES");
        params.put("sort_order", "REVIEW");
        params.put("domain", "ES");
        params.put("lodging_type", "HOTEL,HOSTEL,APART_HOTEL");
        params.put("available_filter", "SHOW_AVAILABLE_ONLY");

        return params;
    }

    private Connection.Response makeRequest(String apiUrl, Map<String, String> params) throws IOException {
        Map<String, String> headers = buildRequestHeaders();

        return Jsoup.connect(apiUrl)
                .data(params)
                .headers(headers)
                .ignoreContentType(true)
                .execute();
    }

    private Map<String, String> buildRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-RapidAPI-Key", "77f0f6e079msh3c7c501786eb1d0p1f2305jsndf8f225650c8");
        headers.put("X-RapidAPI-Host", "hotels-com-provider.p.rapidapi.com");
        return headers;
    }

    private List<JsonObject> processResponse(Connection.Response response) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject jsonResponse = parser.parse(response.body()).getAsJsonObject();

            // Acceder a la clave "properties"
            JsonArray properties = jsonResponse.getAsJsonArray("properties");

            // Crear una lista para almacenar los hoteles
            List<JsonObject> hotelsList = new ArrayList<>();

            if (properties != null) {
                // Iterar sobre la lista de propiedades y agregar cada hotel a la lista
                for (int i = 0; i < properties.size(); i++) {
                    JsonObject hotel = properties.get(i).getAsJsonObject();
                    hotelsList.add(hotel);
                }
            } else {
                System.err.println("Error: La clave 'properties' es nula.");
            }

            // Devolver la lista de hoteles
            return hotelsList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

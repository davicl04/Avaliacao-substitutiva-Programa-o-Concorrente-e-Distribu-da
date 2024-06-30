package projetoFinal_materiaPCD;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;
import org.json.*;

public class Main {

    private static final String BASE_URL = "https://archive-api.open-meteo.com/v1/archive";
    private static final List<String[]> COORDINATES = Arrays.asList(
        new String[]{"-10.9167", "-37.05"},
        new String[]{"-1.4558", "-48.5039"},
        new String[]{"-19.9167", "-43.9333"},
        new String[]{"2.81972", "-60.67333"},
        new String[]{"-15.7939", "-47.8828"},
        new String[]{"-20.44278", "-54.64639"},
        new String[]{"-15.5989", "-56.0949"},
        new String[]{"-25.4297", "-49.2711"},
        new String[]{"-27.5935", "-48.55854"},
        new String[]{"-3.7275", "-38.5275"},
        new String[]{"-16.6667", "-49.25"},
        new String[]{"-7.12", "-34.88"},
        new String[]{"0.033", "-51.05"},
        new String[]{"-9.66583", "-35.73528"},
        new String[]{"-3.1189", "-60.0217"},
        new String[]{"-5.7833", "-35.2"},
        new String[]{"-10.16745", "-48.32766"},
        new String[]{"-30.0331", "-51.23"},
        new String[]{"-8.76194", "-63.90389"},
        new String[]{"-8.05", "-34.9"},
        new String[]{"-9.97472", "-67.81"},
        new String[]{"-22.9111", "-43.2056"},
        new String[]{"-12.9747", "-38.4767"},
        new String[]{"-2.5283", "-44.3044"},
        new String[]{"-23.55", "-46.6333"},
        new String[]{"-5.08917", "-42.80194"},
        new String[]{"-20.2889", "-40.3083"}
        
    );

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        Map<String, dadosClimaticos> mapaDadosClimaticos = new HashMap<>();

        for (String[] coord : COORDINATES) {
            String latitude = coord[0];
            String longitude = coord[1];
            String params = String.format("?latitude=%s&longitude=%s&start_date=%s&end_date=%s&daily=temperature_2m_max,temperature_2m_min",
                    URLEncoder.encode(latitude, StandardCharsets.UTF_8),
                    URLEncoder.encode(longitude, StandardCharsets.UTF_8),
                    URLEncoder.encode("2024-01-01", StandardCharsets.UTF_8),
                    URLEncoder.encode("2024-01-31", StandardCharsets.UTF_8));
            String url = BASE_URL + params;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();

            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    dadosClimaticos dadosClimaticos = processResponse(response.body());
                    mapaDadosClimaticos.put(latitude + "," + longitude, dadosClimaticos);
                } else {
                    System.out.println("Falha ao buscar dados para coordenadas: " + latitude + ", " + longitude);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        displayDadosClimaticos(mapaDadosClimaticos);
    }

    private static dadosClimaticos processResponse(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        JSONArray dailyTempsMax = json.getJSONObject("daily").getJSONArray("temperature_2m_max");
        JSONArray dailyTempsMin = json.getJSONObject("daily").getJSONArray("temperature_2m_min");

        List<Double> temperatures = new ArrayList<>();
        for (int i = 0; i < dailyTempsMax.length(); i++) {
            double maxTemp = dailyTempsMax.getDouble(i);
            double minTemp = dailyTempsMin.getDouble(i);
            temperatures.add(maxTemp);
            temperatures.add(minTemp);
        }

        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (double temp : temperatures) {
            sum += temp;
            if (temp < min) min = temp;
            if (temp > max) max = temp;
        }

        double average = sum / temperatures.size();
        return new dadosClimaticos(average, min, max);
    }

    private static void displayDadosClimaticos(Map<String, dadosClimaticos> mapaDadosClimaticos) {
        for (Map.Entry<String, dadosClimaticos> entry : mapaDadosClimaticos.entrySet()) {
            String coordinates = entry.getKey();
            dadosClimaticos data = entry.getValue();
            System.out.println("Cordenadas: " + coordinates);
            System.out.println("Temperatura Média: " + data.getAverage());
            System.out.println("Temperatura Min: " + data.getMin());
            System.out.println("Temperatura Max: " + data.getMax());
            System.out.println("-----------");
        }
    }
}
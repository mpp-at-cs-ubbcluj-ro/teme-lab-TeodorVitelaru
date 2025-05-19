import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

public class TestRest {

    private static final String apiBaseUrl = "http://localhost:5153";

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        // Ignore SSL cert errors (for localhost testing only!)
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());

        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();

        // GET all excursii
        HttpRequest requestGetAll = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "/api/excursii"))
                .GET()
                .build();

        // Create JSON for new excursie
        String newExcursieJson = """
            {
                "obiectiv": "Test Excursie",
                "firmaTransport": "TravelX",
                "oraPlecarii": "%s",
                "pret": 1500,
                "numarLocuri": 20
            }
            """.formatted(LocalDateTime.now().plusDays(1).toString());

        // POST create excursie
        HttpRequest requestCreateExcursie = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "/api/excursii"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(newExcursieJson))
                .build();

        try {
            System.out.println("Before adding excursions:");
            HttpResponse<String> responseBefore = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
            System.out.println(responseBefore.body());

            System.out.println("Adding new excursie...");
            HttpResponse<String> responseCreate = client.send(requestCreateExcursie, HttpResponse.BodyHandlers.ofString());
            System.out.println("Create response status: " + responseCreate.statusCode());
            System.out.println(responseCreate.body());

            System.out.println("After adding excursions:");
            HttpResponse<String> responseAfter = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
            System.out.println(responseAfter.body());

            // GET by ID
            String id = "2"; // Replace with a valid ID
            HttpRequest requestGetById = HttpRequest.newBuilder()
                    .uri(URI.create(apiBaseUrl + "/api/excursii/" + id))
                    .GET()
                    .build();
            HttpResponse<String> responseGetById = client.send(requestGetById, HttpResponse.BodyHandlers.ofString());
            System.out.println("Get by ID response status: " + responseGetById.statusCode());
            System.out.println(responseGetById.body());

            // DELETE by ID
            HttpRequest requestDelete = HttpRequest.newBuilder()
                    .uri(URI.create(apiBaseUrl + "/api/excursii/" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> responseDelete = client.send(requestDelete, HttpResponse.BodyHandlers.ofString());
            System.out.println("Delete response status: " + responseDelete.statusCode());
            System.out.println(responseDelete.body());

            // Update excursie
            String updateExcursieJson = """
                {
                    "id": 3,
                    "obiectiv": "Updated Excursie 123 123 ",
                    "firmaTransport": "Updated TravelX",
                    "oraPlecarii": "%s",
                    "pret": 2000,
                    "numarLocuri": 15
                }
                """.formatted(LocalDateTime.now().plusDays(2).toString());
            HttpRequest requestUpdateExcursie = HttpRequest.newBuilder()
                    .uri(URI.create(apiBaseUrl + "/api/excursii/3" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(updateExcursieJson))
                    .build();
            HttpResponse<String> responseUpdate = client.send(requestUpdateExcursie, HttpResponse.BodyHandlers.ofString());
            System.out.println("Update response status: " + responseUpdate.statusCode());
            System.out.println(responseUpdate.body());
            System.out.println("After update excursions:");
            HttpResponse<String> responseAfterUpdate = client.send(requestGetAll, HttpResponse.BodyHandlers.ofString());
            System.out.println(responseAfterUpdate.body());


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}

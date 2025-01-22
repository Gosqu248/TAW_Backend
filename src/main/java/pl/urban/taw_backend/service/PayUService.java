package pl.urban.taw_backend.service;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.urban.taw_backend.dto.CreateOrderDTO;
import pl.urban.taw_backend.dto.OrderMenuDTO;
import pl.urban.taw_backend.model.Menu;
import pl.urban.taw_backend.model.Order;
import org.springframework.http.HttpHeaders;
import pl.urban.taw_backend.model.OrderMenu;
import pl.urban.taw_backend.repository.MenuRepository;
import pl.urban.taw_backend.request.PayUOrderRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PayUService {

    @Value("${payu.api.url}")
    private String payuApiUrl;

    @Value("${payu.client.id}")
    private String clientId;

    @Value("${payu.client.secret}")
    private String clientSecret;

    private final RestTemplate restTemplate;
    private final OkHttpClient client = new OkHttpClient();
    private final MenuRepository menuRepository;

    private static String accessToken = null;
    private static Instant tokenExpiration = null;

    public PayUService(RestTemplate restTemplate, MenuRepository menuRepository) {
        this.restTemplate = restTemplate;
        this.menuRepository = menuRepository;
    }

    public String getOAuthToken() {
        if (accessToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            return accessToken;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url("https://secure.snd.payu.com/pl/standard/user/oauth/authorize")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String responseBody = response.body().string();

            if (response.isSuccessful()) {
                JSONObject json = new JSONObject(responseBody);
                accessToken = json.getString("access_token");
                int expiresIn = json.getInt("expires_in");
                tokenExpiration = Instant.now().plusSeconds(expiresIn);
                return accessToken;
            } else {
                System.err.println("Błąd autoryzacji OAuth: " + response.message());
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            return null;
        }
    }

    public Map<String, String> createOrder(CreateOrderDTO order) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        String token = getOAuthToken();
        headers.set("Authorization", "Bearer " + token);

        PayUOrderRequest request = new PayUOrderRequest();
        request.setCustomerIp("192.0.0.1");
        request.setMerchantPosId(clientId);
        request.setDescription("Zamówienie w restauracji TAW_restaurant");
        request.setCurrencyCode("PLN");
        request.setTotalAmount(String.valueOf((int) (order.getTotalPrice() * 100)));
        request.setContinueUrl("http://localhost:4200/payment-confirmation");

        List<PayUOrderRequest.Product> products = getProducts(order);
        request.setProducts(products);

        HttpEntity<PayUOrderRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                payuApiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            JSONObject jsonResponse = new JSONObject(response.getBody());

            if (jsonResponse.has("redirectUri")) {
                String uri = jsonResponse.getString("redirectUri");
                String orderId = jsonResponse.getString("orderId");

                if (orderId.isEmpty() || uri.isEmpty()) {
                    throw new RuntimeException("Nie udało się utworzyć zamówienia");
                }

                return Map.of("redirectUri", uri, "orderId", orderId);
            } else {
                throw new RuntimeException("Brak URL przekierowania w odpowiedzi PayU");
            }
        } catch (JSONException e) {
            throw new RuntimeException("Błąd podczas przetwarzania odpowiedzi PayU", e);
        }
    }

    @NotNull
    private List<PayUOrderRequest.Product> getProducts(CreateOrderDTO order) {
        return order.getOrderMenus().stream()
                .map(orderMenu -> {
                    PayUOrderRequest.Product product = new PayUOrderRequest.Product();
                    Menu menu = menuRepository.findById(orderMenu.getMenuId())
                            .orElseThrow(() -> new IllegalArgumentException("Menu with id " + orderMenu.getMenuId() + " not found"));
                    product.setName(menu.getName());
                    product.setUnitPrice(String.valueOf((int) (menu.getPrice() * 100)));
                    product.setQuantity(String.valueOf(orderMenu.getQuantity()));
                    return product;
                })
                .collect(Collectors.toList());
    }

    public  String getOrderStatus(String orderId) {
        String token = getOAuthToken();

        if (token == null) {
            throw new RuntimeException("Nie udało się uzyskać tokenu OAuth");
        }

        Request request = new Request.Builder()
                .url(payuApiUrl + "/" + orderId)
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Błąd podczas pobierania statusu zamówienia: " + response.message());
            }

            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            JSONArray orders = json.getJSONArray("orders");

            if (orders.length() > 0 ) {
                JSONObject order = orders.getJSONObject(0);
                return order.getString("status");
            } else {
                throw new RuntimeException("Nie znaleziono zamówienia o podanym ID");
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas wykonywania żądania do API PayU", e);
        } catch (JSONException e) {
            throw new RuntimeException("Błąd podczas przetwarzania odpowiedzi JSON", e);
        }

    }
}

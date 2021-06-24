package practice5;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import practice4.Product;
import practice4.StorageRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class MyHttpHandler implements HttpHandler {

    private StorageRepository storageRepository = new StorageRepository();

    @Override
    public void handle(HttpExchange response) throws IOException {

        storageRepository.connect();
        checkAuthorizationToken(response);

        if ("GET".equals(response.getRequestMethod())) {
            if (response.getRequestURI().getPath().equals("/login")) {
                doGetLogin(response);
            } else {
                doGetGoods(response);
            }
        } else if ("DELETE".equals(response.getRequestMethod())) {
            doDelete(response);
        } else if ("PUT".equals(response.getRequestMethod())) {
            doPut(response);
        } else if ("POST".equals(response.getRequestMethod())) {
            doPost(response);
        }

        try {
            storageRepository.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void checkAuthorizationToken(HttpExchange response) throws IOException {
        if (!response.getRequestURI().getPath().equals("/login")) {
            try {
                TokenGenerator.parseJWT(response.getRequestHeaders().getFirst("Token"));
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                response.sendResponseHeaders(HttpServletResponse.SC_FORBIDDEN, -1);
            }
        }
    }

    private void doPost(HttpExchange response) throws IOException {
        String inputString = convertStreamToString(response);
        ObjectMapper objectMapper = new ObjectMapper();

        ResultSet productResult = storageRepository.findProductById(objectMapper.readTree(inputString).get("id").asLong());
        Product updatedProduct = new Product();
        updatedProduct.setGroups(new LinkedList<>());
        try {
            if (objectMapper.readTree(inputString).get("name") != null) {
                updatedProduct.setName(objectMapper.readTree(inputString).get("name").asText());
            } else {
                updatedProduct.setName(productResult.getString("product_name"));
            }

            if (objectMapper.readTree(inputString).get("amount") != null) {
                updatedProduct.setAmount(objectMapper.readTree(inputString).get("amount").asInt());
            } else {
                updatedProduct.setAmount(productResult.getInt("amount"));
            }

            if (objectMapper.readTree(inputString).get("price") != null) {
                updatedProduct.setPrice(objectMapper.readTree(inputString).get("price").asDouble());
            } else {
                updatedProduct.setPrice(productResult.getDouble("price"));
            }

            if (storageRepository.validateParameters(updatedProduct)) {
                storageRepository.update(objectMapper.readTree(inputString).get("id").asLong(), updatedProduct);
                response.sendResponseHeaders(HttpServletResponse.SC_OK, -1);
            } else {
                response.sendResponseHeaders(HttpServletResponse.SC_CONFLICT, -1);
            }

        } catch (SQLException e) {
            response.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, -1);
        }
    }

    private void doPut(HttpExchange response) throws IOException {
        String inputString = convertStreamToString(response);
        ObjectMapper objectMapper = new ObjectMapper();
        Product product = objectMapper.readValue(inputString, Product.class);

        if (!storageRepository.validateParameters(product)) {
            response.sendResponseHeaders(HttpServletResponse.SC_CONFLICT, -1);
        } else {
            storageRepository.save(product);
            ResultSet productResult = storageRepository.findByProductName(product.getName());
            try {
                int productId = productResult.getInt("product_id");
                String id = String.valueOf(productId);
                response.sendResponseHeaders(HttpServletResponse.SC_OK, id.length());
                OutputStream outputStream = response.getResponseBody();
                outputStream.write(id.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (SQLException e) {
                response.sendResponseHeaders(HttpServletResponse.SC_NO_CONTENT, -1);
            }
        }

    }

    private String convertStreamToString(HttpExchange response) throws IOException {
        InputStream is = response.getRequestBody();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }

    private void doDelete(HttpExchange response) throws IOException {
        String[] path = response.getRequestURI().getPath().split("/");
        ResultSet resultProduct = storageRepository.findProductById(Long.parseLong(path[path.length - 1]));
        try {
            storageRepository.delete(resultProduct.getString("product_name"));
            response.sendResponseHeaders(HttpServletResponse.SC_NO_CONTENT, -1);
        } catch (SQLException e) {
            response.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, -1);
        }
    }

    private void doGetGoods(HttpExchange response) throws IOException {
        String[] path = response.getRequestURI().getPath().split("/");
        long productId = Long.parseLong(path[path.length - 1]);
        ResultSet productResult = storageRepository.findProductById(productId);
        ResultSet productGroups = storageRepository.findAllGroupsOfProduct(productId);
        LinkedList<String> groups = findGroupsOfProduct(productGroups);


        Product product = null;
        try {

            product = new Product(productResult.getString("product_name"), groups,
                    productResult.getInt("amount"), productResult.getDouble("price"));
        } catch (
                SQLException e) {
            response.sendResponseHeaders(HttpServletResponse.SC_NOT_FOUND, -1);
        }

        String goodsResponse = "";
        ObjectMapper obj = new ObjectMapper();
        try {
            goodsResponse = obj.writeValueAsString(product);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        OutputStream outputStream = response.getResponseBody();
        response.sendResponseHeaders(HttpServletResponse.SC_OK, goodsResponse.length());
        outputStream.write(goodsResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    private LinkedList<String> findGroupsOfProduct(ResultSet productGroups) {
        LinkedList<String> groups = new LinkedList<>();
        try {
            do {
                groups.add(storageRepository.getGroupRepository().findGroupById(Long.parseLong(productGroups.getString("group_id"))).getString("group_name"));
                productGroups.next();
            } while (productGroups.next());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    private void doGetLogin(HttpExchange httpExchange) throws IOException {
        String login = httpExchange.getRequestHeaders().getFirst("Login");
        String password = httpExchange.getRequestHeaders().getFirst("Password");

        if (login.equals("") || password.equals("")) {
            httpExchange.sendResponseHeaders(HttpServletResponse.SC_UNAUTHORIZED, -1);
        } else {
            String htmlResponse = TokenGenerator.createJWT("0", login, "new-token", 0);
            OutputStream outputStream = httpExchange.getResponseBody();
            httpExchange.sendResponseHeaders(HttpServletResponse.SC_OK, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        }
    }

}
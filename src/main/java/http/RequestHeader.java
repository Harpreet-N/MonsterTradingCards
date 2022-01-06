package http;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class RequestHeader {

    private String username;
    private String token;
    private String body;
    private String method;
    private String path;
    private HashMap<String, String> getParameter = new HashMap<>();
    private List<String> urlParameter = new ArrayList<>();

    public RequestHeader(String username, String token, String body, String method, String path) {
        this.username = username;
        this.token = token;
        this.body = body;
        this.method = method;
        this.path = path;

        this.parsePath(path);
    }

    private void parsePath(String path) {
        if (path.contains("?")) {
            String allParams = path.split("\\?")[1];

            urlParameter.add(path.split("\\?")[0].substring(1));

            if (allParams.contains("&")) {
                List<String> keyValues = new ArrayList<String>(List.of(allParams.split("&")));
                for (String pair : keyValues) {
                    String key = pair.split("=")[0];
                    String value = pair.split("=")[1];

                    getParameter.put(key, value);
                }
            } else {
                String key = allParams.split("=")[0];
                String value = allParams.split("=")[1];

                getParameter.put(key, value);
            }
        } else {
            urlParameter = new ArrayList<String>(List.of(path.split("/")));

            // Remove first index - is empty
            urlParameter.remove(0);
        }
    }

}

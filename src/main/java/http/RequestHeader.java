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
    private List<String> url = new ArrayList<>();

    private static String splitPath = "\\?";
    private static String equals = "=";

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
            String allParams = path.split(splitPath)[1];
            url.add(path.split(splitPath)[0].substring(1));

            if (allParams.contains("&")) {
                List<String> keyValues = new ArrayList<>(List.of(allParams.split("&")));
                for (String pair : keyValues) {
                    String key = pair.split(equals)[0];
                    String value = pair.split(equals)[1];

                    getParameter.put(key, value);
                }
            } else {
                String key = allParams.split(equals)[0];
                String value = allParams.split(equals)[1];
                getParameter.put(key, value);
            }
        } else {
            url = new ArrayList<>(List.of(path.split("/")));
            url.remove(0);
        }
    }

}

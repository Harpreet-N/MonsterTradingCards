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
    }

}

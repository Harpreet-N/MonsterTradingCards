package service;

public class UserNameAndTokenService {

    public String[] getUserNameAndToken(String[] requestsLines) {
        String[] userToken = new String[2];
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            if (header.startsWith("Authorization")) {
                String splitUsername = header.split(" ")[2];
                userToken[0] = splitUsername.split("-")[0];
                userToken[1] = splitUsername.split("-")[1];
            }
        }
        userToken[0] = userToken[0] != null ? userToken[0] : "";
        userToken[1] = userToken[1] != null ? userToken[1] : "";
        return userToken;
    }

}

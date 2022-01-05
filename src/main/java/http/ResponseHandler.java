package http;

import database.Database;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;

public class ResponseHandler {
    private final Database db;
    private final BufferedWriter bufferedWriter;

    private static final Logger logger = Logger.getLogger(ResponseHandler.class);

    private static String HTTP_OK = "HTTP/1.1 200 OK\r\n";
    private static String HTTP_CONTENT_TYPE = "ContentType: text/html\r\n";

    public ResponseHandler(Database db, BufferedWriter bufferedWriter) {
        this.db = db;
        this.bufferedWriter = bufferedWriter;
    }

    public void responseOK() throws IOException {
        bufferedWriter.write(HTTP_OK);
        bufferedWriter.write(HTTP_CONTENT_TYPE);
        bufferedWriter.write("\r\nOK\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
    }

    public void responseError() throws IOException {
        bufferedWriter.write(HTTP_OK);
        bufferedWriter.write(HTTP_CONTENT_TYPE);
        bufferedWriter.write("\r\nERROR\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
    }

    public void responseCustom(String message) throws IOException {
        bufferedWriter.write(HTTP_OK);
        bufferedWriter.write(HTTP_CONTENT_TYPE);
        bufferedWriter.write("\r\n" + message + "\r\n");
        bufferedWriter.write("\r\n");
        bufferedWriter.flush();
    }


}

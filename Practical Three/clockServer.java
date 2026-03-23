import java.io.*;
import java.net.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class clockServer {
    private static final int PORT = 8888;
    private static final String[] CITIES = {
        "South Africa|Africa/Johannesburg",
        "London|Europe/London",
        "New York|America/New_York",
        "Tokyo|Asia/Tokyo",
        "Sydney|Australia/Sydney",
        "Dubai|Asia/Dubai",
        "Singapore|Asia/Singapore",
        "Hong Kong|Asia/Hong_Kong",
        "Paris|Europe/Paris",
        "Kerala|Asia/Kolkata"
    };

    public static void main(String[] args) {
        System.out.println("World Clock Server starting on port " + PORT);
        System.out.println("Open your browser and navigate to: http://127.0.0.1:" + PORT);
        System.out.println("Press Ctrl+C to stop the server\n");

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String requestLine = reader.readLine();

                if (requestLine == null) {
                    socket.close();
                    return;
                }

                System.out.println("Request: " + requestLine);

                // Parse request line (RFC 7230)
                String[] parts = requestLine.split(" ");
                if (parts.length < 3) {
                    sendBadRequest(output);
                    socket.close();
                    return;
                }

                String method = parts[0];
                String path = parts[1];
                String httpVersion = parts[2];

                // Read and store all request headers (RFC 7230)
                Map<String, String> headers = new HashMap<>();
                String headerLine;
                while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                    int colonIndex = headerLine.indexOf(':');
                    if (colonIndex > 0) {
                        String headerName = headerLine.substring(0, colonIndex).trim().toLowerCase();
                        String headerValue = headerLine.substring(colonIndex + 1).trim();
                        headers.put(headerName, headerValue);
                    }
                }

                // Support GET and HEAD (RFC 2616 section 5.1.1)
                if (!method.equals("GET") && !method.equals("HEAD")) {
                    sendMethodNotAllowed(output);
                    socket.close();
                    return;
                }

                // Handle favicon.ico (browser sends this automatically)
                if (path.equals("/favicon.ico")) {
                    sendNotFound(output);
                    socket.close();
                    return;
                }

                // Handle invalid paths - only allow root
                if (!path.equals("/") && !path.startsWith("/?")) {
                    sendNotFound(output);
                    socket.close();
                    return;
                }

                String selectedCity = "South Africa";
                
                // Parse query string
                if (path.contains("?")) {
                    String[] pathParts = path.split("\\?");
                    String query = pathParts[1];
                    if (query.startsWith("city=")) {
                        try {
                            String decodedCity = URLDecoder.decode(query.substring(5), StandardCharsets.UTF_8);
                            // Validate city exists
                            if (isCityValid(decodedCity)) {
                                selectedCity = decodedCity;
                            } else {
                                sendBadRequest(output);
                                socket.close();
                                return;
                            }
                        } catch (Exception e) {
                            sendBadRequest(output);
                            socket.close();
                            return;
                        }
                    }
                }

                String htmlResponse = generateHTML(selectedCity);

                // Build HTTP response (RFC 7231)
                String responseHeaders = "HTTP/1.1 200 OK\r\n" +
                        "Date: " + getHTTPDate() + "\r\n" +
                        "Content-Type: text/html; charset=UTF-8\r\n" +
                        "Content-Length: " + htmlResponse.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "Cache-Control: no-cache, no-store, must-revalidate\r\n" +
                        "Pragma: no-cache\r\n" +
                        "Expires: 0\r\n" +
                        "Vary: Accept-Encoding\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

                output.write(responseHeaders.getBytes(StandardCharsets.UTF_8));

                // For HEAD requests, send headers only (RFC 7231)
                if (method.equals("GET")) {
                    output.write(htmlResponse.getBytes(StandardCharsets.UTF_8));
                }

                output.flush();
                socket.close();
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            }
        }

        // RFC 7231 compliant date format
        private String getHTTPDate() {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
            return now.format(formatter);
        }

        private boolean isCityValid(String city) {
            for (String cityInfo : CITIES) {
                String[] cityParts = cityInfo.split("\\|");
                if (cityParts[0].equals(city)) {
                    return true;
                }
            }
            return false;
        }

        private void sendNotFound(OutputStream output) throws IOException {
            String body = "<!DOCTYPE html><html><body><h1>404 Not Found</h1><p>The requested resource was not found.</p></body></html>";
            String headers = "HTTP/1.1 404 Not Found\r\n" +
                    "Date: " + getHTTPDate() + "\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(body.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }

        private void sendBadRequest(OutputStream output) throws IOException {
            String body = "<!DOCTYPE html><html><body><h1>400 Bad Request</h1><p>Invalid request format.</p></body></html>";
            String headers = "HTTP/1.1 400 Bad Request\r\n" +
                    "Date: " + getHTTPDate() + "\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(body.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }

        private void sendMethodNotAllowed(OutputStream output) throws IOException {
            String body = "<!DOCTYPE html><html><body><h1>405 Method Not Allowed</h1><p>Only GET and HEAD requests are supported.</p></body></html>";
            String headers = "HTTP/1.1 405 Method Not Allowed\r\n" +
                    "Date: " + getHTTPDate() + "\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                    "Allow: GET, HEAD\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            output.write(headers.getBytes(StandardCharsets.UTF_8));
            output.write(body.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }

        private String generateHTML(String selectedCity) {
            StringBuilder html = new StringBuilder();

            html.append("<!DOCTYPE html>\n");
            html.append("<html>\n");
            html.append("<head>\n");
            html.append("<title>World Clock</title>\n");
            html.append("<meta http-equiv=\"REFRESH\" content=\"1\">\n");
            html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            html.append("<style>\n");
            html.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
            html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%); min-height: 100vh; display: flex; align-items: center; justify-content: center; padding: 20px; }\n");
            html.append(".container { background: white; padding: 60px 40px; border-radius: 12px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08); max-width: 500px; width: 100%; }\n");
            html.append("h1 { font-size: 28px; font-weight: 300; text-align: center; color: #1a1a1a; margin-bottom: 40px; letter-spacing: 0.5px; }\n");
            html.append(".city-name { font-size: 16px; text-align: center; color: #666; margin-bottom: 20px; font-weight: 400; text-transform: uppercase; letter-spacing: 1px; }\n");
            html.append(".time-display { font-size: 64px; text-align: center; color: #1a1a1a; font-weight: 300; margin-bottom: 40px; font-family: 'Courier New', monospace; letter-spacing: 2px; }\n");
            html.append(".sa-time { text-align: center; color: #999; font-size: 14px; margin-bottom: 40px; padding-top: 20px; border-top: 1px solid #f0f0f0; }\n");
            html.append(".sa-time strong { color: #1a1a1a; }\n");
            html.append(".city-label { font-size: 12px; text-align: center; color: #999; margin-bottom: 20px; text-transform: uppercase; letter-spacing: 0.8px; font-weight: 500; }\n");
            html.append(".city-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }\n");
            html.append(".city-link { padding: 14px 16px; background: #f5f5f5; color: #1a1a1a; text-decoration: none; border-radius: 8px; transition: all 0.3s ease; border: 2px solid transparent; font-size: 14px; font-weight: 500; text-align: center; cursor: pointer; }\n");
            html.append(".city-link:hover { background: #efefef; transform: translateY(-2px); }\n");
            html.append(".city-link.active { background: #1a1a1a; color: white; border-color: #1a1a1a; }\n");
            html.append("</style>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("<div class=\"container\">\n");
            html.append("<h1>World Clock</h1>\n");

            String selectedTimezone = "Africa/Johannesburg";
            for (String cityInfo : CITIES) {
                String[] cityParts = cityInfo.split("\\|");
                if (cityParts[0].equals(selectedCity)) {
                    selectedTimezone = cityParts[1];
                    break;
                }
            }

            ZoneId zoneId = ZoneId.of(selectedTimezone);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTime = now.format(formatter);

            html.append("<div class=\"city-name\">").append(selectedCity).append("</div>\n");
            html.append("<div class=\"time-display\">").append(formattedTime).append("</div>\n");

            if (!selectedCity.equals("South Africa")) {
                ZoneId saZone = ZoneId.of("Africa/Johannesburg");
                ZonedDateTime saTime = ZonedDateTime.now(saZone);
                String saTimeStr = saTime.format(formatter);
                html.append("<div class=\"sa-time\">\n");
                html.append("<strong>South Africa:</strong> ").append(saTimeStr).append("\n");
                html.append("</div>\n");
            }

            html.append("<div class=\"city-label\">Select a city</div>\n");
            html.append("<div class=\"city-list\">\n");

            for (String cityInfo : CITIES) {
                String[] cityParts = cityInfo.split("\\|");
                String cityName = cityParts[0];
                String isActive = cityName.equals(selectedCity) ? " active" : "";
                try {
                    html.append("<a href=\"/?city=").append(URLEncoder.encode(cityName, StandardCharsets.UTF_8))
                        .append("\" class=\"city-link").append(isActive).append("\">")
                        .append(cityName).append("</a>\n");
                } catch (Exception e) {
                    html.append("<a href=\"/?city=").append(cityName)
                        .append("\" class=\"city-link").append(isActive).append("\">")
                        .append(cityName).append("</a>\n");
                }
            }

            html.append("</div>\n");
            html.append("</div>\n");
            html.append("</body>\n");
            html.append("</html>\n");

            return html.toString();
        }
    }
}

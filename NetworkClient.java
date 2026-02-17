import com.google.gson.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

// ============================================================
// NETWORK CLIENT - Communicates with SocialAppServer
// ============================================================
public class NetworkClient {
    // Configuration: Cloud Server Support
    // Set via environment variable: SOCIAL_APP_SERVER=https://your-cloud-server.com
    // Default: Uses localhost. Set CLOUD_SERVER env var to switch to cloud.
    private static String SERVER_URL = initializeServerURL();
    private static final Gson gson = new Gson();
    private static boolean useNetwork = false;
    
    // Public constants for easy switching
    public static final String LOCALHOST_SERVER = "http://127.0.0.1:8080/api";
    public static final String CLOUD_SERVER = System.getenv("SOCIAL_APP_SERVER") != null ? 
        System.getenv("SOCIAL_APP_SERVER") + "/api" : 
        "https://socialsapp-cloud.herokuapp.com/api"; // Change this URL to your cloud server
    
    private static String initializeServerURL() {
        // Check for cloud server environment variable first
        String cloudServer = System.getenv("SOCIAL_APP_SERVER");
        if(cloudServer != null && !cloudServer.isEmpty()) {
            System.out.println("[NetworkClient] Using cloud server from SOCIAL_APP_SERVER env var: " + cloudServer);
            return cloudServer + "/api";
        }
        
        // Check for CLOUD_MODE environment variable
        String cloudMode = System.getenv("CLOUD_MODE");
        if("true".equalsIgnoreCase(cloudMode)) {
            System.out.println("[NetworkClient] CLOUD_MODE enabled - using cloud server");
            return "https://socialsapp-cloud.herokuapp.com/api"; // Change this URL
        }
        
        // Default to localhost
        return "http://127.0.0.1:8080/api";
    }

    public static void setServerURL(String url) {
        SERVER_URL = url + "/api";
        System.out.println("[NetworkClient] Server URL changed to: " + SERVER_URL);
    }
    
    public static void useCloudServer() {
        SERVER_URL = "https://socialsapp-cloud.herokuapp.com/api"; // Change to your cloud server URL
        System.out.println("[NetworkClient] Switched to cloud server: " + SERVER_URL);
    }
    
    public static void useLocalServer() {
        SERVER_URL = "http://127.0.0.1:8080/api";
        System.out.println("[NetworkClient] Switched to local server: " + SERVER_URL);
    }

    public static void enableNetwork(boolean enabled) {
        useNetwork = enabled;
        if(enabled) {
            System.out.println("✓ Network mode enabled - using server at " + SERVER_URL);
        } else {
            System.out.println("✓ Local mode enabled - using local files");
        }
    }

    public static boolean isNetworkEnabled() {
        return useNetwork;
    }

    public static boolean isServerAvailable() {
        try {
            String response = sendRequest("GET", SERVER_URL.replaceAll("/api$", ""), "");
            return response != null;
        } catch(Exception e) {
            return false;
        }
    }

    // ============================================================
    // USER ENDPOINTS
    // ============================================================
    public static boolean registerUser(String username, String password) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);
        
        String response = sendRequest("POST", SERVER_URL + "/users/register", body.toString());
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        return result.get("success").getAsBoolean();
    }

    public static JsonObject loginUser(String username, String password) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);
        
        String response = sendRequest("POST", SERVER_URL + "/users/login", body.toString());
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        if(result.get("success").getAsBoolean()) {
            return result.getAsJsonObject("user");
        }
        return null;
    }

    public static JsonObject getUser(String username) throws Exception {
        String response = sendRequest("GET", SERVER_URL + "/users/get?username=" + username, "");
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        if(result.get("success").getAsBoolean()) {
            return result.getAsJsonObject("user");
        }
        return null;
    }

    public static JsonArray listUsers() throws Exception {
        String response = sendRequest("GET", SERVER_URL + "/users/list", "");
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        if(result.get("success").getAsBoolean()) {
            return result.getAsJsonArray("users");
        }
        return new JsonArray();
    }

    public static void updateUser(String username, String displayName, String profilePhoto) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        if(displayName != null) body.addProperty("displayName", displayName);
        if(profilePhoto != null) body.addProperty("profilePhoto", profilePhoto);
        
        sendRequest("POST", SERVER_URL + "/users/update", body.toString());
    }

    public static void followUser(String follower, String followee) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("follower", follower);
        body.addProperty("followee", followee);
        
        sendRequest("POST", SERVER_URL + "/users/follow", body.toString());
    }

    public static void unfollowUser(String follower, String followee) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("follower", follower);
        body.addProperty("followee", followee);
        
        sendRequest("POST", SERVER_URL + "/users/unfollow", body.toString());
    }

    // ============================================================
    // POST ENDPOINTS
    // ============================================================
    public static void createPost(String author, String text, String media) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("author", author);
        if(text != null) body.addProperty("text", text);
        if(media != null) body.addProperty("media", media);
        
        sendRequest("POST", SERVER_URL + "/posts/create", body.toString());
    }

    public static JsonArray listPosts() throws Exception {
        String response = sendRequest("GET", SERVER_URL + "/posts/list", "");
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        if(result.get("success").getAsBoolean()) {
            return result.getAsJsonArray("posts");
        }
        return new JsonArray();
    }

    public static void likePost(int postIndex, String username) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("postIndex", postIndex);
        body.addProperty("username", username);
        
        sendRequest("POST", SERVER_URL + "/posts/like", body.toString());
    }

    public static void unlikePost(int postIndex, String username) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("postIndex", postIndex);
        body.addProperty("username", username);
        
        sendRequest("POST", SERVER_URL + "/posts/unlike", body.toString());
    }

    public static void commentPost(int postIndex, String author, String text) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("postIndex", postIndex);
        body.addProperty("author", author);
        body.addProperty("text", text);
        
        sendRequest("POST", SERVER_URL + "/posts/comment", body.toString());
    }

    // ============================================================
    // MESSAGE ENDPOINTS
    // ============================================================
    public static void sendMessage(String sender, String recipient, String text) throws Exception {
        JsonObject body = new JsonObject();
        body.addProperty("sender", sender);
        body.addProperty("recipient", recipient);
        body.addProperty("text", text);
        
        sendRequest("POST", SERVER_URL + "/messages/send", body.toString());
    }

    public static JsonArray getConversation(String user1, String user2) throws Exception {
        String response = sendRequest("GET", SERVER_URL + "/messages/get?user1=" + user1 + "&user2=" + user2, "");
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        if(result.get("success").getAsBoolean()) {
            return result.getAsJsonArray("messages");
        }
        return new JsonArray();
    }

    public static Set<String> getContacts(String username) throws Exception {
        String response = sendRequest("GET", SERVER_URL + "/messages/contacts?username=" + username, "");
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        Set<String> contacts = new HashSet<>();
        if(result.get("success").getAsBoolean()) {
            JsonArray arr = result.getAsJsonArray("contacts");
            for(JsonElement el : arr) {
                contacts.add(el.getAsString());
            }
        }
        return contacts;
    }

    public static int getUnreadCount(String username) throws Exception {
        String response = sendRequest("GET", SERVER_URL + "/messages/unread?username=" + username, "");
        JsonObject result = JsonParser.parseString(response).getAsJsonObject();
        
        if(result.get("success").getAsBoolean()) {
            return result.get("unreadCount").getAsInt();
        }
        return 0;
    }

    // ============================================================
    // HTTP REQUEST HANDLING
    // ============================================================
    private static String sendRequest(String method, String endpoint, String body) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(endpoint).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        if(!body.isEmpty() && (method.equals("POST") || method.equals("PUT"))) {
            conn.setDoOutput(true);
            try(OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }

        try {
            int status = conn.getResponseCode();
            if(status >= 200 && status < 300) {
                return new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new Exception("HTTP " + status);
            }
        } finally {
            conn.disconnect();
        }
    }
}

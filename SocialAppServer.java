import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.Executors;
import java.lang.reflect.Type;

// ============================================================
// PLATFORM UTILITIES
// ============================================================
class PlatformUtilsServer {
    public static String getPath(String... parts) {
        return String.join(File.separator, parts);
    }
    
    public static void ensureDirectory(String dirPath) {
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
    }
}

// ============================================================
// SERVER ENTRY POINT
// ============================================================
public class SocialAppServer {
    // Port defaults to 8080, but can be set via PORT environment variable
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static HttpServer server;

    public static void main(String[] args) throws IOException {
        // Initialize data directory
        PlatformUtilsServer.ensureDirectory("data");
        
        // Listen on 0.0.0.0 (all interfaces) for cloud deployment
        String host = System.getenv().getOrDefault("HOST", "0.0.0.0");
        server = HttpServer.create(new InetSocketAddress(host, PORT), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        // Register API endpoints
        server.createContext("/api/users/register", new RegisterHandler());
        server.createContext("/api/users/login", new LoginHandler());
        server.createContext("/api/users/get", new GetUserHandler());
        server.createContext("/api/users/list", new ListUsersHandler());
        server.createContext("/api/users/update", new UpdateUserHandler());
        server.createContext("/api/users/follow", new FollowHandler());
        server.createContext("/api/users/unfollow", new UnfollowHandler());

        server.createContext("/api/posts/create", new CreatePostHandler());
        server.createContext("/api/posts/list", new ListPostsHandler());
        server.createContext("/api/posts/like", new LikePostHandler());
        server.createContext("/api/posts/unlike", new UnlikePostHandler());
        server.createContext("/api/posts/comment", new CommentPostHandler());

        server.createContext("/api/messages/send", new SendMessageHandler());
        server.createContext("/api/messages/get", new GetConversationHandler());
        server.createContext("/api/messages/contacts", new GetContactsHandler());
        server.createContext("/api/messages/unread", new GetUnreadCountHandler());

        server.createContext("/api/media/upload", new UploadMediaHandler());
        server.createContext("/api/media/download", new DownloadMediaHandler());

        server.start();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SocialApp Server Started Successfully ║");
        System.out.println("║   Host: " + host);
        System.out.println("║   Port: " + PORT);
        System.out.println("║   API:  http://" + host + ":" + PORT + "/api");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Waiting for client connections...");
    }

    // ============================================================
    // UTILITY METHODS
    // ============================================================
    public static void sendJsonResponse(HttpExchange exchange, int statusCode, Map<String, Object> data) throws IOException {
        String response = gson.toJson(data);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }

    static Map<String, String> getQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if(query == null) return params;
        for(String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if(kv.length == 2) {
                try {
                    params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
                } catch(Exception e) {
                    params.put(kv[0], kv[1]);
                }
            }
        }
        return params;
    }

    static String getRequestBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    // ============================================================
    // HANDLERS - USER MANAGEMENT
    // ============================================================
    static class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String username = body.get("username").getAsString();
                    String password = body.get("password").getAsString();

                    boolean success = ServerUserStore.register(username, password);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", success);
                    response.put("message", success ? "User registered" : "Username already taken");
                    
                    sendJsonResponse(exchange, success ? 200 : 400, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String username = body.get("username").getAsString();
                    String password = body.get("password").getAsString();

                    boolean success = ServerUserStore.validate(username, password);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", success);
                    if(success) {
                        response.put("user", ServerUserStore.getUserAsJson(username));
                    }
                    
                    sendJsonResponse(exchange, success ? 200 : 401, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class GetUserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                String username = params.get("username");

                Map<String, Object> response = new HashMap<>();
                JsonObject user = ServerUserStore.getUserAsJson(username);
                if(user != null) {
                    response.put("success", true);
                    response.put("user", user);
                    sendJsonResponse(exchange, 200, response);
                } else {
                    response.put("success", false);
                    response.put("message", "User not found");
                    sendJsonResponse(exchange, 404, response);
                }
            } catch(Exception e) {
                e.printStackTrace();
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Error: " + e.getMessage());
                sendJsonResponse(exchange, 500, error);
            }
        }
    }

    static class ListUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("users", ServerUserStore.getAllUsersAsJson());
                sendJsonResponse(exchange, 200, response);
            } catch(Exception e) {
                e.printStackTrace();
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Error: " + e.getMessage());
                sendJsonResponse(exchange, 500, error);
            }
        }
    }

    static class UpdateUserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String username = body.get("username").getAsString();
                    String displayName = body.has("displayName") ? body.get("displayName").getAsString() : null;
                    String profilePhoto = body.has("profilePhoto") ? body.get("profilePhoto").getAsString() : null;

                    ServerUserStore.updateUser(username, displayName, profilePhoto);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "User updated");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class FollowHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String follower = body.get("follower").getAsString();
                    String followee = body.get("followee").getAsString();

                    ServerUserStore.addFollower(follower, followee);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Now following");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class UnfollowHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String follower = body.get("follower").getAsString();
                    String followee = body.get("followee").getAsString();

                    ServerUserStore.removeFollower(follower, followee);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Unfollowed");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    // ============================================================
    // HANDLERS - POST MANAGEMENT
    // ============================================================
    static class CreatePostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String author = body.get("author").getAsString();
                    String text = body.has("text") ? body.get("text").getAsString() : "";
                    String media = body.has("media") ? body.get("media").getAsString() : null;

                    ServerPostStore.addPost(author, text, media);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Post created");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class ListPostsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("posts", ServerPostStore.getPostsAsJson());
                sendJsonResponse(exchange, 200, response);
            } catch(Exception e) {
                e.printStackTrace();
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Error: " + e.getMessage());
                sendJsonResponse(exchange, 500, error);
            }
        }
    }

    static class LikePostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    int postIndex = body.get("postIndex").getAsInt();
                    String username = body.get("username").getAsString();

                    ServerPostStore.likePost(postIndex, username);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Post liked");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class UnlikePostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    int postIndex = body.get("postIndex").getAsInt();
                    String username = body.get("username").getAsString();

                    ServerPostStore.unlikePost(postIndex, username);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Post unliked");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class CommentPostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    int postIndex = body.get("postIndex").getAsInt();
                    String author = body.get("author").getAsString();
                    String text = body.get("text").getAsString();

                    ServerPostStore.addCommentToPost(postIndex, author, text);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Comment added");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    // ============================================================
    // HANDLERS - MESSAGING
    // ============================================================
    static class SendMessageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equals("POST")) {
                try {
                    JsonObject body = JsonParser.parseString(getRequestBody(exchange)).getAsJsonObject();
                    String sender = body.get("sender").getAsString();
                    String recipient = body.get("recipient").getAsString();
                    String text = body.get("text").getAsString();

                    ServerMessageStore.sendMessage(sender, recipient, text);
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Message sent");
                    sendJsonResponse(exchange, 200, response);
                } catch(Exception e) {
                    e.printStackTrace();
                    Map<String, Object> error = new HashMap<>();
                    error.put("success", false);
                    error.put("message", "Error: " + e.getMessage());
                    sendJsonResponse(exchange, 500, error);
                }
            }
        }
    }

    static class GetConversationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                String user1 = params.get("user1");
                String user2 = params.get("user2");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("messages", ServerMessageStore.getConversationAsJson(user1, user2));
                sendJsonResponse(exchange, 200, response);
            } catch(Exception e) {
                e.printStackTrace();
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Error: " + e.getMessage());
                sendJsonResponse(exchange, 500, error);
            }
        }
    }

    static class GetContactsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                String username = params.get("username");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("contacts", ServerMessageStore.getContacts(username));
                sendJsonResponse(exchange, 200, response);
            } catch(Exception e) {
                e.printStackTrace();
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Error: " + e.getMessage());
                sendJsonResponse(exchange, 500, error);
            }
        }
    }

    static class GetUnreadCountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Map<String, String> params = getQueryParams(exchange.getRequestURI().getQuery());
                String recipient = params.get("username");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("unreadCount", ServerMessageStore.getUnreadCount(recipient));
                sendJsonResponse(exchange, 200, response);
            } catch(Exception e) {
                e.printStackTrace();
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Error: " + e.getMessage());
                sendJsonResponse(exchange, 500, error);
            }
        }
    }

    // ============================================================
    // HANDLERS - MEDIA
    // ============================================================
    static class UploadMediaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Placeholder for media upload - would handle multipart form data
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Media upload placeholder");
            sendJsonResponse(exchange, 200, response);
        }
    }

    static class DownloadMediaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Placeholder for media download
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Media download placeholder");
            sendJsonResponse(exchange, 200, response);
        }
    }
}

// ============================================================
// SERVER-SIDE USER STORE
// ============================================================
class ServerUserStore {
    private static final String FILE = "data/users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, ServerUser> users = new HashMap<>();
    private static final Object lock = new Object();

    static {
        loadUsers();
    }

    static class ServerUser {
        String username;
        String displayName;
        String passwordHash;
        String profilePhoto;
        List<String> followers = new ArrayList<>();
        List<String> following = new ArrayList<>();

        ServerUser(String u, String p) {
            username = u;
            displayName = u;
            passwordHash = hashPassword(p);
        }
    }

    public static boolean register(String username, String password) {
        synchronized(lock) {
            if(users.containsKey(username)) return false;
            users.put(username, new ServerUser(username, password));
            save();
            return true;
        }
    }

    public static boolean validate(String username, String password) {
        synchronized(lock) {
            ServerUser u = users.get(username);
            if(u == null) return false;
            return u.passwordHash.equals(hashPassword(password));
        }
    }

    public static void updateUser(String username, String displayName, String profilePhoto) {
        synchronized(lock) {
            ServerUser u = users.get(username);
            if(u != null) {
                if(displayName != null) u.displayName = displayName;
                if(profilePhoto != null) u.profilePhoto = profilePhoto;
                save();
            }
        }
    }

    public static void addFollower(String follower, String followee) {
        synchronized(lock) {
            ServerUser f = users.get(follower);
            ServerUser e = users.get(followee);
            if(f != null && e != null) {
                if(!f.following.contains(followee)) {
                    f.following.add(followee);
                    e.followers.add(follower);
                    save();
                }
            }
        }
    }

    public static void removeFollower(String follower, String followee) {
        synchronized(lock) {
            ServerUser f = users.get(follower);
            ServerUser e = users.get(followee);
            if(f != null && e != null) {
                f.following.remove(followee);
                e.followers.remove(follower);
                save();
            }
        }
    }

    public static JsonObject getUserAsJson(String username) {
        synchronized(lock) {
            ServerUser u = users.get(username);
            if(u == null) return null;
            return gson.toJsonTree(u).getAsJsonObject();
        }
    }

    public static JsonArray getAllUsersAsJson() {
        synchronized(lock) {
            return gson.toJsonTree(users.values()).getAsJsonArray();
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch(Exception e) {
            return password;
        }
    }

    private static void save() {
        try(Writer w = new java.io.FileWriter(FILE)) {
            gson.toJson(users, w);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers() {
        try {
            if(new File(FILE).exists()) {
                Reader r = new java.io.FileReader(FILE);
                Type t = new TypeToken<Map<String, ServerUser>>(){}.getType();
                Map<String, ServerUser> loaded = gson.fromJson(r, t);
                if(loaded != null) users = loaded;
                r.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

// ============================================================
// SERVER-SIDE POST STORE
// ============================================================
class ServerPostStore {
    private static final String FILE = "data/posts.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static List<ServerPost> posts = new ArrayList<>();
    private static final Object lock = new Object();

    static {
        loadPosts();
    }

    static class ServerPost {
        String author;
        String text;
        String media;
        long timestamp;
        List<String> likes = new ArrayList<>();
        List<ServerComment> comments = new ArrayList<>();

        ServerPost(String a, String t, String m) {
            author = a;
            text = t;
            media = m;
            timestamp = System.currentTimeMillis();
        }
    }

    static class ServerComment {
        String author;
        String text;
        long timestamp;

        ServerComment(String a, String t) {
            author = a;
            text = t;
            timestamp = System.currentTimeMillis();
        }
    }

    public static void addPost(String author, String text, String media) {
        synchronized(lock) {
            posts.add(0, new ServerPost(author, text, media));
            save();
        }
    }

    public static void likePost(int index, String username) {
        synchronized(lock) {
            if(index >= 0 && index < posts.size()) {
                ServerPost p = posts.get(index);
                if(!p.likes.contains(username)) {
                    p.likes.add(username);
                    save();
                }
            }
        }
    }

    public static void unlikePost(int index, String username) {
        synchronized(lock) {
            if(index >= 0 && index < posts.size()) {
                ServerPost p = posts.get(index);
                p.likes.remove(username);
                save();
            }
        }
    }

    public static void addCommentToPost(int index, String author, String text) {
        synchronized(lock) {
            if(index >= 0 && index < posts.size()) {
                ServerPost p = posts.get(index);
                p.comments.add(new ServerComment(author, text));
                save();
            }
        }
    }

    public static JsonArray getPostsAsJson() {
        synchronized(lock) {
            return gson.toJsonTree(posts).getAsJsonArray();
        }
    }

    private static void save() {
        try(Writer w = new java.io.FileWriter(FILE)) {
            gson.toJson(posts, w);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadPosts() {
        try {
            if(new File(FILE).exists()) {
                Reader r = new java.io.FileReader(FILE);
                Type t = new TypeToken<List<ServerPost>>(){}.getType();
                List<ServerPost> loaded = gson.fromJson(r, t);
                if(loaded != null) posts = loaded;
                r.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

// ============================================================
// SERVER-SIDE MESSAGE STORE
// ============================================================
class ServerMessageStore {
    private static final String FILE = "data/messages.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static List<ServerMessage> messages = new ArrayList<>();
    private static final Object lock = new Object();

    static {
        loadMessages();
    }

    static class ServerMessage {
        String sender;
        String recipient;
        String text;
        long timestamp;
        boolean read = false;

        ServerMessage(String s, String r, String t) {
            sender = s;
            recipient = r;
            text = t;
            timestamp = System.currentTimeMillis();
        }
    }

    public static void sendMessage(String sender, String recipient, String text) {
        synchronized(lock) {
            messages.add(0, new ServerMessage(sender, recipient, text));
            save();
        }
    }

    public static JsonArray getConversationAsJson(String user1, String user2) {
        synchronized(lock) {
            List<ServerMessage> convo = new ArrayList<>();
            for(ServerMessage m : messages) {
                if((m.sender.equals(user1) && m.recipient.equals(user2)) ||
                   (m.sender.equals(user2) && m.recipient.equals(user1))) {
                    convo.add(m);
                }
            }
            return gson.toJsonTree(convo).getAsJsonArray();
        }
    }

    public static Set<String> getContacts(String username) {
        synchronized(lock) {
            Set<String> contacts = new HashSet<>();
            for(ServerMessage m : messages) {
                if(m.sender.equals(username)) contacts.add(m.recipient);
                if(m.recipient.equals(username)) contacts.add(m.sender);
            }
            return contacts;
        }
    }

    public static int getUnreadCount(String recipient) {
        synchronized(lock) {
            int count = 0;
            for(ServerMessage m : messages) {
                if(m.recipient.equals(recipient) && !m.read) count++;
            }
            return count;
        }
    }

    private static void save() {
        try(Writer w = new java.io.FileWriter(FILE)) {
            gson.toJson(messages, w);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadMessages() {
        try {
            if(new File(FILE).exists()) {
                Reader r = new java.io.FileReader(FILE);
                Type t = new TypeToken<List<ServerMessage>>(){}.getType();
                List<ServerMessage> loaded = gson.fromJson(r, t);
                if(loaded != null) messages = loaded;
                r.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

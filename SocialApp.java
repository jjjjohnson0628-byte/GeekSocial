import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.lang.reflect.Type;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

// ============================================================
// CROSS-PLATFORM UTILITIES
// ============================================================
class PlatformUtils {
    public static String getPath(String... parts) {
        return String.join(File.separator, parts);
    }
    
    public static void ensureDirectory(String dirPath) {
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public static String getOS() {
        return System.getProperty("os.name").toLowerCase();
    }
    
    public static boolean isWindows() {
        return getOS().contains("win");
    }
    
    public static boolean isMac() {
        return getOS().contains("mac");
    }
    
    public static boolean isLinux() {
        return getOS().contains("linux");
    }
}

// ============================================================
// MAIN APP
// ============================================================
public class SocialApp {
    public static void main(String[] args) {
        // Initialize JavaFX toolkit early
        new JFXPanel();
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

//=============================================================
// THEME/COLORS
//=============================================================
class theme { 
	public static final Color BackG = new Color(64, 36, 94);
	public static final Color ForeG = new Color(27, 94, 32);
	public static final Color Accent1 = new Color(56, 142, 60);
	public static final Color Accent2 = new Color(0, 200, 83);
	
}

// ============================================================
// MAIN WINDOW — CardLayout screens
// ============================================================
class MainWindow extends JFrame {
    private final CardLayout layout = new CardLayout();
    private final JPanel root = new JPanel(layout);
    private String currentUser;
    private JLabel networkStatus;

    MainWindow() {
        setTitle("GeekSocial - Online & Offline");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create menu bar with network controls
        JMenuBar menuBar = new JMenuBar();
        JMenu modeMenu = new JMenu("Mode");
        
        JCheckBoxMenuItem networkToggle = new JCheckBoxMenuItem("Enable Online Mode", false);
        networkToggle.addActionListener(e -> {
            boolean enabled = networkToggle.isSelected();
            NetworkClient.enableNetwork(enabled);
            updateNetworkStatus();
            if(enabled && !NetworkClient.isServerAvailable()) {
                JOptionPane.showMessageDialog(this, 
                    "Server not available!\n\n" +
                    "Local: http://127.0.0.1:8080\n" +
                    "Cloud: Set SOCIAL_APP_SERVER environment variable\n\n" +
                    "Make sure the server is running!", 
                    "Connection Error", JOptionPane.WARNING_MESSAGE);
                networkToggle.setSelected(false);
                NetworkClient.enableNetwork(false);
                updateNetworkStatus();
            }
        });
        modeMenu.add(networkToggle);
        menuBar.add(modeMenu);
        setJMenuBar(menuBar);

        // Add status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.setBackground(theme.BackG);
        networkStatus = new JLabel("✓ Local Mode");
        networkStatus.setForeground(theme.Accent2);
        networkStatus.setFont(new Font("Arial", Font.BOLD, 12));
        statusPanel.add(networkStatus);
        
        root.add(new LoginPanel(this), "login");
        root.add(new FeedPanel(this), "feed");
        root.add(new ProfilePanel(this), "profile");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(root, BorderLayout.CENTER);
        
        add(mainPanel);
        layout.show(root, "login");
    }

    void updateNetworkStatus() {
        if(NetworkClient.isNetworkEnabled()) {
            networkStatus.setText("⚡ Online Mode (Server Connected)");
            networkStatus.setForeground(new Color(0, 200, 83));
        } else {
            networkStatus.setText("✓ Local Mode");
            networkStatus.setForeground(theme.Accent2);
        }
    }

    void setCurrentUser(String u) { currentUser = u; }
    String getCurrentUser() { return currentUser; }

    void show(String screen) {
        layout.show(root, screen);
        if (screen.equals("feed")) ((FeedPanel)getPanel("feed")).refreshFeed();
        if (screen.equals("profile")) ((ProfilePanel)getPanel("profile")).loadUser(currentUser);
    }

    JPanel getPanel(String name) {
        for (Component c : root.getComponents()) {
            if (c instanceof JPanel && ((JPanel)c).getName() != null && ((JPanel)c).getName().equals(name))
                return (JPanel)c;
        }
        return null;
    }
}

// ============================================================
// LOGIN PANEL
// ============================================================
class LoginPanel extends JPanel {
    public LoginPanel(MainWindow window) {
        setLayout(null);
	setOpaque(true);
	setBackground(theme.BackG);
	setForeground(theme.ForeG);

        JLabel title = new JLabel("Welcome - Please Login");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(250, 80, 400, 40);
	title.setOpaque(true);
	title.setBackground(Color.BLACK);
    title.setForeground(theme.Accent1);
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(200, 200, 100, 30);
	userLabel.setOpaque(true);
	userLabel.setBackground(Color.BLACK);
    userLabel.setForeground(theme.Accent2);
        add(userLabel);

        JTextField username = new JTextField();
        username.setBounds(300, 200, 300, 30);
        add(username);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(200, 240, 100, 30);
	passLabel.setOpaque(true);
	passLabel.setBackground(Color.BLACK);
    passLabel.setForeground(theme.Accent2);
        add(passLabel);

        JPasswordField password = new JPasswordField();
        password.setBounds(300, 240, 300, 30);
        add(password);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(350, 280, 200, 40);
	loginBtn.setBackground(theme.ForeG);
	loginBtn.setForeground(theme.BackG);
        loginBtn.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword());
            if (UserStore.validate(u, p)) {
                window.setCurrentUser(u);
                window.show("feed");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(loginBtn);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(350, 330, 200, 35);
	signupBtn.setBackground(theme.ForeG);
	signupBtn.setForeground(theme.BackG);
        signupBtn.addActionListener(e -> SignupPanel.showSignup(window));
        add(signupBtn);
    }
}

// ============================================================
// SIGNUP PANEL
// ============================================================
class SignupPanel {
    public static void showSignup(MainWindow window) {
        JTextField usernameInput = new JTextField();
        JPasswordField passwordInput = new JPasswordField();
        JPasswordField passwordConfirm = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(6,1,5,5));
        panel.add(new JLabel("Username:")); panel.add(usernameInput);
        panel.add(new JLabel("Password:")); panel.add(passwordInput);
        panel.add(new JLabel("Confirm Password:")); panel.add(passwordConfirm);

        int result = JOptionPane.showConfirmDialog(
            window, panel, "Create Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return;

        String u = usernameInput.getText().trim();
        String p = new String(passwordInput.getPassword());
        String c = new String(passwordConfirm.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(window, "Username and password cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!p.equals(c)) {
            JOptionPane.showMessageDialog(window, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!UserStore.register(u,p)) {
            JOptionPane.showMessageDialog(window, "Username already taken", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(window, "Account created! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

// ============================================================
// USER STORE - Supports both Local and Network modes
// ============================================================
class UserStore {
    private static final File FILE = new File(PlatformUtils.getPath("data", "users.json"));
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, User> users = loadUsers();

    // Ensure all old users have passwordHash set and followers/following initialized
    static {
        PlatformUtils.ensureDirectory(PlatformUtils.getPath("data"));
        if (!NetworkClient.isNetworkEnabled()) {
            boolean updated = false;
            for (User u : users.values()) {
                if (u.passwordHash == null) { 
                    u.passwordHash = User.hash("changeme"); // default password for old users
                    updated = true;
                }
                if (u.followers == null) {
                    u.followers = new ArrayList<>();
                    updated = true;
                }
                if (u.following == null) {
                    u.following = new ArrayList<>();
                    updated = true;
                }
            }
            if (updated) save();
        }
    }

    public static boolean register(String username, String password) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                return NetworkClient.registerUser(username, password);
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        if (users.containsKey(username)) return false;
        User u = new User(username,password);
        users.put(username,u);
        save();
        return true;
    }

    public static boolean validate(String username, String password) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                return NetworkClient.loginUser(username, password) != null;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        User u = users.get(username);
        if (u == null || u.passwordHash == null) return false;
        return u.passwordHash.equals(User.hash(password));
    }

    public static User getUser(String username) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                JsonObject userJson = NetworkClient.getUser(username);
                if (userJson != null) {
                    User u = new User(userJson.get("username").getAsString(), "");
                    u.displayName = userJson.has("displayName") ? userJson.get("displayName").getAsString() : username;
                    u.profilePhoto = userJson.has("profilePhoto") ? userJson.get("profilePhoto").getAsString() : null;
                    if(userJson.has("followers")) {
                        u.followers = gson.fromJson(userJson.get("followers"), new TypeToken<java.util.List<String>>(){}.getType());
                    }
                    if(userJson.has("following")) {
                        u.following = gson.fromJson(userJson.get("following"), new TypeToken<java.util.List<String>>(){}.getType());
                    }
                    return u;
                }
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        return users.get(username);
    }
    
    public static java.util.Collection<User> getAllUsers() {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                JsonArray usersJson = NetworkClient.listUsers();
                java.util.List<User> userList = new ArrayList<>();
                for (JsonElement el : usersJson) {
                    JsonObject userJson = el.getAsJsonObject();
                    User u = new User(userJson.get("username").getAsString(), "");
                    u.displayName = userJson.has("displayName") ? userJson.get("displayName").getAsString() : u.username;
                    u.profilePhoto = userJson.has("profilePhoto") ? userJson.get("profilePhoto").getAsString() : null;
                    if(userJson.has("followers")) {
                        u.followers = gson.fromJson(userJson.get("followers"), new TypeToken<java.util.List<String>>(){}.getType());
                    }
                    if(userJson.has("following")) {
                        u.following = gson.fromJson(userJson.get("following"), new TypeToken<java.util.List<String>>(){}.getType());
                    }
                    userList.add(u);
                }
                return userList;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        return users.values();
    }

    public static void save() {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                // In network mode, updates happen through API calls
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
        
        try (Writer w = new FileWriter(FILE)) { gson.toJson(users, w); }
        catch(Exception e){ e.printStackTrace(); }
    }

    private static Map<String, User> loadUsers() {
        if (!FILE.exists()) return new HashMap<>();
        try (Reader r = new FileReader(FILE)) {
            Type t = new TypeToken<Map<String,User>>(){}.getType();
            return gson.fromJson(r, t);
        } catch(Exception e){ e.printStackTrace(); return new HashMap<>(); }
    }
}

// ============================================================
// USER
// ============================================================
class User {
    String username;
    String displayName;
    String passwordHash;
    String profilePhoto;
    java.util.List<String> followers = new ArrayList<>();
    java.util.List<String> following = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.displayName = username;
        this.passwordHash = hash(password);
        this.profilePhoto = null;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
    }

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(byte b: bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch(Exception e){ throw new RuntimeException(e); }
    }
}

// ============================================================
// PROFILE PANEL
// ============================================================
class ProfilePanel extends JPanel {
    private final MainWindow window;
    private JTextField displayNameField;
    private JLabel photoLabel;
    private String currentUser;

    ProfilePanel(MainWindow window) {
        this.window = window;
        setName("profile");
        setLayout(null);
	setOpaque(true);
	setBackground(theme.BackG);
	setForeground(theme.ForeG);

        JLabel title = new JLabel("Profile");
        title.setFont(new Font("Arial",Font.BOLD,26));
        title.setBounds(350,20,200,40);
        add(title);

        JLabel displayLabel = new JLabel("Display Name:");
        displayLabel.setBounds(200,100,100,30);
        add(displayLabel);

        displayNameField = new JTextField();
        displayNameField.setBounds(320,100,300,30);
        add(displayNameField);

        JLabel photoText = new JLabel("Profile Photo:");
        photoText.setBounds(200,150,100,30);
        add(photoText);

        photoLabel = new JLabel();
        photoLabel.setBounds(320,150,200,200);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(photoLabel);

        JButton changePhotoBtn = new JButton("Change Photo");
        changePhotoBtn.setBounds(530,150,140,30);
	changePhotoBtn.setBackground(theme.ForeG);
	changePhotoBtn.setForeground(theme.BackG);
        changePhotoBtn.addActionListener(e -> changePhoto());
        add(changePhotoBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(350,370,100,40);
	saveBtn.setBackground(theme.ForeG);
	saveBtn.setForeground(theme.BackG);
        saveBtn.addActionListener(e -> saveProfile());
        add(saveBtn);

        JButton feedBtn = new JButton("Return to Feed");
        feedBtn.setBounds(460,370,140,40);
	feedBtn.setBackground(theme.ForeG);
	feedBtn.setForeground(theme.BackG);
        feedBtn.addActionListener(e -> window.show("feed"));
        add(feedBtn);
    }

    void loadUser(String username) {
        currentUser = username;
        User u = UserStore.getUser(username);
        displayNameField.setText(u.displayName);
        if (u.profilePhoto!=null) {
            photoLabel.setIcon(new ImageIcon(new ImageIcon(u.profilePhoto).getImage().getScaledInstance(200,200,Image.SCALE_SMOOTH)));
        } else photoLabel.setIcon(null);
    }

    private void changePhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images","jpg","png","jpeg"));
        int result = chooser.showOpenDialog(this);
        if(result==JFileChooser.APPROVE_OPTION){
            User u = UserStore.getUser(currentUser);
            u.profilePhoto = chooser.getSelectedFile().getAbsolutePath();
            photoLabel.setIcon(new ImageIcon(new ImageIcon(u.profilePhoto).getImage().getScaledInstance(200,200,Image.SCALE_SMOOTH)));
            UserStore.save();
        }
    }

    private void saveProfile() {
        User u = UserStore.getUser(currentUser);
        u.displayName = displayNameField.getText().trim();
        UserStore.save();
        JOptionPane.showMessageDialog(this,"Profile updated!");
    }
}

// ============================================================
// CAMERA CAPTURE DIALOG - Photo and Video Recording
// ============================================================
class CameraCapture extends JDialog {
    private volatile BufferedImage currentFrame;
    private volatile boolean capturing = false;
    private volatile boolean videoRecording = false;
    private Thread cameraThread;
    private Object webcam;
    private String capturedFile;
    private boolean isPhotoMode;
    private java.util.List<BufferedImage> recordedFrames;
    
    CameraCapture(MainWindow parent, boolean photoMode) {
        super(parent, "Camera Capture", true);
        this.isPhotoMode = photoMode;
        this.recordedFrames = new ArrayList<>();
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(640, 540);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        JLabel cameraPanel = new JLabel();
        cameraPanel.setBackground(Color.BLACK);
        cameraPanel.setOpaque(true);
        cameraPanel.setHorizontalAlignment(JLabel.CENTER);
        cameraPanel.setVerticalAlignment(JLabel.CENTER);
        add(cameraPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(true);
        
        JButton captureBtn = new JButton(photoMode ? "📷 Capture Photo" : "🎥 Start Recording");
        captureBtn.setBackground(photoMode ? Color.BLUE : Color.RED);
        captureBtn.setForeground(Color.WHITE);
        captureBtn.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(Color.GRAY);
        cancelBtn.setForeground(Color.WHITE);
        
        buttonPanel.add(captureBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        capturing = true;
        startCamera();
        
        // Update camera view
        javax.swing.Timer updateTimer = new javax.swing.Timer(50, e -> {
            if(currentFrame != null) {
                Image scaled = currentFrame.getScaledInstance(cameraPanel.getWidth(), cameraPanel.getHeight(), Image.SCALE_SMOOTH);
                cameraPanel.setIcon(new ImageIcon(scaled));
            }
        });
        updateTimer.start();
        
        if(photoMode) {
            captureBtn.addActionListener(e -> {
                if(currentFrame != null) {
                    String photoFile = savePhoto();
                    if(photoFile != null) {
                        capturedFile = photoFile;
                        JOptionPane.showMessageDialog(this, "Photo saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        capturing = false;
                        updateTimer.stop();
                        dispose();
                    }
                }
            });
        } else {
            final JLabel recordingLabel = new JLabel("");
            recordingLabel.setForeground(Color.RED);
            recordingLabel.setFont(new Font("Arial", Font.BOLD, 12));
            buttonPanel.add(recordingLabel, 0);
            
            captureBtn.addActionListener(e -> {
                if(!videoRecording) {
                    videoRecording = true;
                    recordedFrames.clear();
                    captureBtn.setText("⏹ Stop Recording");
                    captureBtn.setBackground(Color.ORANGE);
                    recordingLabel.setText("Recording: 0s");
                    recordingLabel.setForeground(Color.RED);
                    recordingLabel.setFont(new Font("Arial", Font.BOLD, 12));
                    
                    // Auto-stop after 10 seconds
                    new Thread(() -> {
                        try {
                            for(int i = 0; i < 200; i++) { // 10 seconds at 20 FPS
                                if(!videoRecording) break;
                                int seconds = i / 20;
                                recordingLabel.setText("Recording: " + seconds + "s");
                                Thread.sleep(50);
                            }
                            if(videoRecording) {
                                videoRecording = false;
                                SwingUtilities.invokeLater(() -> captureBtn.doClick()); // Auto-stop
                            }
                        } catch(InterruptedException ex) {}
                    }).start();
                } else {
                    videoRecording = false;
                    recordingLabel.setText("");
                    String videoFile = saveVideo();
                    if(videoFile != null) {
                        capturedFile = videoFile;
                        JOptionPane.showMessageDialog(CameraCapture.this, "Video saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        capturing = false;
                        dispose();
                    }
                    captureBtn.setText("🎥 Start Recording");
                    captureBtn.setBackground(Color.RED);
                }
            });
        }
        
        cancelBtn.addActionListener(e -> {
            capturing = false;
            updateTimer.stop();
            dispose();
        });
        
        setVisible(true);
    }
    
    private void startCamera() {
        cameraThread = new Thread(() -> {
            try {
                Class<?> webcamClass = Class.forName("com.github.sarxos.webcam.Webcam");
                webcam = webcamClass.getMethod("getDefault").invoke(null);
                
                if(webcam != null) {
                    webcam.getClass().getMethod("open").invoke(webcam);
                    
                    while(capturing) {
                        try {
                            Object image = webcam.getClass().getMethod("getImage").invoke(webcam);
                            if(image instanceof BufferedImage) {
                                BufferedImage frame = (BufferedImage) image;
                                currentFrame = frame;
                                
                                // Record frame if in video mode and user is recording
                                if(!isPhotoMode && videoRecording && recordedFrames.size() < 200) {
                                    BufferedImage recordFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                                    Graphics2D g = recordFrame.createGraphics();
                                    g.drawImage(frame, 0, 0, null);
                                    g.dispose();
                                    recordedFrames.add(recordFrame);
                                }
                            }
                            Thread.sleep(50);
                        } catch(Exception e) {
                            Thread.sleep(100);
                        }
                    }
                    
                    if(webcam != null) {
                        webcam.getClass().getMethod("close").invoke(webcam);
                    }
                }
            } catch(Exception e) {
                System.out.println("Camera error: " + e.getMessage());
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();
    }
    
    private String savePhoto() {
        try {
            String photosDir = PlatformUtils.getPath("media", "photos");
            PlatformUtils.ensureDirectory(photosDir);
            
            String photoFile = PlatformUtils.getPath(photosDir, "photo_" + System.currentTimeMillis() + ".jpg");
            ImageIO.write(currentFrame, "jpg", new File(photoFile));
            System.out.println("✓ Photo saved: " + photoFile);
            return photoFile;
        } catch(Exception e) {
            System.out.println("✗ Error saving photo: " + e.getMessage());
            return null;
        }
    }
    
    private String saveVideo() {
        try {
            if(recordedFrames.isEmpty()) {
                System.out.println("✗ No frames recorded");
                return null;
            }
            
            String videosDir = PlatformUtils.getPath("media", "videos");
            PlatformUtils.ensureDirectory(videosDir);
            
            String videoFile = PlatformUtils.getPath(videosDir, "video_" + System.currentTimeMillis() + ".jpg");
            ImageIO.write(recordedFrames.get(recordedFrames.size() - 1), "jpg", new File(videoFile));
            System.out.println("✓ Video saved: " + videoFile + " (" + recordedFrames.size() + " frames)");
            return videoFile;
        } catch(Exception e) {
            System.out.println("✗ Error saving video: " + e.getMessage());
            return null;
        }
    }
    
    public String getCapturedFile() {
        return capturedFile;
    }
}

// ============================================================
// FEED PANEL
// ============================================================
class FeedPanel extends JPanel {
    private final MainWindow window;
    private final JPanel feedArea;
    private final DefaultListModel<String> liveUsersModel;
    private final JList<String> liveUsersList;
    private final JLabel liveVideoLabel;
    private String selectedLiveUser;
    private CameraFeed currentCameraFeed;

    FeedPanel(MainWindow window) {
        this.window = window;
        setName("feed");
        setLayout(new BorderLayout());
	setOpaque(true);

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT));
	nav.setOpaque(true);
        nav.setBackground(theme.Accent1);

        JButton profileBtn = new JButton("Profile");
	profileBtn.setBackground(theme.ForeG);
    profileBtn.setForeground(Color.BLACK);

        JButton logoutBtn = new JButton("Logout");
	logoutBtn.setBackground(theme.ForeG);
	logoutBtn.setForeground(Color.BLACK);
        
        JButton messagesBtn = new JButton("💬 Messages");
	messagesBtn.setBackground(new Color(100, 150, 255));
	messagesBtn.setForeground(Color.WHITE);
	
        JButton followersBtn = new JButton("👥 Followers");
	followersBtn.setBackground(new Color(100, 200, 150));
	followersBtn.setForeground(Color.WHITE);
	
        JButton goLiveBtn = new JButton("Go Live");
	goLiveBtn.setBackground(Color.BLUE);
        goLiveBtn.setForeground(theme.Accent1);
	
        JButton stopLiveBtn = new JButton("Stop Live");
	stopLiveBtn.setBackground(Color.RED);
	stopLiveBtn.setForeground(theme.Accent1);
	
        profileBtn.addActionListener(e -> window.show("profile"));
        logoutBtn.addActionListener(e -> window.show("login"));
        messagesBtn.addActionListener(e -> showMessagesPanel());
        followersBtn.addActionListener(e -> showFollowersPanel());
        goLiveBtn.addActionListener(e -> {
            LiveStore.goLive(window.getCurrentUser());
            CameraFeed feed = new CameraFeed(window.getCurrentUser());
            feed.startCamera();
            feed.startRecording();
            LiveStore.setCameraFeed(window.getCurrentUser(), feed);
            refreshLiveUsers();
        });
        stopLiveBtn.addActionListener(e -> {
            String currentUser = window.getCurrentUser();
            CameraFeed feed = LiveStore.getCameraFeed(currentUser);
            if(feed != null) {
                feed.stopCamera();
                String videoFile = feed.finishRecording();
                if(videoFile != null && !videoFile.isEmpty()) {
                    PostStore.addPost(new Post(currentUser, "Live Stream Replay", videoFile));
                    JOptionPane.showMessageDialog(this, "Live video saved as post!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshFeed();
                }
            }
            LiveStore.stopLive(currentUser);
            LiveStore.setCameraFeed(currentUser, null);
            refreshLiveUsers();
        });

        nav.add(profileBtn);
        nav.add(messagesBtn);
        nav.add(followersBtn);
        nav.add(logoutBtn);
        nav.add(goLiveBtn);
        nav.add(stopLiveBtn);
        add(nav,BorderLayout.NORTH);

        feedArea = new JPanel();
        feedArea.setLayout(new BoxLayout(feedArea,BoxLayout.Y_AXIS));
	feedArea.setOpaque(true);
        JScrollPane feedScroll = new JScrollPane(feedArea);
	feedScroll.setBackground(theme.BackG);
	feedScroll.setForeground(theme.ForeG);
	
        add(feedScroll,BorderLayout.CENTER);

        // LIVE VIDEO PANEL
        JPanel liveVideoPanel = new JPanel(new BorderLayout());
        liveVideoPanel.setBackground(Color.BLACK);
        liveVideoPanel.setBorder(BorderFactory.createTitledBorder("Live Video"));
        liveVideoPanel.setPreferredSize(new Dimension(300, 250));
        
        liveVideoLabel = new JLabel("No live user selected");
        liveVideoLabel.setHorizontalAlignment(JLabel.CENTER);
        liveVideoLabel.setVerticalAlignment(JLabel.CENTER);
        liveVideoLabel.setForeground(Color.WHITE);
        liveVideoLabel.setOpaque(true);
        liveVideoLabel.setBackground(Color.BLACK);
        liveVideoPanel.add(liveVideoLabel, BorderLayout.CENTER);

        liveUsersModel = new DefaultListModel<>();
        liveUsersList = new JList<>(liveUsersModel);
        liveUsersList.setBorder(BorderFactory.createTitledBorder("Live Users - Click to Watch"));
	liveUsersList.setBackground(Color.BLACK);
	liveUsersList.setForeground(theme.Accent2);
	liveUsersList.addListSelectionListener(e -> {
	    String selected = liveUsersList.getSelectedValue();
	    if(selected != null && !selected.isEmpty()) {
	        selectedLiveUser = selected;
	        CameraFeed feed = LiveStore.getCameraFeed(selected);
	        if(feed != null && feed.isRunning()) {
	            currentCameraFeed = feed;
	            liveVideoLabel.setText("Watching: " + selected);
	            // Start updating video
	            updateLiveVideo();
	        } else {
	            liveVideoLabel.setText("Waiting for " + selected + " stream...");
	        }
	    }
	});
	
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(liveVideoPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(liveUsersList), BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        JTextArea postInput = new JTextArea(3,30);
	postInput.setOpaque(true);
	postInput.setBackground(Color.BLACK);
	postInput.setForeground(theme.Accent2);
	
        JButton postBtn = new JButton("Post");
	postBtn.setBackground(theme.ForeG);
	postBtn.setForeground(Color.WHITE);
	
        JButton mediaBtn = new JButton("Add Media");
	mediaBtn.setBackground(theme.ForeG);
	mediaBtn.setForeground(Color.WHITE);
	
        JButton takePhotoBtn = new JButton("📷 Take Photo");
	takePhotoBtn.setBackground(Color.BLUE);
	takePhotoBtn.setForeground(Color.WHITE);
	
        JButton recordVideoBtn = new JButton("🎥 Record Video");
	recordVideoBtn.setBackground(Color.RED);
	recordVideoBtn.setForeground(Color.WHITE);
	
        final String[] selectedMedia = {null};

        mediaBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int r = chooser.showOpenDialog(this);
            if(r==JFileChooser.APPROVE_OPTION) selectedMedia[0]=chooser.getSelectedFile().getAbsolutePath();
        });
        
        takePhotoBtn.addActionListener(e -> {
            CameraCapture captureDialog = new CameraCapture(window, true); // Photo mode
            String photoFile = captureDialog.getCapturedFile();
            if(photoFile != null) {
                selectedMedia[0] = photoFile;
                JOptionPane.showMessageDialog(this, "Photo captured! Click Post to share.", "Photo Captured", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        recordVideoBtn.addActionListener(e -> {
            CameraCapture captureDialog = new CameraCapture(window, false); // Video mode
            String videoFile = captureDialog.getCapturedFile();
            if(videoFile != null) {
                selectedMedia[0] = videoFile;
                JOptionPane.showMessageDialog(this, "Video recorded! Click Post to share.", "Video Recorded", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        postBtn.addActionListener(e -> {
            String text = postInput.getText().trim();
            File mediaFile = selectedMedia[0]!=null ? new File(selectedMedia[0]) : null;
            if(!text.isEmpty() || mediaFile!=null){
                PostStore.addPost(new Post(window.getCurrentUser(), text, mediaFile!=null?mediaFile.getAbsolutePath():null));
                postInput.setText(""); selectedMedia[0]=null;
                refreshFeed();
            }
        });

        JPanel mediaButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mediaButtonsPanel.setOpaque(true);
        mediaButtonsPanel.setBackground(theme.BackG);
        mediaButtonsPanel.add(mediaBtn);
        mediaButtonsPanel.add(takePhotoBtn);
        mediaButtonsPanel.add(recordVideoBtn);

        JPanel postBox = new JPanel(new BorderLayout());
	postBox.setOpaque(true);
        postBox.add(mediaButtonsPanel, BorderLayout.WEST);
        postBox.add(new JScrollPane(postInput),BorderLayout.CENTER);
        postBox.add(postBtn,BorderLayout.EAST);
        add(postBox,BorderLayout.SOUTH);

        // Timer to refresh live video
        javax.swing.Timer liveVideoTimer = new javax.swing.Timer(100, e -> updateLiveVideo());
        liveVideoTimer.start();

        refreshFeed(); 
        refreshLiveUsers();
    }

    private void updateLiveVideo() {
        if(currentCameraFeed != null && currentCameraFeed.isRunning()) {
            BufferedImage frame = currentCameraFeed.getLatestFrame();
            if(frame != null) {
                Image scaledImg = frame.getScaledInstance(280, 210, Image.SCALE_SMOOTH);
                liveVideoLabel.setIcon(new ImageIcon(scaledImg));
                liveVideoLabel.setText("");
            }
        }
    }

    void refreshFeed() {
        feedArea.removeAll();
        java.util.List<Post> posts = PostStore.getPosts();
        String currentUser = window.getCurrentUser();
        
        for(int postIdx = 0; postIdx < posts.size(); postIdx++){
            Post post = posts.get(postIdx);
            final int finalIdx = postIdx;
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            User u = UserStore.getUser(post.author);
            panel.setOpaque(true);
            panel.setBackground(theme.BackG);
            panel.setForeground(theme.Accent2);
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Author header with prominent styling
            JPanel authorPanel = new JPanel(new BorderLayout());
            authorPanel.setOpaque(true);
            authorPanel.setBackground(new Color(180, 240, 180));
            authorPanel.setForeground(Color.BLACK);
            authorPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            
            JLabel authorLabel = new JLabel(u.displayName);
            authorLabel.setForeground(Color.BLACK);
            authorLabel.setFont(new Font("Arial", Font.BOLD, 16));
            authorPanel.add(authorLabel, BorderLayout.CENTER);
            panel.add(authorPanel);
            panel.add(Box.createVerticalStrut(8));

            if(post.text != null && !post.text.isEmpty()) {
                JPanel textPanel = new JPanel(new BorderLayout());
                textPanel.setOpaque(true);
                textPanel.setBackground(theme.BackG);
                textPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                
                JLabel textLabel = new JLabel("<html><span style='font-size: 14px; font-weight: bold;'>" + post.text + "</span></html>");
                textLabel.setForeground(theme.Accent2);
                textLabel.setFont(new Font("Arial", Font.BOLD, 14));
                textPanel.add(textLabel, BorderLayout.CENTER);
                panel.add(textPanel);
            }

            if(post.media != null){
                File f = new File(post.media);
                if(f.exists()){
                    String lower = post.media.toLowerCase();
                    try{
                        if(lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".gif")){
                            panel.add(new JLabel(new ImageIcon(new ImageIcon(post.media).getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH))));
                        } else if(lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".m4v")){
                            JFXPanel fxPanel = new JFXPanel();
                            fxPanel.setPreferredSize(new Dimension(300, 225));
                            panel.add(fxPanel);
                            Platform.runLater(() -> {
                                try {
                                    MediaPlayer player = new MediaPlayer(new Media(f.toURI().toString()));
                                    MediaView view = new MediaView(player);
                                    view.setFitWidth(300);
                                    view.setPreserveRatio(true);
                                    fxPanel.setScene(new Scene(new Group(view)));
                                    player.setAutoPlay(false);
                                } catch(Exception ex) {
                                    System.out.println("✗ Error loading video: " + ex.getMessage());
                                }
                            });
                        } else panel.add(new JLabel("Media: " + post.media));
                    } catch(Exception ex){
                        panel.add(new JLabel("Error loading media"));
                    }
                }
            }

            // Likes and Comments Section
            JPanel interactionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            interactionPanel.setOpaque(true);
            interactionPanel.setBackground(theme.BackG);
            
            boolean userLiked = post.likes.contains(currentUser);
            JButton likeBtn = new JButton("❤ " + post.likes.size() + (userLiked ? " (Liked)" : ""));
            likeBtn.setBackground(userLiked ? theme.Accent1 : theme.ForeG);
            likeBtn.setForeground(theme.BackG);
            final int postIdxForLike = postIdx;
            likeBtn.addActionListener(e -> {
                if(userLiked) {
                    PostStore.unlikePost(postIdxForLike, currentUser);
                } else {
                    PostStore.likePost(postIdxForLike, currentUser);
                }
                refreshFeed();
            });
            interactionPanel.add(likeBtn);
            
            JButton commentBtn = new JButton("💬 " + post.comments.size() + " Comments");
            commentBtn.setBackground(theme.Accent1);
            commentBtn.setForeground(theme.BackG);
            final int postIdxForComment = postIdx;
            commentBtn.addActionListener(e -> showCommentDialog(postIdxForComment, post));
            interactionPanel.add(commentBtn);
            
            panel.add(Box.createVerticalStrut(5));
            panel.add(interactionPanel);
            
            // Display existing comments
            if(!post.comments.isEmpty()) {
                JPanel commentsPanel = new JPanel();
                commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
                commentsPanel.setOpaque(true);
                commentsPanel.setBackground(theme.BackG);
                commentsPanel.setBorder(BorderFactory.createTitledBorder("Comments"));
                
                for(Comment c : post.comments) {
                    JLabel commentLabel = new JLabel("<html><b>" + c.author + ":</b> " + c.text + "</html>");
                    commentLabel.setForeground(theme.Accent2);
                    commentsPanel.add(commentLabel);
                }
                panel.add(commentsPanel);
            }

            feedArea.add(panel);
            feedArea.add(Box.createVerticalStrut(15));
        }
        feedArea.revalidate();
        feedArea.repaint();
    }
    
    private void showCommentDialog(int postIdx, Post post) {
        JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Comments", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(theme.BackG);
        
        JPanel commentsDisplay = new JPanel();
        commentsDisplay.setLayout(new BoxLayout(commentsDisplay, BoxLayout.Y_AXIS));
        commentsDisplay.setOpaque(true);
        commentsDisplay.setBackground(theme.BackG);
        
        for(Comment c : post.comments) {
            JLabel label = new JLabel("<html><b>" + c.author + ":</b> " + c.text + "</html>");
            label.setForeground(theme.Accent2);
            commentsDisplay.add(label);
            commentsDisplay.add(Box.createVerticalStrut(5));
        }
        
        JScrollPane scroll = new JScrollPane(commentsDisplay);
        scroll.setBackground(theme.BackG);
        dialog.add(scroll, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(true);
        inputPanel.setBackground(theme.BackG);
        
        JTextArea commentInput = new JTextArea(3, 40);
        commentInput.setOpaque(true);
        commentInput.setBackground(theme.BackG);
        commentInput.setForeground(theme.Accent2);
        
        JButton submitBtn = new JButton("Post Comment");
        submitBtn.setBackground(theme.Accent1);
        submitBtn.setForeground(theme.BackG);
        submitBtn.addActionListener(e -> {
            String text = commentInput.getText().trim();
            if(!text.isEmpty()) {
                Comment c = new Comment(window.getCurrentUser(), text);
                PostStore.addCommentToPost(postIdx, c);
                refreshFeed();
                dialog.dispose();
            }
        });
        
        inputPanel.add(new JScrollPane(commentInput), BorderLayout.CENTER);
        inputPanel.add(submitBtn, BorderLayout.EAST);
        dialog.add(inputPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showMessagesPanel() {
        JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Messages", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(theme.BackG);
        dialog.setBackground(theme.BackG);
        
        String currentUser = window.getCurrentUser();
        java.util.Set<String> contacts = MessageStore.getContacts(currentUser);
        
        JPanel contactsPanel = new JPanel();
        contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
        contactsPanel.setOpaque(true);
        contactsPanel.setBackground(theme.BackG);
        
        JTextArea messageArea = new JTextArea(15, 50);
        messageArea.setEditable(false);
        messageArea.setOpaque(true);
        messageArea.setBackground(theme.BackG);
        messageArea.setForeground(theme.Accent2);
        JScrollPane messageScroll = new JScrollPane(messageArea);
        
        for(String contact : contacts) {
            JButton contactBtn = new JButton(contact);
            int unreadCount = MessageStore.getUnreadCount(contact);
            if(unreadCount > 0) {
                contactBtn.setText(contact + " (" + unreadCount + " unread)");
                contactBtn.setForeground(theme.Accent2);
            }
            contactBtn.setBackground(theme.ForeG);
            contactBtn.setForeground(theme.BackG);
            contactBtn.addActionListener(e -> {
                java.util.List<Message> convo = MessageStore.getConversation(currentUser, contact);
                messageArea.setText("");
                for(Message m : convo) {
                    messageArea.append((m.sender.equals(currentUser) ? "You" : m.sender) + ": " + m.text + "\n");
                }
                MessageStore.markAsRead(contact, currentUser);
            });
            contactsPanel.add(contactBtn);
            contactsPanel.add(Box.createVerticalStrut(3));
        }
        
        dialog.add(new JScrollPane(contactsPanel), BorderLayout.WEST);
        dialog.add(messageScroll, BorderLayout.CENTER);
        
        // Get list of all users for sending messages
        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.setOpaque(true);
        sendPanel.setBackground(theme.BackG);
        
        JComboBox<String> userList = new JComboBox<>();
        userList.setBackground(theme.ForeG);
        userList.setForeground(theme.BackG);
        for(User u : UserStore.getAllUsers()) {
            if(!u.username.equals(currentUser)) {
                userList.addItem(u.username);
            }
        }
        
        JTextArea msgInput = new JTextArea(3, 40);
        msgInput.setOpaque(true);
        msgInput.setBackground(theme.BackG);
        msgInput.setForeground(theme.Accent2);
        
        JButton sendBtn = new JButton("Send");
        sendBtn.setBackground(theme.Accent1);
        sendBtn.setForeground(theme.BackG);
        sendBtn.addActionListener(e -> {
            String recipient = (String) userList.getSelectedItem();
            String text = msgInput.getText().trim();
            if(recipient != null && !text.isEmpty()) {
                Message msg = new Message(currentUser, recipient, text);
                MessageStore.sendMessage(msg);
                msgInput.setText("");
                messageArea.append("You: " + text + "\n");
            }
        });
        
        JPanel composerSub = new JPanel(new BorderLayout());
        composerSub.setOpaque(true);
        composerSub.setBackground(theme.BackG);
        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(theme.Accent2);
        composerSub.add(toLabel, BorderLayout.WEST);
        composerSub.add(userList, BorderLayout.CENTER);
        
        sendPanel.add(composerSub, BorderLayout.NORTH);
        sendPanel.add(new JScrollPane(msgInput), BorderLayout.CENTER);
        sendPanel.add(sendBtn, BorderLayout.EAST);
        
        dialog.add(sendPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showFollowersPanel() {
        JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Followers & Following", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(1, 2));
        dialog.setBackground(theme.BackG);
        
        String currentUser = window.getCurrentUser();
        final User user = UserStore.getUser(currentUser);
        if(user == null) {
            User newUser = new User(currentUser, "default");
            newUser.followers = new ArrayList<>();
            newUser.following = new ArrayList<>();
            showFollowersPanel();
            return;
        }
        
        // Followers panel
        JPanel followersPanel = new JPanel();
        followersPanel.setLayout(new BoxLayout(followersPanel, BoxLayout.Y_AXIS));
        followersPanel.setOpaque(true);
        followersPanel.setBackground(theme.BackG);
        followersPanel.setBorder(BorderFactory.createTitledBorder("Followers (" + user.followers.size() + ")"));
        
        for(String follower : user.followers) {
            JLabel label = new JLabel(follower);
            label.setForeground(theme.Accent2);
            followersPanel.add(label);
        }
        
        // Following panel
        JPanel followingPanel = new JPanel();
        followingPanel.setLayout(new BoxLayout(followingPanel, BoxLayout.Y_AXIS));
        followingPanel.setOpaque(true);
        followingPanel.setBackground(theme.BackG);
        followingPanel.setBorder(BorderFactory.createTitledBorder("Following (" + user.following.size() + ")"));
        
        for(String followed : user.following) {
            final String followedUser = followed;
            JPanel followBtn = new JPanel(new FlowLayout(FlowLayout.LEFT));
            followBtn.setOpaque(true);
            followBtn.setBackground(theme.BackG);
            JLabel label = new JLabel(followed);
            label.setForeground(theme.Accent2);
            JButton unfollowBtn = new JButton("Unfollow");
            unfollowBtn.setBackground(theme.ForeG);
            unfollowBtn.setForeground(theme.BackG);
            unfollowBtn.addActionListener(e -> {
                user.following.remove(followedUser);
                User followedUserObj = UserStore.getUser(followedUser);
                if(followedUserObj != null) followedUserObj.followers.remove(currentUser);
                UserStore.save();
                showFollowersPanel();
                dialog.dispose();
            });
            followBtn.add(label);
            followBtn.add(unfollowBtn);
            followingPanel.add(followBtn);
        }
        
        dialog.add(new JScrollPane(followersPanel));
        dialog.add(new JScrollPane(followingPanel));
        dialog.setVisible(true);
    }

    void refreshLiveUsers() {
        liveUsersModel.clear();
        for(String u: LiveStore.getLiveUsers()) liveUsersModel.addElement(u);
    }
}

// ============================================================
// POST & POSTSTORE
// ============================================================
class Post {
    String author; 
    String text; 
    String media;
    java.util.List<Comment> comments = new ArrayList<>();
    java.util.List<String> likes = new ArrayList<>();
    long timestamp = System.currentTimeMillis();
    
    public Post(String a, String t, String m) { 
        author = a; 
        text = t; 
        media = m;
        if(comments == null) comments = new ArrayList<>();
        if(likes == null) likes = new ArrayList<>();
    }
}

// ============================================================
// COMMENT CLASS
// ============================================================
class Comment {
    String author;
    String text;
    long timestamp;
    public Comment(String a, String t){ author=a; text=t; timestamp=System.currentTimeMillis(); }
}

// ============================================================
// MESSAGE CLASS
// ============================================================
class Message {
    String sender;
    String recipient;
    String text;
    long timestamp;
    boolean read = false;
    public Message(String s, String r, String t){ sender=s; recipient=r; text=t; timestamp=System.currentTimeMillis(); }
}

class PostStore {
    private static final File FILE = new File(PlatformUtils.getPath("data", "posts.json"));
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static java.util.List<Post> POSTS = loadPosts();

    static {
        PlatformUtils.ensureDirectory(PlatformUtils.getPath("data"));
        if (!NetworkClient.isNetworkEnabled()) {
            // Initialize all posts with null-safe collections
            for(Post p : POSTS) {
                if(p.likes == null) p.likes = new ArrayList<>();
                if(p.comments == null) p.comments = new ArrayList<>();
            }
        }
    }

    public static void addPost(Post p){
        try {
            if (NetworkClient.isNetworkEnabled()) {
                NetworkClient.createPost(p.author, p.text, p.media);
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        if(p.likes == null) p.likes = new ArrayList<>();
        if(p.comments == null) p.comments = new ArrayList<>();
        POSTS.add(0, p);
        savePosts();
    }

    public static java.util.List<Post> getPosts(){
        try {
            if (NetworkClient.isNetworkEnabled()) {
                JsonArray postsJson = NetworkClient.listPosts();
                java.util.List<Post> postList = new ArrayList<>();
                for (JsonElement el : postsJson) {
                    JsonObject postJson = el.getAsJsonObject();
                    String author = postJson.get("author").getAsString();
                    String text = postJson.has("text") ? postJson.get("text").getAsString() : "";
                    String media = postJson.has("media") ? postJson.get("media").getAsString() : null;
                    Post post = new Post(author, text, media);
                    
                    if(postJson.has("likes")) {
                        post.likes = gson.fromJson(postJson.get("likes"), new TypeToken<java.util.List<String>>(){}.getType());
                    }
                    if(postJson.has("comments")) {
                        post.comments = gson.fromJson(postJson.get("comments"), new TypeToken<java.util.List<Comment>>(){}.getType());
                    }
                    postList.add(post);
                }
                return postList;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        // Ensure null-safety on retrieval
        for(Post p : POSTS) {
            if(p.likes == null) p.likes = new ArrayList<>();
            if(p.comments == null) p.comments = new ArrayList<>();
        }
        return new ArrayList<>(POSTS); 
    }
    
    public static void addCommentToPost(int postIndex, Comment c) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                NetworkClient.commentPost(postIndex, c.author, c.text);
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        if(postIndex >= 0 && postIndex < POSTS.size()) {
            Post p = POSTS.get(postIndex);
            if(p.comments == null) p.comments = new ArrayList<>();
            p.comments.add(c);
            savePosts();
        }
    }
    
    public static void likePost(int postIndex, String username) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                NetworkClient.likePost(postIndex, username);
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        if(postIndex >= 0 && postIndex < POSTS.size()) {
            Post p = POSTS.get(postIndex);
            if(p.likes == null) p.likes = new ArrayList<>();
            if(!p.likes.contains(username)) {
                p.likes.add(username);
                savePosts();
            }
        }
    }
    
    public static void unlikePost(int postIndex, String username) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                NetworkClient.unlikePost(postIndex, username);
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        if(postIndex >= 0 && postIndex < POSTS.size()) {
            Post p = POSTS.get(postIndex);
            if(p.likes == null) p.likes = new ArrayList<>();
            p.likes.remove(username);
            savePosts();
        }
    }

    private static void savePosts(){
        try {
            if (NetworkClient.isNetworkEnabled()) {
                // In network mode, updates happen through API calls
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
        
        try(Writer w = new FileWriter(FILE)){ gson.toJson(POSTS, w); } catch(Exception e){ e.printStackTrace(); }
    }

    private static java.util.List<Post> loadPosts(){
        if(!FILE.exists()) return new ArrayList<>();
        try(Reader r = new FileReader(FILE)){
            Type t = new TypeToken<java.util.List<Post>>(){}.getType();
            java.util.List<Post> loaded = gson.fromJson(r, t);
            if(loaded == null) return new ArrayList<>();
            return loaded;
        } catch(Exception e){ e.printStackTrace(); return new ArrayList<>(); }
    }
}

// ============================================================
// MESSAGE STORE
// ============================================================
class MessageStore {
    private static final File FILE = new File(PlatformUtils.getPath("data", "messages.json"));
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static java.util.List<Message> MESSAGES = loadMessages();
    
    static {
        PlatformUtils.ensureDirectory(PlatformUtils.getPath("data"));
    }
    
    public static void sendMessage(Message m) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                NetworkClient.sendMessage(m.sender, m.recipient, m.text);
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        MESSAGES.add(0, m);
        saveMessages();
    }
    
    public static java.util.List<Message> getConversation(String user1, String user2) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                JsonArray messagesJson = NetworkClient.getConversation(user1, user2);
                java.util.List<Message> convo = new ArrayList<>();
                for (JsonElement el : messagesJson) {
                    JsonObject msgJson = el.getAsJsonObject();
                    Message msg = new Message(
                        msgJson.get("sender").getAsString(),
                        msgJson.get("recipient").getAsString(),
                        msgJson.get("text").getAsString()
                    );
                    msg.timestamp = msgJson.get("timestamp").getAsLong();
                    if(msgJson.has("read")) {
                        msg.read = msgJson.get("read").getAsBoolean();
                    }
                    convo.add(msg);
                }
                return convo;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        java.util.List<Message> convo = new ArrayList<>();
        for(Message m : MESSAGES) {
            if((m.sender.equals(user1) && m.recipient.equals(user2)) || 
               (m.sender.equals(user2) && m.recipient.equals(user1))) {
                convo.add(m);
            }
        }
        return convo;
    }
    
    public static int getUnreadCount(String recipient) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                return NetworkClient.getUnreadCount(recipient);
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        int count = 0;
        for(Message m : MESSAGES) {
            if(m.recipient.equals(recipient) && !m.read) count++;
        }
        return count;
    }
    
    public static void markAsRead(String sender, String recipient) {
        for(Message m : MESSAGES) {
            if(m.sender.equals(sender) && m.recipient.equals(recipient) && !m.read) {
                m.read = true;
            }
        }
        saveMessages();
    }
    
    public static java.util.Set<String> getContacts(String username) {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                return NetworkClient.getContacts(username);
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage() + " - falling back to local mode");
        }
        
        java.util.Set<String> contacts = new HashSet<>();
        for(Message m : MESSAGES) {
            if(m.sender.equals(username)) contacts.add(m.recipient);
            if(m.recipient.equals(username)) contacts.add(m.sender);
        }
        return contacts;
    }
    
    private static void saveMessages() {
        try {
            if (NetworkClient.isNetworkEnabled()) {
                // In network mode, updates happen through API calls
                return;
            }
        } catch(Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
        
        try(Writer w = new FileWriter(FILE)) {
            gson.toJson(MESSAGES, w);
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    private static java.util.List<Message> loadMessages() {
        if(!FILE.exists()) return new ArrayList<>();
        try(Reader r = new FileReader(FILE)) {
            Type t = new TypeToken<java.util.List<Message>>(){}.getType();
            return gson.fromJson(r, t);
        } catch(Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }
}

// ============================================================
// CAMERA FEED - Real Webcam Integration with Video Recording
// ============================================================
class CameraFeed {
    private String username;
    private volatile BufferedImage latestFrame;
    private volatile boolean isRunning = false;
    private Thread cameraThread;
    private Object webcam; // com.github.sarxos.webcam.Webcam instance
    private boolean webCamAvailable = true;
    
    private volatile boolean isRecording = false;
    private java.util.List<BufferedImage> recordedFrames = new ArrayList<>();
    private static final String VIDEOS_DIR = PlatformUtils.getPath("media", "videos");

    CameraFeed(String username) {
        this.username = username;
        PlatformUtils.ensureDirectory(VIDEOS_DIR);
    }

    public void startRecording() {
        isRecording = true;
        recordedFrames.clear();
        System.out.println("▶ Recording started for: " + username);
    }
    
    public String finishRecording() {
        isRecording = false;
        if(recordedFrames.isEmpty()) {
            System.out.println("✗ No frames recorded.");
            return null;
        }
        
        // Create videos directory if not exists
        PlatformUtils.ensureDirectory(VIDEOS_DIR);
        
        long timestamp = System.currentTimeMillis();
        String videoFile = PlatformUtils.getPath(VIDEOS_DIR, username + "_" + timestamp + ".mp4");
        System.out.println("💾 Saving " + recordedFrames.size() + " frames to video...");
        
        try {
            // Try to save using external ffmpeg
            if(encodeVideoWithFFmpeg(videoFile)) {
                System.out.println("✓ Video saved: " + videoFile);
                return videoFile;
            } else {
                // Fallback: save as image sequence or single image
                String imageFile = PlatformUtils.getPath(VIDEOS_DIR, username + "_" + timestamp + ".jpg");
                if(recordedFrames.size() > 0) {
                    File imageFileObj = new File(imageFile);
                    javax.imageio.ImageIO.write(recordedFrames.get(recordedFrames.size() - 1), "jpg", imageFileObj);
                    boolean exists = imageFileObj.exists();
                    System.out.println("✓ Frame snapshot saved: " + imageFile + " | File exists: " + exists);
                    return imageFile;
                }
            }
        } catch(Exception e) {
            System.out.println("✗ Error saving video: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean encodeVideoWithFFmpeg(String outputFile) {
        // Try to use ffmpeg if available
        try {
            // Save frames to temporary directory first
            String framesDir = PlatformUtils.getPath("temp", "frames_" + System.currentTimeMillis());
            PlatformUtils.ensureDirectory(framesDir);
            
            for(int i = 0; i < recordedFrames.size(); i++) {
                String framePath = PlatformUtils.getPath(framesDir, "frame_" + String.format("%06d", i) + ".jpg");
                javax.imageio.ImageIO.write(recordedFrames.get(i), "jpg", new File(framePath));
            }
            
            // Use ffmpeg to encode with cross-platform path handling
            String framePattern = PlatformUtils.getPath(framesDir, "frame_%06d.jpg");
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-framerate", "20", "-i", framePattern, 
                "-c:v", "libx264", "-pix_fmt", "yuv420p", outputFile
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // Wait for completion
            int exitCode = process.waitFor();
            
            // Clean up temp frames
            java.nio.file.Files.walk(java.nio.file.Paths.get(framesDir))
                .map(java.nio.file.Path::toFile)
                .forEach(File::delete);
            new File(framesDir).delete();
            
            return exitCode == 0;
        } catch(Exception e) {
            System.out.println("FFmpeg not available or error: " + e.getMessage());
            return false;
        }
    }

    public void startCamera() {
        if(isRunning) return;
        isRunning = true;
        cameraThread = new Thread(() -> {
            try {
                System.out.println("\n=== Starting Camera for user: " + username + " ===");
                // Try to use real webcam
                if(!initializeRealWebcam()) {
                    // Fallback to test pattern if no webcam available
                    System.out.println("⚠ Falling back to animated test pattern.");
                    webCamAvailable = false;
                    simulateCamera();
                    return;
                }
                
                System.out.println("✓ Real webcam initialized. Starting frame capture...");
                // Capture frames from real webcam
                while(isRunning) {
                    try {
                        Object image = webcam.getClass().getMethod("getImage").invoke(webcam);
                        if(image instanceof BufferedImage) {
                            BufferedImage frame = (BufferedImage) image;
                            // Add username label to frame
                            Graphics2D g2d = frame.createGraphics();
                            g2d.setColor(new Color(0, 0, 0, 128));
                            g2d.fillRect(0, frame.getHeight() - 30, frame.getWidth(), 30);
                            g2d.setColor(Color.WHITE);
                            g2d.setFont(new Font("Arial", Font.BOLD, 14));
                            g2d.drawString("LIVE: " + username, 10, frame.getHeight() - 10);
                            g2d.dispose();
                            latestFrame = frame;
                            
                            // Record frame if recording is active
                            if(isRecording && recordedFrames.size() < 3000) { // Limit to ~2.5 min at 20fps
                                BufferedImage recordFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                                Graphics2D g = recordFrame.createGraphics();
                                g.drawImage(frame, 0, 0, null);
                                g.dispose();
                                recordedFrames.add(recordFrame);
                            }
                        }
                        Thread.sleep(50); // ~20 FPS
                    } catch(Exception e) {
                        System.out.println("✗ Webcam frame error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                        e.printStackTrace();
                        Thread.sleep(100);
                    }
                }
                
                // Close webcam
                try {
                    System.out.println("Closing webcam...");
                    webcam.getClass().getMethod("close").invoke(webcam);
                    System.out.println("✓ Webcam closed.");
                } catch(Exception e) {
                    System.out.println("✗ Error closing webcam: " + e.getMessage());
                }
            } catch(Exception e) {
                System.out.println("✗ Webcam fatal error: " + e.getMessage());
                e.printStackTrace();
                isRunning = false;
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    private boolean initializeRealWebcam() {
        try {
            System.out.println("1. Attempting to load Webcam class...");
            Class<?> webcamClass = Class.forName("com.github.sarxos.webcam.Webcam");
            System.out.println("   ✓ Webcam class loaded: " + webcamClass.getName());
            
            System.out.println("2. Getting default webcam...");
            Object defaultWebcam = webcamClass.getMethod("getDefault").invoke(null);
            
            if(defaultWebcam == null) {
                System.out.println("   ✗ No default webcam found (getDefault() returned null)");
                
                // Try to list all webcams
                try {
                    System.out.println("3. Attempting to list all available webcams...");
                    Object webcamList = webcamClass.getMethod("getWebcams").invoke(null);
                    System.out.println("   Available webcams: " + webcamList);
                    if(webcamList != null && webcamList.getClass().getName().contains("List")) {
                        java.util.List<?> list = (java.util.List<?>) webcamList;
                        System.out.println("   Total webcams found: " + list.size());
                        if(list.size() > 0) {
                            defaultWebcam = list.get(0);
                            System.out.println("   ✓ Using first available webcam: " + defaultWebcam);
                        }
                    }
                } catch(Exception ex) {
                    System.out.println("   ✗ Could not list webcams: " + ex.getMessage());
                }
                
                if(defaultWebcam == null) {
                    return false;
                }
            } else {
                System.out.println("   ✓ Default webcam found: " + defaultWebcam);
            }
            
            webcam = defaultWebcam;
            
            System.out.println("4. Opening webcam...");
            webcam.getClass().getMethod("open").invoke(webcam);
            System.out.println("   ✓ Webcam opened successfully!");
            System.out.println("   Camera name: " + webcam.toString());
            return true;
            
        } catch(ClassNotFoundException e) {
            System.out.println("   ✗ FATAL: Webcam library not in classpath!");
            System.out.println("   - Missing: com.github.sarxos.webcam.Webcam");
            System.out.println("   - Required file: webcam-capture-0.3.12.jar");
            return false;
        } catch(NoSuchMethodException e) {
            System.out.println("   ✗ Webcam API changed or incompatible version");
            System.out.println("   - Missing method: " + e.getMessage());
            return false;
        } catch(Exception e) {
            System.out.println("   ✗ Failed to initialize webcam: " + e.getClass().getSimpleName());
            System.out.println("   - Error: " + e.getMessage());
            System.out.println("   - Possible causes:");
            System.out.println("     * Webcam is in use by another application");
            System.out.println("     * Missing native libraries (bridj)");
            System.out.println("     * Webcam driver issues");
            System.out.println("     * Permissions denied");
            e.printStackTrace();
            return false;
        }
    }

    private void simulateCamera() {
        try {
            System.out.println("\n=== Running in TEST MODE (Animated Pattern) ===\n");
            Random rand = new Random();
            int frameCount = 0;
            while(isRunning) {
                BufferedImage frame = new BufferedImage(320, 240, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = frame.createGraphics();
                
                // Background
                g2d.setColor(new Color(30 + rand.nextInt(20), 30 + rand.nextInt(20), 50 + rand.nextInt(20)));
                g2d.fillRect(0, 0, 320, 240);
                
                // Animated shapes
                g2d.setColor(new Color(100 + frameCount % 156, 150, 200));
                g2d.fillRect(50 + (frameCount % 100), 50, 100, 80);
                
                g2d.setColor(new Color(200, 150 + frameCount % 105, 100));
                g2d.fillOval(150 + (frameCount % 50), 100, 80, 80);
                
                // Username label
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("TEST: " + username, 20, 230);
                
                g2d.dispose();
                latestFrame = frame;
                
                // Record frame if recording is active
                if(isRecording && recordedFrames.size() < 3000) {
                    BufferedImage recordFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g = recordFrame.createGraphics();
                    g.drawImage(frame, 0, 0, null);
                    g.dispose();
                    recordedFrames.add(recordFrame);
                }
                
                frameCount++;
                
                Thread.sleep(50);
            }
        } catch(InterruptedException e) {
            isRunning = false;
        }
    }

    public void stopCamera() {
        isRunning = false;
        if(cameraThread != null) {
            try {
                cameraThread.join(1000);
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public BufferedImage getLatestFrame() {
        return latestFrame;
    }

    public boolean isRunning() {
        return isRunning;
    }
}

// ============================================================
// LIVE STORE
// ============================================================
class LiveStore {
    private static final Set<String> LIVE = new HashSet<>();
    private static final Map<String, CameraFeed> CAMERA_FEEDS = new HashMap<>();
    
    public static void goLive(String u) { 
        if(u != null) LIVE.add(u); 
    }
    
    public static void stopLive(String u) { 
        LIVE.remove(u); 
    }
    
    public static Set<String> getLiveUsers() { 
        return new HashSet<>(LIVE); 
    }
    
    public static void setCameraFeed(String username, CameraFeed feed) {
        if(feed == null) {
            CAMERA_FEEDS.remove(username);
        } else {
            CAMERA_FEEDS.put(username, feed);
        }
    }
    
    public static CameraFeed getCameraFeed(String username) {
        return CAMERA_FEEDS.get(username);
    }
}

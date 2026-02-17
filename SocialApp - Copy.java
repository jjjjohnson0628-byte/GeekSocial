import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
// MAIN APP
// ============================================================
public class SocialApp {
    public static void main(String[] args) {
        // Initialize JavaFX toolkit early
        new JFXPanel();
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

// ============================================================
// MAIN WINDOW — CardLayout screens
// ============================================================
class MainWindow extends JFrame {
    private final CardLayout layout = new CardLayout();
    private final JPanel root = new JPanel(layout);
    private String currentUser;

    MainWindow() {
        setTitle("GeekSocial");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        root.add(new LoginPanel(this), "login");
        root.add(new FeedPanel(this), "feed");
        root.add(new ProfilePanel(this), "profile");

        add(root);
        layout.show(root, "login");
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

        JLabel title = new JLabel("Welcome - Please Login");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(250, 80, 400, 40);
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(200, 200, 100, 30);
        add(userLabel);

        JTextField username = new JTextField();
        username.setBounds(300, 200, 300, 30);
        add(username);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(200, 240, 100, 30);
        add(passLabel);

        JPasswordField password = new JPasswordField();
        password.setBounds(300, 240, 300, 30);
        add(password);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(350, 280, 200, 40);
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
// USER STORE
// ============================================================
class UserStore {
    private static final File FILE = new File("users.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<String, User> users = loadUsers();

    public static boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        User u = new User(username,password);
        users.put(username,u);
        save();
        return true;
    }

    public static boolean validate(String username, String password) {
        User u = users.get(username);
        return u != null && u.passwordHash.equals(User.hash(password));
    }

    public static User getUser(String username) { return users.get(username); }

    public static void save() {
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

    public User(String username, String password) {
        this.username = username;
        this.displayName = username;
        this.passwordHash = hash(password);
        this.profilePhoto = null;
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
        changePhotoBtn.addActionListener(e -> changePhoto());
        add(changePhotoBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBounds(350,370,100,40);
        saveBtn.addActionListener(e -> saveProfile());
        add(saveBtn);

        JButton feedBtn = new JButton("Return to Feed");
        feedBtn.setBounds(460,370,140,40);
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
// FEED PANEL
// ============================================================
class FeedPanel extends JPanel {
    private final MainWindow window;
    private final JPanel feedArea;
    private final DefaultListModel<String> liveUsersModel;
    private final JList<String> liveUsersList;

    FeedPanel(MainWindow window) {
        this.window = window;
        setName("feed");
        setLayout(new BorderLayout());

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nav.setBackground(new Color(30,30,30));

        JButton profileBtn = new JButton("Profile");
        JButton logoutBtn = new JButton("Logout");
        JButton goLiveBtn = new JButton("Go Live");
        JButton stopLiveBtn = new JButton("Stop Live");

        profileBtn.addActionListener(e -> window.show("profile"));
        logoutBtn.addActionListener(e -> window.show("login"));
        goLiveBtn.addActionListener(e -> { LiveStore.goLive(window.getCurrentUser()); refreshLiveUsers(); });
        stopLiveBtn.addActionListener(e -> { LiveStore.stopLive(window.getCurrentUser()); refreshLiveUsers(); });

        nav.add(profileBtn); nav.add(logoutBtn); nav.add(goLiveBtn); nav.add(stopLiveBtn);
        add(nav,BorderLayout.NORTH);

        feedArea = new JPanel();
        feedArea.setLayout(new BoxLayout(feedArea,BoxLayout.Y_AXIS));
        JScrollPane feedScroll = new JScrollPane(feedArea);
        add(feedScroll,BorderLayout.CENTER);

        liveUsersModel = new DefaultListModel<>();
        liveUsersList = new JList<>(liveUsersModel);
        liveUsersList.setBorder(BorderFactory.createTitledBorder("Live Users"));
        add(new JScrollPane(liveUsersList),BorderLayout.EAST);

        JTextArea postInput = new JTextArea(3,30);
        JButton postBtn = new JButton("Post");
        JButton mediaBtn = new JButton("Add Media");
        final String[] selectedMedia = {null};

        mediaBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int r = chooser.showOpenDialog(this);
            if(r==JFileChooser.APPROVE_OPTION) selectedMedia[0]=chooser.getSelectedFile().getAbsolutePath();
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

        JPanel postBox = new JPanel(new BorderLayout());
        postBox.add(mediaBtn,BorderLayout.WEST);
        postBox.add(new JScrollPane(postInput),BorderLayout.CENTER);
        postBox.add(postBtn,BorderLayout.EAST);
        add(postBox,BorderLayout.SOUTH);

        refreshFeed(); refreshLiveUsers();
    }

    void refreshFeed() {
        feedArea.removeAll();
        java.util.List<Post> posts = PostStore.getPosts();
        for(Post post: posts){
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
            User u = UserStore.getUser(post.author);
            panel.setBorder(BorderFactory.createTitledBorder(u.displayName));

            if(post.text!=null && !post.text.isEmpty())
                panel.add(new JLabel("<html>"+post.text+"</html>"));

            if(post.media!=null){
                File f = new File(post.media);
                if(f.exists()){
                    String lower = post.media.toLowerCase();
                    try{
                        if(lower.endsWith(".png")||lower.endsWith(".jpg")||lower.endsWith(".jpeg")||lower.endsWith(".gif")){
                            panel.add(new JLabel(new ImageIcon(new ImageIcon(post.media).getImage().getScaledInstance(300,-1,Image.SCALE_SMOOTH))));
                        } else if(lower.endsWith(".mp4")||lower.endsWith(".mov")||lower.endsWith(".m4v")){
                            JFXPanel fxPanel = new JFXPanel();
                            panel.add(fxPanel);
                            Platform.runLater(() -> {
                                MediaPlayer player = new MediaPlayer(new Media(f.toURI().toString()));
                                MediaView view = new MediaView(player);
                                view.setFitWidth(300);
                                view.setPreserveRatio(true);
                                fxPanel.setScene(new Scene(new Group(view)));
                                player.setAutoPlay(false);
                            });
                        } else panel.add(new JLabel("Media: "+post.media));
                    } catch(Exception ex){ panel.add(new JLabel("Error loading media")); ex.printStackTrace(); }
                } else panel.add(new JLabel("Media not found: "+post.media));
            }

            feedArea.add(panel); feedArea.add(Box.createVerticalStrut(10));
        }
        feedArea.revalidate(); feedArea.repaint();
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
    String author; String text; String media;
    public Post(String a, String t, String m){ author=a; text=t; media=m; }
}

class PostStore {
    private static final File FILE = new File("posts.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static java.util.List<Post> POSTS = loadPosts();

    public static void addPost(Post p){
        POSTS.add(0,p);
        savePosts();
    }

    public static java.util.List<Post> getPosts(){ return new java.util.ArrayList<>(POSTS); }

    private static void savePosts(){
        try(Writer w = new FileWriter(FILE)){ gson.toJson(POSTS,w); } catch(Exception e){ e.printStackTrace(); }
    }

    private static java.util.List<Post> loadPosts(){
        if(!FILE.exists()) return new java.util.ArrayList<>();
        try(Reader r = new FileReader(FILE)){
            Type t = new TypeToken<java.util.List<Post>>(){}.getType();
            return gson.fromJson(r,t);
        } catch(Exception e){ e.printStackTrace(); return new java.util.ArrayList<>(); }
    }
}

// ============================================================
// LIVE STORE
// ============================================================
class LiveStore {
    private static final Set<String> LIVE = new HashSet<>();
    public static void goLive(String u){ if(u!=null)LIVE.add(u); }
    public static void stopLive(String u){ LIVE.remove(u); }
    public static Set<String> getLiveUsers(){ return new HashSet<>(LIVE); }
}

package turism.client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.client.gui.encrypt.Crypter;
import turism.model.User;
import turism.services.ITurismServices;


public class LoginController {
    private ITurismServices server;
    private UserController userController;
    private User crtUser;

    private static Logger logger = LogManager.getLogger(LoginController.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button loginButton;

    Parent mainParent;

    public void setServer(ITurismServices s) {
        this.server = s;
    }

    public void setUserController(UserController uc) {
        this.userController = uc;
    }

    public void setParent(Parent p){ mainParent = p; }

    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String passwordString = passwordTextField.getText();


        try{
            String password = Crypter.encrypt(passwordString, "a1b2c3d4e5f6g7h8");
            User copy = new User(username, password);
            crtUser = server.login(copy, userController);
            Stage stage = new Stage();
            stage.setTitle("User: "+crtUser.getUsername());
            stage.setScene(new Scene(mainParent));

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    userController.logout();
                    logger.debug("User " + crtUser.getUsername() + " logged out");
                    System.exit(0);
                }
            });

            stage.show();
            userController.setUser(crtUser);
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();



        } catch (Exception e) {
            System.out.println(e.getMessage());
            showMessage("Error", "Invalid username or password", "Please enter a valid username and password");
        }

    }


    public void showMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

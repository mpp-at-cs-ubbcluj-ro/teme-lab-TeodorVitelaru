package ro.mpp2024.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;
import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.domain.User;
import ro.mpp2024.service.ClientService;
import ro.mpp2024.service.ExcursieService;
import ro.mpp2024.service.RezervareService;
import ro.mpp2024.service.UserService;
import javafx.scene.control.TextField;

public class LoginController {
    UserService userService;
    ExcursieService excursieService;
    RezervareService rezervareService;
    ClientService clientService;

    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button loginButton;

    public void setService(UserService userService, ExcursieService excursieService, RezervareService rezervareService, ClientService clientService) {
        this.userService = userService;
        this.excursieService = excursieService;
        this.rezervareService = rezervareService;
        this.clientService = clientService;
    }

    public void handleLogin(ActionEvent actionEvent) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        User user = userService.findUser(username, password);
        if (user != null) {
            openUserWindow(user);
            //inchidem fereastra de login
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
        } else {
            showMessage("Error", "Invalid username or password", "Please enter a valid username and password");
        }
    }

    public void openUserWindow(User user) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserView.fxml"));
            AnchorPane root = loader.load();
            UserController userController = loader.getController();
            userController.setService(user, userService, excursieService, rezervareService, clientService);
            Stage stage = new Stage();
            stage.setTitle("User: "+user.getUsername());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.out.println("Error opening user window " + e);
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

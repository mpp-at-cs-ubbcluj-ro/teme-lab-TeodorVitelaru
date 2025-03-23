package ro.mpp2024;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ro.mpp2024.controller.LoginController;
import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.domain.User;
import ro.mpp2024.repo.RepoDB.ClientRepoDB;
import ro.mpp2024.repo.RepoDB.ExcursieRepoDB;
import ro.mpp2024.repo.RepoDB.RezervareRepoDB;
import ro.mpp2024.repo.RepoDB.UserRepoDB;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javafx.application.Application;
import ro.mpp2024.service.ClientService;
import ro.mpp2024.service.ExcursieService;
import ro.mpp2024.service.RezervareService;
import ro.mpp2024.service.UserService;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends Application {
    UserService userService;
    ExcursieService excursieService;
    RezervareService rezervareService;
    ClientService clientService;

    @Override
    public void start(Stage stage) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileReader("bd.config"));
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.user");
        String password = properties.getProperty("jdbc.pass");

        UserRepoDB userRepoDB = new UserRepoDB(properties);
        ClientRepoDB clientRepoDB = new ClientRepoDB(properties);
        ExcursieRepoDB excursieRepoDB = new ExcursieRepoDB(properties);
        RezervareRepoDB rezervareRepoDB = new RezervareRepoDB(properties);

        this.userService = new UserService(userRepoDB);
        this.excursieService = new ExcursieService(excursieRepoDB);
        this.rezervareService = new RezervareService(rezervareRepoDB);
        this.clientService = new ClientService(clientRepoDB);

        initView(stage);
        stage.show();

        initView(stage);


    }

    private void initView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
        AnchorPane loginLayout = fxmlLoader.load();
        stage.setScene(new Scene(loginLayout));
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(userService, excursieService, rezervareService, clientService);

    }
}
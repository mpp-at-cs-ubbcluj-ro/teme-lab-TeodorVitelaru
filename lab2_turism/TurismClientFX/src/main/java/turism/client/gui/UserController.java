package turism.client.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import turism.model.Client;
import turism.model.Excursie;
import turism.model.Rezervare;
import turism.model.User;
import turism.services.ITurismObserver;
import turism.services.ITurismServices;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class UserController implements Initializable, ITurismObserver {
    private User loggedInUser;
    private ITurismServices server;
    private List<Excursie> excursies = new ArrayList<>();

    private ObservableList<Excursie> model = FXCollections.observableArrayList();
    private ObservableList<Excursie> modelExcursie = FXCollections.observableArrayList();

    private static Logger logger = LogManager.getLogger(UserController.class);


    @FXML
    private ComboBox<String>  inceputComboBox;
    @FXML
    private ComboBox<String> sfarsitComboBox;
    @FXML
    private TextField obiectivTextField;
    @FXML
    private DatePicker intervalDatePicker;
    @FXML
    private Label tabelLabel;

    //pentru toate excursiile
    @FXML
    private TableView<Excursie> tableViewExcursie;
    @FXML
    private TableColumn<Excursie, String> tableColumnObiectivTuristic;
    @FXML
    private TableColumn<Excursie, String> tableColumnFirma1;
    @FXML
    private TableColumn<Excursie, LocalDateTime> tableColumnOraPlecarii1;
    @FXML
    private TableColumn<Excursie, Integer> tableColumnPret1;
    @FXML
    private TableColumn<Excursie, Integer> tableColumnLocuriDisponibile1;


    //pentru excursiile alese
    @FXML
    private TableView<Excursie> tableView;
    @FXML
    private TableColumn<Excursie, String> tableColumnFirma;
    @FXML
    private TableColumn<Excursie, Integer> tableColumnPret;
    @FXML
    private TableColumn<Excursie, LocalDateTime> tableColumnOraPlecarii;
    @FXML
    private TableColumn<Excursie, Integer> tableColumnLocuriDisponibile;

    @FXML
    private TextField numeClientField;
    @FXML
    private TextField telefonField;
    @FXML
    private TextField nrBileteField;

    @FXML
    private Button logoutButton;

    // No-argument constructor
    public UserController() {
    }

    public UserController(ITurismServices server){
        this.server = server;
        logger.debug("UserController created");

    }

    public void setServer(ITurismServices server) {
        this.server = server;
    }

    public void setUser(User user) {
        this.loggedInUser = user;
        logger.info("User set: " + user.getUsername());
        initilizeComboBox();
        initModelExcursie();
    }



    private void initModelExcursie() {
        try{
            modelExcursie.setAll(server.getAllExcursie());
            initTableExcursie();
        } catch (Exception e) {
            logger.error("Error initializing modelExcursie: " + e.getMessage());
            showMessage("Error", "Error", e.getMessage());
        }
    }

    private Map<Long, Integer> locuriOcupateCache = new HashMap<>();
    private void initTableExcursie() {
        logger.debug("Initializing tableExcursie");
        tableViewExcursie.setItems(modelExcursie);
        tableColumnObiectivTuristic.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getObiectiv()));
        tableColumnFirma1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirmaTransport()));
        tableColumnPret1.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPret()).asObject());
        tableColumnOraPlecarii1.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDataPlecarii()));
        tableColumnLocuriDisponibile1.setCellValueFactory(cellData -> {
            try{
                logger.info("Getting available seats for excursion: " + cellData.getValue().getId());
                if(locuriOcupateCache.containsKey(cellData.getValue().getId())) {
                    return new SimpleIntegerProperty(cellData.getValue().getNrLocuriDisponibile() - locuriOcupateCache.get(cellData.getValue().getId())).asObject();
                } else {
                    int totalSeats = cellData.getValue().getNrLocuriDisponibile();
                    int reservedSeats = server.getLocuriOcupateForExcursie(cellData.getValue());
                    int availableSeats = totalSeats - reservedSeats;
                    locuriOcupateCache.put(cellData.getValue().getId(), reservedSeats);
                    return new SimpleIntegerProperty(availableSeats).asObject();
                }
            } catch (Exception e) {
                logger.error("Error getting available seats: " + e.getMessage());
                logger.info("Error getting available seats for {} ", cellData.getValue());
                showMessage("Error", "Error", e.getMessage());
                return new SimpleIntegerProperty(0).asObject();
            }


        });

        tableViewExcursie.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Excursie excursie, boolean empty) {
                super.updateItem(excursie, empty);
                if (excursie == null || empty) {
                    setStyle("");
                } else {
                    try{
                        int totalSeats = excursie.getNrLocuriDisponibile();
                        int reservedSeats = server.getLocuriOcupateForExcursie(excursie);
                        int availableSeats = totalSeats - reservedSeats;
                        if (availableSeats == 0) {
                            setStyle("-fx-background-color: red;");
                        } else {
                            setStyle("");
                        }
                    } catch (Exception e) {
                        logger.error("Error updating row style: " + e.getMessage());
                        showMessage("Error", "Error", e.getMessage());
                    }
                }
            }
        });


    }

    public void initilizeComboBox() {
        inceputComboBox.getItems().add("Ora start");
        sfarsitComboBox.getItems().add("Ora sfarsit");
        for (int hour = 0; hour < 24; hour++) {
            String time = String.format("%02d:00", hour);
            inceputComboBox.getItems().add(time);
            sfarsitComboBox.getItems().add(time);
        }
    }

    public void populateTable(ActionEvent actionEvent) {
        try {
            String obiectiv = obiectivTextField.getText();
            String inceput = inceputComboBox.getValue();
            String sfarsit = sfarsitComboBox.getValue();
            LocalDate dataExacta = intervalDatePicker.getValue();

            if(inceput.compareTo(sfarsit) > 0) {
                showMessage("Error", "Error", "Ora de inceput trebuie sa fie mai mica decat ora de sfarsit");
                inceputComboBox.setValue("Ora start");
                sfarsitComboBox.setValue("Ora sfarsit");
                return;
            } else if(inceput.equals("Ora start") || sfarsit.equals("Ora inceput")) {
                showMessage("Error", "Error", "Alegeti o ora de inceput si o ora de sfarsit");
                return;
            } else if(obiectiv.equals("")) {
                showMessage("Error", "Error", "Introduceti un obiectiv turistic");
                return;
            }

            if(dataExacta == null) {
                showMessage("Information", "Nu ati ales data", "Data implicita adaugata este cea de azi");
                dataExacta = LocalDate.now();
            }

            LocalDateTime inceputDate = LocalDateTime.of(dataExacta.getYear(), dataExacta.getMonth(), dataExacta.getDayOfMonth(), Integer.parseInt(inceput.split(":")[0]), 0);
            LocalDateTime sfarsitDate = LocalDateTime.of(dataExacta.getYear(), dataExacta.getMonth(), dataExacta.getDayOfMonth(), Integer.parseInt(sfarsit.split(":")[0]), 0);

            List<Excursie> excursies = server.getAllExcursieByDestinationAndDate(obiectiv, inceputDate, sfarsitDate);
            model.setAll(excursies);
            this.excursies = excursies;
            tableView.setItems(model);
            initTable();
            if(excursies.size() == 0) {
                showMessage("Information", "Nu exista excursii", "Nu exista excursii pentru obiectivul " + obiectiv + " intre " + inceput + " si " + sfarsit + " pe data de " + dataExacta);
                return;
            }
            tabelLabel.setText("Excursii pentru obiecvitul " + obiectiv + " intre " + inceput + " si " + sfarsit + " pe data de " + dataExacta);
        } catch (Exception e) {
            showMessage("Error", "Error", e.getMessage());
        }
    }


    private void initTable() {
        logger.debug("Initializing table searching");
        tableColumnFirma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirmaTransport()));
        tableColumnPret.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPret()).asObject());
        tableColumnOraPlecarii.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDataPlecarii()));
        tableColumnLocuriDisponibile.setCellValueFactory(cellData -> {
            try{
                logger.info("Getting available seats for excursion: " + cellData.getValue().getId());
                if(locuriOcupateCache.containsKey(cellData.getValue().getId())) {
                    return new SimpleIntegerProperty(cellData.getValue().getNrLocuriDisponibile() - locuriOcupateCache.get(cellData.getValue().getId())).asObject();
                } else {
                    int totalSeats = cellData.getValue().getNrLocuriDisponibile();
                    int reservedSeats = server.getLocuriOcupateForExcursie(cellData.getValue());
                    int availableSeats = totalSeats - reservedSeats;
                    locuriOcupateCache.put(cellData.getValue().getId(), reservedSeats);
                    return new SimpleIntegerProperty(availableSeats).asObject();
                }
            } catch (Exception e) {
                logger.error("Error getting available seats: " + e.getMessage());
                showMessage("Error", "Error", e.getMessage());
                return new SimpleIntegerProperty(0).asObject();
            }
        });

        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Excursie excursie, boolean empty) {
                super.updateItem(excursie, empty);
                if (excursie == null || empty) {
                    setStyle("");
                } else {
                    try{
                        int totalSeats = excursie.getNrLocuriDisponibile();
                        int reservedSeats = server.getLocuriOcupateForExcursie(excursie);
                        int availableSeats = totalSeats-reservedSeats;
                        if (availableSeats == 0) {
                            setStyle("-fx-background-color: red;");
                        } else {
                            setStyle("");
                        }
                    } catch (Exception e) {
                        logger.error("Error updating row style: " + e.getMessage());
                        showMessage("Error", "Error", e.getMessage());
                    }
                }
            }

        });


    }

    public void handleAddRezervare(ActionEvent actionEvent) {
        List<String> listOfTask = new ArrayList<>();
        if(numeClientField.getText().equals("") || telefonField.getText().equals("") || nrBileteField.getText().equals("")) {
            showMessage("Error", "Error", "Completati toate campurile");
            return;
        } else if(tableView.getSelectionModel().getSelectedItem() == null) {
            showMessage("Error", "Error", "Selectati o excursie");
            return;
        }
        Excursie excursie = tableView.getSelectionModel().getSelectedItem();
        String numeClient = numeClientField.getText();
        String telefon = telefonField.getText();

        String nrBileteText = nrBileteField.getText();
        if (!nrBileteText.matches("\\d+")) {
            showMessage("Error", "Error", "Introduceti un numar valid pentru nr bilete");
            nrBileteField.setText("");
            return;
        }
        try{
            int nrBilete = Integer.parseInt(nrBileteField.getText());
            Client client = server.findClientByNameAndPhoneNumber(numeClient, telefon);

            if(client == null) {
                client = new Client(numeClient, telefon);
                server.addClient(client);
                listOfTask.add("Clientul nu exista, a fost adaugat");
            }

            int locuriDisponibile = excursie.getNrLocuriDisponibile();
            int locuriOcupate = server.getLocuriOcupateForExcursie(excursie);
            if(locuriOcupate + nrBilete > locuriDisponibile) {
                showMessage("Error", "Locuri indisponibile", "Nu sunt suficiente locuri disponibile. Nr maxim de locuri disponibile este: " + (locuriDisponibile - locuriOcupate));
                return;
            }
            Rezervare rez = server.addRezervare(excursie, client, nrBilete, loggedInUser);
            listOfTask.add("Rezervare adaugata");
            logger.info("Rezervare adaugata: " + rez);


            showMessage("Success", "Success", String.join("\n", listOfTask));

            numeClientField.setText("");
            telefonField.setText("");
            nrBileteField.setText("");

        } catch (Exception e) {
            logger.error("Error adding reservation: " + e.getMessage());
            showMessage("Error", "Error", e.getMessage());
        }
    }

    public void showMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }


    public void handelLogout(ActionEvent actionEvent) throws IOException {
        logout();
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        System.exit(0);
    }


    void logout(){
        try {
            server.logout(loggedInUser, this);
        } catch (Exception e) {
            logger.error("Logout error " + e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.debug("INIT : am in lista prieteni ");

        logger.debug("END INIT!!!!!!!!!");
    }


    public void rezervareReceived(Rezervare rezervare) {
       Platform.runLater(() -> {
            if(!Objects.equals(rezervare.getUser().getId(), loggedInUser.getId())) {
                logger.info("Rezervare primita in userController: {} {}" + rezervare, rezervare.getClient().getId(), loggedInUser.getId());
            }
            locuriOcupateCache.remove(rezervare.getExcursie().getId());
            initTableExcursie();
            initTable();
            logger.info("Rezervare primita in userController: " + rezervare);
        });
    }

    public void clientReceived(Client client) {

    }
}

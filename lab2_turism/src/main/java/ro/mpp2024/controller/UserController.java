package ro.mpp2024.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import ro.mpp2024.domain.Client;
import ro.mpp2024.domain.Excursie;
import ro.mpp2024.domain.Rezervare;
import ro.mpp2024.domain.User;
import ro.mpp2024.service.ClientService;
import ro.mpp2024.service.ExcursieService;
import ro.mpp2024.service.RezervareService;
import ro.mpp2024.service.UserService;
import ro.mpp2024.utils.events.ChangeEventType;
import ro.mpp2024.utils.events.EntityChangeEvent;
import ro.mpp2024.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserController implements Observer<EntityChangeEvent> {
    private User loggedInUser;
    private ExcursieService excursieService;
    private RezervareService rezervareService;
    private ClientService clientService;
    private List<Excursie> excursies = new ArrayList<>();

    private ObservableList<Excursie> model = FXCollections.observableArrayList();


    @FXML
    private ComboBox<String>  inceputComboBox;
    @FXML
    private ComboBox<String> sfarsitComboBox;
    @FXML
    private TextField obiectivTextField;
    @FXML
    private Label tabelLabel;

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


    public void setService(User user, UserService userService, ExcursieService excursieService, RezervareService rezervareService, ClientService clientService) {
        this.loggedInUser = user;
        this.excursieService = excursieService;
        this.rezervareService = rezervareService;
        this.clientService = clientService;
        this.rezervareService.addObserver(this);

        initilizeComboBox();
    }

    public void initilizeComboBox() {
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

            LocalDateTime inceputDate = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), Integer.parseInt(inceput.split(":")[0]), 0);
            LocalDateTime sfarsitDate = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), Integer.parseInt(sfarsit.split(":")[0]), 0);
            List<Excursie> excursies = excursieService.getAllExcursieByDestinationAndDate(obiectiv, inceputDate, sfarsitDate);
            model.setAll(excursies);
            this.excursies = excursies;
            tableView.setItems(model);
            initTable();
            tabelLabel.setText("Excursii pentru obiecvitul " + obiectiv + " intre " + inceput + " si " + sfarsit);
        } catch (Exception e) {
            System.out.println("Error populating table " + e);
        }
    }

    private void initTable() {
        tableColumnFirma.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirmaTransport()));
        tableColumnPret.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPret()).asObject());
        tableColumnOraPlecarii.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDataPlecarii()));
        tableColumnLocuriDisponibile.setCellValueFactory(cellData -> {
            int totalSeats = cellData.getValue().getNrLocuriDisponibile();
            int reservedSeats = rezervareService.getLocuriOcupateForExcursie(cellData.getValue());
            int availableSeats = totalSeats - reservedSeats;
            return new SimpleIntegerProperty(availableSeats).asObject();
        });

        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Excursie excursie, boolean empty) {
                super.updateItem(excursie, empty);
                if (excursie == null || empty) {
                    setStyle("");
                } else {
                    int totalSeats = excursie.getNrLocuriDisponibile();
                    int reservedSeats = rezervareService.getLocuriOcupateForExcursie(excursie);
                    int availableSeats = totalSeats - reservedSeats;
                    if (availableSeats == 0) {
                        setStyle("-fx-background-color: red;");
                    } else {
                        setStyle("");
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
            return;
        }
        int nrBilete = Integer.parseInt(nrBileteField.getText());
        Client client = clientService.findClientByNameAndPhoneNumber(numeClient, telefon);

        if(client == null) {
            client = new Client(numeClient, telefon);
            clientService.addClient(numeClient, telefon);
            listOfTask.add("Clientul nu exista, a fost adaugat");
        }

        int locuriDisponibile = excursie.getNrLocuriDisponibile();
        int locuriOcupate = rezervareService.getLocuriOcupateForExcursie(excursie);
        if(locuriOcupate + nrBilete > locuriDisponibile) {
            showMessage("Error", "Error", "Nu sunt suficiente locuri disponibile. Nr maxim de locuri disponibile este: " + (locuriDisponibile - locuriOcupate));
            return;
        }
        rezervareService.addRezervare(excursie, client, nrBilete, loggedInUser);
        listOfTask.add("Rezervare adaugata");


        showMessage("Success", "Success", String.join("\n", listOfTask));

    }

    public void showMessage(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        if(entityChangeEvent.getType().equals(ChangeEventType.ADD)) {
            if(entityChangeEvent.getData() instanceof Rezervare) {
                model.setAll(excursies);
                tableView.setItems(model);
            }
        }
    }

    public void handelLogout(ActionEvent actionEvent) {
        logoutButton.getScene().getWindow().hide();
        
    }
}

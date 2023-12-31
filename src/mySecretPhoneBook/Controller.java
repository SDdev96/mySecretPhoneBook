package mySecretPhoneBook;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class Controller implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private SplitPane mainPane;

    @FXML
    private MenuItem salvaButton;

    @FXML
    private MenuItem esciButton;

    @FXML
    private TextField nomeTfd;

    @FXML
    private TextField cognomeTfd;

    @FXML
    private TextField numeroTfd;

    @FXML
    private Button aggiungiButton;

    @FXML
    private MenuItem deleteButton;

    @FXML
    private MenuItem copyButton;

    @FXML
    private TableView<Contact> tableView;

    @FXML
    private TableColumn<Contact, String> nomeColumn;

    @FXML
    private TableColumn<Contact, String> cognomeColumn;

    @FXML
    private TableColumn<Contact, String> numeroColumn;

    @FXML
    private AnchorPane sbloccoPane;

    @FXML
    private Label otpLbl;

    @FXML
    private TextField sbloccaTfd;

    @FXML
    private Button sbloccaButton;

    private Clipboard clipboard;
    private ClipboardContent content;

    private ObservableList<Contact> contactList;

    private OTPService service;

    /**
     * Metodo per l'inizializzazione dell'interfaccia.
     * Questo metodo istanzia l'arrayList associata alla lista dei contatti e la
     * lega alla tableView.
     * Successivamente si popolano le colonne mediante il metodo
     * {@link TableColumn#setCellValueFactory(javafx.util.Callback)}.
     * Per comodità si utilizza una classe di convenienza
     * {@link PropertyValueFactory} per accedere ai getter della classe dati tramite
     * il nome dell'attributo (i parametri sono case-sensitive).
     * Successivamente si utilizzano i metodi
     * {@link TableColumn#setCellFactory(TextFieldTableCell.forTableColumn)} per
     * trasformare i campi delle righe in textField tramite doppio click su di esse.
     * 
     * Creo il sistema di Clipboard {@link Clipboard#getSystemClipboard()} ed infine
     * istanzio il container il suo contenuto {@link ClipboardContent}
     * 
     * Creo i binding per i pulsanti mediante i metodi e le classi associate, avvio
     * il service creato tramite la classe {@link OTPService} e bindo il suo valore
     * di ritorno alla label dell'anchorPane di sblocco.
     * 
     * @param location
     * @param resource
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contactList = FXCollections.observableArrayList();
        tableView.setItems(contactList);

        nomeColumn.setCellValueFactory(new PropertyValueFactory<>("nome"));
        cognomeColumn.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        numeroColumn.setCellValueFactory(new PropertyValueFactory<>("numero"));

        nomeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        cognomeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        numeroColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        clipboard = Clipboard.getSystemClipboard();
        content = new ClipboardContent();

        initButtons();

        service = new OTPService();
        service.start();

        otpLbl.textProperty().bind(service.valueProperty());
    }

    private void initButtons() {
        aggiungiButton.disableProperty().bind(Bindings.or(Bindings.isEmpty(nomeTfd.textProperty()),
                Bindings.or(Bindings.isEmpty(cognomeTfd.textProperty()), Bindings.isEmpty(numeroTfd.textProperty()))));

        sbloccaButton.disableProperty().bind(Bindings.or(sbloccaTfd.textProperty().isEmpty(),
                sbloccaTfd.textProperty().isNotEqualTo(otpLbl.textProperty())));

        salvaButton.disableProperty().bind(Bindings.isEmpty(contactList));

        deleteButton.disableProperty().bind(Bindings.isNull(tableView.getSelectionModel().selectedItemProperty()));
        copyButton.disableProperty().bind(Bindings.isNull(tableView.getSelectionModel().selectedItemProperty()));
    }

    @FXML
    void aggiungiEvent(ActionEvent event) {
        Contact contact = new Contact(nomeTfd.getText(), cognomeTfd.getText(), numeroTfd.getText());

        if (contactList.contains(contact)) {
            System.out.println("Contatto già esistente. Impossibile aggiungere");
            showError(new Exception("Contatto già presente"));
        } else
            contactList.add(contact);
            
        nomeTfd.clear();
        cognomeTfd.clear();
        numeroTfd.clear();
    }

    private void showError(Exception e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
        alert.showAndWait();
    }

    @FXML
    void esciEvent(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void salvaEvent(ActionEvent event) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream("save.bin")))) {
            oos.writeObject(new ArrayList<>(contactList));
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void sbloccaEvent(ActionEvent event) {
        if (!sbloccaTfd.getText().equals(service.getNumber().toString()))
            System.out.print("Password errata. Riprovare");
        else {
            sbloccoPane.disableProperty();
            sbloccoPane.setVisible(false);

            mainPane.setDisable(false);
            mainPane.setVisible(true);
        }
        service.cancel();
        sbloccaTfd.clear();
    }

    @FXML
    void copyEvent(ActionEvent event) {
        Contact c = tableView.getSelectionModel().getSelectedItem();
        content.putString(c.toString());
        clipboard.setContent(content);

    }

    @FXML
    void deleteEvent(ActionEvent event) {
        Contact c = tableView.getSelectionModel().getSelectedItem();
        contactList.remove(c);
    }

    @FXML
    void nomeCommit(TableColumn.CellEditEvent<Contact, String> event) {
        final Contact item = tableView.getSelectionModel().getSelectedItem();
        if (contactList.contains(item)) {
            System.out.println("Contatto già esistente. Impossibile modificare il campo");
            showError(new Exception("Contatto già presente, impossibile modificare il campo"));
        } else
            item.setNome(event.getNewValue());
    }

    @FXML
    void cognomeCommit(TableColumn.CellEditEvent<Contact, String> event) {
        final Contact item = tableView.getSelectionModel().getSelectedItem();
        if (contactList.contains(item)) {
            System.out.println("Contatto già esistente. Impossibile modificare il campo");
            showError(new Exception("Contatto già presente, impossibile modificare il campo"));
        } else
            item.setCognome(event.getNewValue());
    }

    @FXML
    void numeroCommit(TableColumn.CellEditEvent<Contact, String> event) {
        final Contact item = tableView.getSelectionModel().getSelectedItem();
        if (contactList.contains(item)) {
            System.out.println("Contatto già esistente. Impossibile modificare il campo");
            showError(new Exception("Contatto già presente, impossibile modificare il campo"));
        } else
            item.setNumero(event.getNewValue());
    }
}

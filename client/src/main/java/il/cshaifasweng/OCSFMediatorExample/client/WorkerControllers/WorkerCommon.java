package il.cshaifasweng.OCSFMediatorExample.client.WorkerControllers;

import il.cshaifasweng.OCSFMediatorExample.client.App;
import il.cshaifasweng.OCSFMediatorExample.client.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.events.BranchEvent;
import il.cshaifasweng.OCSFMediatorExample.client.events.LoginEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.client.events.MenuEvent;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WorkerCommon implements Initializable {

    private Branch branch;
    private int privilege;
    private  Worker worker;

    @FXML
    private TableView<Branch> branchTable;

    @FXML
    private TableColumn<Branch, Integer> brIdCol;

    @FXML
    private TableColumn<Branch, String> brOpenCol;

    @FXML
    private TableColumn<Branch, String> brCloseCol;

    @FXML
    private TableView<Meal> menuTable;

    @FXML
    private TableColumn<Meal, Integer> mealIdCol;

    @FXML
    private TableColumn<Meal, String> mealNameCol;

    @FXML
    private TableColumn<Meal, List<String>> mealIngCol;

    @FXML
    private TableColumn<Meal, Double> mealPriceCol;

    @FXML
    private TableColumn<Meal, ImageInfo> mealImageCol;


    @FXML
    private TableColumn<Meal, Boolean>  networkMealMenuCol;
    @FXML
    private TextField nameTF;

    @FXML
    private TextField ingredientsTF;

    @FXML
    private TextField priceTF;

    @FXML
    private TextField branchIdTF;

    @FXML
    private Button denyBtn;

    @FXML
    private Button showRestaurantMapBtn;

    @FXML
    private TableView<SimpleTable> retaurantMapTable;

    @FXML
    private TableColumn<SimpleTable, Integer> tableNumberMapCol;

    @FXML
    private TableColumn<SimpleTable, String> numOfSeatsMapCol;

    @FXML
    private TableColumn<SimpleTable, String> reservationsMapCol;


    @FXML
    private ComboBox hourComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button logOutBtn;

    @FXML
    void logOut(ActionEvent event) {
        String message="#logOut"+' '+ worker.getGovId();
        try{
            SimpleClient.getClient().sendToServer(message);
            App.setRoot("login");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onLoginEvent(LoginEvent event){
        worker= event.getWorker();
    }

    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        Platform.runLater(() -> {
            ObservableList<Meal> mealList = FXCollections.observableArrayList();
            mealList.addAll(event.getMenu().getMeals());
            menuTable.setItems(mealList);
        });
    }


    @Subscribe
    public void onBranchEvent(BranchEvent event) {
        Platform.runLater(() -> {
            ObservableList<Branch> branchList = FXCollections.observableArrayList();
            branchList.addAll(event.getBranches().getBranches());
            branchTable.setItems(branchList);
        });
    }

    @Subscribe
    public void onOccupationMap(OccupationMap event) {
        Platform.runLater(() -> {
            ObservableList<SimpleTable> tableList = FXCollections.observableArrayList();
            tableList.addAll(event.getTables());
            retaurantMapTable.setItems(tableList);
        });
    }


    public void initializeDateAndHours(Branch branch) {


        String openh = branch.getOpenHours();
        String closeh = branch.getCloseHours();
        PurpleLetter p = branch.getPurpleLetter();
        LocalDate qStart = LocalDate.now().minusDays(1);
        LocalDate qEnd = LocalDate.now().minusDays(1);
        boolean quarantine = p.isQuarantine();
        if (quarantine) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            qStart = LocalDate.parse(p.getQuarantineStartDate(), formatter);
            qEnd = LocalDate.parse(p.getQuarantineEndDate(), formatter);
        }
        LocalDate minDate = LocalDate.now();
        final Callback<DatePicker, DateCell> dayCellFactory;

        LocalDate finalQStart = qStart;
        LocalDate finalQEnd = qEnd;
        dayCellFactory = (final DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(minDate)) { //Disable all dates after required date
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); //To set background on different color
                } else if (quarantine) {
                    if (item.isAfter(finalQStart) && item.isBefore(finalQEnd)) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            }
        };
        datePicker.setDayCellFactory(dayCellFactory);


        LocalTime curr = LocalTime.parse(openh).plusMinutes(15);
        LocalTime last = LocalTime.parse(closeh).minusMinutes(59);
        String currString;
        List<String> available = new ArrayList<>();
        while (curr.isBefore(last)) {
            currString = curr.toString();
            curr = curr.plusMinutes(15);
            available.add(currString);
        }
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(available);
        hourComboBox.setItems(list);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        EventBus.getDefault().register(this);


        LocalDate minDate = LocalDate.now();
        final Callback<DatePicker, DateCell> dayCellFactory;

        dayCellFactory = (final DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(minDate)) { //Disable all dates after required date
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); //To set background on different color
                }
            }
        };

        datePicker.setDayCellFactory(dayCellFactory);


        branchTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {


            if (newSelection != null) {
                branch = newSelection;
                String openh = newSelection.getOpenHours();
                String closeh = newSelection.getCloseHours();
                initializeDateAndHours(branch);
            }
        });


        //each cellValueFactory has been set according to the member variables of your entity class
        brIdCol.setCellValueFactory(new PropertyValueFactory<Branch, Integer>("id"));
        brOpenCol.setCellValueFactory(new PropertyValueFactory<Branch, String>("openHours"));
        brCloseCol.setCellValueFactory(new PropertyValueFactory<Branch, String>("closeHours"));


        mealIdCol.setCellValueFactory(new PropertyValueFactory<Meal, Integer>("id"));
        mealNameCol.setCellValueFactory(new PropertyValueFactory<Meal, String>("name"));
        mealPriceCol.setCellValueFactory(new PropertyValueFactory<Meal, Double>("price"));
        mealIngCol.setCellValueFactory(new PropertyValueFactory<Meal, List<String>>("ingredients"));

        tableNumberMapCol.setCellValueFactory(new PropertyValueFactory<SimpleTable, Integer>("id"));
        numOfSeatsMapCol.setCellValueFactory(new PropertyValueFactory<SimpleTable, String>("capacity"));
        reservationsMapCol.setCellValueFactory(new PropertyValueFactory<SimpleTable, String>("status"));

        mealImageCol.setCellValueFactory(new PropertyValueFactory<Meal, ImageInfo>("image"));

        networkMealMenuCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(
                cellData.getValue().getMenu().getId() == 1 ? true:false));

        mealImageCol.setCellFactory(param -> new TableCell<Meal, ImageInfo>() {

            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(ImageInfo item, boolean empty) {
                super.updateItem(item, empty);
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    imageView.setImage(byteArrayToImage(item));
                    setGraphic(imageView);
                }
                this.setItem(item);
            }
        });
    }


    private ImageInfo imageToByteArray(Image i) {
        PixelReader pr = i.getPixelReader();
        WritablePixelFormat<ByteBuffer> wf = PixelFormat.getByteBgraInstance();

        byte[] buffer = new byte[(int) (i.getWidth() * i.getHeight() * 4)];

        pr.getPixels(0, 0, (int) i.getWidth(), (int) i.getHeight(), wf, buffer, 0, (int) (i.getWidth()) * 4);
        return new ImageInfo(buffer, (int) i.getWidth(), (int) i.getHeight());
    }

    private Image byteArrayToImage(ImageInfo imageArray) {

        WritablePixelFormat<ByteBuffer> wf = PixelFormat.getByteBgraInstance();
        WritableImage writableimage = new WritableImage(imageArray.getWidth(), imageArray.getHeight());
        PixelWriter pixelWriter = writableimage.getPixelWriter();
        pixelWriter.setPixels(0, 0, imageArray.getWidth(), imageArray.getHeight(), wf, imageArray.getImage(), 0, 4 * imageArray.getWidth());
        return writableimage;
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        App.setRoot("login");
    }



    @FXML
    void requestRestaurantMap(ActionEvent event) {
        Button b = (Button) event.getSource();
        try {
            String date = datePicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String hour = (String) hourComboBox.getValue();
            String message = "#requestMap " + branchTable.getSelectionModel().getSelectedItem().getId() + ' ' + date + ' ' + hour + ' ' + b.getText().toLowerCase();
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    @FXML
    void requestMenu(ActionEvent event) {
        this.branch = branchTable.getSelectionModel().getSelectedItem();

        int id = branch.getId();
        try {
            String message = "#requestMenu " + Integer.toString(id);
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

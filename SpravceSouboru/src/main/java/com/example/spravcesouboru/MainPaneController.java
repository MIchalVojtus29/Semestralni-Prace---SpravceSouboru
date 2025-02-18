package com.example.spravcesouboru;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Trida MainPaneController, která se stará o komunikaci mezi UI prvky a uživatelem
 *
 * @author  Michal Vojtuš
 * @version 1.9
 */
public class MainPaneController implements Observer {

    private final BundleManager bundleManager = new BundleManager(new Locale("en"));



    private String leftCurrentPath;
    private String rightCurrentPath;

    private Task copyTask;
    private Task moveTask;
    private Task deleteTask;
    private Task addTask;
    private ResourceBundle resourceBundle;

    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem closeMenuItem;
    @FXML
    public MenuItem aboutMenuItem;
    @FXML
    private Menu languageMenu;
    @FXML
    private MenuItem englishMenuItem;
    @FXML
    private MenuItem czechMenuItem;
    @FXML
    private Button leftAddButton;
    @FXML
    private Button leftCopyButton;
    @FXML
    private Button leftMoveButton;
    @FXML
    private Button leftDeleteButton;
    @FXML
    private Button leftRefreshButton;
    @FXML
    private Button rightAddButton;
    @FXML
    private Button rightCopyButton;
    @FXML
    private Button rightMoveButton;
    @FXML
    private Button rightDeleteButton;
    @FXML
    private Button rightRefreshButton;
    @FXML
    private ChoiceBox leftRootChoicebox;
    @FXML
    private ChoiceBox rightRootChoicebox;
    @FXML
    private TableView<SystemObject> rightTableView;
    @FXML
    private TableView<SystemObject> leftTableView;
    @FXML
    private TableColumn<SystemObject, ImageView> leftTableImageColumn;
    @FXML
    private TableColumn<SystemObject, String> leftTableNameColumn;
    @FXML
    private TableColumn<SystemObject, Long> leftTableSizeColumn;
    @FXML
    private TableColumn<SystemObject, Date> leftTableDateColumn;
    @FXML
    private TableColumn<SystemObject, String> leftTableTypeColumn;
    @FXML
    private TableColumn<SystemObject, ImageView> rightTableImageColumn;
    @FXML
    private TableColumn<SystemObject, String> rightTableNameColumn;
    @FXML
    private TableColumn<SystemObject, Long> rightTableSizeColumn;
    @FXML
    private TableColumn<SystemObject, Date> rightTableDateColumn;
    @FXML
    private TableColumn<SystemObject, String> rightTableTypeColumn;
    @FXML
    private TextField leftCurrentPathField;
    @FXML
    private TextField rightCurrentPathField;
    @FXML
    public Button leftTreeButton;
    @FXML
    public Button rightTreeButton;

    public MainPaneController() {
    }


    @FXML
    protected void initialize() {
        resourceBundle = ResourceBundle.getBundle("bundles.lang", Locale.getDefault());
        bundleManager.addObserver(this);

        leftCurrentPathField.setEditable(false);
        rightCurrentPathField.setEditable(false);

        prepareTables();
        listRoots();
        leftTableView.sort();
        rightTableView.sort();
        selectDefaultLanguage();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof BundleManager)
            changeLanguage((Locale) arg);
    }

    private void selectDefaultLanguage() {
        if (Locale.getDefault().toString().contains("cz")) {
            changeLanguageToCzech();
        } else if (Locale.getDefault().toString().contains("en")) {
            changeLanguageToEng();
        }
    }

    @FXML
    private void changeLanguageToEng() {
        czechMenuItem.setVisible(true);
        englishMenuItem.setVisible(false);
        bundleManager.changeAndNotify(new Locale("en"));
    }

    @FXML
    private void changeLanguageToCzech() {
        czechMenuItem.setVisible(false);
        englishMenuItem.setVisible(true);
        bundleManager.changeAndNotify(new Locale("cz"));
    }


    public void closeApplication() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("closeTitle"));
        alert.setHeaderText(resourceBundle.getString("closeHeader"));
        alert.setContentText(resourceBundle.getString("closeMessage"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    public void aboutApplication() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(resourceBundle.getString("aboutTitle"));
        alert.setHeaderText(resourceBundle.getString("aboutHeader"));
        alert.setContentText(resourceBundle.getString("aboutMessage"));
        alert.showAndWait();
    }

    public void showTreeViewLeft(){
        showTreeView(leftCurrentPathField, leftTableView);
    }
    public void showTreeViewRight(){
        showTreeView(rightCurrentPathField, rightTableView);
    }


    public void showTreeView(TextField targetDirectoryPath, TableView<SystemObject> tableView){
        Stage treeStage = new Stage();
        TreeView<SystemObject> strom = new TreeView<>();
        strom.setCellFactory(p->new SystemObjectTreeCell());
        Scene treeScene = new Scene(strom,500,350);
        treeStage.setScene(treeScene);
        SystemObject rootAdresar = new SystemObject(new File(targetDirectoryPath.getText()), resourceBundle);
        strom.setRoot(new TreeItem<>(rootAdresar));
        fillTree(strom.getRoot(), 0);
        treeStage.show();
        strom.setOnMouseClicked(e -> treeView_onMouseClicked(e,strom,tableView,targetDirectoryPath));
    }

    private void fillTree(TreeItem<SystemObject> treeItem, int depth){
        if(depth > 10){
            return;
        }
        if(treeItem.getValue().getFile() != null){
            File[] filepole = treeItem.getValue().getFile().listFiles();
            if(filepole != null) {
                for (File f : filepole) {
                    TreeItem<SystemObject> tItem = new TreeItem<>(new SystemObject(f, resourceBundle));
                    treeItem.getChildren().add(tItem);
                    fillTree(tItem, depth+1);
                }
            }
        }

    }

    private void changeLanguage(Locale locale) {
        resourceBundle = ResourceBundle.getBundle("bundles.lang", locale);
        fileMenu.setText(resourceBundle.getString("file"));
        closeMenuItem.setText(resourceBundle.getString("file.close"));
        aboutMenuItem.setText(resourceBundle.getString("file.about"));
        languageMenu.setText(resourceBundle.getString("language"));
        englishMenuItem.setText(resourceBundle.getString("language.en"));
        czechMenuItem.setText(resourceBundle.getString("language.cz"));

        leftTreeButton.setText(resourceBundle.getString("treeView"));
        leftAddButton.setText(resourceBundle.getString("add"));
        leftDeleteButton.setText(resourceBundle.getString("delete"));
        leftRefreshButton.setText(resourceBundle.getString("refresh"));
        leftCopyButton.setText(resourceBundle.getString("copy"));
        leftMoveButton.setText(resourceBundle.getString("move"));

        rightTreeButton.setText(resourceBundle.getString("treeView"));
        rightAddButton.setText(resourceBundle.getString("add"));
        rightDeleteButton.setText(resourceBundle.getString("delete"));
        rightRefreshButton.setText(resourceBundle.getString("refresh"));
        rightCopyButton.setText(resourceBundle.getString("copy"));
        rightMoveButton.setText(resourceBundle.getString("move"));


        leftTableNameColumn.setText(resourceBundle.getString("table.name"));
        leftTableSizeColumn.setText(resourceBundle.getString("table.size"));
        leftTableDateColumn.setText(resourceBundle.getString("table.date"));
        leftTableTypeColumn.setText(resourceBundle.getString("table.type"));
        rightTableNameColumn.setText(resourceBundle.getString("table.name"));
        rightTableSizeColumn.setText(resourceBundle.getString("table.size"));
        rightTableDateColumn.setText(resourceBundle.getString("table.date"));
        rightTableTypeColumn.setText(resourceBundle.getString("table.type"));
        refreshTableViews();
    }

    private void listRoots() {
        ObservableList<String> rootsList = FXCollections.observableArrayList();
        File[] roots = File.listRoots();
        for (File root : roots) {
            if (!root.toString().equals("A:/"))
                if (root.listFiles() != null)
                    rootsList.add(root.toString());
        }
        leftRootChoicebox.setItems(rootsList);
        rightRootChoicebox.setItems(rootsList);
        leftRootChoicebox.getSelectionModel().selectFirst();
        rightRootChoicebox.getSelectionModel().selectFirst();
    }

    private void prepareTables() {
        leftTableImageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        leftTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        leftTableDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        leftTableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        leftTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        rightTableImageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
        rightTableNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        rightTableDateColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        rightTableSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        rightTableTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        leftTableTypeColumn.setSortType(TableColumn.SortType.DESCENDING);
        rightTableTypeColumn.setSortType(TableColumn.SortType.DESCENDING);

        leftTableView.getSortOrder().add(leftTableTypeColumn);
        rightTableView.getSortOrder().add(rightTableTypeColumn);

        leftTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        rightTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void leftRootChoicebox_OnAction(ActionEvent e) {
        rootChoicebox_onAction(leftRootChoicebox, leftTableView, leftCurrentPathField);
    }

    public void rightRootChoicebox_OnAction(ActionEvent e) {
        rootChoicebox_onAction(rightRootChoicebox, rightTableView, rightCurrentPathField);
    }

    private void rootChoicebox_onAction(ChoiceBox rootChoiceBox, TableView<SystemObject> tableView, TextField currentPathField) {
        try {
            refreshTableView(rootChoiceBox.getSelectionModel().getSelectedItem().toString(), tableView, currentPathField);
            int leftChoiceBoxLastSelected = rootChoiceBox.getSelectionModel().getSelectedIndex();
            rootChoiceBox.getSelectionModel().select(leftChoiceBoxLastSelected);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void leftTableView_OnMouseClicked(MouseEvent event) {
        tableView_onMouseClicked(event, leftTableView, leftCurrentPathField);
    }

    public void rightTableView_OnMouseClicked(MouseEvent event) {
        tableView_onMouseClicked(event, rightTableView, rightCurrentPathField);
    }

    private void tableView_onMouseClicked(MouseEvent event, TableView<SystemObject> tableView, TextField currentPathField) {
        if (event.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem().getFileName() != null && tableView.getSelectionModel().getSelectedItem().getTypeOfFile() != SystemObjectFileType.FILE && tableView.getSelectionModel().getSelectedItem().getTypeOfFile() != null) {
            String name = "";
            name += "/" + tableView.getSelectionModel().getSelectedItem().getFileName();
            String path = currentPathField.getText() + name;
            refreshTableView(path, tableView, currentPathField);
        }
    }

    private void treeView_onMouseClicked(MouseEvent event, TreeView<SystemObject> treeView, TableView<SystemObject> tableView, TextField currentPathField) {
        if (event.getButton() == MouseButton.SECONDARY && treeView.getSelectionModel().getSelectedItem().getValue() != null && treeView.getSelectionModel().getSelectedItem().getValue().getTypeOfFile() == SystemObjectFileType.DIRECTORY) {
            String path = treeView.getSelectionModel().getSelectedItem().getValue().getFile().getAbsolutePath();
            refreshTableView(path, tableView, currentPathField);
        }
    }

    @FXML
    private void refreshLeft() {
        refreshTableView(leftCurrentPathField.getText(), leftTableView, leftCurrentPathField);
    }

    @FXML
    private void refreshRight() {
        refreshTableView(rightCurrentPathField.getText(), rightTableView, rightCurrentPathField);
    }

    private void refreshTableViews() {
        refreshLeft();
        refreshRight();
    }

    private void refreshTableView(String refreshPath, TableView<SystemObject> tableView, TextField pathTextField) {
        List<SystemObject> systemObjectList = new ArrayList<>();
        SystemObject rootFile = new SystemObject(new File(refreshPath), resourceBundle);
        String normalizedPath = FilenameUtils.normalize(rootFile.getFile().getAbsolutePath());
        rootFile = new SystemObject(new File(normalizedPath), resourceBundle);
        if (rootFile.exists() && rootFile.isDirectory()) {
            systemObjectList = rootFile.listSystemFiles(resourceBundle);
        }

        ObservableList<SystemObject> observableList = FXCollections.observableArrayList(systemObjectList);

        String rootParentName = rootFile.getFile().getParent();
        SystemObject rootParentFile;
        if (rootParentName != null) {
            rootParentFile = new SystemObject(new File(rootParentName), resourceBundle);
            rootParentFile.setFileName("/..");
        } else {
            rootParentFile = rootFile;
            rootParentFile.setFileName("");
        }
        rootParentFile.setLastModified("");
        rootParentFile.setFileType("");
        rootParentFile.setTypeOfFile(SystemObjectFileType.ROOT);
        rootParentFile.setSize("");
        ImageView leftImageView = new ImageView(new Image("/images/go-up.png"));
        leftImageView.setFitHeight(20);
        leftImageView.setFitWidth(20);
        rootParentFile.setImage(leftImageView);

        // přidá všechny soubory do tabulky
        observableList.add(0, rootParentFile);
        tableView.setItems(observableList);

        // přiditá cestu do textboxu
        try {
            pathTextField.setText(new File(refreshPath).getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToLeft(){
        addFiles(leftCurrentPathField.getText());
    }
    public void addToRight(){
        addFiles(rightCurrentPathField.getText());
    }

    public void copyToRight() {
        List<SystemObject> selectedObjects = leftTableView.getSelectionModel().getSelectedItems();
        copyFiles(selectedObjects, rightCurrentPathField.getText());
    }

    public void copyToLeft() {
        List<SystemObject> selectedObjects = rightTableView.getSelectionModel().getSelectedItems();
        copyFiles(selectedObjects, leftCurrentPathField.getText());
    }

    private void addFiles(String targetDirectoryPath){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText(resourceBundle.getString("addHeader"));
        td.setTitle(resourceBundle.getString("addTitle"));
        td.showAndWait();
        String text = td.getEditor().getText();
        if(text != null && !text.isEmpty()){
            File file = new File(new File(targetDirectoryPath),text);
            file.mkdirs();
            file.mkdir();
        }
        refreshTableViews();

    }

    private void copyFiles(List<SystemObject> selectedObjects, String targetDirectoryPath) {
        for (SystemObject object : selectedObjects) {
            if (object.getTypeOfFile() != SystemObjectFileType.ROOT) {
                Path sourcePath = object.getFile().toPath();
                Path targetPath = new File(targetDirectoryPath + "/" + sourcePath.getFileName()).toPath();

                File source = new File(sourcePath.toString());
                File dest = new File(targetPath.toString());

                try {
                    // zkontroluje jestli jsou ve stejném adresáři
                    if (source.getCanonicalPath().equals(dest.getCanonicalPath())) {
                        return;
                    }
                    if (dest.exists())
                        dest = throwOverwriteAlert(targetPath);
                    if (dest != null) {
                        runCopyTask(source, dest);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File throwOverwriteAlert(Path destPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("overwriteTitle"));
        alert.setHeaderText(resourceBundle.getString("overwriteHeader"));
        alert.setContentText(resourceBundle.getString("overwriteMessage") + "\n" + destPath.toString());

        ButtonType buttonTypeOverwrite = new ButtonType(resourceBundle.getString("overwrite"));
        ButtonType buttonTypeRename = new ButtonType(resourceBundle.getString("rename"));
        ButtonType buttonTypeCancel = new ButtonType(resourceBundle.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOverwrite, buttonTypeRename, buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeRename) {
            destPath = getUniqueFilePath(destPath);
        } else if (result.get() == buttonTypeCancel) {
            return null;
        }

        return new File(destPath.toString());
    }

    private Path getUniqueFilePath(Path destPath) {
        File destFile = new File(destPath.toString());
        File[] dirFiles = new File(destFile.getParent()).listFiles();
        String newFileName = destFile.getName();
        boolean unique = false;
        int i = 1;
        while (!unique) {
            unique = true;
            for (File file : dirFiles) {
                if (file.getName().equals(newFileName)) {
                    newFileName = " (" + i + ")" + destFile.getName();
                    i++;
                    unique = false;
                }
            }
        }
        return Paths.get(destFile.getParent() + "/" + newFileName);
    }

    private void runCopyTask(File source, File dest) {

        copyTask = new Task() {
            @Override
            protected Void call() throws Exception {
                if (!source.isDirectory()) {
                    FileUtils.copyFile(source, dest, true);
                } else {
                    FileUtils.copyDirectory(source, dest, true);
                }
                return null;
            }
        };
        new Thread(copyTask).start();

        copyTask.setOnSucceeded(e -> {
            refreshTableViews();
        });

        copyTask.setOnCancelled(e -> {
            refreshTableViews();
        });
    }

    public void moveToRight() {
        List<SystemObject> selectedObjects = leftTableView.getSelectionModel().getSelectedItems();
        moveFiles(selectedObjects, rightCurrentPathField.getText());
    }

    public void moveToLeft() {
        List<SystemObject> selectedObjects = rightTableView.getSelectionModel().getSelectedItems();
        moveFiles(selectedObjects, leftCurrentPathField.getText());
    }

    private void moveFiles(List<SystemObject> selectedObjects, String targetDirectoryPath) {
        for (SystemObject object : selectedObjects) {
            if (object.getTypeOfFile() != SystemObjectFileType.ROOT) {
                Path sourcePath = object.getFile().toPath();
                Path targetPath = new File(targetDirectoryPath + "/" + sourcePath.getFileName()).toPath();

                File source = new File(sourcePath.toString());
                File dest = new File(targetPath.toString());

                try {
                    // zkontroluje jestli jsou ve stejném adresáři
                    if (source.getCanonicalPath().equals(dest.getCanonicalPath())) {
                        return;
                    }
                    if (dest.exists())
                        dest = throwOverwriteAlert(targetPath);
                    if (dest != null) {
                        runMoveTask(source, dest);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runMoveTask(File source, File dest) {

        moveTask = new Task() {
            @Override
            protected Void call() throws Exception {
                if (!source.isDirectory()) {
                    FileUtils.moveFile(source, dest);
                } else {
                    FileUtils.moveDirectory(source, dest);
                }
                return null;
            }
        };
        new Thread(moveTask).start();

        moveTask.setOnSucceeded(e -> {
            refreshTableViews();
        });

        moveTask.setOnCancelled(e -> {
            refreshTableViews();
        });
    }

    public void deleteFromLeft() {
        deleteFiles(leftTableView, leftCurrentPathField.getText());
    }

    public void deleteFromRight() {
        deleteFiles(rightTableView, rightCurrentPathField.getText());
    }

    private void deleteFiles(TableView tableView, String currentPath) {
        List<SystemObject> fileObjects;
        List<SystemObject> filesToDelete = new ArrayList<>();
        if ((fileObjects = tableView.getSelectionModel().getSelectedItems()) != null) {
            for (SystemObject fileObject : fileObjects) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(resourceBundle.getString("alertDeleteTitle"));
                alert.setHeaderText(resourceBundle.getString("alertDeleteHeader"));
                alert.setContentText(resourceBundle.getString("alertDeleteContent") + "\n" + fileObject.getFileName());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    filesToDelete.add(new SystemObject(fileObject.getFile(), resourceBundle));
                }
            }
            if (!filesToDelete.isEmpty()) {
                runDeleteTask(filesToDelete);
            }
        }
    }

    private void runDeleteTask(List<SystemObject> filesToDelete) {
        deleteTask = new Task() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                for (SystemObject fileToDelete : filesToDelete) {
                    if (!fileToDelete.isDirectory()) {
                        FileUtils.forceDelete(fileToDelete.getFile());
                    } else {
                        FileUtils.deleteDirectory(fileToDelete.getFile());
                    }
                }
                return null;
            }
        };
        new Thread(deleteTask).start();

        deleteTask.setOnSucceeded(e -> {
//            waitingBoxStage.close();
            refreshTableViews();
        });
        deleteTask.setOnCancelled(e -> {
            refreshTableViews();
        });
    }




}
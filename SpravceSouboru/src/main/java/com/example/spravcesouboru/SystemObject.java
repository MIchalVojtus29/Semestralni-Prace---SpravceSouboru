package com.example.spravcesouboru;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Trida SystemObject reprezenzuje soubor/složku
 *
 * @author  Michal Vojtuš
 * @version 1.2
 */
public class SystemObject {
    private File file;
    private ImageView image;
    private SimpleStringProperty fileName;
    private SimpleStringProperty size;
    private SimpleStringProperty fileType;
    private String lastModified;
    private Date lastModifiedDate;
    private SystemObjectFileType typeOfFile;
    private static Image folderImg = new Image("/images/dir.png");
    private static Image fileImg = new Image("/images/file.png");

    public SystemObject(File file, ResourceBundle resourceBundle) {
        try {
            if (file.exists()) {
                this.file = file;

                // data format
                SimpleDateFormat sdf = new SimpleDateFormat(resourceBundle.getString("date-formatter"));
                this.lastModifiedDate = new Date(file.lastModified());
                if (file.lastModified() != 0)
                    this.lastModified = sdf.format(lastModifiedDate);
                else
                    this.lastModified = "";

                // Nastavení velikosti souboru
                if (file.isFile()) {
                    long size = Files.size(file.toPath());
                    if (size < 1024)
                        this.size = new SimpleStringProperty(String.valueOf(size) + "B");
                    else if (size < 1_024_000)
                        this.size = new SimpleStringProperty((String.format("%.1f", (double) size / 1_024)) + "kB");
                    else if (size < 1_024_000_000)
                        this.size = new SimpleStringProperty((String.format("%.1f", (double) size / 1_024_000)) + "MB");
                    else
                        this.size = new SimpleStringProperty(
                                (String.format("%.1f", (double) size / 1_024_000_000)) + "GB");
                } else
                    this.size = new SimpleStringProperty("");

                // nastavení jména souboru

                this.fileName = new SimpleStringProperty(file.getName());

                if (file.isDirectory())
                    this.image = new ImageView(folderImg);
                else
                    this.image = new ImageView(fileImg);
                image.setFitHeight(20);
                image.setFitWidth(20);

                // nastavení typu souboru podle přípony
                if (file.isFile()) {
                    if (file.getName().contains(".")) {
                        this.fileType = new SimpleStringProperty(resourceBundle.getString("file") + " "
                                + file.getName().substring(file.getName().lastIndexOf(".") + 1));
                    } else {
                        this.fileType = new SimpleStringProperty("");
                        this.typeOfFile = SystemObjectFileType.FILE;
                    }
                } else {
                    this.fileType = new SimpleStringProperty(resourceBundle.getString("directory"));
                    this.typeOfFile = SystemObjectFileType.DIRECTORY;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "SystemObject{" +
                "file=" + file +
                ", image=" + image +
                ", fileName=" + fileName +
                ", size=" + size +
                ", fileType=" + fileType +
                ", lastModified='" + lastModified + '\'' +
                ", lastModifiedDate=" + lastModifiedDate +
                ", typeOfFile=" + typeOfFile +
                '}';
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public SimpleStringProperty fileNameProperty() {
        return fileName;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    public String getFileType() {
        return fileType.get();
    }

    public void setFileType(String fileType) {
        this.fileType.set(fileType);
    }

    public SimpleStringProperty fileTypeProperty() {
        return fileType;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public SystemObjectFileType getTypeOfFile() {
        return typeOfFile;
    }

    public void setTypeOfFile(SystemObjectFileType typeOfFile) {
        this.typeOfFile = typeOfFile;
    }

    public boolean isDirectory() {
        return getFile().isDirectory();
    }

    public boolean exists() {
        return getFile().exists();
    }

    public Path toPath() {
        return getFile().toPath();
    }

    public List<SystemObject> listSystemFiles(ResourceBundle properties) {
        List<SystemObject> systemFiles = new ArrayList<>();
        for (File f : Objects.requireNonNull(getFile().listFiles())) {
            if(f.getName() != null){
                systemFiles.add(new SystemObject(f, properties));
            }
        }
        return systemFiles;
    }
}

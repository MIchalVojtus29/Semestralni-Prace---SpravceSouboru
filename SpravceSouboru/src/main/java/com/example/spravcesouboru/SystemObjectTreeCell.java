package com.example.spravcesouboru;

import javafx.scene.control.TreeCell;


/**
 * Třída SystemObjectTreeCell reprezentuje jeden prvek TreeView a slouží k jeho naplnění jménem a ikonkou  souboru
 *
 * @author  Michal Vojtuš
 * @version 1.1
 */
public class SystemObjectTreeCell extends TreeCell<SystemObject> {

    @Override
    protected void updateItem(SystemObject item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getFile().getName());
            setGraphic(item.getImage());
        }
    }
}
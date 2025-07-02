package se233.chapter1.controller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import se233.chapter1.Launcher;
import se233.chapter1.model.DamageType;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.item.Armor;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;

import java.util.ArrayList;

public class AllCustomHandler{
    private static boolean flag = false;
    public static class GenCharacterHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            ArrayList<BasedEquipment> allEquipmentArray = new ArrayList<>();
            allEquipmentArray = GenItemList.setUpItem();
            Launcher.setAllEquipments(allEquipmentArray);
            Launcher.setEquippedArmor(null);
            Launcher.setEquippedWeapon(null);
            Launcher.setMainCharacter(GenCharacter.setUpCharacter());
            Launcher.refreshPane();
        }
    }
    public static void onDragDetected(MouseEvent event, BasedEquipment equipment, ImageView imgView) {
        Dragboard db = imgView.startDragAndDrop(TransferMode.MOVE);
        db.setDragView(imgView.getImage());
        ClipboardContent content = new ClipboardContent();
        content.put(equipment.DATA_FORMAT, equipment);
        db.setContent(content);
        event.consume();
    }
    public static void onDragOver(DragEvent event, String type) {
        Dragboard dragboard = event.getDragboard();
        BasedEquipment retrievedEquipment = (BasedEquipment) dragboard.getContent(BasedEquipment.DATA_FORMAT);
        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT)&& retrievedEquipment.getClass().getSimpleName().equals(type))
            event.acceptTransferModes(TransferMode.MOVE);
    }
    public static void onDragDropped(DragEvent event, Label lbl, StackPane imgGroup) {
        boolean dragCompleted = false;
        Dragboard dragboard = event.getDragboard();
        ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
        if (dragboard.hasContent(BasedEquipment.DATA_FORMAT)){
            BasedEquipment retrievedEquipment = (BasedEquipment)dragboard.getContent(BasedEquipment.DATA_FORMAT);
            BasedCharacter character = Launcher.getMainCharacter();

            if (retrievedEquipment instanceof Weapon){

                if (((Weapon) retrievedEquipment).getDamageType().equals(character.getType()) || character.getType().equals(DamageType.Hybrid)){
                    if (Launcher.getEquippedWeapon() != null)
                        allEquipments.add(Launcher.getEquippedWeapon());
                    Launcher.setEquippedWeapon((Weapon)retrievedEquipment);
                    character.equipWeapon((Weapon)retrievedEquipment);
                    if (imgGroup.getChildren().size() != 1){
                        imgGroup.getChildren().remove(1);
                        Launcher.refreshPane();
                    }
                    dragCompleted = true;
                    flag = true;
                }else {
                    imgGroup.getChildren().remove(2);
                    Launcher.refreshPane();
                }
            } else if (retrievedEquipment instanceof Armor && (character.getType().equals(DamageType.physical)|| character.getType().equals(DamageType.magical))) {
                if (Launcher.getEquippedArmor() != null)
                    allEquipments.add(Launcher.getEquippedArmor());
                Launcher.setEquippedArmor((Armor)retrievedEquipment);
                character.equipArmor((Armor) retrievedEquipment);
                if (imgGroup.getChildren().size() != 1){
                    imgGroup.getChildren().remove(1);
                    Launcher.refreshPane();
                }
                dragCompleted = true;
                flag = true;
            }else {
                    imgGroup.getChildren().remove(1);
                    Launcher.refreshPane();

            }

            Launcher.setMainCharacter(character);
            Launcher.setAllEquipments(allEquipments);
            ImageView imgView = new ImageView();
            lbl.setText(retrievedEquipment.getClass().getSimpleName() + ":\n"+retrievedEquipment.getName());
            imgView.setImage(new Image(Launcher.class.getResource(retrievedEquipment.getImgpath()).toString()));
            imgGroup.getChildren().add(imgView);


        }
        event.setDropCompleted(dragCompleted);
        event.consume();
    }
    public static void onEquipDone(DragEvent event) {

        if (flag){
            Dragboard dragboard = event.getDragboard();
            ArrayList<BasedEquipment> allEquipments = Launcher.getAllEquipments();
            BasedEquipment retrievdEqipment = (BasedEquipment) dragboard.getContent(BasedEquipment.DATA_FORMAT);
            int pos = -1;
            for(int i = 0;i <  allEquipments.size();i++){
                if (allEquipments.get(i).getName().equals(retrievdEqipment.getName())){
                    pos = i;
                }
            }
            if (pos != -1){
                allEquipments.remove(pos);
            }
            Launcher.setAllEquipments(allEquipments);
            Launcher.refreshPane();

        }

    flag = false;

    }


    public static void unEquip(ActionEvent event){
        BasedCharacter character = Launcher.getMainCharacter();
        ArrayList<BasedEquipment> allEquipmentArray = new ArrayList<>();
        allEquipmentArray = GenItemList.setUpItem();
        Launcher.setAllEquipments(allEquipmentArray);
        Launcher.setEquippedArmor(null);
        Launcher.setEquippedWeapon(null);
        character.unequipArmor();
        character.unequipWeapon();
        Launcher.refreshPane();
    }

}

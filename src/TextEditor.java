package src;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;

/**
 * this program is a text editor
 */

public class TextEditor extends Application {

    float init_width = 640;
    float init_height = 480;

    File fileName;
    BorderPane root;
    Canvas canvas;
    GraphicsContext g;
    TextArea textArea;

    MenuBar menu;
    Menu file;
    Menu edit;
    Menu view;
    Menu draw;

    BackgroundFill fill;
    boolean isDrawing = false;

    @Override
    public void start(Stage stage) throws Exception {
        root = new BorderPane();
        textArea = new TextArea();
        if(fill != null){
            textArea.setBackground(new Background(fill));
        }
        canvas = new Canvas(600, 500);
        g = canvas.getGraphicsContext2D();

        // File
        MenuItem newText = new MenuItem("New");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");

        // Edit
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");
        // View
        MenuItem editor = new MenuItem("Editor Settings");

        // Menus
        file = new Menu("File", null, newText, open, save, saveAs);
        edit = new Menu("Edit", null, undo, redo);
        view = new Menu("View", null, editor);
        draw = new Menu("Draw");

        draw.setOnAction(e -> drawingOn());
        open.setOnAction(e -> {
            try {
                openFile(textArea, stage);
            } catch (Exception e1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Failed");
                alert.setTitle("File Open");
                alert.setContentText("Failed Opening chosen Path File");
                alert.show();
            }
        });
        save.setOnAction(e -> {
            try {
                saveFile(stage);
            } catch (Exception e1) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Failed");
                alert.setTitle("File Open");
                alert.setContentText("Failed Opening chosen Path File");
                alert.show();
            }
        });
        editor.setOnAction(e -> openEditor());

        //MenuBar
        menu = new MenuBar(file, edit, view);

        root.setCenter(textArea);
        root.setTop(menu);
        root.setMinHeight(init_height);
        root.setMinWidth(init_width);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        if(fileName != null){
            stage.setTitle(fileName.getName());
        }else stage.setTitle("New");
        stage.show();

        stage.setOnCloseRequest(e->{
            Platform.exit();
        });
    }
    
    private void saveFile(Stage stage) throws Exception{
        List<String> text = new ArrayList<String>();
        String[] lines = textArea.getText().split("\n");
        for(int i = 0; i < lines.length; i++){
            text.add(lines[i]);
        }
        if(fileName != null){
            Files.write(fileName.toPath(), text, StandardOpenOption.CREATE);
        }else{
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("text");
            fileChooser.setTitle("Save");
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "."), new ExtensionFilter("txt", "*.txt"));
            File file = fileChooser.showSaveDialog(stage);
            if(file != null){
                try{
                    PrintWriter writer;
                    writer = new PrintWriter(file);
                    for (String string : lines) {
                        writer.println(string);
                    }
                    writer.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                fileName = file;
                String name = file.getName();
                int dot = name.indexOf(".");
                name = name.substring(0, dot);
                stage.setTitle(name);
            }
        }
    }
    private void openFile(TextArea t, Stage stage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        File file;
        fileChooser.setTitle("Open File");
        fileChooser.setInitialDirectory(new File("./"));
        
        file = fileChooser.showOpenDialog(null);
        if(file != null){
            textArea.clear();
            List<String> lines = Files.readAllLines(file.toPath());
            lines.forEach(line -> {
                t.appendText(line + "\n");
            });
        }
        if(fileName == null){
            fileName = file;
            String name = file.getName();
            int dot = name.indexOf(".");
            name = name.substring(0, dot);
            stage.setTitle(name);
        }else{
        }
    }
    private void drawingOn() {
        if(isDrawing == true){
            isDrawing = false;
            return;
        } 
        if(isDrawing == false) isDrawing = true;
    }
    private void openEditor(){
            Stage editor = new Stage();
            editor.setTitle("Editor");
            Button color = new Button("Color");
            VBox options = new VBox(color);
            VBox colorSettings = colorSettings();
            BorderPane root = new BorderPane();
            root.setLeft(options);
            root.setMinHeight(init_height/2);
            root.setMinWidth(init_width/2);
            color.setOnAction(e -> {
                root.setCenter(colorSettings);
            });
            Scene scene = new Scene(root);
            editor.setScene(scene);
            editor.initModality(Modality.APPLICATION_MODAL);
            editor.showAndWait();
            editor.requestFocus();
    }
    VBox colorSettings(){
        Text text = new Text("Change editor color settings: ");
        ColorPicker picker = new ColorPicker(); 
        picker.setValue(Color.CORAL);
        picker.setOnAction(e->{
            fill = new BackgroundFill(picker.getValue(), CornerRadii.EMPTY, new Insets(0));
            String fillCode = fill.getFill().toString().substring(2, 8);
            textArea.setBackground(new Background(fill));
            textArea.setStyle("-fx-control-inner-background:#"+fillCode);
        });
        VBox settings = new VBox(text, picker);
        

        return settings;
    }
   
    public static void main(String[] args) {
        launch(args);
    }
}
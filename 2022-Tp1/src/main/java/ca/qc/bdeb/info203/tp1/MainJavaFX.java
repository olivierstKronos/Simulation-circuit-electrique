package ca.qc.bdeb.info203.tp1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class MainJavaFX extends Application {
    private final String[] paths = new String[]{"/mots-croises1.txt", "/mots-croises2.txt", "/mots-croises3.txt"};
    private MotsCroises jeu;
    private GridPane grilleJeu;
    //panneau pour afficher majoritairement les erreurs et pour afficher la réussite du tableau
    private Text infoPane;
    private int focusWord;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        var root = new BorderPane();
        var scroller = new ScrollPane(root);
        var top = new VBox();
        var bottom = new VBox();
        var titre = new HBox(10);
        var choseFile = new Button("Ouvrir un autre mot croisée");
        var scene = new Scene(scroller, 750, 600);
        root.setPrefSize(750, 600);
        root.setPadding(new Insets(15, 20, 10, 20));
        jeu = new MotsCroises();
        grilleJeu = new GridPane();
        infoPane = new Text();

        //Top
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem exit = new MenuItem("exit");
        exit.setOnAction(event -> Platform.exit());
        menuFile.getItems().add(exit);
        menuBar.getMenus().add(menuFile);
        Image image = new Image("/mots.png", 60, 60, true, true);
        ImageView icon = new ImageView(image);
        titre.getChildren().add(icon);
        Text text = new Text("Super Mots-Croisés Master 3000");
        text.setFont(new Font(18));
        titre.getChildren().add(text);
        titre.setAlignment(Pos.CENTER);

        ComboBox<String> box = new ComboBox<>(FXCollections.observableArrayList(paths));
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object != null ? object.substring(1) : "";
            }

            @Override
            public String fromString(String string) {
                return "/" + string;
            }
        });
        box.setOnAction(event -> {
            try {
                infoPane.setText("");
                jeu.tryLoadGame(MainJavaFX.class.getResourceAsStream(box.getSelectionModel().getSelectedItem()));
                initialiserGrilleJeu();
                initialiserDescription(bottom);
            } catch (IOException e) {
                infoPane.setText(e.getMessage());
                infoPane.setFill(Color.RED);
            } catch (IllegalArgumentException | NullPointerException e) {
                infoPane.setText("le fichier est corrompus. Veuillez essayer une autre partie");
                infoPane.setFill(Color.RED);

            }
        });
        choseFile.setOnAction(event -> {
            infoPane.setText("");
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir un fichier de mot croisé");
            File anFile = fileChooser.showOpenDialog(primaryStage);
            if (anFile != null) {
                try {
                    jeu.tryLoadGame(new FileInputStream(anFile));
                    initialiserGrilleJeu();
                    initialiserDescription(bottom);
                } catch (IOException | IllegalArgumentException e) {
                    infoPane.setText(e.getMessage());
                    infoPane.setFill(Color.RED);
                }
            }
        });
        infoPane.setFont(new Font(18));
        top.getChildren().addAll(menuBar, titre, box, choseFile, infoPane);
        top.setAlignment(Pos.CENTER);
        top.setSpacing(5);
        root.setTop(top);

        //Center
        if (paths.length == 0) {
            infoPane.setText("Aucun fichier de base existe");
            infoPane.setFill(Color.RED);
        }
        for (int i = 0; i < paths.length; i++) {
            try {
                jeu.tryLoadGame(MainJavaFX.class.getResourceAsStream(paths[i]));
                box.getSelectionModel().select(i);
                break;
            } catch (IOException e) {
                infoPane.setText(e.getMessage());
                infoPane.setFill(Color.RED);
            } catch (IllegalArgumentException | NullPointerException e) {
                continue;
            }
            if (i == paths.length - 1) {
                infoPane.setText("Tout les fichiers fournis sont invalide! Vous pouvez essayer votre propre fichier");
                infoPane.setFill(Color.RED);
            }


        }
        initialiserGrilleJeu();
        grilleJeu.setAlignment(Pos.CENTER);
        root.setCenter(grilleJeu);

        //Bottom
        initialiserDescription(bottom);
        root.setBottom(bottom);

        //----------------------------
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mots-Croisés");
        primaryStage.getIcons().add(new Image("/icon.png"));
        primaryStage.show();
    }

    /**
     * Construit ou reconstruit la description des mots relatifs au mot croisé.
     *
     * @param bottom contenant des éléments de la description
     */
    public void initialiserDescription(VBox bottom) {
        bottom.getChildren().clear();
        for (int i = 1; i < jeu.getDescriptions().size() + 1; i++) {
            final int num = i;
            HBox descriptions = new HBox(10);
            descriptions.setPadding(new Insets(3, 0, 3, 20));
            Text numero = new Text(num + ".");
            TextField answerField = new TextField();
            Text description = new Text(jeu.getDescription(num));
            final int limite = jeu.getReponse(num).length();
            answerField.lengthProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.intValue() > oldValue.intValue() &&
                        answerField.getText().length() >= limite) {
                    answerField.setText(answerField.getText().substring(0, limite));
                }
            });
            answerField.setOnKeyTyped(event -> {
                if (jeu.getReponse(num).length() < answerField.getLength()) {
                    answerField.appendText("\b");
                    return;
                }
                if (event.getCharacter().equals("\r")) {
                    infoPane.setText("");
                    jeu.transfer(num, answerField.getText());
                    paintTab();
                    boolean isWordOk = jeu.isComplete(num);
                    if (isWordOk) {
                        answerField.setDisable(true);
                        description.setStrikethrough(true);
                    }
                    if (jeu.estComplet()) {
                        infoPane.setText("Vous avez finis la grille");
                        infoPane.setFill(Color.GREEN);
                    }
                }
            });
            answerField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    focusWord = num;
                } else if (focusWord == num)
                    focusWord = -1;
                paintTab(num);
            });
            descriptions.getChildren().addAll(numero, answerField, description);
            bottom.getChildren().add(descriptions);
        }
    }

    /**
     * Construit ou reconstruit le panneau du mot croisé
     */
    public void paintTab() {
        for (int i = 0; i < jeu.getLongueurLigne(); i++) {
            for (int j = 0; j < jeu.getLongueurColonne(); j++) {
                redoComponent(i,j);
            }
        }
    }

    public void paintTab(int numeroMot) {
        Mot mot = jeu.getMot(numeroMot);
        Position pos = mot.getPos();
        boolean horizontal = mot.isHorizontal();
        int line = pos.ligne();
        int colonne = pos.colonne();
        for (int k = 0; k < mot.getReponse().length(); k++) {
            Position nextPos = horizontal ? new Position(line, colonne + k) : new Position(line + k, colonne);
            int i = nextPos.ligne();
            int j = nextPos.colonne();
            redoComponent(i,j);
        }
    }
    public void redoComponent(int i,int j){
        HBox cellule = (HBox) grilleJeu.getChildren().get((i + 1) * (jeu.getLongueurColonne() + 1) + j);
        CellState state = jeu.etatCase(i, j);
        Color color;

        switch (state) {
            case COMPLETE -> color = Color.LIGHTGREEN;
            case VIDE -> color = Color.color(0.25, 0.25, 0.25);
            case ERONNE -> color = Color.RED;
            default -> color = Color.WHITE;
        }
        if (jeu.isPositionInWord(i, j, focusWord))
            color = color.interpolate(Color.GRAY, 0.7);
        cellule.setBackground(new Background(new BackgroundFill(color, null, null)));
        Text lettre = (Text) cellule.getChildren().get(0);
        lettre.setText(jeu.getLettre(i, j) + "");
    }

    public void initialiserGrilleJeu() {
        grilleJeu.getChildren().clear();
        for (int i = 0; i < jeu.getLongueurColonne(); i++) {
            Label numeroCol = new Label(i + "");
            numeroCol.setAlignment(Pos.CENTER);
            numeroCol.setPadding(new Insets(3, 8, 3, 8));
            numeroCol.setMaxSize(30, 30);
            numeroCol.setMinSize(30, 30);
            grilleJeu.add(numeroCol, i + 1, 0);
        }
        for (int i = 0; i < jeu.getLongueurLigne(); i++) {
            Label numLigne = new Label(i + "");
            numLigne.setAlignment(Pos.CENTER);
            numLigne.setPadding(new Insets(3, 8, 3, 8));
            numLigne.setMaxSize(30, 30);
            numLigne.setMinSize(30, 30);
            grilleJeu.add(numLigne, 0, i + 1);
            for (int j = 0; j < jeu.getLongueurColonne(); j++) {
                var cellule = new HBox();
                cellule.setPadding(new Insets(3, 8, 3, 8));
                cellule.setMaxSize(30, 30);
                cellule.setMinSize(30, 30);
                cellule.setBorder(new Border(new BorderStroke(Color.BLACK,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                CellState state = jeu.etatCase(i, j);
                Color color;
                switch (state) {
                    case COMPLETE -> color = Color.LIGHTGREEN;
                    case VIDE -> color = Color.color(0.25, 0.25, 0.25);
                    case ERONNE -> color = Color.RED;
                    default -> color = Color.WHITE;
                }
                cellule.setBackground(new Background(new BackgroundFill(color, null, null)));
                Text lettre = new Text(jeu.getLettre(i, j) + "");
                lettre.setFont(Font.font("monospace", 20));
                cellule.getChildren().add(lettre);
                int numeroMot = jeu.getIndexIfFirstLetter(i, j);
                if (numeroMot != -1) {
                    var numero = new Text(numeroMot + "");
                    numero.setFont(Font.font(10));
                    cellule.getChildren().add(numero);
                }
                grilleJeu.add(cellule, j + 1, i + 1);
            }
        }
    }
}


package com.mycompany.sounds;
import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppGUI extends Application {

    // Variables globales para poder usarlas en toda la clase
    private TableView<Cancion> tablaCanciones;
    private ObservableList<Cancion> listaObservableCanciones;

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane layoutPrincipal = new BorderPane();

        // --- 1. PANEL IZQUIERDO ---
        VBox menuIzquierdo = new VBox(15);
        menuIzquierdo.setPrefWidth(220);
        menuIzquierdo.setStyle("-fx-background-color: #000000; -fx-padding: 20px;");
        
        Label textoMenu = new Label("MI BIBLIOTECA");
        textoMenu.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Button btnCargarMusica = new Button("Cargar Carpeta MP3");
        btnCargarMusica.setStyle("-fx-background-color: #1db954; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        btnCargarMusica.setPrefWidth(180);
        
        menuIzquierdo.getChildren().addAll(textoMenu, btnCargarMusica);

        // --- 2. PANEL CENTRAL ---
        VBox panelCentral = new VBox(10);
        panelCentral.setStyle("-fx-background-color: #121212; -fx-padding: 20px;");
        
        Label tituloCentral = new Label("Biblioteca Principal");
        tituloCentral.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        tablaCanciones = new TableView<>();
        tablaCanciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Cancion, String> colNombre = new TableColumn<>("Título");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        TableColumn<Cancion, String> colArtista = new TableColumn<>("Artista");
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        
        TableColumn<Cancion, String> colAlbum = new TableColumn<>("Álbum");
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        
        tablaCanciones.getColumns().addAll(colNombre, colArtista, colAlbum);
        VBox.setVgrow(tablaCanciones, javafx.scene.layout.Priority.ALWAYS);
        
        panelCentral.getChildren().addAll(tituloCentral, tablaCanciones);

        // --- 3. PANEL INFERIOR ---
        HBox barraReproduccion = new HBox(20);
        barraReproduccion.setPrefHeight(90);
        barraReproduccion.setAlignment(Pos.CENTER);
        barraReproduccion.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");
        
        Button btnAnterior = new Button("⏮");
        Button btnPlay = new Button("▶ Play");
        Button btnPausa = new Button("⏸ Pausa");
        Button btnSiguiente = new Button("⏭");
        
        String estiloBotones = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;";
        btnAnterior.setStyle(estiloBotones);
        btnPlay.setStyle(estiloBotones + "-fx-font-size: 20px;");
        btnPausa.setStyle(estiloBotones + "-fx-font-size: 20px;");
        btnSiguiente.setStyle(estiloBotones);
        
        barraReproduccion.getChildren().addAll(btnAnterior, btnPlay, btnPausa, btnSiguiente);

        // --- EVENTOS (La magia sucede aquí) ---
        
        // Acción del botón Cargar Música
        btnCargarMusica.setOnAction(evento -> {
            DirectoryChooser selectorDirectorio = new DirectoryChooser();
            selectorDirectorio.setTitle("Selecciona la carpeta con tu música");
            
            // Mostrar la ventana para elegir carpeta
            File carpetaSeleccionada = selectorDirectorio.showDialog(escenarioPrincipal);
            
            if (carpetaSeleccionada != null) {
                System.out.println("Carpeta seleccionada: " + carpetaSeleccionada.getAbsolutePath());
                
                // Usar tu clase LectorArchivos para procesar los MP3
                LectorArchivos lector = new LectorArchivos();
                lector.leerCarpetaRecursivamente(carpetaSeleccionada.getAbsolutePath());
                List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
                
                // Convertir la lista normal a una lista que la tabla pueda entender y mostrar
                listaObservableCanciones = FXCollections.observableArrayList(cancionesLeidas);
                tablaCanciones.setItems(listaObservableCanciones);
                
                System.out.println("¡Se cargaron " + cancionesLeidas.size() + " canciones en la tabla!");
            }
        });

        // --- Ensamblar y Mostrar ---
        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setBottom(barraReproduccion);

        Scene escena = new Scene(layoutPrincipal, 1000, 700); 
        
        // Agregar una pequeña hoja de estilos para que la tabla se vea oscura y combine con el resto
        escena.getRoot().setStyle("-fx-base: #121212; -fx-control-inner-background: #121212; -fx-table-cell-border-color: transparent; -fx-table-header-background-color: #282828;");

        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
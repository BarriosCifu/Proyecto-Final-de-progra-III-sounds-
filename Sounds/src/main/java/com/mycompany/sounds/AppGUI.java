
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

    // Variables globales
    private TableView<Cancion> tablaCanciones;
    private ObservableList<Cancion> listaObservableCanciones;
    
    // Reproductor, canción actual y nuestra nueva "bandera" de estado
    private Reproductor reproductor = new Reproductor();
    private Cancion cancionActual = null;
    private boolean estaReproduciendo = false; // <-- Controla el estado del botón

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

        // --- 3. PANEL INFERIOR (Controles) ---
        HBox barraReproduccion = new HBox(30); // Aumenté un poco el espacio para que respire
        barraReproduccion.setPrefHeight(90);
        barraReproduccion.setAlignment(Pos.CENTER);
        barraReproduccion.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");
        
        Button btnAnterior = new Button("⏮");
        Button btnPlayPausa = new Button("▶ Play"); // ¡Nuestro nuevo botón unificado!
        Button btnSiguiente = new Button("⏭");
        
        String estiloBotones = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;";
        btnAnterior.setStyle(estiloBotones);
        btnSiguiente.setStyle(estiloBotones);
        
        // Le damos un ancho mínimo al Play para que al cambiar a "Pausa" los otros botones no se muevan
        btnPlayPausa.setStyle(estiloBotones + "-fx-font-size: 22px; -fx-min-width: 110px;"); 
        
        barraReproduccion.getChildren().addAll(btnAnterior, btnPlayPausa, btnSiguiente);

        // --- EVENTOS DE INTERFAZ ---
        
        // 1. Al seleccionar una canción en la tabla
        tablaCanciones.getSelectionModel().selectedItemProperty().addListener((observable, viejaSeleccion, nuevaSeleccion) -> {
            if (nuevaSeleccion != null) {
                cancionActual = nuevaSeleccion;
            }
        });
        
        // 2. Botón Cargar Música
        btnCargarMusica.setOnAction(evento -> {
            DirectoryChooser selectorDirectorio = new DirectoryChooser();
            selectorDirectorio.setTitle("Selecciona la carpeta con tu música");
            File carpetaSeleccionada = selectorDirectorio.showDialog(escenarioPrincipal);
            
            if (carpetaSeleccionada != null) {
                LectorArchivos lector = new LectorArchivos();
                lector.leerCarpetaRecursivamente(carpetaSeleccionada.getAbsolutePath());
                List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
                
                listaObservableCanciones = FXCollections.observableArrayList(cancionesLeidas);
                tablaCanciones.setItems(listaObservableCanciones);
            }
        });
        
        // 3. Botón Play / Pausa (La lógica del toggle)
        btnPlayPausa.setOnAction(evento -> {
            if (cancionActual != null) {
                if (estaReproduciendo) {
                    // Si está sonando, lo detenemos y cambiamos la cara del botón a Play
                    reproductor.detener();
                    btnPlayPausa.setText("▶ Play");
                    estaReproduciendo = false;
                    System.out.println("Reproducción detenida.");
                } else {
                    // Si está detenido, aseguramos que todo se apague, le damos play y cambiamos a Pausa
                    reproductor.detener(); 
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸ Pausa");
                    estaReproduciendo = true;
                    System.out.println("Sonando: " + cancionActual.getNombre());
                }
            } else {
                System.out.println("Por favor, selecciona una canción de la tabla primero.");
            }
        });
        // 4. Botón Siguiente (⏭)
        btnSiguiente.setOnAction(evento -> {
            int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
            // Verificamos que no estemos en la última canción de la lista
            if (indiceActual >= 0 && indiceActual < tablaCanciones.getItems().size() - 1) {
                // Seleccionamos la siguiente fila en la tabla
                tablaCanciones.getSelectionModel().select(indiceActual + 1);
                
                // Actualizamos la bandera y hacemos que suene
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸ Pausa");
                estaReproduciendo = true;
                System.out.println("Saltando a la siguiente: " + cancionActual.getNombre());
            }
        });

        // 5. Botón Anterior (⏮)
        btnAnterior.setOnAction(evento -> {
            int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
            // Verificamos que no estemos en la primera canción (índice 0)
            if (indiceActual > 0) {
                // Seleccionamos la fila anterior en la tabla
                tablaCanciones.getSelectionModel().select(indiceActual - 1);
                
                // Actualizamos la bandera y hacemos que suene
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸ Pausa");
                estaReproduciendo = true;
                System.out.println("Regresando a: " + cancionActual.getNombre());
            }
        });

        // --- Ensamblar y Mostrar ---
        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setBottom(barraReproduccion);

        Scene escena = new Scene(layoutPrincipal, 1000, 700); 
        escena.getRoot().setStyle("-fx-base: #121212; -fx-control-inner-background: #121212; -fx-table-cell-border-color: transparent; -fx-table-header-background-color: #282828;");

        escenarioPrincipal.setOnCloseRequest(evento -> {
            reproductor.detener();
            System.exit(0);
        });

        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
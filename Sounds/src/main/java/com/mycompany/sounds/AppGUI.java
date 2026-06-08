package com.mycompany.sounds;


import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private List<Cancion> listaOriginalCanciones; 
    
    private Reproductor reproductor = new Reproductor();
    private Cancion cancionActual = null;
    private boolean estaReproduciendo = false;
    
    private ArbolAVL arbolBuscador = new ArbolAVL();

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
        
        // --- BARRA DE BÚSQUEDA EN TIEMPO REAL ---
        HBox barraBusqueda = new HBox(10);
        barraBusqueda.setAlignment(Pos.CENTER_LEFT);
        
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por título o artista...");
        txtBuscar.setPrefWidth(350);
        txtBuscar.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        
        Button btnLimpiar = new Button("✖ Limpiar");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-cursor: hand;");
        
        barraBusqueda.getChildren().addAll(txtBuscar, btnLimpiar);
        // -----------------------------------------

        Label tituloCentral = new Label("Biblioteca Principal");
        tituloCentral.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10px 0px 0px 0px;");
        
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
        
        panelCentral.getChildren().addAll(barraBusqueda, tituloCentral, tablaCanciones);

        // --- 3. PANEL INFERIOR (Controles) ---
        HBox barraReproduccion = new HBox(30); 
        barraReproduccion.setPrefHeight(90);
        barraReproduccion.setAlignment(Pos.CENTER);
        barraReproduccion.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");
        
        Button btnAnterior = new Button("⏮");
        Button btnPlayPausa = new Button("▶ Play"); 
        Button btnSiguiente = new Button("⏭");
        
        String estiloBotones = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;";
        btnAnterior.setStyle(estiloBotones);
        btnSiguiente.setStyle(estiloBotones);
        btnPlayPausa.setStyle(estiloBotones + "-fx-font-size: 22px; -fx-min-width: 110px;"); 
        
        barraReproduccion.getChildren().addAll(btnAnterior, btnPlayPausa, btnSiguiente);

        // --- EVENTOS DE INTERFAZ ---
        
        tablaCanciones.getSelectionModel().selectedItemProperty().addListener((observable, viejaSeleccion, nuevaSeleccion) -> {
            if (nuevaSeleccion != null) {
                cancionActual = nuevaSeleccion;
            }
        });
        
        btnCargarMusica.setOnAction(evento -> {
            DirectoryChooser selectorDirectorio = new DirectoryChooser();
            selectorDirectorio.setTitle("Selecciona la carpeta con tu música");
            File carpetaSeleccionada = selectorDirectorio.showDialog(escenarioPrincipal);
            
            if (carpetaSeleccionada != null) {
                LectorArchivos lector = new LectorArchivos();
                lector.leerCarpetaRecursivamente(carpetaSeleccionada.getAbsolutePath());
                listaOriginalCanciones = lector.getCancionesCargadas();
                
                listaObservableCanciones = FXCollections.observableArrayList(listaOriginalCanciones);
                tablaCanciones.setItems(listaObservableCanciones);
            }
        });

        // --- LA MAGIA DEL BUSCADOR EN TIEMPO REAL ---
        txtBuscar.textProperty().addListener((observable, textoViejo, textoNuevo) -> {
            // Si no hay música cargada aún, no hacemos nada
            if (listaOriginalCanciones == null) return;

            // Si el buscador está vacío, mostramos toda la biblioteca original
            if (textoNuevo == null || textoNuevo.trim().isEmpty()) {
                tablaCanciones.setItems(FXCollections.observableArrayList(listaOriginalCanciones));
            } else {
                String busqueda = textoNuevo.toLowerCase();
                
                // Filtramos la lista aplicando la validación != null a cada propiedad
                List<Cancion> cancionesFiltradas = listaOriginalCanciones.stream()
                        .filter(cancion -> {
                            // Extraemos los datos de forma segura. Si es null, asignamos texto vacío ("")
                            String tituloSeguro = (cancion.getNombre() != null) ? cancion.getNombre().toLowerCase() : "";
                            String artistaSeguro = (cancion.getArtista() != null) ? cancion.getArtista().toLowerCase() : "";
                            
                            // Verificamos si la búsqueda coincide con el título o el artista
                            return tituloSeguro.contains(busqueda) || artistaSeguro.contains(busqueda);
                        })
                        .collect(Collectors.toList());
                
                // Actualizamos la tabla
                tablaCanciones.setItems(FXCollections.observableArrayList(cancionesFiltradas));
            }
        });

        // El botón limpiar solo borra el texto, lo cual dispara automáticamente el evento de arriba y restaura la tabla
        btnLimpiar.setOnAction(evento -> {
            txtBuscar.clear();
        });
        // ---------------------------------------------
        
        // Botones de Reproducción
        btnPlayPausa.setOnAction(evento -> {
            if (cancionActual != null) {
                if (estaReproduciendo) {
                    reproductor.detener();
                    btnPlayPausa.setText("▶ Play");
                    estaReproduciendo = false;
                } else {
                    reproductor.detener(); 
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸ Pausa");
                    estaReproduciendo = true;
                }
            }
        });

        btnSiguiente.setOnAction(evento -> {
            int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
            if (indiceActual >= 0 && indiceActual < tablaCanciones.getItems().size() - 1) {
                tablaCanciones.getSelectionModel().select(indiceActual + 1);
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸ Pausa");
                estaReproduciendo = true;
            }
        });

        btnAnterior.setOnAction(evento -> {
            int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
            if (indiceActual > 0) {
                tablaCanciones.getSelectionModel().select(indiceActual - 1);
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸ Pausa");
                estaReproduciendo = true;
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
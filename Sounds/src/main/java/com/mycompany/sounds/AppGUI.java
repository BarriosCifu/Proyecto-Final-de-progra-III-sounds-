package com.mycompany.sounds;


import java.io.File;
import java.util.Collections;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppGUI extends Application {

    // Variables globales para la Biblioteca Principal
    private TableView<Cancion> tablaCanciones;
    private ObservableList<Cancion> listaObservableCanciones;
    private List<Cancion> listaOriginalCanciones; 
    
    // Variables globales para "Siguiente en la Fila" (La Cola)
    private TableView<Cancion> tablaCola;
    private ObservableList<Cancion> listaObservableCola = FXCollections.observableArrayList();
    
    // Motor de audio y control de estados
    private Reproductor reproductor = new Reproductor();
    private Cancion cancionActual = null;
    private boolean estaReproduciendo = false;

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane layoutPrincipal = new BorderPane();

        // --- 1. PANEL IZQUIERDO (Menú lateral) ---
        VBox menuIzquierdo = new VBox(15);
        menuIzquierdo.setPrefWidth(220);
        menuIzquierdo.setStyle("-fx-background-color: #000000; -fx-padding: 20px;");
        
        Label textoMenu = new Label("MI BIBLIOTECA");
        textoMenu.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Button btnCargarMusica = new Button("Cargar Carpeta MP3");
        btnCargarMusica.setStyle("-fx-background-color: #1db954; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        btnCargarMusica.setPrefWidth(180);
        
        menuIzquierdo.getChildren().addAll(textoMenu, btnCargarMusica);

        // --- 2. PANEL CENTRAL (Biblioteca Principal) ---
        VBox panelCentral = new VBox(10);
        panelCentral.setStyle("-fx-background-color: #121212; -fx-padding: 20px;");
        
        HBox barraBusqueda = new HBox(10);
        barraBusqueda.setAlignment(Pos.CENTER_LEFT);
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por título o artista...");
        txtBuscar.setPrefWidth(300);
        txtBuscar.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        Button btnLimpiar = new Button("✖ Limpiar");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-cursor: hand;");
        barraBusqueda.getChildren().addAll(txtBuscar, btnLimpiar);

        HBox encabezadoBiblioteca = new HBox(20);
        encabezadoBiblioteca.setAlignment(Pos.CENTER_LEFT);
        Label tituloCentral = new Label("Biblioteca Principal");
        tituloCentral.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        Button btnAñadirACola = new Button("+ Añadir a la Fila");
        btnAñadirACola.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-border-color: #b3b3b3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        encabezadoBiblioteca.getChildren().addAll(tituloCentral, btnAñadirACola);
        
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
        
        panelCentral.getChildren().addAll(barraBusqueda, encabezadoBiblioteca, tablaCanciones);

        // --- 3. PANEL DERECHO (Siguiente en la Fila) ---
        VBox panelDerecho = new VBox(10);
        panelDerecho.setPrefWidth(280);
        panelDerecho.setStyle("-fx-background-color: #000000; -fx-padding: 20px; -fx-border-color: #282828; -fx-border-width: 0 0 0 1;");
        
        Label tituloCola = new Label("Siguiente en la fila");
        tituloCola.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        tablaCola = new TableView<>();
        tablaCola.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Cancion, String> colNombreCola = new TableColumn<>("Canción");
        colNombreCola.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tablaCola.getColumns().add(colNombreCola);
        tablaCola.setItems(listaObservableCola);
        VBox.setVgrow(tablaCola, javafx.scene.layout.Priority.ALWAYS);
        
        HBox botonesCola = new HBox(10);
        botonesCola.setAlignment(Pos.CENTER);
        Button btnQuitarCola = new Button("Quitar");
        Button btnSubirCola = new Button("🔼");
        Button btnBajarCola = new Button("🔽");
        
        String estiloBotonesCola = "-fx-background-color: #282828; -fx-text-fill: white; -fx-cursor: hand;";
        btnQuitarCola.setStyle(estiloBotonesCola);
        btnSubirCola.setStyle(estiloBotonesCola);
        btnBajarCola.setStyle(estiloBotonesCola);
        botonesCola.getChildren().addAll(btnQuitarCola, btnSubirCola, btnBajarCola);
        
        panelDerecho.getChildren().addAll(tituloCola, tablaCola, botonesCola);

        // --- 4. PANEL INFERIOR (Controles de reproducción) ---
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
        
        // EVENTO NUEVO: Doble clic en la tabla para reproducir inmediatamente
        tablaCanciones.setOnMouseClicked(evento -> {
            if (evento.getButton().equals(MouseButton.PRIMARY) && evento.getClickCount() == 2) {
                cancionActual = tablaCanciones.getSelectionModel().getSelectedItem();
                if (cancionActual != null) {
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸ Pausa");
                    estaReproduciendo = true;
                }
            }
        });
        
        // Cargar carpeta de música
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

        // Buscador en tiempo real
        txtBuscar.textProperty().addListener((observable, textoViejo, textoNuevo) -> {
            if (listaOriginalCanciones == null) return;
            if (textoNuevo == null || textoNuevo.trim().isEmpty()) {
                tablaCanciones.setItems(FXCollections.observableArrayList(listaOriginalCanciones));
            } else {
                String busqueda = textoNuevo.toLowerCase();
                List<Cancion> cancionesFiltradas = listaOriginalCanciones.stream()
                        .filter(cancion -> {
                            String tituloSeguro = (cancion.getNombre() != null) ? cancion.getNombre().toLowerCase() : "";
                            String artistaSeguro = (cancion.getArtista() != null) ? cancion.getArtista().toLowerCase() : "";
                            return tituloSeguro.contains(busqueda) || artistaSeguro.contains(busqueda);
                        })
                        .collect(Collectors.toList());
                tablaCanciones.setItems(FXCollections.observableArrayList(cancionesFiltradas));
            }
        });

        btnLimpiar.setOnAction(evento -> txtBuscar.clear());
        
        // Añadir canción a la cola
        btnAñadirACola.setOnAction(evento -> {
            Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                listaObservableCola.add(seleccionada);
            }
        });

        // Quitar de la cola
        btnQuitarCola.setOnAction(evento -> {
            Cancion seleccionada = tablaCola.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                listaObservableCola.remove(seleccionada);
            }
        });

        // Mover hacia arriba en la cola (🔼)
        btnSubirCola.setOnAction(evento -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
                Collections.swap(listaObservableCola, indice, indice - 1);
                tablaCola.getSelectionModel().select(indice - 1);
            }
        });

        // Mover hacia abajo en la cola (🔽)
        btnBajarCola.setOnAction(evento -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice >= 0 && indice < listaObservableCola.size() - 1) {
                Collections.swap(listaObservableCola, indice, indice + 1);
                tablaCola.getSelectionModel().select(indice + 1);
            }
        });
        
        // Botón Play / Pausa (Lógica FIFO corregida)
        btnPlayPausa.setOnAction(evento -> {
            if (estaReproduciendo) {
                reproductor.detener();
                btnPlayPausa.setText("▶ Play");
                estaReproduciendo = false;
            } else {
                if (!listaObservableCola.isEmpty()) {
                    // Saca la primera canción de la cola (FIFO)
                    cancionActual = listaObservableCola.remove(0); 
                    tablaCanciones.getSelectionModel().select(cancionActual);
                } else {
                    // Si no hay cola, toma la seleccionada en la tabla central
                    cancionActual = tablaCanciones.getSelectionModel().getSelectedItem();
                }

                if (cancionActual != null) {
                    reproductor.detener(); 
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸ Pausa");
                    estaReproduciendo = true;
                }
            }
        });

        // Botón Siguiente
        btnSiguiente.setOnAction(evento -> {
            if (!listaObservableCola.isEmpty()) {
                cancionActual = listaObservableCola.remove(0); 
                tablaCanciones.getSelectionModel().select(cancionActual);
                
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸ Pausa");
                estaReproduciendo = true;
            } else {
                int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
                if (indiceActual >= 0 && indiceActual < tablaCanciones.getItems().size() - 1) {
                    tablaCanciones.getSelectionModel().select(indiceActual + 1);
                    cancionActual = tablaCanciones.getSelectionModel().getSelectedItem();
                    
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸ Pausa");
                    estaReproduciendo = true;
                }
            }
        });

        // Botón Anterior
        btnAnterior.setOnAction(evento -> {
            int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
            if (indiceActual > 0) {
                tablaCanciones.getSelectionModel().select(indiceActual - 1);
                cancionActual = tablaCanciones.getSelectionModel().getSelectedItem();
                
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸ Pausa");
                estaReproduciendo = true;
            }
        });

        // --- Ensamblar y Mostrar ---
        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setRight(panelDerecho);
        layoutPrincipal.setBottom(barraReproduccion);

        Scene escena = new Scene(layoutPrincipal, 1150, 700); 
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
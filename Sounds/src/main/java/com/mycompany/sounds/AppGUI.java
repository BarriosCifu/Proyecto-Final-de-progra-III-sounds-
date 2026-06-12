package com.mycompany.sounds;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AppGUI extends Application {

    private TableView<Cancion> tablaCanciones;
    private ObservableList<Cancion> listaObservableCanciones;
    private List<Cancion> listaOriginalCanciones; 
    
    private TableView<Cancion> tablaCola;
    private ObservableList<Cancion> listaObservableCola = FXCollections.observableArrayList();
    
    private Reproductor reproductor = new Reproductor();
    private Cancion cancionActual = null;
    private boolean estaReproduciendo = false;
    
    private Slider sliderProgreso;
    private Label lblTiempoActual;
    private Label lblTiempoTotal;
    private boolean arrastrandoSlider = false;
    
    private Slider sliderVolumen;

    private ImageView vistaCaratula;
    private Label lblInfoCancionActual;
    
    private Label lblContadorCanciones;

    private ArbolAVL arbolBibliotecaCentral = new ArbolAVL();
    private ArbolBinarioBusqueda arbolNormalCentral = new ArbolBinarioBusqueda();

    private boolean modoRepeticion = false;
    private ListaCircular listaRepeticion = new ListaCircular(); 
    
    // --- PILA Y LISTA ESPEJO PARA HISTORIAL ---
    private Pila historial = new Pila(); 
    private ObservableList<Cancion> listaObservableHistorial = FXCollections.observableArrayList();

    private ObservableList<String> nombresPlaylists = FXCollections.observableArrayList();
    private ListView<String> vistaPlaylists;
    private Map<String, ArbolBinarioBusqueda> mapaPlaylists = new HashMap<>();
    private String playlistSeleccionada = null;
    private Label tituloCentral;
    private Set<String> cancionesFavoritas = new HashSet<>();
    private boolean modoAleatorio = false;

    // MOTORES DE RECOLECCIÓN DE DATOS PARA ESTADÍSTICAS
    private Map<String, Integer> contadorCanciones = new HashMap<>();
    private Map<String, Integer> contadorArtistas = new HashMap<>();
    private Map<String, Integer> contadorGeneros = new HashMap<>();
    private List<Double> tiemposBusquedaAVL = new ArrayList<>();
    private List<Double> tiemposBusquedaABB = new ArrayList<>();

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane layoutPrincipal = new BorderPane();

        nombresPlaylists.add("Mis me gusta");
        mapaPlaylists.put("Mis me gusta", new ArbolBinarioBusqueda()); 

        // --- 1. PANEL IZQUIERDO ---
        VBox menuIzquierdo = new VBox(15);
        menuIzquierdo.setPrefWidth(220);
        menuIzquierdo.setStyle("-fx-background-color: #000000; -fx-padding: 20px;");
        
        Label textoMenu = new Label("MI BIBLIOTECA");
        textoMenu.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        
        Button btnCargarMusica = new Button("Cargar Carpeta MP3");
        btnCargarMusica.setStyle("-fx-background-color: #1db954; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        btnCargarMusica.setPrefWidth(180);

        // NUEVO: Botón de Historial
        Button btnHistorial = new Button("🕒 Historial de Reproducción");
        btnHistorial.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 0;");
        
        Label textoPlaylists = new Label("MIS PLAYLISTS");
        textoPlaylists.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 10px 0 0 0;");
        
        HBox barraPersistencia = new HBox(10);
        Button btnGuardar = new Button("💾 Guardar");
        Button btnCargar = new Button("📂 Cargar");
        String estiloPersistencia = "-fx-background-color: #282828; -fx-text-fill: white; -fx-font-size: 11px; -fx-cursor: hand; -fx-background-radius: 5;";
        btnGuardar.setStyle(estiloPersistencia);
        btnCargar.setStyle(estiloPersistencia);
        btnGuardar.setPrefWidth(85);
        btnCargar.setPrefWidth(85);
        barraPersistencia.getChildren().addAll(btnGuardar, btnCargar);

        Button btnNuevaPlaylist = new Button("+ Nueva Playlist");
        btnNuevaPlaylist.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 0;");
        
        vistaPlaylists = new ListView<>(nombresPlaylists);
        vistaPlaylists.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #000000; -fx-border-color: transparent;");
        VBox.setVgrow(vistaPlaylists, Priority.ALWAYS); 
        
        VBox contenedorCaratula = new VBox(8);
        contenedorCaratula.setAlignment(Pos.CENTER);
        contenedorCaratula.setStyle("-fx-background-color: #121212; -fx-background-radius: 8; -fx-padding: 12px; -fx-border-color: #282828; -fx-border-radius: 8;");
        
        vistaCaratula = new ImageView();
        vistaCaratula.setFitWidth(140);
        vistaCaratula.setFitHeight(140);
        vistaCaratula.setPreserveRatio(true);
        
        lblInfoCancionActual = new Label("Sin reproducir");
        lblInfoCancionActual.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 11px; -fx-alignment: center;");
        lblInfoCancionActual.setWrapText(true);
        lblInfoCancionActual.setMaxWidth(160);
        
        contenedorCaratula.getChildren().addAll(vistaCaratula, lblInfoCancionActual);
        menuIzquierdo.getChildren().addAll(textoMenu, btnCargarMusica, btnHistorial, textoPlaylists, barraPersistencia, btnNuevaPlaylist, vistaPlaylists, contenedorCaratula);

        // --- LÓGICA DE EVENTOS DEL MENÚ IZQUIERDO ---
        btnHistorial.setOnAction(e -> {
            vistaPlaylists.getSelectionModel().clearSelection();
            playlistSeleccionada = null;
            tituloCentral.setText("Historial de Reproducción");
            tablaCanciones.setItems(listaObservableHistorial);
            tablaCanciones.refresh();
            lblContadorCanciones.setText(listaObservableHistorial.size() + " canciones escuchadas");
        });

        // --- GUARDAR (PLAYLISTS E HISTORIAL) ---
        btnGuardar.setOnAction(evento -> {
            try {
                // 1. Guardar Playlists
                try (PrintWriter writer = new PrintWriter(new FileWriter("mis_playlists.txt"))) {
                    for (Map.Entry<String, ArbolBinarioBusqueda> entry : mapaPlaylists.entrySet()) {
                        String nombreLista = entry.getKey();
                        for (Cancion c : entry.getValue().obtenerListaInOrden()) {
                            String genero = c.getGenero() != null ? c.getGenero() : "Desconocido";
                            String datosPlanos = nombreLista + "||" + c.getNombre() + "||" + c.getArtista() + "||" + c.getAlbum() + "||" + genero + "||" + c.getRuta();
                            writer.println(GestorEncriptacion.cifrar(datosPlanos));
                        }
                    }
                }

                // 2. Guardar Historial (Pila)
                try (PrintWriter writerHistorial = new PrintWriter(new FileWriter("historial_cifrado.txt"))) {
                    for (Cancion c : listaObservableHistorial) {
                        String genero = c.getGenero() != null ? c.getGenero() : "Desconocido";
                        String datosPlanos = "HISTORIAL||" + c.getNombre() + "||" + c.getArtista() + "||" + c.getAlbum() + "||" + genero + "||" + c.getRuta();
                        writerHistorial.println(GestorEncriptacion.cifrar(datosPlanos));
                    }
                }

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Guardado Exitoso");
                alerta.setHeaderText(null);
                alerta.setContentText("Las Playlists y el Historial han sido cifrados y guardados.");
                alerta.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                alerta.showAndWait();

            } catch (Exception e) { System.out.println("Error al guardar: " + e.getMessage()); }
        });

        // --- CARGAR (PLAYLISTS E HISTORIAL) ---
        btnCargar.setOnAction(evento -> {
            // 1. Cargar Playlists
            File archivoPl = new File("mis_playlists.txt");
            if (archivoPl.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(archivoPl))) {
                    String lineaCifrada;
                    mapaPlaylists.clear(); nombresPlaylists.clear(); cancionesFavoritas.clear();
                    nombresPlaylists.add("Mis me gusta"); mapaPlaylists.put("Mis me gusta", new ArbolBinarioBusqueda());
                    
                    while ((lineaCifrada = reader.readLine()) != null) {
                        String datosPlanos = GestorEncriptacion.descifrar(lineaCifrada);
                        String[] partes = datosPlanos.split("\\|\\|");
                        if (partes.length >= 5) {
                            Cancion c = new Cancion();
                            c.setNombre(partes[1]); 
                            c.setArtista(partes[2]); 
                            c.setAlbum(partes[3]);
                            if (partes.length == 6) { c.setGenero(partes[4]); c.setRuta(partes[5]); } 
                            else { c.setGenero("Desconocido"); c.setRuta(partes[4]); }
                            
                            String nombreLista = partes[0];
                            if (!mapaPlaylists.containsKey(nombreLista)) { 
                                nombresPlaylists.add(nombreLista); 
                                mapaPlaylists.put(nombreLista, new ArbolBinarioBusqueda());
                            }
                            mapaPlaylists.get(nombreLista).insertar(c);
                            if (nombreLista.equals("Mis me gusta")) cancionesFavoritas.add(c.getRuta());
                        }
                    }
                } catch (Exception e) { System.out.println("Error al cargar playlists: " + e.getMessage()); }
            }

            // 2. Cargar Historial
            File archivoHistorial = new File("historial_cifrado.txt");
            if (archivoHistorial.exists()) {
                try (BufferedReader readerHist = new BufferedReader(new FileReader(archivoHistorial))) {
                    String lineaCifrada;
                    listaObservableHistorial.clear();
                    List<Cancion> tempReversa = new ArrayList<>();
                    
                    while ((lineaCifrada = readerHist.readLine()) != null) {
                        String datosPlanos = GestorEncriptacion.descifrar(lineaCifrada);
                        String[] partes = datosPlanos.split("\\|\\|");
                        if (partes.length >= 6) {
                            Cancion c = new Cancion();
                            c.setNombre(partes[1]);
                            c.setArtista(partes[2]);
                            c.setAlbum(partes[3]);
                            c.setGenero(partes[4]);
                            c.setRuta(partes[5]);
                            listaObservableHistorial.add(c);
                            tempReversa.add(c);
                        }
                    }
                    // Reconstruir la pila original matemáticamente
                    historial = new Pila(); 
                    for (int i = tempReversa.size() - 1; i >= 0; i--) {
                        historial.push(tempReversa.get(i));
                    }
                } catch (Exception e) { System.out.println("Error al cargar historial: " + e.getMessage()); }
            }

            if (tituloCentral.getText().equals("Historial de Reproducción")) {
                tablaCanciones.setItems(listaObservableHistorial);
                lblContadorCanciones.setText(listaObservableHistorial.size() + " canciones escuchadas");
            }
            tablaCanciones.refresh();
        });

        // --- 2. PANEL CENTRAL ---
        VBox panelCentral = new VBox(10);
        panelCentral.setStyle("-fx-background-color: #121212; -fx-padding: 20px;");
        
        HBox barraBusqueda = new HBox(15);
        barraBusqueda.setAlignment(Pos.CENTER_LEFT);
        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por título o artista...");
        txtBuscar.setPrefWidth(250);
        txtBuscar.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-prompt-text-fill: gray;");
        Button btnLimpiar = new Button("✖ Limpiar");
        btnLimpiar.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-cursor: hand;");
        
        Label lblTiempoAVL = new Label("⏱ AVL: 0.00 ms");
        lblTiempoAVL.setStyle("-fx-text-fill: #1db954; -fx-font-weight: bold; -fx-font-size: 13px;");
        Label lblTiempoABB = new Label("⏱ ABB: 0.00 ms");
        lblTiempoABB.setStyle("-fx-text-fill: #ff4d4d; -fx-font-weight: bold; -fx-font-size: 13px;");
        
        Region espaciadorBusqueda = new Region();
        HBox.setHgrow(espaciadorBusqueda, Priority.ALWAYS);

        Button btnConfiguracion = new Button("⚙ Ajustes");
        btnConfiguracion.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 10 5 10;");
        btnConfiguracion.setOnMouseEntered(e -> btnConfiguracion.setStyle("-fx-background-color: #3e3e3e; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 10 5 10;"));
        btnConfiguracion.setOnMouseExited(e -> btnConfiguracion.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 5 10 5 10;"));
        
        btnConfiguracion.setOnAction(e -> abrirPanelConfiguraciones());

        barraBusqueda.getChildren().addAll(txtBuscar, btnLimpiar, lblTiempoAVL, lblTiempoABB, espaciadorBusqueda, btnConfiguracion);

        HBox encabezadoBiblioteca = new HBox(15);
        encabezadoBiblioteca.setAlignment(Pos.BASELINE_LEFT); 
        
        tituloCentral = new Label("Biblioteca Principal");
        tituloCentral.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        lblContadorCanciones = new Label("0 canciones");
        lblContadorCanciones.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        encabezadoBiblioteca.getChildren().addAll(tituloCentral, lblContadorCanciones);
        
        txtBuscar.textProperty().addListener((observable, valorViejo, valorNuevo) -> {
            if (listaOriginalCanciones != null && !listaOriginalCanciones.isEmpty()) {
                if (tituloCentral.getText().equals("Biblioteca Principal")) {
                    long inicioAVL = System.nanoTime();
                    List<Cancion> filtradasAVL = arbolBibliotecaCentral.buscarPorFiltro(valorNuevo);
                    long finAVL = System.nanoTime();
                    double msAVL = (finAVL - inicioAVL) / 1_000_000.0;
                    
                    long inicioABB = System.nanoTime();
                    arbolNormalCentral.buscarPorFiltro(valorNuevo);
                    long finABB = System.nanoTime();
                    double msABB = (finABB - inicioABB) / 1_000_000.0;
                    
                    if (!valorNuevo.trim().isEmpty()) {
                        tiemposBusquedaAVL.add(msAVL);
                        tiemposBusquedaABB.add(msABB);
                    }
                    
                    lblTiempoAVL.setText(String.format("⏱ AVL: %.4f ms", msAVL));
                    lblTiempoABB.setText(String.format("⏱ ABB: %.4f ms", msABB));
                    
                    tablaCanciones.setItems(FXCollections.observableArrayList(filtradasAVL));
                    lblContadorCanciones.setText(filtradasAVL.size() + " resultados");
                    
                } else if (!tituloCentral.getText().equals("Historial de Reproducción")) {
                    List<Cancion> filtradasLista = listaOriginalCanciones.stream()
                        .filter(c -> c.getNombre().toLowerCase().contains(valorNuevo.toLowerCase()) || 
                                     c.getArtista().toLowerCase().contains(valorNuevo.toLowerCase()))
                        .collect(Collectors.toList());
                    tablaCanciones.setItems(FXCollections.observableArrayList(filtradasLista));
                    lblContadorCanciones.setText(filtradasLista.size() + " resultados");
                }
            }
        });

        btnLimpiar.setOnAction(e -> {
            txtBuscar.clear();
            txtBuscar.requestFocus();
            lblTiempoAVL.setText("⏱ AVL: 0.00 ms");
            lblTiempoABB.setText("⏱ ABB: 0.00 ms");
            if (tituloCentral.getText().equals("Biblioteca Principal") && listaOriginalCanciones != null) {
                lblContadorCanciones.setText(listaOriginalCanciones.size() + " canciones");
            }
        });

        tablaCanciones = new TableView<>();
        tablaCanciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Cancion, String> colFavorita = new TableColumn<>("");
        colFavorita.setPrefWidth(45);
        colFavorita.setResizable(false);
        colFavorita.setCellFactory(column -> {
            return new TableCell<Cancion, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null); setGraphic(null);
                    } else {
                        Cancion cancion = getTableRow().getItem();
                        if (cancionesFavoritas.contains(cancion.getRuta())) {
                            setText("⭐"); setStyle("-fx-text-fill: #FFD700; -fx-alignment: CENTER; -fx-cursor: hand; -fx-font-size: 16px;");
                        } else {
                            setText("☆"); setStyle("-fx-text-fill: #535353; -fx-alignment: CENTER; -fx-cursor: hand; -fx-font-size: 16px;");
                        }
                    }
                }
                { setOnMouseClicked(evento -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        Cancion cancion = getTableRow().getItem();
                        ArbolBinarioBusqueda playlistFavoritos = mapaPlaylists.get("Mis me gusta");
                        if (cancionesFavoritas.contains(cancion.getRuta())) {
                            cancionesFavoritas.remove(cancion.getRuta()); 
                            playlistFavoritos.eliminar(cancion.getNombre());
                        } else {
                            cancionesFavoritas.add(cancion.getRuta()); 
                            playlistFavoritos.insertar(cancion);
                        }
                        tablaCanciones.refresh(); 
                    }
                }); }
            };
        });

        TableColumn<Cancion, String> colNombre = new TableColumn<>("Título");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        TableColumn<Cancion, String> colArtista = new TableColumn<>("Artista");
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        
        TableColumn<Cancion, String> colAlbum = new TableColumn<>("Álbum");
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));

        TableColumn<Cancion, String> colGenero = new TableColumn<>("Género");
        colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        
        tablaCanciones.getColumns().addAll(colFavorita, colNombre, colArtista, colAlbum, colGenero);
        VBox.setVgrow(tablaCanciones, Priority.ALWAYS);
        
        ContextMenu menuContextual = new ContextMenu();
        menuContextual.setStyle("-fx-base: #282828; -fx-control-inner-background: #282828; -fx-text-fill: white;");
        MenuItem itemAñadirCola = new MenuItem("➕ Agregar a la fila de reproducción");
        MenuItem itemAñadirPlaylist = new MenuItem("🎵 Agregar a playlist...");
        MenuItem itemFavorito = new MenuItem("⭐ Guardar en Mis me gusta"); 
        MenuItem itemEditar = new MenuItem("✏️ Editar información");
        SeparatorMenuItem separador = new SeparatorMenuItem();
        MenuItem itemEliminarPlaylist = new MenuItem("➖ Eliminar de esta playlist");
        menuContextual.getItems().addAll(itemAñadirCola, itemAñadirPlaylist, itemFavorito, itemEditar, separador, itemEliminarPlaylist);
        tablaCanciones.setContextMenu(menuContextual);

        itemAñadirCola.setOnAction(e -> { Cancion s = tablaCanciones.getSelectionModel().getSelectedItem(); if(s!=null) listaObservableCola.add(s); });
        
        itemAñadirPlaylist.setOnAction(evento -> {
            Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                if (nombresPlaylists.size() <= 1) return;
                ChoiceDialog<String> dialogo = new ChoiceDialog<>(nombresPlaylists.get(1), nombresPlaylists);
                dialogo.setTitle("Añadir a Playlist");
                dialogo.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                dialogo.showAndWait().ifPresent(nombrePlaylist -> {
                    ArbolBinarioBusqueda arbolLista = mapaPlaylists.get(nombrePlaylist);
                    if (arbolLista != null) arbolLista.insertar(seleccionada); 
                });
            }
        });

        itemEliminarPlaylist.setOnAction(evento -> {
            Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null && playlistSeleccionada != null) {
                ArbolBinarioBusqueda arbolLista = mapaPlaylists.get(playlistSeleccionada);
                if (arbolLista != null) {
                    arbolLista.eliminar(seleccionada.getNombre());
                    if (playlistSeleccionada.equals("Mis me gusta")) cancionesFavoritas.remove(seleccionada.getRuta());
                    vistaPlaylists.getSelectionModel().clearSelection();
                    vistaPlaylists.getSelectionModel().select(playlistSeleccionada);
                }
            }
        });

        itemEditar.setOnAction(evento -> {
            Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                Dialog<Cancion> dialog = new Dialog<>();
                dialog.setTitle("Modificar Canción");
                dialog.setHeaderText("Editando metadatos de:\n" + seleccionada.getNombre());
                dialog.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white; -fx-control-inner-background: #121212;");

                ButtonType btnGuardarData = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(btnGuardarData, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 20, 10, 10));

                TextField fieldNombre = new TextField(seleccionada.getNombre());
                TextField fieldArtista = new TextField(seleccionada.getArtista());
                TextField fieldAlbum = new TextField(seleccionada.getAlbum());
                TextField fieldGenero = new TextField(seleccionada.getGenero() != null ? seleccionada.getGenero() : "Desconocido");

                String tfStyle = "-fx-background-color: #3e3e3e; -fx-text-fill: white;";
                fieldNombre.setStyle(tfStyle);
                fieldArtista.setStyle(tfStyle);
                fieldAlbum.setStyle(tfStyle);
                fieldGenero.setStyle(tfStyle);

                Label lbl1 = new Label("Nombre:"); lbl1.setStyle("-fx-text-fill: white;");
                Label lbl2 = new Label("Artista:"); lbl2.setStyle("-fx-text-fill: white;");
                Label lbl3 = new Label("Álbum:"); lbl3.setStyle("-fx-text-fill: white;");
                Label lbl4 = new Label("Género:"); lbl4.setStyle("-fx-text-fill: white;");

                grid.add(lbl1, 0, 0); grid.add(fieldNombre, 1, 0);
                grid.add(lbl2, 0, 1); grid.add(fieldArtista, 1, 1);
                grid.add(lbl3, 0, 2); grid.add(fieldAlbum, 1, 2);
                grid.add(lbl4, 0, 3); grid.add(fieldGenero, 1, 3);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == btnGuardarData) {
                        String viejoNombre = seleccionada.getNombre();
                        String nuevoNombre = fieldNombre.getText().trim();
                        
                        seleccionada.setArtista(fieldArtista.getText().trim());
                        seleccionada.setAlbum(fieldAlbum.getText().trim());
                        seleccionada.setGenero(fieldGenero.getText().trim());

                        if (!viejoNombre.equals(nuevoNombre)) {
                            seleccionada.setNombre(nuevoNombre);
                            
                            if (listaOriginalCanciones != null) {
                                arbolBibliotecaCentral = new ArbolAVL();
                                arbolNormalCentral = new ArbolBinarioBusqueda();
                                for (Cancion c : listaOriginalCanciones) {
                                    arbolBibliotecaCentral.insertar(c);
                                    arbolNormalCentral.insertar(c);
                                }
                            }

                            for (Map.Entry<String, ArbolBinarioBusqueda> entry : mapaPlaylists.entrySet()) {
                                List<Cancion> cancionesPl = entry.getValue().obtenerListaInOrden();
                                ArbolBinarioBusqueda nuevoArbol = new ArbolBinarioBusqueda();
                                for(Cancion c : cancionesPl) {
                                    nuevoArbol.insertar(c); 
                                }
                                mapaPlaylists.put(entry.getKey(), nuevoArbol);
                            }
                        }
                        return seleccionada;
                    }
                    return null;
                });

                dialog.showAndWait().ifPresent(resultado -> {
                    if (tituloCentral.getText().equals("Biblioteca Principal") && arbolBibliotecaCentral != null) {
                        listaOriginalCanciones = arbolBibliotecaCentral.obtenerListaInOrden();
                        listaObservableCanciones = FXCollections.observableArrayList(listaOriginalCanciones);
                        tablaCanciones.setItems(listaObservableCanciones);
                    } else if (playlistSeleccionada != null) {
                        ArbolBinarioBusqueda arbolLista = mapaPlaylists.get(playlistSeleccionada);
                        if (arbolLista != null) {
                            tablaCanciones.setItems(FXCollections.observableArrayList(arbolLista.obtenerListaInOrden()));
                        }
                    }
                    tablaCanciones.refresh();
                });
            }
        });
        
        panelCentral.getChildren().addAll(barraBusqueda, encabezadoBiblioteca, tablaCanciones);

        // --- 3. PANEL DERECHO ---
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
        VBox.setVgrow(tablaCola, Priority.ALWAYS);
        HBox botonesCola = new HBox(10); botonesCola.setAlignment(Pos.CENTER);
        Button btnQuitarCola = new Button("Quitar"); Button btnSubirCola = new Button("🔼"); Button btnBajarCola = new Button("🔽");
        String estiloBotonesCola = "-fx-background-color: #282828; -fx-text-fill: white; -fx-cursor: hand;";
        btnQuitarCola.setStyle(estiloBotonesCola); btnSubirCola.setStyle(estiloBotonesCola); btnBajarCola.setStyle(estiloBotonesCola);
        botonesCola.getChildren().addAll(btnQuitarCola, btnSubirCola, btnBajarCola);
        panelDerecho.getChildren().addAll(tituloCola, tablaCola, botonesCola);

        btnQuitarCola.setOnAction(e -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice >= 0) {
                listaObservableCola.remove(indice);
            }
        });

        btnSubirCola.setOnAction(e -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice > 0) { 
                Cancion seleccionada = listaObservableCola.remove(indice);
                listaObservableCola.add(indice - 1, seleccionada);
                tablaCola.getSelectionModel().select(indice - 1); 
            }
        });

        btnBajarCola.setOnAction(e -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice >= 0 && indice < listaObservableCola.size() - 1) { 
                Cancion seleccionada = listaObservableCola.remove(indice);
                listaObservableCola.add(indice + 1, seleccionada);
                tablaCola.getSelectionModel().select(indice + 1); 
            }
        });

        // --- 4. PANEL INFERIOR CONTROLES ---
        VBox panelInferior = new VBox(10);
        panelInferior.setPrefHeight(100);
        panelInferior.setAlignment(Pos.CENTER);
        panelInferior.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0; -fx-padding: 10px 30px;");
        
        HBox contenedorProgreso = new HBox(15);
        contenedorProgreso.setAlignment(Pos.CENTER);
        lblTiempoActual = new Label("0:00"); lblTiempoActual.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        sliderProgreso = new Slider(); sliderProgreso.setPrefWidth(350); sliderProgreso.setMinWidth(250); 
        sliderProgreso.setStyle("-fx-base: #181818; -fx-accent: #30D5C8; -fx-cursor: hand;");
        lblTiempoTotal = new Label("0:00"); lblTiempoTotal.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        contenedorProgreso.getChildren().addAll(lblTiempoActual, sliderProgreso, lblTiempoTotal);

        HBox barraBotones = new HBox(20); 
        barraBotones.setAlignment(Pos.CENTER);
        
        Button btnAleatorio = new Button("🔀"); Button btnRepetir = new Button("🔁");
        Button btnAnterior = new Button("⏮"); Button btnPlayPausa = new Button("▶"); 
        Button btnSiguiente = new Button("⏭");
        Button btnVerFila = new Button(""); 
        
        String estiloBotones = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;";
        btnAleatorio.setStyle(estiloBotones); btnRepetir.setStyle(estiloBotones);
        btnAnterior.setStyle(estiloBotones); btnSiguiente.setStyle(estiloBotones);
        btnVerFila.setStyle(estiloBotones + "-fx-font-size: 16px;"); 
        btnPlayPausa.setStyle(estiloBotones + "-fx-font-size: 24px; -fx-min-width: 60px;"); 
        
        Label lblVolumen = new Label("🔊");
        lblVolumen.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 16px;");
        
        sliderVolumen = new Slider(0.0, 1.0, 0.5); 
        sliderVolumen.setPrefWidth(80);
        sliderVolumen.setStyle("-fx-base: #181818; -fx-accent: #1db954; -fx-cursor: hand;");
        
        HBox contenedorVolumen = new HBox(8);
        contenedorVolumen.setAlignment(Pos.CENTER);
        contenedorVolumen.setStyle("-fx-padding: 0 0 0 20px;"); 
        contenedorVolumen.getChildren().addAll(lblVolumen, sliderVolumen);
        
        barraBotones.getChildren().addAll(btnAleatorio, btnRepetir, btnAnterior, btnPlayPausa, btnSiguiente, btnVerFila, contenedorVolumen);
        panelInferior.getChildren().addAll(contenedorProgreso, barraBotones);

        // --- LÓGICA GENERAL Y EVENTOS ---
        vistaPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, viejoValor, nuevoValor) -> {
            if (nuevoValor != null) {
                playlistSeleccionada = nuevoValor;
                tituloCentral.setText(nuevoValor);
                
                ArbolBinarioBusqueda arbolLista = mapaPlaylists.get(nuevoValor);
                if (arbolLista != null) {
                    List<Cancion> cancionesPlaylist = arbolLista.obtenerListaInOrden();
                    tablaCanciones.setItems(FXCollections.observableArrayList(cancionesPlaylist));
                    lblContadorCanciones.setText(cancionesPlaylist.size() + " canciones");
                }
            }
        });

        sliderVolumen.valueProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (reproductor != null) { reproductor.setVolumen(nuevoValor.doubleValue()); }
        });

        sliderProgreso.setOnMousePressed(e -> arrastrandoSlider = true);
        sliderProgreso.setOnMouseReleased(e -> { if(cancionActual!=null) reproductor.saltarA(sliderProgreso.getValue()/100.0); btnPlayPausa.setText("⏸"); estaReproduciendo=true; arrastrandoSlider=false; });

        Timeline temporizador = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            if (reproductor.isReproduciendo()) {
                double p = reproductor.getProgreso(); 
                int t = reproductor.getDuracionEstimadaSegundos();
                if (!arrastrandoSlider) sliderProgreso.setValue(p * 100); 
                lblTiempoActual.setText(formatearTiempo((int) (t * p)));
                lblTiempoTotal.setText(formatearTiempo(t));
                
                if (p >= 0.99 && estaReproduciendo) {
                    if (modoRepeticion && tituloCentral.getText().equals("Biblioteca Principal") && listaObservableCola.isEmpty()) {
                        reproducirActual(); 
                    } else {
                        btnSiguiente.fire(); 
                    }
                }
            }
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE); temporizador.play();

        textoMenu.setOnMouseClicked(e -> { 
            vistaPlaylists.getSelectionModel().clearSelection(); 
            playlistSeleccionada = null; 
            tituloCentral.setText("Biblioteca Principal"); 
            if (listaOriginalCanciones != null) { 
                tablaCanciones.setItems(FXCollections.observableArrayList(listaOriginalCanciones)); 
                tablaCanciones.refresh(); 
                lblContadorCanciones.setText(listaOriginalCanciones.size() + " canciones");
            } 
        });

        btnNuevaPlaylist.setOnAction(e -> { 
            TextInputDialog d = new TextInputDialog(); 
            d.setTitle("Nueva Playlist"); 
            d.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;"); 
            d.showAndWait().ifPresent(n -> { 
                if (!n.trim().isEmpty() && !nombresPlaylists.contains(n)) { 
                    nombresPlaylists.add(n); 
                    mapaPlaylists.put(n, new ArbolBinarioBusqueda());
                } 
            }); 
        });

        btnAleatorio.setOnAction(e -> { modoAleatorio = !modoAleatorio; btnAleatorio.setStyle(estiloBotones + (modoAleatorio ? "-fx-text-fill: #1db954;" : "")); });
        btnRepetir.setOnAction(e -> { modoRepeticion = !modoRepeticion; btnRepetir.setStyle(estiloBotones + (modoRepeticion ? "-fx-text-fill: #1db954;" : "")); });

        tablaCanciones.setOnMouseClicked(e -> { 
            if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) { 
                Cancion s = tablaCanciones.getSelectionModel().getSelectedItem(); 
                if (s != null) { 
                    if (cancionActual != null && !cancionActual.equals(s)) {
                        agregarAlHistorial(cancionActual);
                    }
                    cancionActual = s; 
                    reproducirActual(); 
                    btnPlayPausa.setText("⏸"); 
                    estaReproduciendo=true; 
                } 
            } 
        });
        
        btnCargarMusica.setOnAction(evento -> {
            DirectoryChooser selectorDirectorio = new DirectoryChooser();
            selectorDirectorio.setTitle("Selecciona la carpeta con tu música");
            File carpetaSeleccionada = selectorDirectorio.showDialog(escenarioPrincipal);
            
            if (carpetaSeleccionada != null) {
                LectorArchivos lector = new LectorArchivos();
                lector.leerCarpetaRecursivamente(carpetaSeleccionada.getAbsolutePath());
                List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
                
                arbolBibliotecaCentral = new ArbolAVL();
                arbolNormalCentral = new ArbolBinarioBusqueda();
                
                long inicioAVL = System.nanoTime();
                for (Cancion c : cancionesLeidas) {
                    arbolBibliotecaCentral.insertar(c); 
                }
                long finAVL = System.nanoTime();
                double msAVL = (finAVL - inicioAVL) / 1_000_000.0;
                
                long inicioABB = System.nanoTime();
                for (Cancion c : cancionesLeidas) {
                    arbolNormalCentral.insertar(c);
                }
                long finABB = System.nanoTime();
                double msABB = (finABB - inicioABB) / 1_000_000.0;
                
                listaOriginalCanciones = arbolBibliotecaCentral.obtenerListaInOrden();
                listaObservableCanciones = FXCollections.observableArrayList(listaOriginalCanciones);
                tablaCanciones.setItems(listaObservableCanciones);
                
                lblContadorCanciones.setText(listaOriginalCanciones.size() + " canciones");
                
                modoRepeticion = false; modoAleatorio = false;
                btnRepetir.setStyle(estiloBotones); btnAleatorio.setStyle(estiloBotones);
                vistaPlaylists.getSelectionModel().clearSelection(); playlistSeleccionada = null;
                tituloCentral.setText("Biblioteca Principal"); tablaCanciones.refresh();
            }
        });

        btnPlayPausa.setOnAction(e -> {
            if (estaReproduciendo) { 
                reproductor.pausar(); btnPlayPausa.setText("▶"); estaReproduciendo = false;
            } else { 
                if (cancionActual != null && reproductor.getProgreso() > 0) { 
                    reproductor.continuar(); btnPlayPausa.setText("⏸"); estaReproduciendo = true;
                } else { 
                    if (!listaObservableCola.isEmpty()) { 
                        if (cancionActual != null) agregarAlHistorial(cancionActual);
                        cancionActual = listaObservableCola.remove(0); 
                        reproducirActual(); btnPlayPausa.setText("⏸"); estaReproduciendo=true;
                    } else { 
                        Cancion s = tablaCanciones.getSelectionModel().getSelectedItem(); 
                        if (s != null) { 
                            if (cancionActual != null) agregarAlHistorial(cancionActual);
                            cancionActual = s; reproducirActual(); btnPlayPausa.setText("⏸"); estaReproduciendo=true; 
                        } 
                    } 
                } 
            }
        });

        btnSiguiente.setOnAction(e -> {
            if (cancionActual != null) agregarAlHistorial(cancionActual);

            if (modoAleatorio && !tablaCanciones.getItems().isEmpty()) { 
                int randomIndex = (int) (Math.random() * tablaCanciones.getItems().size());
                tablaCanciones.getSelectionModel().select(randomIndex); 
                cancionActual = tablaCanciones.getItems().get(randomIndex);
                reproducirActual(); 
                btnPlayPausa.setText("⏸");
                estaReproduciendo = true;
            } else if (!listaObservableCola.isEmpty()) { 
                cancionActual = listaObservableCola.remove(0); 
                reproducirActual(); 
                btnPlayPausa.setText("⏸");
                estaReproduciendo = true;
            } else { 
                int i = tablaCanciones.getSelectionModel().getSelectedIndex(); 
                if (i >= 0 && i < tablaCanciones.getItems().size() - 1) { 
                    tablaCanciones.getSelectionModel().select(i + 1); 
                    cancionActual = tablaCanciones.getSelectionModel().getSelectedItem(); 
                    reproducirActual(); 
                    btnPlayPausa.setText("⏸"); 
                    estaReproduciendo = true;
                } else if (modoRepeticion && !tituloCentral.getText().equals("Biblioteca Principal") && !tablaCanciones.getItems().isEmpty()) {
                    tablaCanciones.getSelectionModel().select(0);
                    cancionActual = tablaCanciones.getSelectionModel().getSelectedItem(); 
                    reproducirActual(); 
                    btnPlayPausa.setText("⏸"); 
                    estaReproduciendo = true;
                } else {
                    estaReproduciendo = false;
                    btnPlayPausa.setText("▶");
                }
            }
        });

        btnAnterior.setOnAction(e -> { 
            Cancion ant = sacarDelHistorial(); 
            if (ant != null) { 
                cancionActual = ant; 
                tablaCanciones.getSelectionModel().select(cancionActual); 
                reproducirActual(); 
                btnPlayPausa.setText("⏸"); 
                estaReproduciendo = true;
            } else if (cancionActual != null) {
                reproductor.saltarA(0.0); 
            } 
        });

        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setRight(null); 
        layoutPrincipal.setBottom(panelInferior); 

        btnVerFila.setOnAction(evento -> {
            if (layoutPrincipal.getRight() == null) {
                layoutPrincipal.setRight(panelDerecho);
                btnVerFila.setStyle(estiloBotones + "-fx-text-fill: #30D5C8; -fx-font-size: 16px;"); 
            } else {
                layoutPrincipal.setRight(null);
                btnVerFila.setStyle(estiloBotones + "-fx-text-fill: white; -fx-font-size: 16px;"); 
            }
        });

        Scene escena = new Scene(layoutPrincipal, 1150, 750); 
        escena.getRoot().setStyle("-fx-base: #121212; -fx-control-inner-background: #121212; -fx-table-cell-border-color: transparent; -fx-table-header-background-color: #282828;");
        escena.getStylesheets().add("data:text/css,.list-cell { -fx-text-fill: #b3b3b3; -fx-font-weight: bold; } .list-cell:selected { -fx-text-fill: white; -fx-background-color: #282828; } .context-menu { -fx-background-color: #282828; } .menu-item:focused { -fx-background-color: #3e3e3e; }");

        escenarioPrincipal.setOnCloseRequest(e -> { reproductor.detener(); System.exit(0); });
        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        
        try {
            File archivoLogo = new File("default"); 
            if (!archivoLogo.exists()) archivoLogo = new File("default.png"); 
            if (archivoLogo.exists()) {
                escenarioPrincipal.getIcons().add(new Image(archivoLogo.toURI().toString()));
            }
        } catch (Exception ex) {}

        escenarioPrincipal.show();
    }

    // --- MÉTODOS DE SINCRONIZACIÓN DE HISTORIAL ---
    private void agregarAlHistorial(Cancion c) {
        historial.push(c);
        listaObservableHistorial.add(0, c); // Lo añadimos al inicio visualmente
    }

    private Cancion sacarDelHistorial() {
        Cancion c = historial.pop();
        if (c != null && !listaObservableHistorial.isEmpty()) {
            listaObservableHistorial.remove(0); // Lo removemos visualmente
        }
        return c;
    }

    private void abrirPanelConfiguraciones() {
        Stage ventanaConfig = new Stage();
        ventanaConfig.setTitle("⚙ Ajustes y Estadísticas");

        VBox layout = new VBox(15);
        layout.setStyle("-fx-background-color: #121212; -fx-padding: 20px;");
        layout.setAlignment(Pos.CENTER);

        Label lblTitulo = new Label("Módulo de Estadísticas y Rendimiento");
        lblTitulo.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        TextArea txtReporte = new TextArea();
        txtReporte.setEditable(false);
        txtReporte.setPrefSize(500, 350);
        txtReporte.setStyle("-fx-control-inner-background: #282828; -fx-text-fill: white; -fx-font-family: 'Consolas'; -fx-font-size: 13px;");
        txtReporte.setText(generarReporteEstadisticas());

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);

        Button btnActualizar = new Button("🔄 Actualizar");
        Button btnGuardarStats = new Button("🔒 Guardar (Cifrado)");
        Button btnCargarStats = new Button("🔓 Cargar (Descifrado)");

        String estiloBtn = "-fx-background-color: #1db954; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-background-radius: 5; -fx-padding: 8px 15px;";
        btnActualizar.setStyle(estiloBtn);
        btnGuardarStats.setStyle(estiloBtn);
        btnCargarStats.setStyle(estiloBtn);

        btnActualizar.setOnAction(e -> txtReporte.setText(generarReporteEstadisticas()));

        btnGuardarStats.setOnAction(e -> {
            if(txtReporte.getText().trim().isEmpty() || txtReporte.getText().contains("No hay canciones")) {
                 Alert a = new Alert(Alert.AlertType.WARNING, "No hay datos válidos para guardar.");
                 a.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                 a.show();
                 return;
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter("estadisticas_cifradas.txt"))) {
                String[] lineas = txtReporte.getText().split("\n");
                for (String linea : lineas) {
                    writer.println(GestorEncriptacion.cifrar(linea));
                }
                Alert a = new Alert(Alert.AlertType.INFORMATION, "El reporte estadístico ha sido encriptado y guardado exitosamente.");
                a.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                a.show();
            } catch (Exception ex) {}
        });

        btnCargarStats.setOnAction(e -> {
            File file = new File("estadisticas_cifradas.txt");
            if (!file.exists()) {
                Alert a = new Alert(Alert.AlertType.WARNING, "No se encontró archivo de estadísticas.");
                a.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                a.show();
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String lineaCifrada;
                while ((lineaCifrada = reader.readLine()) != null) {
                    sb.append(GestorEncriptacion.descifrar(lineaCifrada)).append("\n");
                }
                txtReporte.setText(sb.toString());
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Reporte desencriptado en pantalla.");
                a.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                a.show();
            } catch (Exception ex) {}
        });

        botones.getChildren().addAll(btnActualizar, btnGuardarStats, btnCargarStats);
        layout.getChildren().addAll(lblTitulo, txtReporte, botones);

        Scene escena = new Scene(layout, 550, 480);
        ventanaConfig.setScene(escena);
        ventanaConfig.show();
    }

    private String generarReporteEstadisticas() {
        if (listaOriginalCanciones == null || listaOriginalCanciones.isEmpty()) {
            return "No hay canciones cargadas en la Biblioteca para analizar.";
        }

        String topCancion = contadorCanciones.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Aún sin datos");
        String topArtista = contadorArtistas.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Aún sin datos");
        String topGenero = contadorGeneros.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("Aún sin datos");
        
        String topPlaylist = "Ninguna";
        int maxPlaylistSize = 0;
        for (Map.Entry<String, ArbolBinarioBusqueda> entry : mapaPlaylists.entrySet()) {
            int size = entry.getValue().obtenerListaInOrden().size();
            if (size > maxPlaylistSize) {
                maxPlaylistSize = size;
                topPlaylist = entry.getKey() + " (" + size + " pistas)";
            }
        }

        long tamanoTotalBytes = 0;
        Map<String, List<Cancion>> cancionesPorNombre = new HashMap<>();
        Set<String> rutasOrigen = new HashSet<>();
        
        for (Cancion c : listaOriginalCanciones) {
            File f = new File(c.getRuta());
            if (f.exists()) tamanoTotalBytes += f.length();
            if (f.getParent() != null) rutasOrigen.add(f.getParent());
            else rutasOrigen.add(c.getRuta());
            
            cancionesPorNombre.computeIfAbsent(c.getNombre().toLowerCase(), k -> new ArrayList<>()).add(c);
        }
        
        StringBuilder rutasStr = new StringBuilder();
        for (String r : rutasOrigen) rutasStr.append("   - ").append(r).append("\n");
        
        double tamanoMB = tamanoTotalBytes / (1024.0 * 1024.0);
        double minutosTotalesAprox = tamanoMB; 
        double promedioDuracionMinutos = minutosTotalesAprox / listaOriginalCanciones.size();
        int minPromedio = (int) promedioDuracionMinutos;
        int segPromedio = (int) ((promedioDuracionMinutos - minPromedio) * 60);
        
        int duplicadosCount = 0;
        StringBuilder duplicadosNombres = new StringBuilder();
        for (Map.Entry<String, List<Cancion>> entry : cancionesPorNombre.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicadosCount += (entry.getValue().size() - 1);
                double pesoDuplicadoMB = 0;
                for(int i = 1; i < entry.getValue().size(); i++) {
                     File f = new File(entry.getValue().get(i).getRuta());
                     if (f.exists()) pesoDuplicadoMB += f.length() / (1024.0 * 1024.0);
                }
                duplicadosNombres.append("   - ").append(entry.getValue().get(0).getNombre())
                                 .append(" (").append(entry.getValue().size()).append(" copias, desperdicia ")
                                 .append(String.format("%.2f MB", pesoDuplicadoMB)).append(")\n");
            }
        }

        double avgAVL = tiemposBusquedaAVL.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double avgABB = tiemposBusquedaABB.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        String reporte = "📊 ESTADÍSTICAS GLOBALES DEL SISTEMA\n" +
                "──────────────────────────────────────────\n" +
                "🎵 Canción más reproducida: " + topCancion + "\n" +
                "🎤 Artista más escuchado: " + topArtista + "\n" +
                "🎸 Género más frecuente: " + topGenero + "\n" +
                "🗂️ Playlist más grande: " + topPlaylist + "\n" +
                "⏳ Promedio de duración x pista: " + String.format("%d:%02d min", minPromedio, segPromedio) + "\n\n" +
                "⚙️ RENDIMIENTO DE ESTRUCTURAS O(log n)\n" +
                "──────────────────────────────────────────\n" +
                "⏱️ Tiempo Promedio Búsqueda Árbol AVL: " + String.format("%.4f ms", avgAVL) + "\n" +
                "⏱️ Tiempo Promedio Búsqueda Árbol Binario: " + String.format("%.4f ms", avgABB) + "\n\n" +
                "💾 ANÁLISIS DE ALMACENAMIENTO\n" +
                "──────────────────────────────────────────\n" +
                "📦 Tamaño total de la biblioteca: " + String.format("%.2f MB", tamanoMB) + "\n" +
                "⚠️ Total de archivos duplicados: " + duplicadosCount + "\n\n" +
                "📂 RUTAS DE ORIGEN DE LOS ARCHIVOS\n" + 
                "──────────────────────────────────────────\n" +
                rutasStr.toString() + "\n";
        
        if (duplicadosCount > 0) {
            reporte += "Lista de repeticiones encontradas en memoria:\n" + duplicadosNombres.toString();
        }

        return reporte;
    }

    private void actualizarCaratulaYMetadatos() {
        if (cancionActual != null) {
            lblInfoCancionActual.setText(cancionActual.getNombre() + "\n" + cancionActual.getArtista());
            byte[] bytesImagen = cancionActual.getImagenCaratula();
            if (bytesImagen != null && bytesImagen.length > 0) {
                try {
                    ByteArrayInputStream bais = new ByteArrayInputStream(bytesImagen);
                    Image img = new Image(bais);
                    vistaCaratula.setImage(img);
                } catch (Exception e) { cargarImagenPorDefecto(); }
            } else { cargarImagenPorDefecto(); }
        }
    }

    private void cargarImagenPorDefecto() {
        try {
            File archivoLogo = new File("default"); 
            if (!archivoLogo.exists()) archivoLogo = new File("default.png"); 
            if (archivoLogo.exists()) {
                Image imgDefault = new Image(archivoLogo.toURI().toString());
                vistaCaratula.setImage(imgDefault);
            } else { vistaCaratula.setImage(null); }
        } catch (Exception ex) { vistaCaratula.setImage(null); }
    }

    private void reproducirActual() { 
        if (cancionActual != null) { 
            contadorCanciones.put(cancionActual.getNombre(), contadorCanciones.getOrDefault(cancionActual.getNombre(), 0) + 1);
            contadorArtistas.put(cancionActual.getArtista(), contadorArtistas.getOrDefault(cancionActual.getArtista(), 0) + 1);
            String gen = cancionActual.getGenero() != null ? cancionActual.getGenero() : "Desconocido";
            contadorGeneros.put(gen, contadorGeneros.getOrDefault(gen, 0) + 1);

            reproductor.detener(); 
            reproductor.reproducir(cancionActual.getRuta()); 
            reproductor.setVolumen(sliderVolumen.getValue());
            actualizarCaratulaYMetadatos(); 
        } 
    }
    
    private String formatearTiempo(int seg) { return String.format("%d:%02d", seg / 60, seg % 60); }
    public static void main(String[] args) { launch(args); }
}

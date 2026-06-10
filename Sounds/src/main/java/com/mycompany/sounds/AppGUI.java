package com.mycompany.sounds;

import java.io.BufferedReader;
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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    
    // NUEVO: Variable para el control de volumen
    private Slider sliderVolumen;

    private boolean modoRepeticion = false;
    private ListaCircular listaRepeticion = new ListaCircular(); 
    private Pila historial = new Pila(); 

    private ObservableList<String> nombresPlaylists = FXCollections.observableArrayList();
    private ListView<String> vistaPlaylists;
    private Map<String, ListaSimple> mapaPlaylists = new HashMap<>();
    private String playlistSeleccionada = null;
    private Label tituloCentral;
    private Set<String> cancionesFavoritas = new HashSet<>();
    private boolean modoAleatorio = false;

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane layoutPrincipal = new BorderPane();

        nombresPlaylists.add("Mis me gusta");
        mapaPlaylists.put("Mis me gusta", new ListaSimple());

        // --- 1. PANEL IZQUIERDO ---
        VBox menuIzquierdo = new VBox(15);
        menuIzquierdo.setPrefWidth(220);
        menuIzquierdo.setStyle("-fx-background-color: #000000; -fx-padding: 20px;");
        
        Label textoMenu = new Label("MI BIBLIOTECA");
        textoMenu.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        
        Button btnCargarMusica = new Button("Cargar Carpeta MP3");
        btnCargarMusica.setStyle("-fx-background-color: #1db954; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-cursor: hand;");
        btnCargarMusica.setPrefWidth(180);
        
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
        
        menuIzquierdo.getChildren().addAll(textoMenu, btnCargarMusica, textoPlaylists, barraPersistencia, btnNuevaPlaylist, vistaPlaylists);

        // --- LÓGICA DE GUARDAR Y CARGAR ---
        btnGuardar.setOnAction(evento -> {
            try (PrintWriter writer = new PrintWriter(new FileWriter("mis_playlists.txt"))) {
                for (Map.Entry<String, ListaSimple> entry : mapaPlaylists.entrySet()) {
                    String nombreLista = entry.getKey();
                    NodoLista actual = entry.getValue().getCabeza();
                    while (actual != null) {
                        Cancion c = actual.getCancion();
                        String datosPlanos = nombreLista + "||" + c.getNombre() + "||" + c.getArtista() + "||" + c.getAlbum() + "||" + c.getRuta();
                        String lineaCifrada = GestorEncriptacion.cifrar(datosPlanos);
                        writer.println(lineaCifrada);
                        actual = actual.getSiguiente();
                    }
                }
            } catch (Exception e) { System.out.println("Error al guardar: " + e.getMessage()); }
        });

        btnCargar.setOnAction(evento -> {
            File archivo = new File("mis_playlists.txt");
            if (!archivo.exists()) return;
            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                String lineaCifrada;
                mapaPlaylists.clear(); nombresPlaylists.clear(); cancionesFavoritas.clear();
                nombresPlaylists.add("Mis me gusta"); mapaPlaylists.put("Mis me gusta", new ListaSimple());
                while ((lineaCifrada = reader.readLine()) != null) {
                    String datosPlanos = GestorEncriptacion.descifrar(lineaCifrada);
                    String[] partes = datosPlanos.split("\\|\\|");
                    if (partes.length == 5) {
                        String nombreLista = partes[0];
                        Cancion c = new Cancion();
                        c.setNombre(partes[1]); c.setArtista(partes[2]); c.setAlbum(partes[3]); c.setRuta(partes[4]);
                        if (!mapaPlaylists.containsKey(nombreLista)) { nombresPlaylists.add(nombreLista); mapaPlaylists.put(nombreLista, new ListaSimple()); }
                        mapaPlaylists.get(nombreLista).insertar(c);
                        if (nombreLista.equals("Mis me gusta")) cancionesFavoritas.add(c.getRuta());
                    }
                }
                tablaCanciones.refresh();
            } catch (Exception e) { System.out.println("Error al cargar: " + e.getMessage()); }
        });

        // --- 2. PANEL CENTRAL ---
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

        HBox encabezadoBiblioteca = new HBox(15);
        encabezadoBiblioteca.setAlignment(Pos.CENTER_LEFT);
        tituloCentral = new Label("Biblioteca Principal");
        tituloCentral.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        encabezadoBiblioteca.getChildren().addAll(tituloCentral);
        
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
                        ListaSimple playlistFavoritos = mapaPlaylists.get("Mis me gusta");
                        if (cancionesFavoritas.contains(cancion.getRuta())) {
                            cancionesFavoritas.remove(cancion.getRuta()); playlistFavoritos.eliminar(cancion.getNombre());
                        } else {
                            cancionesFavoritas.add(cancion.getRuta()); playlistFavoritos.insertar(cancion);
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
        
        tablaCanciones.getColumns().addAll(colFavorita, colNombre, colArtista, colAlbum);
        VBox.setVgrow(tablaCanciones, Priority.ALWAYS);
        
        ContextMenu menuContextual = new ContextMenu();
        menuContextual.setStyle("-fx-base: #282828; -fx-control-inner-background: #282828; -fx-text-fill: white;");
        MenuItem itemAñadirCola = new MenuItem("➕ Agregar a la fila de reproducción");
        MenuItem itemAñadirPlaylist = new MenuItem("🎵 Agregar a playlist...");
        MenuItem itemFavorito = new MenuItem("⭐ Guardar en Mis me gusta"); 
        SeparatorMenuItem separador = new SeparatorMenuItem();
        MenuItem itemEliminarPlaylist = new MenuItem("➖ Eliminar de esta playlist");
        menuContextual.getItems().addAll(itemAñadirCola, itemAñadirPlaylist, itemFavorito, separador, itemEliminarPlaylist);
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
                    ListaSimple lista = mapaPlaylists.get(nombrePlaylist);
                    if (lista != null) lista.insertar(seleccionada); 
                });
            }
        });

        itemEliminarPlaylist.setOnAction(evento -> {
            Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null && playlistSeleccionada != null) {
                ListaSimple lista = mapaPlaylists.get(playlistSeleccionada);
                if (lista != null && lista.eliminar(seleccionada.getNombre())) {
                    if (playlistSeleccionada.equals("Mis me gusta")) cancionesFavoritas.remove(seleccionada.getRuta());
                    vistaPlaylists.getSelectionModel().clearSelection();
                    vistaPlaylists.getSelectionModel().select(playlistSeleccionada);
                }
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
        
        // --- NUEVO: CONTENEDOR DEL SLIDER DE VOLUMEN ---
        Label lblVolumen = new Label("🔊");
        lblVolumen.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 16px;");
        
        // Inicializa el slider de 0.0 a 1.0, y arranca a la mitad (0.5)
        sliderVolumen = new Slider(0.0, 1.0, 0.5); 
        sliderVolumen.setPrefWidth(80);
        sliderVolumen.setStyle("-fx-base: #181818; -fx-accent: #1db954; -fx-cursor: hand;");
        
        HBox contenedorVolumen = new HBox(8);
        contenedorVolumen.setAlignment(Pos.CENTER);
        contenedorVolumen.setStyle("-fx-padding: 0 0 0 20px;"); // Un poco de espacio a la izquierda
        contenedorVolumen.getChildren().addAll(lblVolumen, sliderVolumen);
        
        barraBotones.getChildren().addAll(btnAleatorio, btnRepetir, btnAnterior, btnPlayPausa, btnSiguiente, btnVerFila, contenedorVolumen);
        panelInferior.getChildren().addAll(contenedorProgreso, barraBotones);

        // --- LÓGICA GENERAL ---
        
        // NUEVO: Lógica del cambio de volumen
        sliderVolumen.valueProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (reproductor != null) {
                // Mandamos el valor al reproductor (entre 0.0 y 1.0)
                reproductor.setVolumen(nuevoValor.doubleValue());
            }
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
                if (p >= 0.99 && estaReproduciendo) btnSiguiente.fire(); 
            }
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE); temporizador.play();

        textoMenu.setOnMouseClicked(e -> { vistaPlaylists.getSelectionModel().clearSelection(); playlistSeleccionada = null; tituloCentral.setText("Biblioteca Principal"); if (listaOriginalCanciones != null) { tablaCanciones.setItems(FXCollections.observableArrayList(listaOriginalCanciones)); tablaCanciones.refresh(); } });

        btnNuevaPlaylist.setOnAction(e -> { TextInputDialog d = new TextInputDialog(); d.setTitle("Nueva Playlist"); d.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;"); d.showAndWait().ifPresent(n -> { if (!n.trim().isEmpty() && !nombresPlaylists.contains(n)) { nombresPlaylists.add(n); mapaPlaylists.put(n, new ListaSimple()); } }); });

        btnAleatorio.setOnAction(e -> { modoAleatorio = !modoAleatorio; btnAleatorio.setStyle(estiloBotones + (modoAleatorio ? "-fx-text-fill: #1db954;" : "")); });
        btnRepetir.setOnAction(e -> { modoRepeticion = !modoRepeticion; btnRepetir.setStyle(estiloBotones + (modoRepeticion ? "-fx-text-fill: #1db954;" : "")); });

        tablaCanciones.setOnMouseClicked(e -> { if (e.getButton().equals(MouseButton.PRIMARY) && e.getClickCount() == 2) { Cancion s = tablaCanciones.getSelectionModel().getSelectedItem(); if (s != null) { if (cancionActual != null && !cancionActual.equals(s)) historial.push(cancionActual); cancionActual = s; reproducirActual(); btnPlayPausa.setText("⏸"); estaReproduciendo=true; } } });
        
        btnCargarMusica.setOnAction(evento -> {
            DirectoryChooser selectorDirectorio = new DirectoryChooser();
            selectorDirectorio.setTitle("Selecciona la carpeta con tu música");
            File carpetaSeleccionada = selectorDirectorio.showDialog(escenarioPrincipal);
            
            if (carpetaSeleccionada != null) {
                LectorArchivos lector = new LectorArchivos();
                lector.leerCarpetaRecursivamente(carpetaSeleccionada.getAbsolutePath());
                List<Cancion> cancionesLeidas = lector.getCancionesCargadas();
                
                long inicioAVL = System.nanoTime();
                ArbolAVL arbolBiblioteca = new ArbolAVL();
                for (Cancion c : cancionesLeidas) {
                    arbolBiblioteca.insertar(c); 
                }
                long finAVL = System.nanoTime();
                double tiempoAVL = (finAVL - inicioAVL) / 1_000_000.0;
                
                long inicioBinario = System.nanoTime();
                ArbolBinarioBusqueda arbolNormal = new ArbolBinarioBusqueda();
                for (Cancion c : cancionesLeidas) {
                    arbolNormal.insertar(c); 
                }
                long finBinario = System.nanoTime();
                double tiempoBinario = (finBinario - inicioBinario) / 1_000_000.0;

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Análisis de Rendimiento");
                alerta.setHeaderText("Comparación de Carga de Árboles");
                alerta.setContentText(String.format(
                    "Canciones cargadas: %d\n\n" +
                    "⏱️ Tiempo Árbol AVL (Balanceado): %.4f ms\n" +
                    "⏱️ Tiempo Árbol Binario de Búsqueda: %.4f ms\n\n" +
                    "Nota: El AVL puede tardar fracciones de milisegundo más en cargar por el cálculo de factores de equilibrio y rotaciones, pero garantiza búsquedas O(log n).", 
                    cancionesLeidas.size(), tiempoAVL, tiempoBinario));
                alerta.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");
                alerta.showAndWait();
                
                listaOriginalCanciones = arbolBiblioteca.obtenerListaInOrden();
                listaObservableCanciones = FXCollections.observableArrayList(listaOriginalCanciones);
                tablaCanciones.setItems(listaObservableCanciones);
                
                modoRepeticion = false; modoAleatorio = false;
                btnRepetir.setStyle(estiloBotones); btnAleatorio.setStyle(estiloBotones);
                vistaPlaylists.getSelectionModel().clearSelection(); playlistSeleccionada = null;
                tituloCentral.setText("Biblioteca Principal"); tablaCanciones.refresh();
            }
        });

        btnPlayPausa.setOnAction(e -> {
            if (estaReproduciendo) { reproductor.pausar(); btnPlayPausa.setText("▶"); estaReproduciendo = false;
            } else { if (cancionActual != null && reproductor.getProgreso() > 0) { reproductor.continuar(); btnPlayPausa.setText("⏸"); estaReproduciendo = true;
                } else { if (!listaObservableCola.isEmpty()) { if (cancionActual != null) historial.push(cancionActual); cancionActual = listaObservableCola.remove(0); reproducirActual(); btnPlayPausa.setText("⏸"); estaReproduciendo=true;
                    } else { Cancion s = tablaCanciones.getSelectionModel().getSelectedItem(); if (s != null) { if (cancionActual != null) historial.push(cancionActual); cancionActual = s; reproducirActual(); btnPlayPausa.setText("⏸"); estaReproduciendo=true; } } } }
        });

        btnSiguiente.setOnAction(e -> {
            if (modoAleatorio && !tablaCanciones.getItems().isEmpty()) { if (cancionActual != null) historial.push(cancionActual); cancionActual = tablaCanciones.getItems().get((int) (Math.random() * tablaCanciones.getItems().size())); reproducirActual(); btnPlayPausa.setText("⏸");
            } else if (!listaObservableCola.isEmpty()) { if (cancionActual != null) historial.push(cancionActual); cancionActual = listaObservableCola.remove(0); reproducirActual(); btnPlayPausa.setText("⏸");
            } else { int i = tablaCanciones.getSelectionModel().getSelectedIndex(); if (i >= 0 && i < tablaCanciones.getItems().size() - 1) { if (cancionActual != null) historial.push(cancionActual); tablaCanciones.getSelectionModel().select(i + 1); cancionActual = tablaCanciones.getSelectionModel().getSelectedItem(); reproducirActual(); btnPlayPausa.setText("⏸"); } }
        });

        btnAnterior.setOnAction(e -> { Cancion ant = historial.pop(); if (ant != null) { cancionActual = ant; reproducirActual(); btnPlayPausa.setText("⏸"); } else if (cancionActual != null) reproductor.saltarA(0.0); });

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

        Scene escena = new Scene(layoutPrincipal, 1150, 700); 
        escena.getRoot().setStyle("-fx-base: #121212; -fx-control-inner-background: #121212; -fx-table-cell-border-color: transparent; -fx-table-header-background-color: #282828;");
        escena.getStylesheets().add("data:text/css,.list-cell { -fx-text-fill: #b3b3b3; -fx-font-weight: bold; } .list-cell:selected { -fx-text-fill: white; -fx-background-color: #282828; } .context-menu { -fx-background-color: #282828; } .menu-item:focused { -fx-background-color: #3e3e3e; }");

        escenarioPrincipal.setOnCloseRequest(e -> { reproductor.detener(); System.exit(0); });
        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    private void reproducirActual() { 
        if (cancionActual != null) { 
            reproductor.detener(); 
            reproductor.reproducir(cancionActual.getRuta()); 
            // NUEVO: Asegurarnos de que el volumen actual de la barra se aplique al cambiar de canción
            reproductor.setVolumen(sliderVolumen.getValue());
        } 
    }
    private String formatearTiempo(int seg) { return String.format("%d:%02d", seg / 60, seg % 60); }
    public static void main(String[] args) { launch(args); }
}
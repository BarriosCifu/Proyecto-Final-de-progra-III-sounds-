package com.mycompany.sounds;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
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
import static javafx.util.Duration.minutes;

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

    // --- ESTRUCTURAS DE DATOS ---
    private boolean modoRepeticion = false;
    private ListaCircular listaRepeticion = new ListaCircular(); 
    private Pila historial = new Pila(); 

    // --- PLAYLISTS CON LISTA SIMPLE ---
    private ObservableList<String> nombresPlaylists = FXCollections.observableArrayList();
    private ListView<String> vistaPlaylists;
    // El mapa conecta el nombre de la playlist con tu objeto ListaSimple
    private Map<String, ListaSimple> mapaPlaylists = new HashMap<>();
    private String playlistSeleccionada = null;
    private Label tituloCentral;

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane layoutPrincipal = new BorderPane();

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
        textoPlaylists.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 20px 0 0 0;");
        
        Button btnNuevaPlaylist = new Button("+ Nueva Playlist");
        btnNuevaPlaylist.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 0;");
        
        vistaPlaylists = new ListView<>(nombresPlaylists);
        vistaPlaylists.setStyle("-fx-background-color: transparent; -fx-control-inner-background: #000000; -fx-border-color: transparent;");
        VBox.setVgrow(vistaPlaylists, Priority.ALWAYS); 
        
        menuIzquierdo.getChildren().addAll(textoMenu, btnCargarMusica, textoPlaylists, btnNuevaPlaylist, vistaPlaylists);

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
        
        Button btnAñadirACola = new Button("+ Fila");
        btnAñadirACola.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-border-color: #b3b3b3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        
        // BOTÓN NUEVO: Añadir a la Playlist Simple seleccionada
        Button btnAñadirAPlaylist = new Button("+ Playlist");
        btnAñadirAPlaylist.setStyle("-fx-background-color: #282828; -fx-text-fill: white; -fx-border-color: #b3b3b3; -fx-border-radius: 15; -fx-background-radius: 15; -fx-cursor: hand;");
        
        encabezadoBiblioteca.getChildren().addAll(tituloCentral, btnAñadirACola, btnAñadirAPlaylist);
        
        tablaCanciones = new TableView<>();
        tablaCanciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Cancion, String> colNombre = new TableColumn<>("Título");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        TableColumn<Cancion, String> colArtista = new TableColumn<>("Artista");
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        TableColumn<Cancion, String> colAlbum = new TableColumn<>("Álbum");
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        
        tablaCanciones.getColumns().addAll(colNombre, colArtista, colAlbum);
        VBox.setVgrow(tablaCanciones, Priority.ALWAYS);
        
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

        // --- 4. PANEL INFERIOR ---
        VBox panelInferior = new VBox(10);
        panelInferior.setPrefHeight(100);
        panelInferior.setAlignment(Pos.CENTER);
        panelInferior.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0; -fx-padding: 10px 30px;");
        
        HBox contenedorProgreso = new HBox(15);
        contenedorProgreso.setAlignment(Pos.CENTER);
        
        lblTiempoActual = new Label("0:00");
        lblTiempoActual.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        
        sliderProgreso = new Slider();
        sliderProgreso.setPrefWidth(350); 
        sliderProgreso.setMinWidth(250); 
        sliderProgreso.setStyle("-fx-base: #181818; -fx-accent: #30D5C8; -fx-cursor: hand;");
        
        lblTiempoTotal = new Label("0:00");
        lblTiempoTotal.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 12px;");
        
        contenedorProgreso.getChildren().addAll(lblTiempoActual, sliderProgreso, lblTiempoTotal);

        HBox barraBotones = new HBox(30); 
        barraBotones.setAlignment(Pos.CENTER);
        
        Button btnRepetir = new Button("🔁");
        Button btnAnterior = new Button("⏮");
        Button btnPlayPausa = new Button("▶"); 
        Button btnSiguiente = new Button("⏭");
        
        String estiloBotones = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;";
        btnRepetir.setStyle(estiloBotones);
        btnAnterior.setStyle(estiloBotones);
        btnSiguiente.setStyle(estiloBotones);
        btnPlayPausa.setStyle(estiloBotones + "-fx-font-size: 24px; -fx-min-width: 60px;"); 
        
        barraBotones.getChildren().addAll(btnRepetir, btnAnterior, btnPlayPausa, btnSiguiente);
        panelInferior.getChildren().addAll(contenedorProgreso, barraBotones);

        // --- EVENTOS DEL SLIDER ---
        sliderProgreso.setOnMousePressed(evento -> arrastrandoSlider = true);
        sliderProgreso.setOnMouseReleased(evento -> {
            if (cancionActual != null) {
                double porcentaje = sliderProgreso.getValue() / 100.0; 
                reproductor.saltarA(porcentaje);
                btnPlayPausa.setText("⏸");
                estaReproduciendo = true;
            }
            arrastrandoSlider = false; 
        });

        // --- TIMELINE ---
        Timeline temporizador = new Timeline(new KeyFrame(Duration.millis(200), evento -> {
            if (reproductor.isReproduciendo()) {
                double progreso = reproductor.getProgreso(); 
                int totalSegundos = reproductor.getDuracionEstimadaSegundos();
                int segundosActuales = (int) (totalSegundos * progreso);

                if (!arrastrandoSlider) {
                    sliderProgreso.setValue(progreso * 100); 
                }

                lblTiempoActual.setText(formatearTiempo(segundosActuales));
                lblTiempoTotal.setText(formatearTiempo(totalSegundos));
                
                if (progreso >= 0.99 && estaReproduciendo) {
                    btnSiguiente.fire(); 
                }
            }
        }));
        temporizador.setCycleCount(Timeline.INDEFINITE);
        temporizador.play();

        // --- EVENTO REGRESAR A BIBLIOTECA PRINCIPAL ---
        textoMenu.setOnMouseClicked(evento -> {
            vistaPlaylists.getSelectionModel().clearSelection();
            playlistSeleccionada = null;
            tituloCentral.setText("Biblioteca Principal");
            if (listaOriginalCanciones != null) {
                tablaCanciones.setItems(FXCollections.observableArrayList(listaOriginalCanciones));
            }
        });

        // --- EVENTO CREAR NUEVA PLAYLIST ---
        btnNuevaPlaylist.setOnAction(evento -> {
            TextInputDialog dialogo = new TextInputDialog();
            dialogo.setTitle("Nueva Playlist");
            dialogo.setHeaderText("Crear una nueva lista de reproducción");
            dialogo.setContentText("Nombre de la Playlist:");
            dialogo.getDialogPane().setStyle("-fx-base: #282828; -fx-text-fill: white;");

            Optional<String> resultado = dialogo.showAndWait();
            resultado.ifPresent(nombre -> {
                if (!nombre.trim().isEmpty() && !nombresPlaylists.contains(nombre)) {
                    nombresPlaylists.add(nombre);
                    // CONEXIÓN CON TU ESTRUCTURA: Instanciamos tu ListaSimple
                    mapaPlaylists.put(nombre, new ListaSimple()); 
                }
            });
        });

        // --- EVENTO SELECCIONAR UNA PLAYLIST EN EL PANEL IZQUIERDO ---
        vistaPlaylists.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null) {
                playlistSeleccionada = nuevo;
                tituloCentral.setText("Playlist: " + nuevo);
                
                // RECORRIDO DE TU LISTA SIMPLE: Extraemos los nodos para la UI
                ListaSimple listaActual = mapaPlaylists.get(nuevo);
                ObservableList<Cancion> cancionesPlaylist = FXCollections.observableArrayList();
                
                if (listaActual != null) {
                    NodoLista nodoActual = listaActual.getCabeza();
                    while (nodoActual != null) {
                        cancionesPlaylist.add(nodoActual.getCancion());
                        nodoActual = nodoActual.getSiguiente(); // Avanzar al siguiente nodo simple
                    }
                }
                tablaCanciones.setItems(cancionesPlaylist);
            }
        });

        // --- EVENTO AÑADIR CANCIÓN SELECCIONADA A LA PLAYLIST SIMPLE ---
        btnAñadirAPlaylist.setOnAction(evento -> {
            Cancion cancionSeleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (cancionSeleccionada != null && playlistSeleccionada != null) {
                ListaSimple lista = mapaPlaylists.get(playlistSeleccionada);
                if (lista != null) {
                    // LLAMADA A TU MÉTODO: Inserta el nodo en la lista enlazada
                    lista.insertar(cancionSeleccionada); 
                    System.out.println("Insertada en ListaSimple (" + playlistSeleccionada + "): " + cancionSeleccionada.getNombre());
                    
                    // Refrescar visualmente la tabla si estamos parados sobre esa playlist
                    vistaPlaylists.getSelectionModel().clearSelection();
                    vistaPlaylists.getSelectionModel().select(playlistSeleccionada);
                }
            } else {
                System.out.println("Por favor selecciona una canción y marca una playlist primero.");
            }
        });

        // --- EVENTO REPETIR ---
        btnRepetir.setOnAction(evento -> {
            modoRepeticion = !modoRepeticion;
            if (modoRepeticion) {
                btnRepetir.setStyle(estiloBotones + "-fx-text-fill: #1db954;"); 
                btnRepetir.setText("🔂"); 
                listaRepeticion = new ListaCircular();
                if (cancionActual != null) {
                    listaRepeticion.insertar(cancionActual);
                }
            } else {
                btnRepetir.setStyle(estiloBotones); 
                btnRepetir.setText("🔁"); 
            }
        });

        // --- EVENTOS GENERALES ---
        tablaCanciones.setOnMouseClicked(evento -> {
            if (evento.getButton().equals(MouseButton.PRIMARY) && evento.getClickCount() == 2) {
                Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    if (cancionActual != null && !cancionActual.equals(seleccionada)) {
                        historial.push(cancionActual);
                    }
                    
                    cancionActual = seleccionada;
                    
                    if (modoRepeticion) {
                        listaRepeticion = new ListaCircular();
                        listaRepeticion.insertar(cancionActual);
                    }
                    
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸");
                    estaReproduciendo = true;
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
                listaOriginalCanciones = lector.getCancionesCargadas();
                listaObservableCanciones = FXCollections.observableArrayList(listaOriginalCanciones);
                tablaCanciones.setItems(listaObservableCanciones);
                
                modoRepeticion = false;
                btnRepetir.setStyle(estiloBotones);
                btnRepetir.setText("🔁");
                
                vistaPlaylists.getSelectionModel().clearSelection();
                playlistSeleccionada = null;
                tituloCentral.setText("Biblioteca Principal");
            }
        });

        // BUSCADOR INTELIGENTE: Filtra dinámicamente según el contexto (Biblioteca o Playlist)
        txtBuscar.textProperty().addListener((observable, textoViejo, textoNuevo) -> {
            List<Cancion> cancionesAEvaluar = new ArrayList<>();
            
            if (playlistSeleccionada == null) {
                if (listaOriginalCanciones != null) cancionesAEvaluar.addAll(listaOriginalCanciones);
            } else {
                ListaSimple ls = mapaPlaylists.get(playlistSeleccionada);
                if (ls != null) {
                    NodoLista target = ls.getCabeza();
                    while (target != null) {
                        cancionesAEvaluar.add(target.getCancion());
                        target = target.getSiguiente();
                    }
                }
            }

            if (textoNuevo == null || textoNuevo.trim().isEmpty()) {
                tablaCanciones.setItems(FXCollections.observableArrayList(cancionesAEvaluar));
            } else {
                String busqueda = textoNuevo.toLowerCase();
                List<Cancion> cancionesFiltradas = cancionesAEvaluar.stream()
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
        
        btnAñadirACola.setOnAction(evento -> {
            Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                listaObservableCola.add(seleccionada);
            }
        });

        btnQuitarCola.setOnAction(evento -> {
            Cancion seleccionada = tablaCola.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                listaObservableCola.remove(seleccionada);
            }
        });

        btnSubirCola.setOnAction(evento -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice > 0) {
                Collections.swap(listaObservableCola, indice, indice - 1);
                tablaCola.getSelectionModel().select(indice - 1);
            }
        });

        btnBajarCola.setOnAction(evento -> {
            int indice = tablaCola.getSelectionModel().getSelectedIndex();
            if (indice >= 0 && indice < listaObservableCola.size() - 1) {
                Collections.swap(listaObservableCola, indice, indice + 1);
                tablaCola.getSelectionModel().select(indice + 1);
            }
        });
        
        btnPlayPausa.setOnAction(evento -> {
            if (estaReproduciendo) {
                reproductor.pausar();
                btnPlayPausa.setText("▶");
                estaReproduciendo = false;
            } else {
                if (cancionActual != null && reproductor.getProgreso() > 0 && reproductor.getProgreso() < 1) {
                    reproductor.continuar();
                    btnPlayPausa.setText("⏸");
                    estaReproduciendo = true;
                } else {
                    if (!listaObservableCola.isEmpty()) {
                        if (cancionActual != null) historial.push(cancionActual);
                        
                        cancionActual = listaObservableCola.remove(0); 
                        tablaCanciones.getSelectionModel().select(cancionActual);
                        if (modoRepeticion) {
                            listaRepeticion = new ListaCircular();
                            listaRepeticion.insertar(cancionActual);
                        }
                    } else {
                        Cancion seleccionada = tablaCanciones.getSelectionModel().getSelectedItem();
                        if (seleccionada != null && !seleccionada.equals(cancionActual)) {
                            if (cancionActual != null) historial.push(cancionActual);
                            cancionActual = seleccionada;
                        }
                    }

                    if (cancionActual != null) {
                        reproductor.detener(); 
                        reproductor.reproducir(cancionActual.getRuta());
                        btnPlayPausa.setText("⏸");
                        estaReproduciendo = true;
                    }
                }
            }
        });

        btnSiguiente.setOnAction(evento -> {
            if (modoRepeticion && listaRepeticion != null) {
                cancionActual = listaRepeticion.irSiguiente();
                if (cancionActual != null) {
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸");
                    estaReproduciendo = true;
                }
            } else if (!listaObservableCola.isEmpty()) {
                if (cancionActual != null) historial.push(cancionActual);
                
                cancionActual = listaObservableCola.remove(0); 
                tablaCanciones.getSelectionModel().select(cancionActual);
                
                reproductor.detener();
                reproductor.reproducir(cancionActual.getRuta());
                btnPlayPausa.setText("⏸");
                estaReproduciendo = true;
            } else {
                int indiceActual = tablaCanciones.getSelectionModel().getSelectedIndex();
                if (indiceActual >= 0 && indiceActual < tablaCanciones.getItems().size() - 1) {
                    if (cancionActual != null) historial.push(cancionActual);
                    
                    tablaCanciones.getSelectionModel().select(indiceActual + 1);
                    cancionActual = tablaCanciones.getSelectionModel().getSelectedItem();
                    
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸");
                    estaReproduciendo = true;
                }
            }
        });

        btnAnterior.setOnAction(evento -> {
            if (modoRepeticion && listaRepeticion != null) {
                cancionActual = listaRepeticion.irAnterior();
                if (cancionActual != null) {
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸");
                    estaReproduciendo = true;
                }
            } else {
                Cancion cancionAnterior = historial.pop();
                
                if (cancionAnterior != null) {
                    cancionActual = cancionAnterior;
                    tablaCanciones.getSelectionModel().select(cancionActual); 
                    
                    reproductor.detener();
                    reproductor.reproducir(cancionActual.getRuta());
                    btnPlayPausa.setText("⏸");
                    estaReproduciendo = true;
                } else {
                    if (cancionActual != null) {
                        reproductor.saltarA(0.0);
                    }
                }
            }
        });

        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setRight(panelDerecho);
        layoutPrincipal.setBottom(panelInferior); 

        Scene escena = new Scene(layoutPrincipal, 1150, 700); 
        escena.getRoot().setStyle("-fx-base: #121212; -fx-control-inner-background: #121212; -fx-table-cell-border-color: transparent; -fx-table-header-background-color: #282828;");
        escena.getStylesheets().add("data:text/css,.list-cell { -fx-text-fill: #b3b3b3; -fx-font-weight: bold; } .list-cell:selected { -fx-text-fill: white; -fx-background-color: #282828; }");

        escenarioPrincipal.setOnCloseRequest(evento -> {
            reproductor.detener();
            System.exit(0);
        });

        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

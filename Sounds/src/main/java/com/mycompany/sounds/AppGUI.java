
package com.mycompany.sounds;
import javafx.application.Application;
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
import javafx.stage.Stage;

public class AppGUI extends Application {

    @Override
    public void start(Stage escenarioPrincipal) {
        BorderPane layoutPrincipal = new BorderPane();

        // --- 1. PANEL IZQUIERDO (Menú y Playlists) ---
        VBox menuIzquierdo = new VBox(15); // 15px de espacio entre elementos
        menuIzquierdo.setPrefWidth(220);
        menuIzquierdo.setStyle("-fx-background-color: #000000; -fx-padding: 20px;");
        
        Label textoMenu = new Label("MI BIBLIOTECA");
        textoMenu.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Button btnCargarMúsica = new Button("Cargar Carpeta MP3");
        btnCargarMúsica.setStyle("-fx-background-color: #1db954; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        btnCargarMúsica.setPrefWidth(180);
        
        menuIzquierdo.getChildren().addAll(textoMenu, btnCargarMúsica);

        // --- 2. PANEL CENTRAL (Tabla de canciones) ---
        VBox panelCentral = new VBox(10);
        panelCentral.setStyle("-fx-background-color: #121212; -fx-padding: 20px;");
        
        Label tituloCentral = new Label("Biblioteca Principal");
        tituloCentral.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Creación de la tabla
        TableView<Cancion> tablaCanciones = new TableView<>();
        tablaCanciones.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Para que las columnas ocupen todo el ancho
        
        // Configurar las columnas (El texto en comillas de PropertyValueFactory DEBE coincidir con los atributos de tu clase Cancion)
        TableColumn<Cancion, String> colNombre = new TableColumn<>("Título");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        TableColumn<Cancion, String> colArtista = new TableColumn<>("Artista");
        colArtista.setCellValueFactory(new PropertyValueFactory<>("artista"));
        
        TableColumn<Cancion, String> colAlbum = new TableColumn<>("Álbum");
        colAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        
        tablaCanciones.getColumns().addAll(colNombre, colArtista, colAlbum);
        
        // Hacer que la tabla ocupe el resto del espacio vertical
        VBox.setVgrow(tablaCanciones, javafx.scene.layout.Priority.ALWAYS);
        
        panelCentral.getChildren().addAll(tituloCentral, tablaCanciones);

        // --- 3. PANEL INFERIOR (Controles de reproducción) ---
        HBox barraReproduccion = new HBox(20); // 20px de espacio entre botones
        barraReproduccion.setPrefHeight(90);
        barraReproduccion.setAlignment(Pos.CENTER); // Centrar los botones
        barraReproduccion.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");
        
        Button btnAnterior = new Button("⏮");
        Button btnPlay = new Button("▶ Play");
        Button btnPausa = new Button("⏸ Pausa");
        Button btnSiguiente = new Button("⏭");
        
        // Estilo básico para los botones (sin borde, fondo transparente, texto blanco)
        String estiloBotones = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;";
        btnAnterior.setStyle(estiloBotones);
        btnPlay.setStyle(estiloBotones + "-fx-font-size: 20px;"); // El play un poco más grande
        btnPausa.setStyle(estiloBotones + "-fx-font-size: 20px;");
        btnSiguiente.setStyle(estiloBotones);
        
        barraReproduccion.getChildren().addAll(btnAnterior, btnPlay, btnPausa, btnSiguiente);

        // --- Ensamblar y Mostrar ---
        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setBottom(barraReproduccion);

        Scene escena = new Scene(layoutPrincipal, 1000, 700); 
        
        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
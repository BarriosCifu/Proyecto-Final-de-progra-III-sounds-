
package com.mycompany.sounds;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AppGUI extends Application {

    @Override
    public void start(Stage escenarioPrincipal) {
        // Panel principal que divide la pantalla en zonas
        BorderPane layoutPrincipal = new BorderPane();

        // 1. PANEL IZQUIERDO (Menú y Playlists)
        VBox menuIzquierdo = new VBox();
        menuIzquierdo.setPrefWidth(220); // Ancho de la barra lateral
        menuIzquierdo.setStyle("-fx-background-color: #000000;"); // Negro puro tipo Spotify
        
        Label textoMenu = new Label("Menú / Playlists");
        textoMenu.setStyle("-fx-text-fill: white; -fx-padding: 20px; -fx-font-weight: bold;");
        menuIzquierdo.getChildren().add(textoMenu);

        // 2. PANEL CENTRAL (Lista de canciones de la biblioteca)
        VBox panelCentral = new VBox();
        panelCentral.setStyle("-fx-background-color: #121212;"); // Gris oscuro de fondo
        
        Label textoCentral = new Label("Biblioteca de Canciones");
        textoCentral.setStyle("-fx-text-fill: white; -fx-padding: 20px; -fx-font-size: 24px; -fx-font-weight: bold;");
        panelCentral.getChildren().add(textoCentral);

        // 3. PANEL INFERIOR (Controles de reproducción)
        HBox barraReproduccion = new HBox();
        barraReproduccion.setPrefHeight(90); // Alto de la barra
        // Gris un poco más claro con una línea divisoria arriba
        barraReproduccion.setStyle("-fx-background-color: #181818; -fx-border-color: #282828; -fx-border-width: 1 0 0 0;");
        
        Label textoControles = new Label("Controles (Play, Pausa, Siguiente)");
        textoControles.setStyle("-fx-text-fill: white; -fx-padding: 30px;");
        barraReproduccion.getChildren().add(textoControles);

        // --- Ensamblar todas las piezas en el layout principal ---
        layoutPrincipal.setLeft(menuIzquierdo);
        layoutPrincipal.setCenter(panelCentral);
        layoutPrincipal.setBottom(barraReproduccion);

        // Crear la escena con un tamaño más grande (1000x700)
        Scene escena = new Scene(layoutPrincipal, 1000, 700); 

        // Configurar y mostrar la ventana
        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
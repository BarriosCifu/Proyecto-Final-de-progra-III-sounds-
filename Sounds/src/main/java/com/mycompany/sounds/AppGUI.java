
package com.mycompany.sounds;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppGUI extends Application {

    @Override
    public void start(Stage escenarioPrincipal) {
        // 1. Crear un texto de prueba
        Label saludo = new Label("¡Bienvenido a Sounds! El motor gráfico está listo.");
        
        // 2. Colocarlo en un panel (Layout) central
        StackPane raiz = new StackPane();
        raiz.getChildren().add(saludo);
        
        // 3. Crear la escena (El lienzo de tu ventana de 800x600 pixeles)
        Scene escena = new Scene(raiz, 800, 600); 
        
        // 4. Configurar la ventana principal
        escenarioPrincipal.setTitle("Sounds - Reproductor Musical");
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.show(); // Mostrar la ventana
    }

    public static void main(String[] args) {
        // Este método enciende el motor de JavaFX
        launch(args);
    }
}
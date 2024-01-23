package com.example.androidchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private EditText inputMessage;
    private ListView listOfMessages;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // El usuario ya está autenticado, puedes redirigir a otra actividad o realizar otras acciones
            Toast.makeText(this, "Usuario ya autenticado: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        } else {
            // El usuario no está autenticado, inicia el flujo de inicio de sesión
            startSignIn();
        }

        inputMessage = findViewById(R.id.input);
        listOfMessages = findViewById(R.id.list_of_messages);

        // Configurar adaptador para la lista de mensajes
        messageAdapter = new MessageAdapter(this, new ArrayList<>());
        listOfMessages.setAdapter(messageAdapter);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://chat-android-74785-default-rtdb.europe-west1.firebasedatabase.app");
        DatabaseReference messagesRef = database.getReference("messages");

        // Agregar un listener para detectar cambios en los mensajes
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar el adaptador antes de agregar mensajes actualizados
                messageAdapter.clear();

                // Iterar a través de los mensajes y agregarlos al adaptador
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    ChatMessage chatMessage = messageSnapshot.getValue(ChatMessage.class);
                    messageAdapter.add(chatMessage);
                }

                // Notificar al adaptador que los datos han cambiado
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de base de datos si es necesario
            }
        });

    }

    private void startSignIn() {
        // Crear un ActivityResultLauncher para gestionar el resultado del inicio de sesión
        ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> {
                    handleSignInResult(result);
                }
        );

        // Llamar a la función para iniciar el flujo de inicio de sesión desde FirebaseUIActivity
        FirebaseUIActivity.createSignInIntent(signInLauncher);
    }

    private void handleSignInResult(FirebaseAuthUIAuthenticationResult result) {
        // Manejar el resultado del inicio de sesión aquí
        // Puedes agregar más lógica según tus necesidades
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Toast.makeText(this, "Inicio de sesión exitoso para " + user.getEmail(), Toast.LENGTH_SHORT).show();
            // Aquí puedes realizar acciones adicionales después del inicio de sesión exitoso
        } else {
            // El inicio de sesión ha fallado o el usuario canceló el proceso
            // Puedes manejar estos casos según tus necesidades
            Toast.makeText(this, "Inicio de sesión cancelado o fallido", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {
        String messageText = inputMessage.getText().toString().trim();
        String messageUser = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (!messageText.isEmpty()) {
            // Crear un objeto ChatMessage con la información del mensaje
            ChatMessage chatMessage = new ChatMessage(messageText, messageUser);

            // Limpiar el cuadro de texto
            inputMessage.setText("");

            // Obtener una referencia única utilizando push()
            DatabaseReference messagesRef = FirebaseDatabase.getInstance("https://chat-android-74785-default-rtdb.europe-west1.firebasedatabase.app").getReference("messages").push();

            // Guardar el mensaje en la base de datos con una clave única
            messagesRef.setValue(chatMessage);

        } else {
            Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show();
        }
    }


}

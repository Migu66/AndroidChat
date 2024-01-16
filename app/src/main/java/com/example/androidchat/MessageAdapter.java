package com.example.androidchat;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

// MessageAdapter.java
public class MessageAdapter extends ArrayAdapter<ChatMessage> {

    public MessageAdapter(Context context, List<ChatMessage> messages) {
        super(context, R.layout.message_item, messages);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
        }

        // Obtener el mensaje actual
        ChatMessage message = getItem(position);

        // Obtener referencias a las vistas del diseño
        TextView messageUser = convertView.findViewById(R.id.message_user);
        TextView messageTime = convertView.findViewById(R.id.message_time);
        TextView messageText = convertView.findViewById(R.id.message_text);

        // Establecer los valores en las vistas
        if (message != null) {
            messageUser.setText(message.getMessageUser());
            // Formatear la fecha y hora
            String formattedTime = DateFormat.format("dd-MM-yyyy (HH:mm:ss)", message.getMessageTime()).toString();
            messageTime.setText(formattedTime);
            messageText.setText(message.getMessageText());
        }

        return convertView;
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fatec.app.service;

import com.fatec.app.info.Chat;
import com.fatec.app.info.Chat.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chris
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555);

            System.out.println("Todos sistemas online!");

            while (true) {
                socket = serverSocket.accept();

                new Thread(new ListenerSocket(socket)).start();
            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Chat message = null;
            try {
                while ((message = (Chat) input.readObject()) != null) {
                    Action action = message.getAction();

                    switch (action) {
                        case CONNECT:
                            boolean isConnect = connect(message, output);
                            if (isConnect) {
                                mapOnlines.put(message.getName(), output);
                                sendOnlines();
                            }
                            break;
                        case DISCONNECT:
                            disconnect(message, output);
                            sendOnlines();
                            return;
                        case SEND_ONE:
                            sendOne(message);
                            break;
                        case SEND_ALL:
                            sendAll(message);
                            break;
                    }
                }
            } catch (IOException ex) {
                Chat cm = new Chat();
                cm.setName(message.getName());
                disconnect(cm, output);
                sendOnlines();
                System.out.println(message.getName() + " deixou o chat!");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean connect(Chat message, ObjectOutputStream output) {
        if (mapOnlines.size() == 0) {
            message.setText("YES");
            send(message, output);
            return true;
        }

        if (mapOnlines.containsKey(message.getName())) {
            message.setText("NO");
            send(message, output);
            return false;
        } else {
            message.setText("YES");
            send(message, output);
            return true;
        }
    }

    private void disconnect(Chat message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName());

        message.setText(encriptar(" desconectou-se."));

        message.setAction(Action.SEND_ONE);

        sendAll(message);

        System.out.println("Server: " + message.getName() + " sai da sala");
    }

    private void send(Chat message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String encriptar(String text) {
        String base = "abcçdefghijklmnopqrstuvwxyz0123456789ABCÇDEFGHIJKLMNOPQRSTUVWXYZ;:/?~^]}{`´+=_-)(*&¨%$#@!<>.,áéíóúÁÉÍÓÚàèìòùÀÈÌÒÙ";
        int tamanhotxt = text.length();
        int posicao;
        String resultado = "";
        for (int i = 0; i < tamanhotxt; i++) {
            if (text.charAt(i) != ' ') {
                System.err.println("---------------------------------------------------");
                System.err.println("Letra escolhida cifra: " + text.charAt(i));
                posicao = base.indexOf(text.charAt(i));
                System.err.println("P i = 0; i < tamanhotxt; i++) {\n"
                        + "            if (text.charAt(i) != ' ') {osição letra escolhida antes cifra: " + posicao);
                posicao += 12;
                System.err.println("Posição cifra ant if: " + posicao);
                if (posicao > base.length() - 1) {
                    posicao -= base.length();
                }
                System.err.println("Posiçãpo cifra pos if: " + posicao);
                resultado += base.charAt(posicao);
                System.err.println("Letra escolhida desifra : " + resultado);
            } else {
                resultado += ' ';
            }
        }
        System.err.println("cifra: " + resultado);
        return resultado;
    }

    private void sendOne(Chat message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNameReserved())) {
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendAll(Chat message) {
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (!kv.getKey().equals(message.getName())) {
                message.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(message);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void sendOnlines() {
        Set<String> setNames = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());
        }

        Chat message = new Chat();
        message.setAction(Action.USERS_ONLINE);
        message.setSetOnline(setNames);

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setName(kv.getKey());
            try {
                kv.getValue().writeObject(message);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

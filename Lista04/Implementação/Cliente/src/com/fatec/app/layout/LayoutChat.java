/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fatec.app.layout;

import com.fatec.app.info.Chat;
import com.fatec.app.info.Chat.Action;
import com.fatec.app.service.ClienteService;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

/**
 *
 * @author chris
 */
public class LayoutChat extends javax.swing.JFrame {

    private Socket socket;
    private Chat message;
    private ClienteService service;

    /**
     * Creates new form LayoutChat
     */
    public LayoutChat() {
        initComponents();

        ImageIcon myimage = new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("logo.png")));
        Image img1 = myimage.getImage();
        Image img2 = img1.getScaledInstance(lbl_carro.getWidth(), lbl_carro.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon i = new ImageIcon(img2);
        lbl_carro.setIcon(i);

        Color color1 = new Color(154, 154, 154);
        Color color2 = new Color(98, 153, 196);

        //btn_chatindividual.setContentAreaFilled(false);
        //btn_chatindividual.setOpaque(true);
        //btn_chatindividual.setBackground(color1);

        btn_enviar.setContentAreaFilled(false);
        btn_enviar.setOpaque(true);
        btn_enviar.setBackground(color1);

        btn_conectar.setContentAreaFilled(false);
        btn_conectar.setOpaque(true);
        btn_conectar.setBackground(color1);

        btn_sair.setContentAreaFilled(false);
        btn_sair.setOpaque(true);
        btn_sair.setBackground(color2);

        btn_chat_sair.setContentAreaFilled(false);
        btn_chat_sair.setOpaque(true);
        btn_chat_sair.setBackground(color2);

        txt_mensagem.setOpaque(true);
        txt_chat.setOpaque(true);
    }

    private class ListenerSocket implements Runnable {

        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(LayoutChat.class.getName()).log(Level.SEVERE, null, ex);
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
                            connected(message);
                            break;
                        case DISCONNECT:
                            disconnected();
                            socket.close();
                            break;
                        case SEND_ONE:
                            System.out.println("LOCAL: " + message.getText() + "<<<");
                            receive(message);
                            break;
                        case USERS_ONLINE:
                            refreshOnlines(message);
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(LayoutChat.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(LayoutChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void connected(Chat message) {
        if (message.getText().equals("NO")) {
            this.txt_nome.setText("");
            JOptionPane.showMessageDialog(this, "Conexão não efetuada!\nTente outro nome.");
            return;
        }
        this.message = message;
        JOptionPane.showMessageDialog(this, "Conexão efetuada com sucesso!");
        CardLayout cl = (CardLayout) pnl_controlador.getLayout();
        cl.show(pnl_controlador, "Chat");
    }

    private void disconnected() {
        CardLayout cl = (CardLayout) pnl_controlador.getLayout();
        cl.show(pnl_controlador, "Identificacao");
    }

    private void receive(Chat message) {
        this.txt_chat.append(message.getName() + ":" + decriptar(message.getText()) + "\n");
    }

    private void refreshOnlines(Chat message) {
        System.out.println(message.getSetOnline().toString());

        Set<String> names = message.getSetOnline();

        names.remove(message.getName());

        String[] array = (String[]) names.toArray(new String[names.size()]);

        this.list_onlines.setListData(array);
        this.list_onlines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list_onlines.setLayoutOrientation(JList.VERTICAL);
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
                System.err.println("P i = 0; i < tamanhotxt; i++) {\n" +
"            if (text.charAt(i) != ' ') {osição letra escolhida antes cifra: " + posicao);
                posicao += 12;
                System.err.println("Posição cifra ant if: " + posicao);
                if (posicao > base.length()-1) {
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

    private String decriptar(String text) {
        String base = "abcçdefghijklmnopqrstuvwxyz0123456789ABCÇDEFGHIJKLMNOPQRSTUVWXYZ;:/?~^]}{`´+=_-)(*&¨%$#@!<>.,áéíóúÁÉÍÓÚàèìòùÀÈÌÒÙ";
        int tamanhotxt = text.length();
        int posicao;
        String resultado = "";
        for (int i = 0; i < tamanhotxt; i++) {
            if (text.charAt(i) != ' ') {
                System.err.println("---------------------------------------------------");
                System.err.println("Letra escolhida desifra: " + text.charAt(i));
                posicao = base.indexOf(text.charAt(i));
                System.err.println("Posição letra escolhida antes desifra: " + posicao);
                posicao -= 12;
                System.err.println("Posição desifra ant if: " + posicao);
                if (posicao < 0) {
                    posicao = base.length() - Math.abs(posicao);
                }
                System.err.println("Posiçãpo desifra pos if: " + posicao);
                resultado += base.charAt(posicao);
                System.err.println("Letra escolhida desifra : " + resultado);
            }
            else{
                resultado += ' ';
            }
        }
        System.err.println("Desifra: " + resultado);
        return resultado;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        pnl_controlador = new javax.swing.JPanel();
        pnl_identificacao = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        txt_nome = new javax.swing.JTextField();
        btn_sair = new javax.swing.JButton();
        btn_conectar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lbl_carro = new javax.swing.JLabel();
        pnl_chat = new javax.swing.JPanel();
        list_onlines = new javax.swing.JList<>();
        btn_enviar = new javax.swing.JButton();
        btn_chat_sair = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txt_chat = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txt_mensagem = new javax.swing.JTextArea();

        jFormattedTextField1.setText("jFormattedTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pnl_controlador.setLayout(new java.awt.CardLayout());

        pnl_identificacao.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "Identificação de Usuario", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BELOW_TOP, new java.awt.Font("Century Gothic", 1, 20))); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_nome.setFont(new java.awt.Font("Century Gothic", 0, 16)); // NOI18N
        txt_nome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nomeActionPerformed(evt);
            }
        });
        jPanel1.add(txt_nome, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 124, 220, 30));

        btn_sair.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        btn_sair.setForeground(new java.awt.Color(255, 255, 255));
        btn_sair.setText("Sair");
        btn_sair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_sairMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_sairMouseReleased(evt);
            }
        });
        btn_sair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sairActionPerformed(evt);
            }
        });
        jPanel1.add(btn_sair, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 200, 290, 24));

        btn_conectar.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        btn_conectar.setForeground(new java.awt.Color(255, 255, 255));
        btn_conectar.setText("Conectar");
        btn_conectar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_conectarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_conectarMouseReleased(evt);
            }
        });
        btn_conectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_conectarActionPerformed(evt);
            }
        });
        jPanel1.add(btn_conectar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 290, 24));

        jLabel4.setFont(new java.awt.Font("Century Gothic", 0, 18)); // NOI18N
        jLabel4.setText("Nome:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 123, 70, 30));
        jPanel1.add(lbl_carro, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 110, 80));

        pnl_identificacao.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 58, 327, 250));

        pnl_controlador.add(pnl_identificacao, "Identificacao");

        pnl_chat.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        list_onlines.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnl_chat.add(list_onlines, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 10, 190, 300));

        btn_enviar.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        btn_enviar.setForeground(new java.awt.Color(255, 255, 255));
        btn_enviar.setText("Enviar");
        btn_enviar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_enviarMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_enviarMouseReleased(evt);
            }
        });
        btn_enviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_enviarActionPerformed(evt);
            }
        });
        pnl_chat.add(btn_enviar, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 320, 190, 50));

        btn_chat_sair.setFont(new java.awt.Font("Century Gothic", 1, 16)); // NOI18N
        btn_chat_sair.setForeground(new java.awt.Color(255, 255, 255));
        btn_chat_sair.setText("Sair");
        btn_chat_sair.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn_chat_sairMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn_chat_sairMouseReleased(evt);
            }
        });
        btn_chat_sair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_chat_sairActionPerformed(evt);
            }
        });
        pnl_chat.add(btn_chat_sair, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 380, 130, 30));

        txt_chat.setEditable(false);
        txt_chat.setColumns(20);
        txt_chat.setRows(5);
        txt_chat.setBorder(null);
        jScrollPane2.setViewportView(txt_chat);

        pnl_chat.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 630, 300));

        txt_mensagem.setColumns(20);
        txt_mensagem.setRows(5);
        txt_mensagem.setBorder(null);
        jScrollPane3.setViewportView(txt_mensagem);

        pnl_chat.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 630, 50));

        pnl_controlador.add(pnl_chat, "Chat");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_controlador, javax.swing.GroupLayout.PREFERRED_SIZE, 853, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnl_controlador, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_enviarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_enviarMousePressed
        Color colore = new Color(103, 102, 102);
        btn_enviar.setBackground(colore);
    }//GEN-LAST:event_btn_enviarMousePressed

    private void btn_enviarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_enviarMouseReleased
        Color colore = new Color(154, 154, 154);
        btn_enviar.setBackground(colore);
    }//GEN-LAST:event_btn_enviarMouseReleased

    private void btn_enviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_enviarActionPerformed
        String text = this.txt_mensagem.getText();
        String name = this.message.getName();
        if (!text.isEmpty()) {
            text = text.replaceAll("\\s+$", "");
            text = text.replaceAll("^\\s+", "");
            this.message = new Chat();

            if (this.list_onlines.getSelectedIndex() > -1) {
                this.message.setNameReserved((String) this.list_onlines.getSelectedValue());
                this.message.setAction(Action.SEND_ONE);
                this.list_onlines.clearSelection();
            } else {
                this.message.setAction(Action.SEND_ALL);
            }

            this.message.setName(name);
            this.message.setText(encriptar(text));

            this.txt_chat.append(name + ": " + text + "\n");

            this.service.send(this.message);

            this.txt_mensagem.setText("");
        }
    }//GEN-LAST:event_btn_enviarActionPerformed

    private void btn_conectarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_conectarMousePressed
        Color colore = new Color(103, 102, 102);
        btn_conectar.setBackground(colore);
    }//GEN-LAST:event_btn_conectarMousePressed

    private void btn_conectarMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_conectarMouseReleased
        Color colore = new Color(154, 154, 154);
        btn_conectar.setBackground(colore);
    }//GEN-LAST:event_btn_conectarMouseReleased

    private void btn_conectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_conectarActionPerformed
        String name = this.txt_nome.getText();

        if (!name.isEmpty()) {
            this.message = new Chat();
            this.message.setAction(Action.CONNECT);
            this.message.setName(name);

            this.service = new ClienteService();
            this.socket = this.service.connect();

            new Thread(new ListenerSocket(this.socket)).start();

            this.service.send(message);
        }
    }//GEN-LAST:event_btn_conectarActionPerformed

    private void btn_sairMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_sairMousePressed
        Color colore = new Color(58, 103, 138);
        btn_sair.setBackground(colore);
    }//GEN-LAST:event_btn_sairMousePressed

    private void btn_sairMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_sairMouseReleased
        Color colore = new Color(98, 153, 196);
        btn_sair.setBackground(colore);
    }//GEN-LAST:event_btn_sairMouseReleased

    private void btn_sairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sairActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btn_sairActionPerformed

    private void txt_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_nomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_nomeActionPerformed

    private void btn_chat_sairMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_chat_sairMousePressed
        Color colore = new Color(58, 103, 138);
        btn_chat_sair.setBackground(colore);
    }//GEN-LAST:event_btn_chat_sairMousePressed

    private void btn_chat_sairMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_chat_sairMouseReleased
        Color colore = new Color(98, 153, 196);
        btn_chat_sair.setBackground(colore);
    }//GEN-LAST:event_btn_chat_sairMouseReleased

    private void btn_chat_sairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_chat_sairActionPerformed
        Chat message = new Chat();
        message.setName(this.message.getName());
        message.setAction(Action.DISCONNECT);
        this.service.send(message);
        disconnected();
    }//GEN-LAST:event_btn_chat_sairActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LayoutChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LayoutChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LayoutChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LayoutChat.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LayoutChat().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_chat_sair;
    private javax.swing.JButton btn_conectar;
    private javax.swing.JButton btn_enviar;
    private javax.swing.JButton btn_sair;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbl_carro;
    private javax.swing.JList<String> list_onlines;
    private javax.swing.JPanel pnl_chat;
    private javax.swing.JPanel pnl_controlador;
    private javax.swing.JPanel pnl_identificacao;
    private javax.swing.JTextArea txt_chat;
    private javax.swing.JTextArea txt_mensagem;
    private javax.swing.JTextField txt_nome;
    // End of variables declaration//GEN-END:variables
}

package com.ifbaiano;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteJogador {
    public static void main(String[] args) {
        // Configuração: Localhost se estiver na mesma máquina
        String host = "127.0.0.1"; 
        int porta = 2424;

        try {
            Socket socket = new Socket(host, porta);
            System.out.println("Conectando ao Jogo, aguarde os outros jogadores");

            // Thread para receber mensagens do servidor
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String mensagemServidor;
                    while ((mensagemServidor = in.readLine()) != null) {
                        System.out.println(mensagemServidor);
                        // Se o jogo acabar, o servidor fecha a conexão ou envia mensagem específica
                        if (mensagemServidor.startsWith("PARABÉNS")) {
                            System.out.println("O jogo acabou. Pressione Enter para sair.");
                            System.exit(0);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Conexão com o servidor encerrada.");
                }
            }).start();

            // Thread principal para envio de mensagens (input do usuário)
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                out.println(input);
            }
            
        } catch (IOException e) {
            System.out.println("Não foi possível conectar ao servidor.");
            e.printStackTrace();
        }
    }
}

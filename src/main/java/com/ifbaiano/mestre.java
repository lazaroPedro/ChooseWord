package com.ifbaiano;

import java.io.*;
import java.net.*;

// Servidor / Mestre
public class mestre {

    public static void main(String[] args) {
        final int PORTA = 2424;
        String nome1 = "";
        String nome2 = "";

        try {
            System.out.println("=====  Servidor Iniciando  =====");
            ServerSocket servidor = new ServerSocket(PORTA);
            System.out.println("Aguardando jogadores na porta " + PORTA + "...");

            // Conecta Jogador 1
            Socket jogador1 = servidor.accept();
            System.out.println("Jogador 1 conectado: " + jogador1.getInetAddress().getHostAddress());
            BufferedReader in1 = new BufferedReader(
                    new InputStreamReader(jogador1.getInputStream())
            );
            PrintWriter out1 = new PrintWriter(jogador1.getOutputStream(), true);
            out1.println("Você se conectou ao jogo. Aguarde o outro jogador se conectar...");

            // Conecta Jogador 2
            Socket jogador2 = servidor.accept();
            System.out.println("Jogador 2 conectado: " + jogador2.getInetAddress().getHostAddress());
            BufferedReader in2 = new BufferedReader(
                    new InputStreamReader(jogador2.getInputStream())
            );
            PrintWriter out2 = new PrintWriter(jogador2.getOutputStream(), true);
            out2.println("Você se conectou ao jogo. Aguarde o mestre definir a palavra secreta...");

            System.out.println("Os dois jogadores estão conectados!");
            System.out.println("Esperando os dois jogadores informarem os nomes.");

            // Cadastro de nomes
            out1.println("Jogador 1 conectado. Digite seu nome: ");
            String resposta1 = in1.readLine();
            nome1 = (resposta1 == null || resposta1.isBlank()) ? "Jogador 1" : resposta1;
            System.out.println("Jogador 1 se chama: " + nome1);

            out2.println("Jogador 2 conectado. Digite seu nome: ");
            String resposta2 = in2.readLine();
            nome2 = (resposta2 == null || resposta2.isBlank()) ? "Jogador 2" : resposta2;
            System.out.println("Jogador 2 se chama: " + nome2);

            out1.println("Olá, " + nome1 + "! Aguarde enquanto o mestre escolhe a palavra secreta.");
            out2.println("Olá, " + nome2 + "! Aguarde enquanto o mestre escolhe a palavra secreta.");

            // etapa do jogo em si
            BufferedReader mestreEntrada = new BufferedReader(new InputStreamReader(System.in));

            // O Mestre escolhe a palavra
            System.out.println("\n=== Escolha a palavra secreta do jogo ===");
            System.out.print("Digite a palavra secreta: ");
            String palavraSecreta = mestreEntrada.readLine();
            System.out.println("A palavra secreta foi definida!");

            // envia um aviso aos jogadores
            out1.println("A PARTIDA VAI COMEÇAR!!!");
            out2.println("A PARTIDA VAI COMEÇAR!!!");

            int jogadorDaVez = 1;
            boolean jogoAtivo = true;

            while (jogoAtivo) {

                PrintWriter outAtual;
                BufferedReader inAtual;
                PrintWriter outOutro;
                String nomeAtual;
                String nomeOutro;

                if (jogadorDaVez == 1) {
                    outAtual = out1;
                    inAtual = in1;
                    nomeAtual = nome1;

                    outOutro = out2;
                    nomeOutro = nome2;
                } else {
                    outAtual = out2;
                    inAtual = in2;
                    nomeAtual = nome2;

                    outOutro = out1;
                    nomeOutro = nome1;
                }

                // proteção extra: se por algum motivo não tiver stream, encerra
                if (outAtual == null || inAtual == null) {
                    System.out.println("Erro: stream do jogador não disponível.");
                    break;
                }

                String mensagem = ">>> VEZ DE " + nomeAtual;
                System.out.println(mensagem);
                outAtual.println(mensagem);
                outOutro.println(mensagem);
                outOutro.println("Aguarde, é a vez de " + nomeAtual + ".");

                boolean repetirMenu = true;

                while (repetirMenu && jogoAtivo) {
                    // ---- Menu do jogador ----
                    outAtual.println();
                    outAtual.println("Menu do jogo - perfil jogador:");
                    outAtual.println("1. Ver as regras do jogo.");
                    outAtual.println("2. Fazer uma pergunta ao mestre.");
                    outAtual.println("3. Tentar adivinhar a palavra.");
                    outAtual.println("4. Passar a vez.");
                    outAtual.print("Escolha uma opção (1-4): ");

                    String acao = inAtual.readLine();
                    if (acao == null) {
                        System.out.println("Jogador desconectou durante a leitura da ação.");
                        jogoAtivo = false;
                        break;
                    }
                    acao = acao.trim();

                    switch (acao) {
                        case "1":
                            // Ver regras, continuar na mesma vez
                            mostrarRegras(outAtual, inAtual);
                            // repetirMenu continua true (mesmo jogador verá o menu de novo)
                            break;

                        case "2":
                            // ---- Jogador faz uma pergunta ----
                            outAtual.println("Digite sua pergunta:");
                            String pergunta = inAtual.readLine();
                            if (pergunta == null) {
                                System.out.println("Jogador desconectou ao enviar a pergunta.");
                                jogoAtivo = false;
                                break;
                            }

                            // ---- Menu do MESTRE responder ----
                            System.out.println("\n=== O jogador " + nomeAtual + " fez uma pergunta ===");
                            System.out.println("Pergunta: " + pergunta);
                            System.out.println("\nMenu do Mestre:");
                            System.out.println("1. Sim");
                            System.out.println("2. Não");
                            System.out.println("3. Talvez");
                            System.out.println("4. Não sei");
                            System.out.println("5. Não posso responder");
                            System.out.println("6. Relembrar palavra secreta");

                            String respostaMestre = "";
                            while (true) {
                                System.out.print("Escolha uma opção (1–6): ");
                                String opcao = mestreEntrada.readLine();
                                if (opcao == null) {
                                    opcao = "";
                                }
                                switch (opcao.trim()) {
                                    case "1":
                                        respostaMestre = "Sim";
                                        break;
                                    case "2":
                                        respostaMestre = "Não";
                                        break;
                                    case "3":
                                        respostaMestre = "Talvez";
                                        break;
                                    case "4":
                                        respostaMestre = "Não sei";
                                        break;
                                    case "5":
                                        respostaMestre = "Não posso responder";
                                        break;
                                    case "6":
                                        System.out.println(">>> A palavra secreta é: " + palavraSecreta);
                                        continue;
                                    default:
                                        System.out.println("Opção inválida!");
                                        continue;
                                }
                                break;
                            }

                            // envia para os dois jogadores
                            out1.println("O jogador " + nomeAtual + " perguntou: " + pergunta);
                            out2.println("O jogador " + nomeAtual + " perguntou: " + pergunta);
                            out1.println("MESTRE RESPONDE: " + respostaMestre);
                            out2.println("MESTRE RESPONDE: " + respostaMestre);

                            // depois da pergunta, passa a vez
                            repetirMenu = false;
                            break;

                        case "3":
                            // ---- Jogador tenta adivinhar ----
                            outAtual.println("Digite sua tentativa:");
                            String tentativa = inAtual.readLine();
                            if (tentativa == null) {
                                System.out.println("Jogador desconectou ao enviar a tentativa.");
                                jogoAtivo = false;
                                break;
                            }

                            System.out.println("\nTentativa recebida de " + nomeAtual + ": " + tentativa);

                            if (tentativa.equalsIgnoreCase(palavraSecreta)) {
                                String msgVitoria = "PARABÉNS, " + nomeAtual + "! VOCÊ ACERTOU!!!";
                                outAtual.println(msgVitoria);
                                outOutro.println("O jogador " + nomeAtual + " acertou a palavra!");
                                jogoAtivo = false;  // encerra jogo
                                repetirMenu = false;
                            } else {
                                outAtual.println("ERROU! A palavra não é essa.");

                                // ----- Função de frio ou quente -----
                                System.out.println("\n=== Menu de Aproximação ===");
                                System.out.println("1. Frio");
                                System.out.println("2. Morno");
                                System.out.println("3. Quente");
                                System.out.println("4. Quentíssimo");
                                System.out.println("5. Relembrar palavra secreta");

                                String temperatura = "";

                                while (true) {
                                    System.out.print("Escolha uma opção (1-5): ");
                                    String opcao = mestreEntrada.readLine();
                                    if (opcao == null) opcao = "";

                                    switch (opcao.trim()) {
                                        case "1": temperatura = "Frio"; break;
                                        case "2": temperatura = "Morno"; break;
                                        case "3": temperatura = "Quente"; break;
                                        case "4": temperatura = "Quentíssimo"; break;
                                        case "5":
                                            System.out.println(">>> A palavra secreta é: " + palavraSecreta);
                                            continue;
                                        default:
                                            System.out.println("Opção invalida!");
                                            continue;
                                    }
                                    break;
                                }

                                // envia para os dois jogadores
                                out1.println("O jogador " + nomeAtual + " chutou a palavra " + tentativa +
                                        ". O Mestre diz que essa palavra está " + temperatura + "!");
                                out2.println("O jogador " + nomeAtual + " chutou a palavra " + tentativa +
                                        ". O Mestre diz que essa palavra está " + temperatura + "!");

                                // depois do chute, passa a vez
                                repetirMenu = false;
                            }
                            break;

                        case "4":
                            // Passar a vez
                            outAtual.println("Você passou a vez.");
                            outOutro.println(nomeAtual + " passou a vez.");
                            repetirMenu = false;
                            break;

                        default:
                            outAtual.println("Opção inválida. Tente novamente.");
                            // repetirMenu continua true -> volta ao menu
                    }
                }

                // Se o jogo terminou dentro do menu, sai do laço principal
                if (!jogoAtivo) {
                    break;
                }

                // Alterna jogador
                jogadorDaVez = (jogadorDaVez == 1 ? 2 : 1);
            }

            System.out.println("Jogo encerrado.");
            out1.println("Jogo encerrado.");
            out2.println("Jogo encerrado.");

        } catch (IOException e) {
            System.out.println("Erro no servidor: " + e.getMessage());
        }
    }

    // Mostra regras e espera ENTER para voltar ao menu
    private static void mostrarRegras(PrintWriter out, BufferedReader in) throws IOException {
        out.println();
        out.println("===== REGRAS DO JOGO =====");
        out.println("1) O mestre escolhe uma palavra secreta.");
        out.println("2) Em sua vez, você pode:");
        out.println("   - Ver as regras do jogo;");
        out.println("   - Fazer uma pergunta ao mestre (Sim/Não/Talvez/etc.);");
        out.println("   - Tentar adivinhar a palavra;");
        out.println("   - Passar a vez.");
        out.println("3) Se você errar a palavra, o mestre informa se está FRIO, MORNO, QUENTE ou QUENTÍSSIMO.");
        out.println("4) Ganha quem adivinhar a palavra primeiro.");
        out.print("Pressione ENTER para voltar ao menu...");
        out.println("===========================");
        in.readLine(); // só para esperar o jogador
    }
}

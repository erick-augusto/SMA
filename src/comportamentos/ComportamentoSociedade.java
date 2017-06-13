/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comportamentos;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import smaprojeto.agentes.Sociedade;
import static comportamentos.ComportamentoCidade.*;
import static smaprojeto.agentes.Cidade.*;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import java.awt.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ufabc
 */
public class ComportamentoSociedade extends CyclicBehaviour {

    protected int direcaoTurnoAnterior;
    protected Random rnd;
    protected Sociedade agentPP;
    protected boolean ultimoRandom = false;
    public agenteFSM fsm = new agenteFSM();

    public ComportamentoSociedade(Sociedade agent) {
        super(agent);
        agentPP = agent;
        rnd = new Random();
    }

    @Override
    public void action() {
        recebeMsg();
        //block(10000);
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        direcaoTurnoAnterior = retornaDecisao();
        //System.out.printf("Agente %s(%d; %d) decidiu %s%s\n",
        //        myAgent.getLocalName(),
        //        agentPP.posX, agentPP.posY,
        //        DIRECOES[direcaoTurnoAnterior], ultimoRandom ? "(RND)" : "");

        enviaMsg(direcaoTurnoAnterior);
        //agentPP.turnoAtual++;

    }

    void recebeMsg() {
        ACLMessage mensagem = myAgent.blockingReceive();
        String msgRecebida = mensagem.getContent();
        //System.out.println("msg in: "+msgRecebida);
        //Atualizando as crencas
        String[] linhasMsgRecebida = msgRecebida.split("\n");
        //pos 0 indica se moveu ou nao

        if (linhasMsgRecebida[0].endsWith("false")) {
            direcaoTurnoAnterior = DIRECTION_NONE;
        } else {
            atualizaPosicaoInterna(direcaoTurnoAnterior);
        }
        for (int y = 1; y < linhasMsgRecebida.length; y++) {
            String[] colunasDeUmaLinha = linhasMsgRecebida[y].split(",");
            System.arraycopy(colunasDeUmaLinha, 0,
                    agentPP.mapaVisao[y - 1],
                    0, colunasDeUmaLinha.length);
        }

    }

    int retornaDecisao() {
        //caso da planta:
        /*if (agentPP.distancia_visao == 0) {
         return DIRECTION_NONE;
         }*/
        ArrayList<String> vizinhos = new ArrayList<String>();
        int direcao = 0;
        String[][] mapaVisao = agentPP.mapaVisao;

        int centerY = mapaVisao.length / 2;
        int centerX = mapaVisao[0].length / 2;

        //int agenteMaisPertoX = -1;
        //boolean agenteMaisPertoFugir = false;
        //int agenteMaisPertoY = -1;
        //int distanciaAgenteMaisPerto = Integer.MAX_VALUE;
        int count = 0;
        int y = 0;
        int x = 0;
        int realY = 0;
        int realX = 0;
        for (y = 0; y < mapaVisao.length; y++) {
            for (x = 0; x < mapaVisao[0].length; x++) {
                //System.out.print(mapaVisao[y][x]+" ");
                if ("0".equals(mapaVisao[y][x]) || "1".equals(mapaVisao[y][x])) {
                    //nao faz nada
                } else {
                    //System.out.println("Algo: "+mapaVisao[y][x]);
                    if (y == 0) {
                        realY = agentPP.posY - 1;
                    }
                    if (y == 2) {
                        realY = agentPP.posY + 1;
                    }
                    if (x == 0) {
                        realX = agentPP.posX - 1;
                    }
                    if (x == 2) {
                        realX = agentPP.posX + 1;
                    }
                    if (y == 1 && x != 1) {
                        realY = agentPP.posY;
                    }
                    if (y != 1 && x == 1) {
                        realX = agentPP.posX;
                    }
                    Sociedade p = mapa[realY][realX];
                    //Sociedade atual = mapa[agentPP.posY][agentPP.posX];
                    String s = "";
                    if (p != null) {
                        s = p.getStatus();
                        count++;
                        vizinhos.add(s);
                        //System.out.println("status "+p.getLocalName()+": "+s);
                    }
                    /*if ((s.equals("I") || s.equals("NQ")) && atual.getStatus().equals("S")) {
                     fsm.entradaStatus("C");
                     }*/

                    /*Boolean relacao = agentPP.relacaoAgentes.get(mapaVisao[y][x]);
                     if (relacao != null) {
                     if (relacao == Sociedade.CACAR) {
                     int distancia = (int) (Math.pow((x - centerY), 2)
                     + Math.pow((y - centerX), 2));
                     if (distancia < distanciaAgenteMaisPerto) {
                     distanciaAgenteMaisPerto = distancia;
                     agenteMaisPertoX = x;
                     agenteMaisPertoY = y;
                     agenteMaisPertoFugir = false;
                     }
                     } else if (relacao == Sociedade.FUGIR) {
                     int distancia = (int) (Math.pow((x - centerY), 2)
                     + Math.pow((y - centerX), 2));
                     if (distancia < distanciaAgenteMaisPerto) {
                     distanciaAgenteMaisPerto = distancia;
                     agenteMaisPertoX = x;
                     agenteMaisPertoY = y;
                     agenteMaisPertoFugir = true;
                     }
                     }
                     }*/
                }
            }
            //System.out.println("");
        }
        verificaStatus(mapa[agentPP.posY][agentPP.posX].getStatus(), vizinhos);
        String s = statusAtual;
        if ((s != null) && (!s.equals(mapa[agentPP.posY][agentPP.posX].getStatus()))) {
            mapa[agentPP.posY][agentPP.posX].setStatus(s);

            if (s.equals("S")) {
                //System.out.println("tentando mudar imagem...");
                mapa[agentPP.posY][agentPP.posX].setAvatar("res/S_state.png");
            } else if (s.equals("I")) {
                mapa[agentPP.posY][agentPP.posX].setAvatar("res/I_state.png");
            } else if (s.equals("Q")) {
                mapa[agentPP.posY][agentPP.posX].setAvatar("res/Q_state.png");
            } else if (s.equals("NQ")) {
                mapa[agentPP.posY][agentPP.posX].setAvatar("res/NQ_state.png");
            } else if (s.equals("M")) {
                mapa[agentPP.posY][agentPP.posX].setAvatar("res/M_state.png");
            } else if (s.equals("D")) {
                mapa[agentPP.posY][agentPP.posX].setAvatar("res/D_state.png");
            }
        }
        //System.out.println(mapa[agentPP.posY][agentPP.posX].getLocalName()+" "+s);

        /*if ((vizinhos.contains("I") || vizinhos.contains("NQ") && mapa[agentPP.posY][agentPP.posX].getStatus().equals("S"))) {
         fsm.entradaStatus("C");
         } else {
         fsm.entradaStatus(mapa[agentPP.posY][agentPP.posX].getStatus());
         }*/
        //System.out.println(mapa[agentPP.posY][agentPP.posX].getLocalName()+" "+mapa[agentPP.posY][agentPP.posX].getStatus());
        //System.out.println("cont: "+count);
        /*if (count > 0) {
         mapa[agentPP.posY][agentPP.posX].setStatus("I");
         //System.out.println("new status "+mapa[agentPP.posY][agentPP.posX].getName()+": "+mapa[agentPP.posY][agentPP.posX].getStatus());
         }*/
        if (!mapa[agentPP.posY][agentPP.posX].getStatus().equals("Q") && !mapa[agentPP.posY][agentPP.posX].getStatus().equals("D")) {
            direcao = geraDirecaoAleatoria(mapaVisao, centerY, centerX);
            ultimoRandom = true;
        }

        //if (agenteMaisPertoX != -1) {
        /*int distanciay = agenteMaisPertoY - centerY;
         int distanciax = agenteMaisPertoX - centerX;
         ultimoRandom = false;
         direcao = decidirDirecao(distanciax, distanciay, agenteMaisPertoFugir, false);
         if (verificaColisao(direcao, mapaVisao, centerY, centerX)) {
         direcao = decidirDirecao(distanciax, distanciay, agenteMaisPertoFugir, true);
         }
         if (verificaColisao(direcao, mapaVisao, centerY, centerX)) {
         direcao = geraDirecaoAleatoria(mapaVisao, centerY, centerX);
         ultimoRandom = true;
         }*/
        /*} else {
         direcao = geraDirecaoAleatoria(mapaVisao, centerY, centerX);
         ultimoRandom = true;
         }*/
        return direcao;
    }

    void atualizaPosicaoInterna(int direcao) {
        if (direcao == DIRECTION_UP) {
            agentPP.posY--;
        } else if (direcao == DIRECTION_DOWN) {
            agentPP.posY++;
        } else if (direcao == DIRECTION_RIGHT) {
            agentPP.posX++;
        } else if (direcao == DIRECTION_LEFT) {
            agentPP.posX--;
        }
    }

    /*private int decidirDirecao(int distanciax, int distanciay,
     boolean agenteMaisPertoFugir, boolean inverter) {
     int direcao;

     if (inverter ^ (Math.abs(distanciax)
     > Math.abs(distanciay))) {
     if (distanciax < 0) {
     //Agente esta a esquerda do centro
     if (!agenteMaisPertoFugir) {
     direcao = DIRECTION_LEFT;
     } else {
     direcao = DIRECTION_RIGHT;
     }
     } else {
     //Agente esta a direita do centro
     if (agenteMaisPertoFugir) {
     direcao = DIRECTION_LEFT;
     } else {
     direcao = DIRECTION_RIGHT;
     }
     }
     } else {
     if (distanciay > 0) {
     //Agente esta abaixo do centro
     if (!agenteMaisPertoFugir) {
     direcao = DIRECTION_DOWN;
     } else {
     direcao = DIRECTION_UP;
     }
     } else {
     //Agente esta acima do centro
     if (agenteMaisPertoFugir) {
     direcao = DIRECTION_DOWN;
     } else {
     direcao = DIRECTION_UP;
     }
     }
     }
     return direcao;
     }*/
    private boolean verificaColisao(int direction, String[][] mapaVisao, int centerY, int centerX) {
        if (direction == DIRECTION_UP
                && "1".equals(mapaVisao[centerY - 1][centerX])) {
            return true;
        } else if (direction == DIRECTION_DOWN
                && "1".equals(mapaVisao[centerY + 1][centerX])) {
            return true;
        } else if (direction == DIRECTION_LEFT
                && "1".equals(mapaVisao[centerY][centerX - 1])) {
            return true;
        } else if (direction == DIRECTION_RIGHT
                && "1".equals(mapaVisao[centerY][centerX + 1])) {
            return true;
        } else {
            return false;
        }
    }

    private void enviaMsg(int direcao) {
        ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);

        //Preencher os campos necesários da mensagem
        mensagem.setSender(myAgent.getAID());
        mensagem.addReceiver(new AID("Cidade", AID.ISLOCALNAME));
        mensagem.setContent(Integer.toString(direcao));
        myAgent.send(mensagem);
        //System.out.println("msg out: "+mensagem);
    }

    private int geraDirecaoAleatoria(String[][] mapaVisao, int centerY, int centerX) {
        //Gera uma direcao aleatoria
        LinkedList<Integer> direcoes = new LinkedList<Integer>();
        direcoes.add(DIRECTION_UP);
        direcoes.add(DIRECTION_DOWN);
        direcoes.add(DIRECTION_LEFT);
        direcoes.add(DIRECTION_RIGHT);

        //Se a direcao gerada for a oposta que andou no turno anterior,
        //ou a direção gerada for obstaculo,
        //tirar da lista de direcoes possiveis a serem geradas
        if (direcaoTurnoAnterior == DIRECTION_DOWN
                || verificaColisao(DIRECTION_UP, mapaVisao, centerY, centerX)) {
            direcoes.removeFirstOccurrence(DIRECTION_UP);
        }
        if (direcaoTurnoAnterior == DIRECTION_UP
                || verificaColisao(DIRECTION_DOWN, mapaVisao, centerY, centerX)) {
            direcoes.removeFirstOccurrence(DIRECTION_DOWN);
        }
        if (direcaoTurnoAnterior == DIRECTION_RIGHT
                || verificaColisao(DIRECTION_LEFT, mapaVisao, centerY, centerX)) {
            direcoes.removeFirstOccurrence(DIRECTION_LEFT);
        }
        if (direcaoTurnoAnterior == DIRECTION_LEFT
                || verificaColisao(DIRECTION_RIGHT, mapaVisao, centerY, centerX)) {
            direcoes.removeFirstOccurrence(DIRECTION_RIGHT);
        }

        int direcao;
        if (direcoes.size() == 0) {
            direcao = DIRECTION_NONE;
        } else {
            direcao = direcoes.get(rnd.nextInt(direcoes.size()));
        }
        return direcao;
    }

    public String statusAnterior;
    public String statusAtual;
    public int encubacao = 0;
    int quarentena = 0;

    public void verificaStatus(String status, ArrayList<String> vizinhos) {
        int NQ_State = 0;
        int I_State = 0;
        if (!vizinhos.isEmpty()) {
            for (String vizinho : vizinhos) {
                if (vizinho.equals("I")) {
                    I_State++;
                } else if (vizinho.equals("NQ")) {
                    NQ_State++;
                }
            }
        }
        if ((I_State > 0 || NQ_State > 0) && status.equals("S")) {
            //statusAnterior = "S";
            statusAtual = "C";
            ContatoBehaviour();
        } else if (status.equals("I")) {
            statusAnterior = "I";
            if (encubacao < 5) {
                encubacao++;
                statusAtual = "I";
            } else {
                InfectadoBehaviour(vizinhos.size());
            }
        } else if (status.equals("Q")) {
            if (quarentena < 7) {
                quarentena++;
            } else {
                QuarentenaBehaviour();
            }
        } else if (status.equals("NQ")) {
            NaoQuarentenaBehaviour();
        }
    }

    public void ContatoBehaviour() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        Long time = Long.parseLong(timeStamp);
        //System.out.println("data: "+timeStamp);
        int seed = (int) (long) time;
        //System.out.println("seed: "+seed);
        Random random = new Random(); //seed
        int valor = random.nextInt(100);
        //System.out.println("Valor: "+valor);
        //return getEntrada();
        if (valor < 50) {
            statusAtual = "S";
        } else {
            statusAtual = "I";
        }
    }

    public void InfectadoBehaviour(int vizinhos) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        Long time = Long.parseLong(timeStamp);
        //System.out.println("data: "+timeStamp);
        int seed = (int) (long) time;
        //System.out.println("seed: "+seed);
        SimpleDateFormat h = new SimpleDateFormat("HHmmss");
        Date hora = Calendar.getInstance().getTime();
        String tempo = h.format(hora);
        int t = Integer.parseInt(tempo);
        if (vizinhos == 0) {
            vizinhos = 1;
        }
        Random random = new Random(); //(seed * t) / vizinhos
        int valor = random.nextInt(100);
        //System.out.println("Valor Q: " + valor);
        //return getEntrada();
        if (valor < 60) {
            statusAtual = "Q";
        } else {
            statusAtual = "NQ";
        }
        encubacao = 0;
    }

    public void QuarentenaBehaviour() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        Long time = Long.parseLong(timeStamp);
        //System.out.println("data: "+timeStamp);
        int seed = (int) (long) time;
        //System.out.println("seed: "+seed);
        Random random = new Random();//seed
        int valor = random.nextInt(100);
        //System.out.println("Valor: "+valor);
        //return getEntrada();
        if (valor < 50) {
            statusAtual = "Q";
            quarentena--;
        } else {
            SimpleDateFormat h = new SimpleDateFormat("HHmmss");
            Date hora = Calendar.getInstance().getTime();
            String tempo = h.format(hora);
            time = Long.parseLong(tempo);
            //System.out.println("data: "+timeStamp);
            seed = (int) (long) time;
            //System.out.println("seed: "+seed);
            random = new Random(seed);
            valor = random.nextInt(100);
            if (valor < 85) {
                RecuperadoBehaviour();
            } else {
                statusAtual = "D";
            }
            quarentena = 0;
        }
    }

    public void NaoQuarentenaBehaviour() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        Long time = Long.parseLong(timeStamp);
        //System.out.println("data: "+timeStamp);
        int seed = (int) (long) time;
        //System.out.println("seed: "+seed);
        Random random = new Random();//seed
        int valor = random.nextInt(100);
        //System.out.println("Valor: " + valor);
        //return getEntrada();
        if (valor < 75) {
            statusAtual = "NQ";
        } else {
            SimpleDateFormat h = new SimpleDateFormat("HHmmss");
            Date hora = Calendar.getInstance().getTime();
            String tempo = h.format(hora);
            time = Long.parseLong(tempo);
            //System.out.println("data: " + tempo);
            seed = (int) (long) time;
            //System.out.println("seed: " + seed);
            random = new Random();//seed * valor
            valor = random.nextInt(100);
            //System.out.println("Valor: " + valor);
            if (valor < 65) {
                statusAtual = "D";
            } else {
                RecuperadoBehaviour();
            }
        }
    }

    public void RecuperadoBehaviour() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        Long time = Long.parseLong(timeStamp);
        //System.out.println("data: "+timeStamp);
        int seed = (int) (long) time;
        //System.out.println("seed: "+seed);
        SimpleDateFormat h = new SimpleDateFormat("HHmmss");
        Date hora = Calendar.getInstance().getTime();
        String tempo = h.format(hora);
        int t = Integer.parseInt(tempo);
        Random random = new Random();//seed * t
        int valor = random.nextInt(100);
        //System.out.println("Valor Q: " + valor);
        //return getEntrada();
        if (valor < 85) {
            statusAtual = "S";
        } else {
            statusAtual = "M";
        }
    }

    public class agenteFSM extends FSMBehaviour {

        private static final String S_STATE = "Sucetível";
        private static final String C_STATE = "Contato";
        private static final String I_STATE = "Infectado";
        private static final String Q_STATE = "Quarentena";
        private static final String NQ_STATE = "NãoQuarentena";
        private static final String R_STATE = "Recuperado";
        private static final String M_STATE = "Imunizado";
        private static final String D_STATE = "Morto";

        private final int S = 1;
        private final int C = 2;
        private final int I = 3;
        private final int Q = 4;
        private final int NQ = 5;
        private final int R = 6;
        private final int M = 7;
        private final int D = 8;

        private int transicao = 0;
        private String entrada = "";

        public agenteFSM() {

        }

        public void onStart() {
            registerFirstState(new SucetivelBehaviour(), S_STATE);
            registerState(new ContatoBehaviour(), C_STATE);
            registerState(new InfectadoBehaviour(), I_STATE);
            registerState(new QuarentenaBehaviour(), Q_STATE);
            registerState(new NaoQuarentenaBehaviour(), NQ_STATE);
            registerState(new RecuperadoBehaviour(), R_STATE);
            registerLastState(new MortoBehaviour(), D_STATE);
            registerLastState(new ImunizadoBehaviour(), M_STATE);

            registerTransition(S_STATE, C_STATE, C);
            registerTransition(C_STATE, I_STATE, I);
            registerTransition(C_STATE, S_STATE, S);
            registerTransition(I_STATE, I_STATE, I);
            registerTransition(I_STATE, Q_STATE, Q);
            registerTransition(I_STATE, NQ_STATE, NQ);
            registerTransition(Q_STATE, Q_STATE, Q);
            registerTransition(Q_STATE, R_STATE, R);
            registerTransition(Q_STATE, D_STATE, D);
            registerTransition(NQ_STATE, NQ_STATE, NQ);
            registerTransition(NQ_STATE, R_STATE, R);
            registerTransition(NQ_STATE, D_STATE, D);
            registerTransition(R_STATE, M_STATE, M);
            registerTransition(R_STATE, S_STATE, S);
        }

        public void entradaStatus(String status) {
            entrada = status;
        }

        protected boolean checkTermination(boolean currentDone, int currentResult) {
            System.out.println("** Terminado estado: " + currentName);
            System.out.println("-----------------------------");
            return super.checkTermination(currentDone, currentResult);
        }

        private class SucetivelBehaviour extends OneShotBehaviour {

            private boolean contato = false;

            public void action() {
                System.out.println("Agente está Sucetivel");
                /*try {
                 Thread.sleep(4000);
                 } catch (Exception e) {
                 System.out.println("Erro: " + e);
                 }*/
                //ACLMessage msg = myAgent.receive();
                /*if (msg != null) {
                 System.out.println("msg not null!");
                 String content = msg.getContent();
                 if (content.equalsIgnoreCase("Infectado")) {
                 contato = true;
                 }
                 } else {
                 System.out.println("msg null!");
                 block();
                 }*/
                if (entrada.equals("C")) {
                    contato = true;
                }
            }

            public int onEnd() {
                System.out.println("-----------------------------");
                //return getEntrada();
                if (contato) {
                    transicao = 2;
                }
                return transicao;
            }
        }

        private class ContatoBehaviour extends OneShotBehaviour {

            public void action() {
                System.out.println("Agente está em Contato");
            }

            public int onEnd() {
                System.out.println("-----------------------------");
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                Long time = Long.parseLong(timeStamp);
                //System.out.println("data: "+timeStamp);
                int seed = (int) (long) time;
                //System.out.println("seed: "+seed);
                Random random = new Random(seed);
                int valor = random.nextInt(100);
                //System.out.println("Valor: "+valor);
                //return getEntrada();
                if (valor < 50) {
                    transicao = 1;
                } else {
                    transicao = 3;
                }
                return transicao;
            }
        }

        private class InfectadoBehaviour extends OneShotBehaviour {

            private int encubacao = 0;
            private boolean periodo = false;

            public void action() {
                System.out.println("Agente está Infectado");

                /*ACLMessage msg = myAgent.receive();
                 if (msg != null && msg.getContent().equalsIgnoreCase("Dia")) {*/
                //String content = msg.getContent();
                encubacao++;
                if (encubacao == 5) {
                    periodo = true;
                    encubacao = 0;
                }
                //}
            }

            public int onEnd() {
                if (periodo) {
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                    Long time = Long.parseLong(timeStamp);
                    //System.out.println("data: "+timeStamp);
                    int seed = (int) (long) time;
                    //System.out.println("seed: "+seed);
                    SimpleDateFormat h = new SimpleDateFormat("HHmmss");
                    Date hora = Calendar.getInstance().getTime();
                    String tempo = h.format(hora);
                    int t = Integer.parseInt(tempo);
                    Random random = new Random(seed * t);
                    int valor = random.nextInt(100);
                    System.out.println("Valor Q: " + valor);
                    //return getEntrada();
                    if (valor < 60) {
                        transicao = 4;
                    } else {
                        transicao = 5;
                    }
                } else {
                    transicao = 3;
                }
                return transicao;
            }
        }

        private class QuarentenaBehaviour extends OneShotBehaviour {

            public void action() {
                System.out.println("Agente está em Quarentena");
            }

            public int onEnd() {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                Long time = Long.parseLong(timeStamp);
                //System.out.println("data: "+timeStamp);
                int seed = (int) (long) time;
                //System.out.println("seed: "+seed);
                Random random = new Random(seed);
                int valor = random.nextInt(100);
                //System.out.println("Valor: "+valor);
                //return getEntrada();
                if (valor < 50) {
                    transicao = 4;
                } else {
                    SimpleDateFormat h = new SimpleDateFormat("HHmmss");
                    Date hora = Calendar.getInstance().getTime();
                    String tempo = h.format(hora);
                    time = Long.parseLong(tempo);
                    //System.out.println("data: "+timeStamp);
                    seed = (int) (long) time;
                    //System.out.println("seed: "+seed);
                    random = new Random(seed);
                    valor = random.nextInt(100);
                    if (valor < 30) {
                        transicao = 8;
                    } else {
                        transicao = 6;
                    }
                }
                return transicao;
            }
        }

        private class NaoQuarentenaBehaviour extends OneShotBehaviour {

            public void action() {
                System.out.println("Agente não está em Quarentena");
            }

            public int onEnd() {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                Long time = Long.parseLong(timeStamp);
                //System.out.println("data: "+timeStamp);
                int seed = (int) (long) time;
                //System.out.println("seed: "+seed);
                Random random = new Random(seed);
                int valor = random.nextInt(100);
                System.out.println("Valor: " + valor);
                //return getEntrada();
                if (valor < 50) {
                    transicao = 4;
                } else {
                    SimpleDateFormat h = new SimpleDateFormat("HHmmss");
                    Date hora = Calendar.getInstance().getTime();
                    String tempo = h.format(hora);
                    time = Long.parseLong(tempo);
                    System.out.println("data: " + tempo);
                    seed = (int) (long) time;
                    System.out.println("seed: " + seed);
                    random = new Random(seed * valor);
                    valor = random.nextInt(100);
                    System.out.println("Valor: " + valor);
                    if (valor < 70) {
                        transicao = 8;
                    } else {
                        transicao = 6;
                    }
                }
                return transicao;
            }
        }

        private class RecuperadoBehaviour extends OneShotBehaviour {

            public void action() {
                System.out.println("Agente está Recuperado");
            }

            public int onEnd() {
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                Long time = Long.parseLong(timeStamp);
                //System.out.println("data: "+timeStamp);
                int seed = (int) (long) time;
                //System.out.println("seed: "+seed);
                SimpleDateFormat h = new SimpleDateFormat("HHmmss");
                Date hora = Calendar.getInstance().getTime();
                String tempo = h.format(hora);
                int t = Integer.parseInt(tempo);
                Random random = new Random(seed * t);
                int valor = random.nextInt(100);
                System.out.println("Valor Q: " + valor);
                //return getEntrada();
                if (valor < 70) {
                    transicao = 1;
                } else {
                    transicao = 7;
                }
                return transicao;
            }
        }

        private class MortoBehaviour extends OneShotBehaviour {

            public void action() {
                System.out.println("Agente está Morto");
            }

            public int onEnd() {
                System.out.println("-----------------------------");
                return 0;
            }
        }

        private class ImunizadoBehaviour extends OneShotBehaviour {

            public void action() {
                System.out.println("Agente está Imunizado");
            }

            public int onEnd() {
                System.out.println("-----------------------------");
                return 0;
            }
        }
    }
}

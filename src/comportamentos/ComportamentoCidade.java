/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comportamentos;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import smaprojeto.agentes.Sociedade;
import smaprojeto.agentes.Cidade;
import smaprojeto.agentes.Cidade.PosicaoPP;
import static smaprojeto.agentes.Cidade.mapa;

/**
 *
 * @author ufabc
 */
public class ComportamentoCidade extends CyclicBehaviour {

    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_UP = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_RIGHT = 4;
    public static final String[] DIRECOES = {"NONE", "UP", "DOWN", "LEFT", "RIGHT"};
    public String[][] relatorio = new String[200][500];
    public int index = 0;

    LinkedList<ACLMessage> mensagensRecebidas;
    HashMap<AID, Boolean> decisoes;
    long time;

    public ComportamentoCidade(Cidade a) {
        super(a);
        mensagensRecebidas = new LinkedList<ACLMessage>();

        decisoes = new HashMap<AID, Boolean>();

        for (String name : Cidade.listaPosicoes.keySet()) {
            decisoes.put(new AID(name, true), false);
        }
        time = System.currentTimeMillis();
    }

    @Override
    public void action() {
        //System.out.printf("Cycle time:%d\n", System.currentTimeMillis() - time);
        time = System.currentTimeMillis();
        enviaMensagens();
        recebeMensagens();
        tomaDecisao();
        /*
         for (int y = 0; y < Selva.mapa.length; y++) {
         for (int x = 0; x < Selva.mapa[0].length; x++) {
         if (Selva.mapa[y][x] != null
         && (Selva.mapa[y][x].posX != x
         || Selva.mapa[y][x].posY != y)) {
         System.out.println("inconsistencia de posicao");

         }
         }
         }
         */

        try {
            int timeToSleep = (int) (400L - System.currentTimeMillis() + time);

            if (timeToSleep > 0) {
                //System.out.printf("timeToSleep %d\n", timeToSleep);
                Thread.sleep(timeToSleep);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ComportamentoCidade.class.getName()).log(Level.SEVERE, null, ex);
        }

        Cidade.numTurno++;

    }

    //Ok
    void enviaMensagens() {

        for (Entry<String, PosicaoPP> nome_pos : Cidade.listaPosicoes.entrySet()) {

            PosicaoPP posicaoPP = nome_pos.getValue();
            int centerX = posicaoPP.posX;
            int centerY = posicaoPP.posY;
            // Criação do objeto ACLMessage
            ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);

            //Preencher os campos necesários da mensagem
            mensagem.setSender(myAgent.getAID());

            AID receiver = new AID(nome_pos.getKey(), AID.ISLOCALNAME);
            mensagem.addReceiver(receiver);

            StringBuilder sb = new StringBuilder();
            sb.append("moveu=").append(decisoes.get(receiver)).append("\n");
            for (int y = 0; y < posicaoPP.pp.distancia_visao * 2 + 1; y++) {
                for (int x = 0; x < posicaoPP.pp.distancia_visao * 2 + 1; x++) {
                    int posRealY = centerY - posicaoPP.pp.distancia_visao + y;
                    int posRealX = centerX - posicaoPP.pp.distancia_visao + x;
                    if (posRealY < 0 || posRealX < 0
                            || posRealY >= Cidade.mapa.length
                            || posRealX >= Cidade.mapa[0].length) {
                        sb.append("1");
                    } else if (Cidade.mapa[posRealY][posRealX] == null) {
                        sb.append("0");
                    } else {
                        sb.append(Cidade.mapa[posRealY][posRealX].getClass().getName());
                    }
                    sb.append(",");
                }
                sb.append("\n");
            }

            mensagem.setContent(sb.toString());
            myAgent.send(mensagem);
            //System.out.println("msg s out: "+mensagem);
        }
    }

    //OK
    private void recebeMensagens() {
        //mensagensRecebidas.clear();
        for (int numMsg = 0; numMsg < Cidade.listaPosicoes.size(); numMsg++) {
            ACLMessage mensagem = myAgent.blockingReceive();
            mensagensRecebidas.add(mensagem);
            //System.out.println("msg s in: "+mensagem);
        }
    }

    private void tomaDecisao() {
        decisoes.clear();
        Collections.shuffle(mensagensRecebidas);
        //PresaPredador novoMapa[][] =
        //        new PresaPredador[Selva.mapa.length][Selva.mapa[0].length];
        while (mensagensRecebidas.size() > 0) {
            ACLMessage mensagem = mensagensRecebidas.poll();
            int direcao = Integer.parseInt(mensagem.getContent());
            AID sender = mensagem.getSender();

            PosicaoPP pp = Cidade.listaPosicoes.get(sender.getLocalName());
            //ignorar mensagens de agentes mortos;
            if (pp == null) {
                continue;
            }
            int y = pp.posY, x = pp.posX;
            if (y == Cidade.mapa.length || x == Cidade.mapa[0].length) {
                System.out.println("1 - DEBUG HERE");
            }
            int novoX = x;
            int novoY = y;
            if (direcao == DIRECTION_UP) {
                novoY--;
            } else if (direcao == DIRECTION_DOWN) {
                novoY++;
            } else if (direcao == DIRECTION_RIGHT) {
                novoX++;
            } else if (direcao == DIRECTION_LEFT) {
                novoX--;
            }

            try {
                //Boolean relacao;
                if (!mapa[y][x].getStatus().equals("D")) {
                    //System.out.println(mapa[y][x].getStatus());
                    if (novoY < 0 || novoY >= Cidade.mapa.length
                            || novoX < 0 || novoX >= Cidade.mapa[0].length) {
                        //novoMapa[y][x] = Selva.mapa[y][x];
                        System.out.println("10 - DEBUG HERE");
                        decisoes.put(sender, Boolean.FALSE);
                    } else if (Cidade.mapa[novoY][novoX] == null /*|| (relacao = Cidade.mapa[y][x].relacaoAgentes.
                             get(Cidade.mapa[novoY][novoX].getClass().getName())) != null
                             && relacao == Sociedade.CACAR*/) {
                        /*if (Cidade.mapa[novoY][novoX] != null) {
                         //Cidade.mapa[y][x].ultimoTurnoComeu = Cidade.numTurno;
                         //Cidade.mapa[y][x].numComeu++;
                         //decisoes.remove(Cidade.mapa[novoY][novoX].getAID());
                         //Selva.removePresaPredador(Selva.mapa[novoY][novoX]);
                         }*/

                        Cidade.mapa[novoY][novoX] = Cidade.mapa[y][x];
                        Cidade.mapa[y][x] = null;
                        pp.posX = novoX;
                        pp.posY = novoY;
                        decisoes.put(sender, Boolean.TRUE);
                    } else {
                        decisoes.put(sender, Boolean.FALSE);
                    }
                } else {
                    //System.out.println(mapa[y][x].getName()+" "+mapa[y][x].getStatus());
                    decisoes.remove(Cidade.mapa[y][x].getAID());
                    Cidade.removePessoa(Cidade.mapa[y][x]);
                    mapa[y][x] = null;
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("2 - DEBUG HERE");
            }
        }
        //verificarEfeitosAlimentacao();
        criarRelatorio();
    }

    private void criarRelatorio() {
        int i = 0;
        Collection<PosicaoPP> posicoes = Cidade.listaPosicoes.values();
        LinkedList<Sociedade> mortos = new LinkedList<Sociedade>();
        if (index < 200) {
            //System.out.println("lista: "+posicoes.size());
            for (PosicaoPP posicao : posicoes) {
                Sociedade p = posicao.pp;
                //System.out.println("pessoa: "+p.getStatus());
                relatorio[index][i] = p.getStatus();
                //System.out.print(relatorio[index][i] + " ");
                if (p.getStatus().equals("D")) {
                    mortos.add(p);
                    //System.out.println("morto: "+p.getLocalName());
                }
                i++;
            }
            //System.out.println("");
            System.out.println("index: " + index);
        }
        index++;
        for (Sociedade morto : mortos) {
            decisoes.remove(morto.getAID());
            Cidade.removePessoa(morto);
        }
        int h = 0;
        int w = 0;
        int cont = 0;
        if(index >= 200){
            for(PosicaoPP posicao : posicoes){
                System.out.println("Agente "+posicao.pp.getName()+" deletado!");
                posicao.pp.doDelete();
                cont++;
            } 
            posicoes.clear();
            System.out.println(cont+" agentes apagados! Restando "+posicoes.size());
            myAgent.doDelete();
            System.out.println("Agente Cidade deletado!");
        }
        geraEstatisticas();
        /*if(index == 200){
            for(h=0; h<Cidade.tamanhoMapaH; h++){
                for(w=0; w<Cidade.tamanhoMapaW; w++){
                    System.out.print(mapa[h][w]+" ");
                }
                System.out.println("");
            }
        }*/
    }
    
    public void geraEstatisticas(){
        
    }

    /*private void verificarEfeitosAlimentacao() {
     Collection<PosicaoPP> posicoes = Selva.listaPosicoes.values();
     LinkedList<PresaPredador> agentesARemover = new LinkedList<PresaPredador>();
     LinkedList<PresaPredador> agentesAAdicionar = new LinkedList<PresaPredador>();
     for (PosicaoPP ppp : posicoes) {
     PresaPredador p = ppp.pp;
     if (Selva.numTurno - p.ultimoTurnoComeu > p.resistencia) {
     Selva.mapa[ppp.posY][ppp.posX] = null;
     agentesARemover.add(p);
     System.out.printf("%s morreu de fome!\n", p.getLocalName());
     }
     if (p.numComeu > p.comidaParaReproducao) {

     p.numComeu = 0;
     int x, y;
     do {
     x = Selva.rnd.nextInt(mapa[0].length);
     y = Selva.rnd.nextInt(mapa.length);
     //System.out.println("oops");
     } while (mapa[y][x] != null);
     try {
     agentesAAdicionar.add((PresaPredador) (p.getClass().getConstructors()[0].
     newInstance(p.name + "_filho" + (++p.numFilhos), x, y)));
     } catch (InstantiationException ex) {
     Logger.getLogger(ComportamentoSelva.class.getName()).log(Level.SEVERE, null, ex);
     } catch (IllegalAccessException ex) {
     Logger.getLogger(ComportamentoSelva.class.getName()).log(Level.SEVERE, null, ex);
     } catch (IllegalArgumentException ex) {
     Logger.getLogger(ComportamentoSelva.class.getName()).log(Level.SEVERE, null, ex);
     } catch (InvocationTargetException ex) {
     Logger.getLogger(ComportamentoSelva.class.getName()).log(Level.SEVERE, null, ex);
     }


     System.out.printf("%s teve um filho!\n", p.name);
     }
     }
     for (PresaPredador p : agentesARemover) {
     decisoes.remove(p.getAID());
     Selva.removePresaPredador(p);
     }

     for (PresaPredador p : agentesAAdicionar) {
     ((Selva) myAgent).adicionaPresaPredador(p);
     }

     }*/
}

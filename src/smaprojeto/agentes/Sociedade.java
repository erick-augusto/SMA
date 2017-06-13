/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smaprojeto.agentes;

import comportamentos.ComportamentoSociedade;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.util.leap.Serializable;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author ufabc
 */
public class Sociedade extends Agent implements Serializable {

    //public static final boolean FUGIR = false;
    //public static final boolean CACAR = true;
    public static final HashMap<String, Image> avatars = new HashMap<String, Image>();
    private Image avatar;
    public String mapaVisao[][];
    public int distancia_visao;
    public int posX, posY;
    public String name;
    public String status;
    //public String path;
    public HashMap<String, Boolean> relacaoAgentes;
    public int turnoAtual;
    //public int ultimoTurnoComeu;
    //public int numComeu;
    //public int resistencia;
    //public int comidaParaReproducao;
    //public int numFilhos;
    protected Behaviour behavior;

    @Override
    protected void setup() {
        addBehaviour(behavior);
        //Object[] args = getArguments();
        //this.posX = (Integer)args[0];
        //this.posY = (Integer)args[1];
        //this.distancia_visao = (Integer)args[2];
    }

    public Sociedade(String name, int distancia_visao, int posX, int posY, String status, String path//,
            /*String[] agentesParaFugir, String[] agentesParaCacar, int resistencia, int comidaParaReproducao*/) {

        this(name, distancia_visao, posX, posY, status, path,
                /*agentesParaFugir, agentesParaCacar, resistencia,
                comidaParaReproducao,*/ null);
    }

    public Sociedade(String name, int distancia_visao, int posX, int posY, String status, String path,
            /*String[] agentesParaFugir, String[] agentesParaCacar, int resistencia,
            int comidaParaReproducao,*/ Class<? extends Behaviour> behaviorClass) {
        this.name = name;
        this.distancia_visao = distancia_visao;
        this.posX = posX;
        this.posY = posY;
        this.status = status;
        //this.path = path;
        
        //relacaoAgentes = new HashMap<String, Boolean>();
        /*for (String agente : agentesParaFugir) {
            relacaoAgentes.put(agente, FUGIR);
        }
        for (String agente : agentesParaCacar) {
            relacaoAgentes.put(agente, CACAR);
        }*/

        mapaVisao = new String[distancia_visao * 2 + 1][distancia_visao * 2 + 1];
        avatar = avatars.get(path);
        if (avatar == null) {
            try {
                avatar = ImageIO.read(new File(path));
                avatars.put(path, avatar);
            } catch (IOException ex) {
                Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        turnoAtual = 0;
        //ultimoTurnoComeu = 0;
        //numComeu = 0;
        //this.resistencia = resistencia;
        //this.comidaParaReproducao = comidaParaReproducao;
        //numFilhos = 0;
        if (behaviorClass == null) {
            behaviorClass = ComportamentoSociedade.class;

        }
        try {
            behavior = (Behaviour) behaviorClass.getConstructors()[0].newInstance(this);
        } catch (InstantiationException ex) {
            Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Image getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String path){
        this.avatar = avatars.get(path);
        if (avatar == null) {
            try {
                avatar = ImageIO.read(new File(path));
                avatars.put(path, avatar);
            } catch (IOException ex) {
                Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
    /*public void setPath(String path){
        this.path = path;
        avatar = avatars.get(path);
        if (avatar != null) {
            try {
                avatar = ImageIO.read(new File(path));
                avatars.put(path, avatar);
            } catch (IOException ex) {
                Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void getPath(String path){
        avatar = avatars.get(path);
        if (avatar != null) {
            try {
                avatar = ImageIO.read(new File(path));
                avatars.put(path, avatar);
            } catch (IOException ex) {
                Logger.getLogger(Sociedade.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }*/
}

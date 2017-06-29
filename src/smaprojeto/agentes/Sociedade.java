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

    public static final HashMap<String, Image> avatars = new HashMap<String, Image>();
    private Image avatar;
    public String mapaVisao[][];
    public int distancia_visao;
    public int posX, posY;
    public String name;
    public String status;
    public HashMap<String, Boolean> relacaoAgentes;
    public int turnoAtual;
    protected Behaviour behavior;

    @Override
    protected void setup() {
        addBehaviour(behavior);
    }

    public Sociedade(String name, int distancia_visao, int posX, int posY, String status, String path) {
        this(name, distancia_visao, posX, posY, status, path, null);
    }

    public Sociedade(String name, int distancia_visao, int posX, int posY, String status, String path,
            Class<? extends Behaviour> behaviorClass) {
        this.name = name;
        this.distancia_visao = distancia_visao;
        this.posX = posX;
        this.posY = posY;
        this.status = status;

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
}

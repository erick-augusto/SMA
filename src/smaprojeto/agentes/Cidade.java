/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smaprojeto.agentes;

import comportamentos.ComportamentoCidade;
import jade.core.Agent;
import jade.wrapper.ContainerController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import smaprojeto.SMAProjeto;
import smaprojeto.agentes.pessoas.Infectado;
import smaprojeto.agentes.pessoas.Pessoa;

/**
 *
 * @author ufabc
 */
public class Cidade extends Agent {

    public class PosicaoPP {

        public int posX, posY;
        public Sociedade pp;

        private PosicaoPP(int posY, int posX, Sociedade p) {
            this.posX = posX;
            this.posY = posY;
            this.pp = p;
        }
    }
    ContainerController containerController;
    public static int numTurno = 0;
    public static final int tamanhoMapaH = 40;
    public static final int tamanhoMapaW = (int) (((float) tamanhoMapaH) / 9 * 16);
    public static Sociedade mapa[][];
    public static HashMap<String, PosicaoPP> listaPosicoes =
            new HashMap<String, PosicaoPP>(1000);
    public static Random rnd;

    @Override
    protected void setup() {
        containerController = this.getContainerController();
        mapa = new Sociedade[tamanhoMapaH][tamanhoMapaW];
        rnd = new Random();
        //System.out.println("w: "+tamanhoMapaW);
        for (int i = 0; i < 495; i++) {
            int x, y;
            do {
                x = rnd.nextInt(mapa[0].length);
                y = rnd.nextInt(mapa.length);
            } while (mapa[y][x] != null);
            adicionaPessoa(new Pessoa("Pessoa" + i, x, y));
            System.out.println("Pessoa"+i+" x:"+x+" y:"+y);
        }
        
        for (int i = 0; i < 5; i++) {
            int x, y;
            do {
                x = rnd.nextInt(mapa[0].length);
                y = rnd.nextInt(mapa.length);
            } while (mapa[y][x] != null);
            adicionaPessoa(new Infectado("Infectado" + i, x, y));
            System.out.println("Infectado"+i+" x:"+x+" y:"+y);
        }
        
        //addAgent(getContainerController(), "Sniffer", "jade.tools.sniffer.Sniffer",
        //        new Object[]{"Miau", ";", "Auau", ";", "Selva"});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cidade.class.getName()).log(Level.SEVERE, null, ex);
        }
        addBehaviour(new ComportamentoCidade(this));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void adicionaPessoa(Sociedade p) {
        //containerController = this.getContainerController();
        mapa[p.posY][p.posX] = p;
        PosicaoPP posicaoPP = new PosicaoPP(p.posY, p.posX, p);
        SMAProjeto.addExistingAgent(containerController, p.name,
                mapa[p.posY][p.posX]);
        listaPosicoes.put(p.getLocalName(), posicaoPP);

    }

    //Esta função NÃO retira a presapredador do mapa.
    public static void removePessoa(Sociedade p) {
        //ContainerController containerController = this.getContainerController();
        if (p == null) {
            return;
        }
        listaPosicoes.remove(p.getLocalName());
        p.doDelete();


    }


    private static void createAndShowGUI() {
        JFrame f = new JFrame("SMA: Modelo Epidemia");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 500);
        f.setVisible(true);
        MyPanel myPanel = new MyPanel(mapa, f);
        Thread thread = new Thread(myPanel);

        f.add(myPanel);
        f.pack();
        f.setVisible(true);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

    }
}

class MyPanel extends JPanel implements Runnable {

    Sociedade[][] mapa;
    JFrame frame;
    int mousex, mousey;

    public MyPanel(Sociedade[][] mapa, JFrame frame) {
        this.mapa = mapa;
        this.frame = frame;
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mousex = e.getX();
                mousey = e.getY();

            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1280, 720);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        float scaleY = getHeight() / mapa.length;
        float scaleX = getWidth() / mapa[0].length;
        float scale = scaleX < scaleY ? scaleX : scaleY;
        g2d.setColor(Color.LIGHT_GRAY);
        for (int y = 0; y < mapa.length; y++) {
            for (int x = 0; x < mapa[0].length; x++) {
                g2d.fillRect((int) (x * scale), (int) (y * scale), (int) scale, (int) scale);
                if (mapa[y][x] != null) {
                    g2d.drawImage(mapa[y][x].getAvatar(), (int) (x * scale), (int) (y * scale), (int) scale, (int) scale, null);
                }
            }
        }

        g2d.setColor(Color.black);
        for (int y = 0; y <= mapa.length; y++) {
            g2d.drawLine(0, (int) (y * scale), (int) (mapa[0].length * scale), (int) (y * scale));
        }
        for (int x = 0; x <= mapa[0].length; x++) {
            g2d.drawLine((int) (x * scale), 0, (int) (x * scale), (int) (mapa.length * scale));
        }

        //g2d.drawString(String.valueOf(scale), 50, 50);
        g2d.setColor(Color.red);
        for (int y = 0; y < mapa.length; y++) {
            for (int x = 0; x < mapa[0].length; x++) {

                if (mapa[y][x] != null && mousex > (int) (x * scale) && mousey > (int) (y * scale)
                        && mousex < (int) ((x + 1) * scale) && mousey < (int) ((y + 1) * scale)) {
                    g2d.drawString(mapa[y][x].getLocalName(), mousex, mousey);
                }
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            //System.out.println("Pintou");
            frame.repaint();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

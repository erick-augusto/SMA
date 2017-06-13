package smaprojeto.agentes.pessoas;

import smaprojeto.agentes.Sociedade;

public class Infectado extends Sociedade {
    public Infectado(String name, int posX, int posY) {
        super(name, 1, posX, posY, "I", "res/I_state.png"//,
                /*new String[]{},
                new String[]{"smaprojeto.agentes.presaspredadores.Gato"},200,3*/);
    }
}

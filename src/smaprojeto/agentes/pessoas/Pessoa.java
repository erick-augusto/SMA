package smaprojeto.agentes.pessoas;

import smaprojeto.agentes.Sociedade;

public class Pessoa extends Sociedade {
    public Pessoa(String name, int posX, int posY) {
        super(name, 1, posX, posY, "S", "res/S_state.png"//,
                /*new String[]{},
                new String[]{"smaprojeto.agentes.presaspredadores.Gato"},200,3*/);
    }
}

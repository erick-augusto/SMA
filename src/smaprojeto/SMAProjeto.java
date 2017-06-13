package smaprojeto;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import smaprojeto.agentes.Cidade;

public class SMAProjeto {

    static ContainerController containerController;
    static AgentController agentController;

    public static void main(String[] args) throws InterruptedException {
        //iniciando main container
        //startMainContainer(Profile.LOCAL_HOST, Profile.LOCAL_PORT, "UFABC");
        startMainContainer("127.0.0.1", Profile.LOCAL_PORT, "UFABC");
        //adicionando agente
        //SINTAXE: addAgent(container, nome_do_agente, classe, parametros de inicializacao)
        //addAgent(containerController, "Selva", Cidade.class.getName(), null);
        addAgent(containerController, "Cidade", Cidade.class.getName(), null);
        
        //adicionando agente RMA
        //addAgent(containerController, "rma", "jade.tools.rma.rma", null);
        //addAgent(containerController, "rma", jade.tools.rma.rma.class.getName(), null);

    }

    public static void startMainContainer(String host, String port, String name) {
        jade.core.Runtime runtime = jade.core.Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        profile.setParameter(Profile.PLATFORM_ID, name);

        containerController = runtime.createMainContainer(profile);
    }

    public static void addAgent(ContainerController cc, String agent, String classe, Object[] args) {
        try {
            //agentController = cc.createNewAgent(agent, classe, args);
            agentController = cc.createNewAgent(agent, classe, args);
            agentController.start();
        } catch (StaleProxyException s) {
            s.printStackTrace();
        }
    }

    public static void addExistingAgent(ContainerController cc,
            String nickname, Agent agent) {
        //long time = System.currentTimeMillis();
        
        try {
            //agentController = cc.createNewAgent(agent, classe, args);
            agentController = cc.acceptNewAgent(nickname, agent);
            
            agentController.start();
            //System.out.printf("Time to add agent: %d\n", System.currentTimeMillis() - time);
        } catch (StaleProxyException s) {
            System.out.println("error!");
            s.printStackTrace();
        }
        //System.out.printf("agente adicionado: %d\n", System.currentTimeMillis() - time);
    }
}
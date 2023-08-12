package TCC;

import java.lang.System;

import java.util.ArrayList;
import java.util.List;

import main.grammar.Description;
import main.grammar.Token;

public class BuscaLocal {
	
	final static boolean debug = false;
	
	final static List<String> listaNomesJogos = Funcoes.carregarListaJogos();
	final static List<String> listaAI = Funcoes.carregarListaAI();
	final static List<Description> jogosCarregados = Funcoes.carregarListaJogos(listaNomesJogos);
	
	public static void alterarParametro(Description description) {
		//Funcoes.imprimirTokenTree(jogosCarregados.get(0));
		//jogosCarregados.get(2).callTree().ludemeFormat(20).toString().toString();
		//for(Token token : description.tokenForest().tokenTrees()) {
			//if(token.isTerminal()) {
				//System.out.println(token.arguments().toString().toString());
			//}
		//}
	}
	
	public static void realizarExperimento(String nomeAI) {
		Funcoes.preencherListaTerminais(jogosCarregados);
		
		final String diretorioExperimento = Funcoes.criarDiretorioExperimento();
		if(diretorioExperimento == "") {
			return;
		}
		
		//for(int indiceJogo = 0; indiceJogo < 1; indiceJogo++) {
		for(int i = 0; i < 2; i++) {
			Description descricaoJogo = jogosCarregados.get(0);
			//Funcoes.alterarCallTree(Funcoes.obterCallTree(descricaoJogo));
			if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), jogosCarregados.get(0), diretorioExperimento)) {
				
			}
			Funcoes.criarAnalise(jogosCarregados.get(0), nomeAI, 1, 40);
		}
		//}
		

		//Funcoes.mostrarListaTerminais();
		
		//alterarParametro(jogosCarregados.get(0));
		//System.out.println(jogosCarregados.get(0).callTree().ludemeFormat(10));
		
	}

	public static void main(String[] args) {
		String nomeAI = "UCT";
		
		if (debug) {
			nomeAI = Funcoes.selecionarAI();
		}
		
		long tempoInicial = System.nanoTime();
		realizarExperimento(nomeAI);
		long tempoExecucao = System.nanoTime() - tempoInicial;
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000 + " s");
	}

}

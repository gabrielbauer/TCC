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
	final static ArrayList<Description> jogosCarregados = Funcoes.carregarListaJogos(listaNomesJogos);
	
	public static void alterarParametro(Description description) {
		Funcoes.imprimirTokenTree(jogosCarregados.get(0));
		jogosCarregados.get(0).callTree().ludemeFormat(20).toString().toString();
		for(Token token : description.tokenForest().tokenTrees()) {
			if(token.isTerminal()) {
				//System.out.println(token.arguments().toString().toString());
			}
		}
	}

	public static void main(String[] args) {
		String nomeAI = "UCT";
		
		if (debug) {
			nomeAI = Funcoes.selecionarAI();
		}
			
		//System.out.println("AI selecionada: " + nomeAI);
		
		long tempoInicial = System.nanoTime();
		
		System.out.println("Preenchendo a lista com terminais de " + jogosCarregados.size() + " jogos");
		Funcoes.preencherListaTerminais(jogosCarregados);
		//Funcoes.mostrarListaTerminais();
		Funcoes.alterarCallTree(jogosCarregados.get(0).callTree());
		//alterarParametro(jogosCarregados.get(0));
		System.out.println(jogosCarregados.get(0).callTree().ludemeFormat(10));
		
		Funcoes.salvarJogo("PORFAVORFUNCIONA", jogosCarregados.get(0));
		long tempoExecucao = System.nanoTime() - tempoInicial;
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000 + " s");
	}

}

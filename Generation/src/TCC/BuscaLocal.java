package TCC;

import java.lang.System;

import java.util.ArrayList;
import java.util.List;

import main.grammar.Description;
import main.grammar.Token;

public class BuscaLocal {
	
	final static List<String> listaNomesJogos = Funcoes.carregarListaJogos();
	final static List<String> listaAI = Funcoes.carregarListaAI();
	final static ArrayList<Description> jogosCarregados = Funcoes.carregarListaJogos(listaNomesJogos);
	
	public static void alterarParametro(Description description) {
		Funcoes.imprimirTokenTree(jogosCarregados.get(0));
		for(Token token : description.tokenForest().tokenTrees()) {
			if(token.isTerminal()) {
				//System.out.println(token.arguments().toString().toString());
			}
		}
	}

	public static void main(String[] args) {
		
		final String nomeAI = Funcoes.selecionarAI();
		System.out.println("AI selecionada: " + nomeAI);
		
		long tempoInicial = System.nanoTime();
		
		System.out.println("Preenchendo a lista com terminais de " + jogosCarregados.size() + " jogos");
		Funcoes.preencherListaTerminais(jogosCarregados);
		//Funcoes.mostrarListaTerminais();
		
		long tempoExecucao = System.nanoTime() - tempoInicial;
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000 + " s");
	}

}

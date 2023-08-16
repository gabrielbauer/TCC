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
	
	public static Description alterarParametro(Description descricao) {
		//System.out.println("{\n\tkey: " + elemento.toString() + "\n\tvalue: " + elemento.split(":")[1] + ",\n},");
		
		final List<Object> listaTerminais = Funcoes.obterListaTerminaisJogo(Funcoes.obterCallTree(descricao));
		
		//System.out.println("**************************************");
		//System.out.println("[TCC] Call tree incial:");
		//Funcoes.percorrerCallTree(Funcoes.obterCallTree(descricao));
		int posicao = Funcoes.escolherNumeroIntervaloLista(listaTerminais.size());
		//System.out.println("[TCC] Terminal ["+ posicao +"] alterado: " + listaTerminais.get(posicao));
		final List<Object> listaTerminaisIguais = new ArrayList<Object>();
		Object elementoEscolhido = listaTerminais.get(posicao);
		for(Object elemento : listaTerminais) {
			if(elementoEscolhido.equals(elemento)) {
				listaTerminaisIguais.add(elemento);
			}
		}
		/*for(int indice = 0; indice < listaTerminais.size(); indice++) {
			if(listaTerminais.get(indice) == elementoEscolhido) {
				System.out.println("[TCC] listaTerminais [" + indice + "]: " + listaTerminais.get(indice));
			}
		}*/
		//System.out.println("[TCC] Elemento a ser alterado: " + posicao);
		int terminalAlterado = Funcoes.escolherNumeroIntervaloLista(listaTerminaisIguais.size());
		descricao.setCallTree(Funcoes.alterarTerminalCallTree(Funcoes.obterCallTree(descricao), elementoEscolhido, terminalAlterado));
		//System.out.println("[TCC] Call tree final:");
		Funcoes.percorrerCallTree(Funcoes.obterCallTree(descricao));
		//System.out.println("**************************************");
		
		return descricao;
	}
	
	public static void realizarExperimento(Description descricao, String nomeAI) {
		
		int limiteBusca = 10;
		
		final String diretorioExperimento = Funcoes.criarDiretorioExperimento();
		if(diretorioExperimento == "") {
			return;
		}
		
		Description descricaoAtual = descricao;
		Funcoes.criarAnalise(descricaoAtual, nomeAI, 5, 40);
		float scoreAtual = Funcoes.carregarScoreAvaliacao();
		
		if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), descricaoAtual, diretorioExperimento)) {
			
		}
		
		System.out.println("Score inicial: " + scoreAtual);
		
		int buscaAtual = 0;
		float scoreBusca = 0;
		
		while(scoreBusca < scoreAtual || buscaAtual < limiteBusca) {
			Description descricaoBusca = alterarParametro(descricao);
			if(Funcoes.salvarJogo(Funcoes.gerarNomeArquivo(), descricaoBusca, diretorioExperimento)) {
				
			}
			Funcoes.criarAnalise(descricaoBusca, nomeAI, 5, 40);
			scoreBusca = Funcoes.carregarScoreAvaliacao();
			System.out.println("Score [" +buscaAtual+ "]: "+ scoreBusca);
			if(scoreBusca > scoreAtual) {
				descricao = descricaoBusca;
				scoreAtual = scoreBusca;
			}
			buscaAtual++;
		}
				
	}

	public static void main(String[] args) {
		String nomeAI = "UCT";
		
		if (debug) {
			nomeAI = Funcoes.selecionarAI();
		}
		
		Description descricao = jogosCarregados.get(0);

		long tempoInicial = System.nanoTime();
		
		//alterarParametro(descricao);
		realizarExperimento(descricao, nomeAI);
		//Funcoes.percorrerCallTree(Funcoes.obterCallTree(descricao));
		
		//final List<Object> listaTerminais = Funcoes.obterListaTerminaisJogo(Funcoes.obterCallTree(descricao));
		
		long tempoExecucao = System.nanoTime() - tempoInicial;
		
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000 + " s");
	}

}

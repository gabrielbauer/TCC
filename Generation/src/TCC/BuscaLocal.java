package TCC;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import main.grammar.Description;
import main.grammar.Report;
import metrics.Evaluation;
import metrics.Metric;
import java.lang.System;

public class BuscaLocal {
	
	final static List<String> listaNomesJogos = Funcoes.carregarListaJogos();
	final static List<String> listaAI = Funcoes.carregarListaAI();
	final static ArrayList<Description> jogosCarregados = Funcoes.carregarListaJogos(listaNomesJogos);
	//get(0) = UCT
	final static String nomeAI = listaAI.get(0);
	
	public static void main(String[] args) {
		
		int posicao = 0;
		long tempoInicial = System.nanoTime();
		for (final Description description : jogosCarregados) {
		
			final Game game = Funcoes.carregarJogoPorDescricao(description);
			
			//System.out.println(game.description().tokenForest().tokenTrees().toString());
			System.out.println("Avaliando: " + listaNomesJogos.get(posicao));
			{
				final Evaluation evaluation = new Evaluation();
				final Report report = new Report();
				final List<String> options = new ArrayList<String>();
				final int numberTrials = 1;
				final int maxTurns = 60;
				final double thinkTime = 0.5;
				final List<Metric> metricsToEvaluate = new Evaluation().dialogMetrics();
				final ArrayList<Double> weights = new ArrayList<Double>();
				final boolean useDatabaseGames = false;
				
				Funcoes.avaliarJogo(game, evaluation, report, options, numberTrials, maxTurns, thinkTime,
						metricsToEvaluate, weights, nomeAI, useDatabaseGames);
				
			}
			posicao = posicao + 1;
			System.out.println("------------------------------------------------------------------------------");
		}
		long tempoExecucao = System.nanoTime() - tempoInicial;
		System.out.println("Tempo decorrido: " + tempoExecucao/1_000_000_000);
	}

}

package cilabo.labo.developing.twostage;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import cilabo.gbml.algorithm.HybridMoFGBMLwithNSGAII;
import cilabo.gbml.solution.pittsburghSolution.PittsburghSolution;
import cilabo.util.fileoutput.PittsburghSolutionListOutput;

/**
 *
 * cilabo.gbml.algorithm.HybridMoFGBMLwithNSGAII を参考に作成
 * 主にrun()の中身を書き換える
 *
 * 2ndステージの初期個体群は1stステージの最終個体群を基に決定する（デフォルトではそのまま渡す）
 * -> createInitialPopulation()の中身を書き換えるのはどうか？（現在は違う仕様）
 * 評価個体数を1stの終了時から始めたい -> run()をOverrideする必要があるため、継承して作成．
   */
public class SecondStageAlgorithm<S extends PittsburghSolution<?>> extends HybridMoFGBMLwithNSGAII<S> {

	/** Constructor */
	public SecondStageAlgorithm(
			/* Arguments */
			Problem<S> problem,
			int populationSize,
			int offspringPopulationSize,
			int frequency,
			String outputRootDir,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator,
			Termination termination,
			/* Requirments for 2nd algorithm */
			//List<S> population, int nowEvaluations
			/* Requirments for 2nd algorithm (if you use archive) */
			List<S> population, int nowEvaluations, Set<S> Archivepopulation
			)
	{
		super(problem, populationSize, offspringPopulationSize, frequency, outputRootDir,
				crossoverOperator, mutationOperator, termination);
		setPopulation(population);
		setEvaluations(nowEvaluations);
		/*1stから受け継いだアーカイブを2ndのアーカイブにセット*/
		setArchivePopulation(Archivepopulation);
	}

	@Override
	protected void initProgress() {
		getAlgorithmStatusData().put("EVALUATIONS", getEvaluations());
		getAlgorithmStatusData().put("POPULATION", getPopulation());
		getAlgorithmStatusData().put("COMPUTING_TIME", System.currentTimeMillis() - getstartTime());

		getObservable().setChanged();
		getObservable().notifyObservers(getAlgorithmStatusData());
	}

	@Override
	public void run() {

		/* === START === */
		List<S> offspringPopulation;
		List<S> matingPopulation;

		initProgress();
/**************************************************************************/
//		/* Step 1. 初期個体群生成 - Initialization Population */
//		population = createInitialPopulation();
//		/* Step 2. 初期個体群評価 - Initial Population Evaluation */
//		population = evaluatePopulation(population);
//		/* JMetal progress initialization */
//		initProgress();
/**************************************************************************/

		/* GA loop */
		while(!isStoppingConditionReached()) {
			/* 親個体選択 - Mating Selection */
			matingPopulation = selection(population);
			/* 子個体群生成 - Offspring Generation */
			offspringPopulation = reproduction(matingPopulation);
			/* 子個体群評価 - Offsprign Evaluation */
			offspringPopulation = evaluatePopulation(offspringPopulation);
			/* 未勝利個体削除*/
			offspringPopulation = removeNoWinnerMichiganSolution(offspringPopulation);
			/* 個体群更新・環境選択 - Environmental Selection */
			population = replacement(population, offspringPopulation);

			/*更新した個体群をアーカイブに追加*/
			for (S solution : population) {
			    PittsburghSolution<?> copiedSolution = solution.copy();
			    this.getArchivePopulation().add((S) copiedSolution);
			}

			/*アーカイブから非劣解を抽出（ただし，計算量大きくなるのでコメントアウト）*/
			/*使用する際は，SetからListへの変換が必要*/
			//this.setArchivePopulation(SolutionListUtils.getNonDominatedSolutions(this.getArchivePopulation()));

			/* JMetal progress update */
			updateProgress();
		}

		/* ===  END  === */
		setTotalComputingTime(System.currentTimeMillis() - getstartTime());
	}

	@Override
	protected void updateProgress() {
		setEvaluations((int) (getEvaluations() + getOffspringPopulationSize()));
		getAlgorithmStatusData().put("EVALUATIONS", getEvaluations());
		getAlgorithmStatusData().put("POPULATION", getPopulation());
		getAlgorithmStatusData().put("COMPUTING_TIME", System.currentTimeMillis() - getstartTime());

		getObservable().setChanged();
		getObservable().notifyObservers(getAlgorithmStatusData());

		//Output for each generation;
		String sep = File.separator;
		Integer evaluations = (Integer)getAlgorithmStatusData().get("EVALUATIONS");
	    if(evaluations != null) {
	    	if(evaluations * 10 % getFrequency() == 0 && evaluations % getFrequency() != 0) System.out.print(". ");

	    	if(evaluations % getFrequency() == 0) {
	    		System.out.print(" ->");
	    		for(int i=0; i<getPopulation().get(0).getNumberOfObjectives(); i++) {
	    			double tmp=0;
	    			for(int j=0; j<getPopulation().size(); j++) {
	    				tmp += getPopulation().get(j).getObjective(i);
	    			}
	    			tmp /= getPopulation().size();
		    		System.out.print(String.format("objectives[%d]: %.8f.. ", i, tmp));
	    		}
	    		System.out.println(); System.out.println();

	    		/*出力された数値が0埋めで同じ桁数になるversion*/
	    	    /*new PittsburghSolutionListOutput((List<PittsburghSolution<?>>) this.getResult())
		            .setVarFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("VAR-%010d.csv", evaluations), ","))
		            .setFunFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("FUN-%010d.csv", evaluations), ","))
		            .print();*/

	    		/*出力された数値が0埋めされないversion*/
	    	    new PittsburghSolutionListOutput((List<PittsburghSolution<?>>) this.getResult())
	            .setVarFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("VAR-%d.csv", evaluations), ","))
	            .setFunFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("FUN-%d.csv", evaluations), ","))
	            .print();

	    	    /*frequencyごとにアーカイブ出力（容量重すぎるのでいったんコメントアウト）*/
	    	    /*使用する際は，SetからListへの変換が必要*/
	    	    /*new PittsburghSolutionListOutput((List<PittsburghSolution<?>>) this.getArchivePopulation())
	            .setVarFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("VARARC-%d.csv", evaluations), ","))
	            .setFunFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("FUNARC-%d.csv", evaluations), ","))
	            .print();*/

	    	    /* "切り替え世代 + frequency" 時のアーカイブ情報（必要か不明なので一旦コメントアウト）*/
	    	    /*使用する際は，SetからListへの変換が必要*/
	    	    /*if(evaluations == CommandLineArgs.changeStageSetting + getFrequency()) {
	    	        new PittsburghSolutionListOutput((List<PittsburghSolution<?>>) this.getArchivePopulation())
                    .setVarFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("VARARCCS-%d.csv", evaluations), ","))
                    .setFunFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("FUNARCCS-%d.csv", evaluations), ","))
                    .print();
	    	    }*/

	    	    /*最終的なアーカイブを出力（FUNARCはresultsでカバーできるので不要）（非劣解抽出前なので，コメントアウト）*/
	    	    /*使用する際は，SetからListへの変換が必要*/
	    	    /*if(evaluations == Consts.TERMINATE_EVALUATION) {
	    	        new PittsburghSolutionListOutput((List<PittsburghSolution<?>>) this.getArchivePopulation())
                    .setVarFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("VARARC-%d.csv", evaluations), ","))
                    .print();
	    	    }*/

	    		/*Element population = XML_manager.createElement(XML_TagName.population);

	    		for(S solution: this.getResult()) {
	    			Element pittsburghSolution = solution.toElement();
	    			XML_manager.addElement(population, pittsburghSolution);
	    		}

	    		Element evaluationsElement = XML_manager.createElement(XML_TagName.evaluations, XML_TagName.evaluation, String.valueOf(evaluations));

	    		XML_manager.addElement(evaluationsElement, population);
		    	XML_manager.addElement(XML_manager.getRoot(), evaluationsElement);*/
	    	}
	    }
		else {
			JMetalLogger.logger.warning(getClass().getName()
			+ ": The algorithm has not registered yet any info related to the EVALUATIONS key");
		}
	}
}


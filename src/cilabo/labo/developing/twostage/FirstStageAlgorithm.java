package cilabo.labo.developing.twostage;

import java.io.File;
import java.util.List;

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
 * Terminationを使って、ステージの切り替えを行う
 */
public class FirstStageAlgorithm<S extends PittsburghSolution<?>> extends HybridMoFGBMLwithNSGAII<S> {

	/** Constructor */
	public FirstStageAlgorithm(


			/* Arguments */
			Problem<S> problem,
			int populationSize,
			int offspringPopulationSize,
			int frequency,
			String outputRootDir,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator,
			Termination termination)
	{
		super(problem, populationSize, offspringPopulationSize, frequency, outputRootDir,
				crossoverOperator, mutationOperator, termination);
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

	    	    /*切り替え時のアーカイブ情報（2ndで出力される情報で十分なので，コメントアウト）*/
	    	    /*使用する際は，SetからListへの変換が必要*/
	    	    /*if(evaluations == CommandLineArgs.changeStageSetting) {
	    	        new PittsburghSolutionListOutput((List<PittsburghSolution<?>>) this.getArchivePopulation())
                    .setVarFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("VARARCCS-%d.csv", evaluations), ","))
                    .setFunFileOutputContext(new DefaultFileOutputContext(getOutputRootDir() + sep + String.format("FUNARCCS-%d.csv", evaluations), ","))
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
		else {			JMetalLogger.logger.warning(getClass().getName()
			+ ": The algorithm has not registered yet any info related to the EVALUATIONS key");
		}
	}

}

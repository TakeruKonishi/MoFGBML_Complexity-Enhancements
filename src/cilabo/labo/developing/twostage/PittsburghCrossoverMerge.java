package cilabo.labo.developing.twostage;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
//import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import cilabo.gbml.solution.michiganSolution.MichiganSolution;
import cilabo.gbml.solution.michiganSolution.impl.MichiganSolution_Basic;
import cilabo.gbml.solution.pittsburghSolution.PittsburghSolution;
import cilabo.gbml.solution.util.attribute.NumberOfWinner;
import cilabo.main.Consts;

/**
 * cilabo.gbml.operator.crossover.PittsburghCrossover を参考に作成
 * CrossoverOperatorをimplements
 *
 * 2つの親個体をマージして子個体を生成する
 */
public class PittsburghCrossoverMerge <pittsburghSolution extends PittsburghSolution<michiganSolution>,michiganSolution extends MichiganSolution<?>>
    implements CrossoverOperator<pittsburghSolution> {
    private double crossoverProbability;
    private RandomGenerator<Double> crossoverRandomGenerator;
    BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public PittsburghCrossoverMerge(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghCrossoverMerge(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghCrossoverMerge(
			double crossoverProbability,
			RandomGenerator<Double> crossoverRandomGenerator,
			BoundedRandomGenerator<Integer> selectRandomGenerator) {
		if(crossoverProbability < 0) {
			throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
		}
		this.crossoverProbability = crossoverProbability;
		this.crossoverRandomGenerator = crossoverRandomGenerator;
		this.selectRandomGenerator = selectRandomGenerator;
	}

	/* Getter */
	@Override
	public double getCrossoverProbability() {
		return this.crossoverProbability;
	}

	/* Setter */
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}

	@Override
	public List<pittsburghSolution> execute(List<pittsburghSolution> solutions) {
		Check.isNotNull(solutions);
		Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());
		return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
	}

	/**
	 * 後件部の学習はここでは行わない
	 * @param probability
	 * @param _parent1
	 * @param _parent2
	 * @return
	 */
	public List<pittsburghSolution> doCrossover(double probability, pittsburghSolution parent1, pittsburghSolution parent2) {
		// Cast IntegerSolution to PittsburghSolution
		if(parent1.getNumberOfVariables() < 1) {System.err.println("incorrect input: number Of Rules is less than 1@" + this.getClass().getSimpleName());}
		if(parent2.getNumberOfVariables() < 1) {System.err.println("incorrect input: number Of Rules is less than 1@" + this.getClass().getSimpleName());}
		List<pittsburghSolution> offspring = new ArrayList<>();

		/* Do crossover */
		if(crossoverRandomGenerator.getRandomValue() < probability) {
			offspring.add((pittsburghSolution) parent1.copy());
			offspring.get(0).clearVariables();
			offspring.get(0).clearAttributes();

			int N = parent1.getNumberOfVariables() + parent2.getNumberOfVariables();

			// Replenishing lack of number of rules
			if(N < Consts.MIN_RULE_NUM) {
				int lackNum = Consts.MIN_RULE_NUM - N;
				for(int i = 0; i < lackNum; i++) {
                    N++;
				}
			}

			if(N < 1) {System.err.print("number of rule to be generate is less than 1");}

			// Crossover
			List<MichiganSolution> michiganPopulation = new ArrayList<>();	// offspring

			String attributeId = new NumberOfWinner<MichiganSolution_Basic<?>>().getAttributeId();

			// Inheriting
			// from parint1
			for(int i = 0; i < parent1.getNumberOfVariables(); i++) {
				michiganPopulation.add(parent1.getVariable(i).copy());
			}
			// from parint2
			for(int i = 0; i < parent2.getNumberOfVariables(); i++) {
				michiganPopulation.add(parent2.getVariable(i).copy());
			}

			pittsburghSolution child = (pittsburghSolution) parent1.copy();

			child.clearVariables();
			//add
			child.clearAttributes();
			//end

			for(int i=0; i<michiganPopulation.size(); i++) {
				child.addVariable(null);
			}
			//add
			for(int i=0; i<michiganPopulation.size(); i++) {
				((michiganSolution) michiganPopulation.get(i)).setAttribute(attributeId, 0);
			}
			//end

			for(int i=0; i<michiganPopulation.size(); i++) {
				child.setVariable(i, (michiganSolution) michiganPopulation.get(i));
			}
			offspring.clear();
			offspring.add(child);
		}
		/* Don't crossover */
		else {
			offspring.add((pittsburghSolution) parent1.copy());
			offspring.add((pittsburghSolution) parent2.copy());
			// Select one offspring by random.
			int remove = selectRandomGenerator.getRandomValue(0, 1);
			offspring.remove(remove);
		}

		if(offspring.get(0).getNumberOfVariables() < 1) {System.err.println("number Of generated rules is less than 1 @" + this.getClass().getSimpleName());}
		return offspring;
	}
}

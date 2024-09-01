package cilabo.gbml.operator.crossover;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
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
import cilabo.utility.GeneralFunctions;
import cilabo.utility.Random;

public class PittsburghCrossover <pittsburghSolution extends PittsburghSolution<michiganSolution>,michiganSolution extends MichiganSolution<?>>
	implements CrossoverOperator<pittsburghSolution> {
	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public PittsburghCrossover(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghCrossover(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghCrossover(
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

		if(parent1.getNumberOfVariables() < 1) {System.err.println("incorrect input: number Of Rules is less than 1@" + this.getClass().getSimpleName());}
		if(parent2.getNumberOfVariables() < 1) {System.err.println("incorrect input: number Of Rules is less than 1@" + this.getClass().getSimpleName());}
		List<pittsburghSolution> offspring = new ArrayList<>();

		/* Do crossover */
		if(crossoverRandomGenerator.getRandomValue() < probability) {
			offspring.add((pittsburghSolution) parent1.copy());
			offspring.get(0).clearVariables();
			offspring.get(0).clearAttributes();
			/** Number of rules inherited from parent1.  */
			int N1 = selectRandomGenerator.getRandomValue(0, parent1.getNumberOfVariables());
			/** Number of rules inherited from parent2.  */
			int N2 = selectRandomGenerator.getRandomValue(0, parent2.getNumberOfVariables());

			// Reduciong excess of rules
			if((N1+N2) > Consts.MAX_RULE_NUM) {
				int delNum = (N1+N2) - Consts.MAX_RULE_NUM;
				for(int i = 0; i < delNum; i++) {
					if( N1 > 0 && N2 > 0){
						if(selectRandomGenerator.getRandomValue(0, 1) == 0) {
							N1--;
						}
						else {
							N2--;
						}
					}
					else if(N1 == 0 && N2 > 0) {
						N2--;
					}
					else if(N1 > 0 && N2 == 0) {
						N1--;
					}
					else {
						break;
					}
				}
			}

			//
			if((N1+N2) < Consts.MIN_RULE_NUM) {
				int lackNum = Consts.MIN_RULE_NUM - (N1+N2);
				for(int i = 0; i < lackNum; i++) {

					if( N1 < parent1.getNumberOfVariables() &&
						N2 < parent2.getNumberOfVariables())
					{
						if(selectRandomGenerator.getRandomValue(0, 1) == 0) {
							N1++;
						}
						else {
							N2++;
						}
					}
					else if(N1 >= parent1.getNumberOfVariables() &&
							N2 < parent2.getNumberOfVariables()) {
						N2++;
					}
					else if(N1 < parent1.getNumberOfVariables() &&
							N2 >= parent2.getNumberOfVariables()) {
						N1++;
					}
					else {
						throw new ArithmeticException("Number of Rule that both parent have is less than " + String.valueOf(Consts.MIN_RULE_NUM)
						+ " @" + this.getClass().getSimpleName());
					}
				}
			}

			if((N1+N2) < 1) {System.err.print("number of rule to be generate is less than 1");}

			// Select inherited rules
			int ruleNum = parent1.getNumberOfVariables();
			Integer[][] index1 = GeneralFunctions.samplingWithoutForOption2(ruleNum, N1, Random.getInstance().getGEN());
			ruleNum = parent2.getNumberOfVariables();
			Integer[][] index2 = GeneralFunctions.samplingWithoutForOption2(ruleNum, N2, Random.getInstance().getGEN());

			String attributeId = new NumberOfWinner<MichiganSolution_Basic<?>>().getAttributeId();

			// Inheriting
			for(int i = 0; i < index1[0].length; i++) {
				michiganSolution michiganSolution_buf = (michiganSolution) parent1.getVariable(index1[0][i]).copy();
				michiganSolution_buf.setAttribute(attributeId, 0);
				offspring.get(0).addVariable(michiganSolution_buf);
			}
			for(int i = 0; i < index2[0].length; i++) {
				michiganSolution michiganSolution_buf = (michiganSolution) parent2.getVariable(index2[0][i]).copy();
				michiganSolution_buf.setAttribute(attributeId, 0);
				offspring.get(0).addVariable(michiganSolution_buf);
			}
//
//			// Inheriting
//			for(int i = 0; i < index1[1].length; i++) {
//				michiganSolution michiganSolution_buf = (michiganSolution) parent1.getVariable(index1[1][i]).copy();
//				michiganSolution_buf.setAttribute(attributeId, 0);
//				offspring.get(1).addVariable(michiganSolution_buf);
//			}
//			for(int i = 0; i < index2[1].length; i++) {
//				michiganSolution michiganSolution_buf = (michiganSolution) parent2.getVariable(index2[1][i]).copy();
//				michiganSolution_buf.setAttribute(attributeId, 0);
//				offspring.get(1).addVariable(michiganSolution_buf);
//			}
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

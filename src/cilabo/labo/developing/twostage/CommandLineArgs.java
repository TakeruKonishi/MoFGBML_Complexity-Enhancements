package cilabo.labo.developing.twostage;

import java.io.File;

import cilabo.main.AbstractArgs;
import cilabo.main.Consts;
import cilabo.utility.Output;

public class CommandLineArgs extends AbstractArgs {
	// ************************************************************
	/** データセット名 */
	public static String dataName;
	/** 実験設定の識別用ID (results/*) */
	public static String algorithmID;
	/** 実験設定の識別用ID (results/*\/iris_*) */
	public static String experimentID;
	/** ForkJoinPoolの並列コア数 */
	public static int parallelCores;
	/** 学習用データセット ファイル名 */
	public static String trainFile;
	/** 検証用データセット ファイル名 */
	public static String validationFile;
	/** 評価用データセット ファイル名 */
	public static String testFile;
	/** ステージ切り替え評価回数 */
	public static int changeStageSetting;

	// ************************************************************
	@Override
	protected void load(String[] args) {
		int n = 7;
		if(args.length < n) {
			System.out.println("Need n=" + String.valueOf(n) + " arguments.");
			System.out.println("---");
			System.out.print(CommandLineArgs.getParamsString());
			System.out.println("---");
			return;
		}

		dataName = args[0];

		algorithmID = args[1];
		Consts.ALGORITHM_ID_DIR = Consts.ROOTFOLDER + File.separator + algorithmID;
		Output.mkdirs(Consts.ALGORITHM_ID_DIR);

		experimentID = args[2];
		Consts.EXPERIMENT_ID_DIR = Consts.ALGORITHM_ID_DIR + File.separator + dataName + "_" + experimentID;
		Output.mkdirs(Consts.EXPERIMENT_ID_DIR);

		parallelCores = Integer.parseInt(args[3]);

		trainFile = args[4];
		validationFile = args[5];
		testFile = args[6];

		changeStageSetting = Integer.parseInt(args[7]);

	}
}

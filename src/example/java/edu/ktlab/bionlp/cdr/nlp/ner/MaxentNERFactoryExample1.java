package edu.ktlab.bionlp.cdr.nlp.ner;

import opennlp.model.TrainUtil;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.OutcomePriorFeatureGenerator;
import opennlp.tools.util.featuregen.PrefixFeatureGenerator;
import opennlp.tools.util.featuregen.PreviousMapFeatureGenerator;
import opennlp.tools.util.featuregen.SentenceFeatureGenerator;
import opennlp.tools.util.featuregen.SuffixFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

import com.google.common.io.Resources;

import edu.ktlab.bionlp.cdr.nlp.ner.features.JeniaFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.ner.features.WordLengthFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.tools.jeniatagger.Jenia;
import edu.ktlab.ml.minorfourth.sequential.opennlp.features.NgramTokenFeatureGenerator;

public class MaxentNERFactoryExample1 {

	public static AdaptiveFeatureGenerator createFeatureGenerator() throws Exception {
		Jenia.setModelsPath(Resources.getResource("models/genia").getFile());
		AdaptiveFeatureGenerator featureGenerator = new CachedFeatureGenerator(
				new AdaptiveFeatureGenerator[] {
						new WindowFeatureGenerator(new TokenClassFeatureGenerator(true), 2, 2),
						new WindowFeatureGenerator(new TokenFeatureGenerator(true), 2, 2),
						new WindowFeatureGenerator(new NgramTokenFeatureGenerator(true, 2, 2), 2, 2),
						new WindowFeatureGenerator(new NgramTokenFeatureGenerator(true, 3, 3), 2, 2),
						new WindowFeatureGenerator(new JeniaFeatureGenerator(), 2, 2),
						new PrefixFeatureGenerator(), new SuffixFeatureGenerator(),
						new WordLengthFeatureGenerator(), new OutcomePriorFeatureGenerator(),
						new PreviousMapFeatureGenerator(),
						new SentenceFeatureGenerator(true, false) });
		return featureGenerator;
	}

	public static void main(String[] args) throws Exception {
		MaxentNERFactory ner = new MaxentNERFactory(createFeatureGenerator());
		ner.trainNER("data/cdr/cdr_training/CDR_TrainingSet.opennlp", "models/ner/cdr_train.mebs.model",
				TrainUtil.PERCEPTRON_VALUE, 100, 1);
		ner.evaluatebyExactMatching("data/cdr/cdr_dev/CDR_DevelopmentSet.opennlp",
				"models/ner/cdr_train.mebs.model", 5);
	}
}

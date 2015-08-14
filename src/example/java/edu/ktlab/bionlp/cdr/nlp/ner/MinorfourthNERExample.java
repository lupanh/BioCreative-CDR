package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.CachedFeatureGenerator;
import opennlp.tools.util.featuregen.PrefixFeatureGenerator;
import opennlp.tools.util.featuregen.SentenceFeatureGenerator;
import opennlp.tools.util.featuregen.SuffixFeatureGenerator;
import opennlp.tools.util.featuregen.TokenClassFeatureGenerator;
import opennlp.tools.util.featuregen.TokenFeatureGenerator;
import opennlp.tools.util.featuregen.WindowFeatureGenerator;

import com.google.common.io.Resources;

import edu.ktlab.bionlp.cdr.nlp.ner.features.JeniaFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.ner.features.WordLengthFeatureGenerator;
import edu.ktlab.bionlp.cdr.nlp.tools.jeniatagger.Jenia;
import edu.ktlab.ml.minorfourth.classify.eval.Evaluation;
import edu.ktlab.ml.minorfourth.classify.linear.LogisticRegressionLearner;
import edu.ktlab.ml.minorfourth.classify.linear.MaxEntLearner;
import edu.ktlab.ml.minorfourth.classify.svm.SVMLearner;
import edu.ktlab.ml.minorfourth.sequential.CMMLearner;
import edu.ktlab.ml.minorfourth.sequential.CRFLearner;
import edu.ktlab.ml.minorfourth.sequential.DatasetSequenceClassifierTeacher;
import edu.ktlab.ml.minorfourth.sequential.GenericCollinsLearnerV1;
import edu.ktlab.ml.minorfourth.sequential.SequenceClassifier;
import edu.ktlab.ml.minorfourth.sequential.SequenceClassifierLearner;
import edu.ktlab.ml.minorfourth.sequential.SequenceDataset;
import edu.ktlab.ml.minorfourth.sequential.opennlp.SequenceDatasetTransform;
import edu.ktlab.ml.minorfourth.sequential.opennlp.features.NgramTokenFeatureGenerator;

public class MinorfourthNERExample {
	static String trainingPath = "data/cdr/cdr_training/CDR_TrainingSet.opennlp";
	static String testingPath = "data/cdr/cdr_dev/CDR_DevelopmentSet.opennlp";

	static int windowSize = 1;

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
						new WordLengthFeatureGenerator(),
						// new BigramNameFeatureGenerator(),
						// new OutcomePriorFeatureGenerator(),
						// new PreviousMapFeatureGenerator(),
						new SentenceFeatureGenerator(true, false) });
		return featureGenerator;
	}

	public static void main(String... strings) throws Exception {
		Charset charset = Charset.forName("UTF-8");
		File trainSeqFile = new File("temp/train.seq");
		File testSeqFile = new File("temp/test.seq");
		SequenceDataset trainSeq = new SequenceDataset();
		SequenceDataset testSeq = new SequenceDataset();
		
		boolean newFeatures = false;
		if (!trainSeqFile.exists() || newFeatures) {
			ObjectStream<String> trainStream = new PlainTextByLineStream(new FileInputStream(trainingPath), charset);
			ObjectStream<NameSample> trainingStream = new NameSampleDataStream(trainStream);
			trainSeq = SequenceDatasetTransform.transformFromStream(trainingStream,	createFeatureGenerator());
			System.out.println(trainSeq.getSchema());
			trainSeq.saveAs(trainSeqFile, "Minorthird Sequential Dataset");	
		} else {
			trainSeq = (SequenceDataset) trainSeq.restore(trainSeqFile);
			System.out.println(trainSeq.getSchema());
		}
		
		if (!testSeqFile.exists() || newFeatures) {
			ObjectStream<String> testStream = new PlainTextByLineStream(new FileInputStream(testingPath), charset);
			ObjectStream<NameSample> testingStream = new NameSampleDataStream(testStream);
			testSeq = SequenceDatasetTransform.transformFromStream(testingStream, createFeatureGenerator());
			System.out.println(testSeq.getSchema());
			testSeq.saveAs(testSeqFile, "Minorthird Sequential Dataset");
		} else {
			testSeq = (SequenceDataset) testSeq.restore(testSeqFile);
			System.out.println(testSeq.getSchema());
		}		

		SequenceClassifierLearner crf = new CRFLearner();
		SequenceClassifierLearner cpl = new GenericCollinsLearnerV1(3, 50);
		SequenceClassifierLearner lr = new CMMLearner(new LogisticRegressionLearner(), 1);
		SequenceClassifierLearner svm = new CMMLearner(new SVMLearner(), 1);
		SequenceClassifierLearner memm = new CMMLearner(new MaxEntLearner("maxIters 100"), 1);

		SequenceClassifier classify = new DatasetSequenceClassifierTeacher(trainSeq).train(memm);
		Evaluation e = new Evaluation(trainSeq.getSchema());
		e.extend(classify, testSeq);
		
		System.out.println(e.confusionMatrix());

		double[] stats1 = new double[4];
		stats1[0] = e.errorRate();
		stats1[1] = e.averagePrecision();
		stats1[2] = e.maxF1();
		stats1[3] = e.averageLogLoss();

		System.out.println("Error Rate: " + e.errorRate());
		System.out.println("Avg Precision: " + e.averagePrecision());
		System.out.println("Max F1: " + e.maxF1());
		System.out.println("Avg Log Loss: " + e.averageLogLoss());
	}
}

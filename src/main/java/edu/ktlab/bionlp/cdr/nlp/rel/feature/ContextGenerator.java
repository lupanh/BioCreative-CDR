package edu.ktlab.bionlp.cdr.nlp.rel.feature;

import java.util.ArrayList;

import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.Token;

public class ContextGenerator {
	private FeatureGenerator<Token, Sentence>[] mFeatureGenerators;

	public ContextGenerator(FeatureGenerator<Token, Sentence>[] mFeatureGenerators) {
		this.mFeatureGenerators = mFeatureGenerators;
	}

	public ArrayList<String> getContext(Token tokens, Sentence sentence) {
		ArrayList<String> context = new ArrayList<String>();
		for (FeatureGenerator<Token, Sentence> generator : mFeatureGenerators) {
			ArrayList<String> extractedFeatures = generator.extractFeatures(tokens, sentence);
			context.addAll(extractedFeatures);
		}
		return context;
	}
}

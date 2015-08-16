package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.FileInputStream;
import java.io.InputStream;

import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.TextSpan;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

public class MaxentNERRecognizer {
	AdaptiveFeatureGenerator featureGenerator;
	NameFinderME nerFinder;
	int beamSize = 5;

	public MaxentNERRecognizer(String modelPath, AdaptiveFeatureGenerator featureGenerator) throws Exception {
		this.featureGenerator = featureGenerator;
		loadNERModel(modelPath);
	}

	void loadNERModel(String modelPath) throws Exception {
		InputStream model = new FileInputStream(modelPath);
		loadNERModel(model);
	}

	void loadNERModel(InputStream model) throws Exception {
		TokenNameFinderModel nerModel = new TokenNameFinderModel(model);
		nerFinder = new NameFinderME(nerModel, featureGenerator, beamSize);
	}

	public String recognize(Document doc, Sentence sentence) {
		String output = "";
		String[] tokens = sentence.getStringTokens();
		Span[] spans = nerFinder.find(tokens);
		for (Span span : spans) {
			int startOffset = Integer.MAX_VALUE;
			int endOffset = 0;
			for (int i = span.getStart(); i < span.getEnd(); i++) {
				if (sentence.getTokens().get(i).getStartBaseOffset() <= startOffset)
					startOffset = sentence.getTokens().get(i).getStartBaseOffset();
				if (sentence.getTokens().get(i).getEndBaseOffset() >= endOffset)
					endOffset = sentence.getTokens().get(i).getEndBaseOffset();
			}
			
			String entity = doc.getContent().substring(startOffset, endOffset);
			output += doc.getPmid() + "\t" + startOffset + "\t" + endOffset + "\t" + entity.trim() + "\t" + span.getType() + "\n";
		}
		return output;
	}

	public String recognize(String[] tokens) {
		Span[] spans = nerFinder.find(tokens);
		return TextSpan.getStringNameSample(spans, tokens);
	}

	public Span[] find(String[] tokens) {
		return nerFinder.find(tokens);
	}
}

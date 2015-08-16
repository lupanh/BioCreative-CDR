package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.base.TextSpan;
import edu.ktlab.bionlp.cdr.nlp.nen.MentionNormalization;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

public class CDRNERRecognizer {
	AdaptiveFeatureGenerator featureGenerator;
	NameFinderME nerFinder;
	int beamSize = 5;

	public CDRNERRecognizer(String modelPath, AdaptiveFeatureGenerator featureGenerator) throws Exception {
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

	public List<Annotation> recognize(Document doc, Sentence sentence, MentionNormalization normalizer) {
		List<Annotation> anns = new ArrayList<Annotation>();
		String output = "";
		String[] sentenceTokens = sentence.getStringTokens();
		Span[] spans = nerFinder.find(sentenceTokens);

		for (Span span : spans) {
			Annotation ann = new Annotation();
			
			int startOffset = Integer.MAX_VALUE;
			int endOffset = 0;
			for (int i = span.getStart(); i < span.getEnd(); i++) {
				ann.addToken(sentence.getTokens().get(i));
				if (sentence.getTokens().get(i).getStartBaseOffset() <= startOffset)
					startOffset = sentence.getTokens().get(i).getStartBaseOffset();
				if (sentence.getTokens().get(i).getEndBaseOffset() >= endOffset)
					endOffset = sentence.getTokens().get(i).getEndBaseOffset();
			}
			
			String mention = doc.getContent().substring(startOffset, endOffset);			
			
			ann.setContent(mention);
			ann.setStartBaseOffset(startOffset);
			ann.setEndBaseOffset(endOffset);
			ann.setType(span.getType());
			
			// output += doc.getPmid() + "\t" + startOffset + "\t" + endOffset + "\t" + mention.trim() + "\t" + span.getType();
			
			String[] mentionTokens = SimpleTokenizer.INSTANCE.tokenize(mention.toLowerCase());
			if (normalizer != null) {
				String meshId = normalizer.normalize(mention, mentionTokens);
				// output += "\t" + meshId;
				ann.setReference(meshId);
			}
			// output += "\n";
			anns.add(ann);
		}
		return anns;
	}

	public String recognize(String[] tokens) {
		Span[] spans = nerFinder.find(tokens);
		return TextSpan.getStringNameSample(spans, tokens);
	}

	public Span[] find(String[] tokens) {
		return nerFinder.find(tokens);
	}
}

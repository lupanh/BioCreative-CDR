package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

import edu.ktlab.bionlp.cdr.base.Annotation;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.nlp.nen.MentionNormalization;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class PerceptronNERRecognizerExample {
	public static void main(String[] args) throws Exception {
		CDRNERRecognizer nerFinder = new CDRNERRecognizer("models/ner/cdr_full.perc.model",
				MaxentNERFactoryExample.createFeatureGenerator());
		MentionNormalization normalizer = new MentionNormalization("models/nen/cdr_full.txt",
				"models/nen/mesh2015.gzip");

		String[] lines = FileHelper.readFileAsLines("temp/test_webservice.txt");
		String text = "";
		for (int i = 0; i < lines.length; i++) {
			text += lines[i] + "\n";
			if (i % 2 == 1) {
				Document doc = CollectionFactory.loadDocumentFromString(text, false);

				for (Sentence sent : doc.getSentences()) {
					List<Annotation> anns = nerFinder.recognize(doc, sent, normalizer);
					for (Annotation ann : anns)
						doc.addAnnotation(ann);
					// text += tagged;
				}

				// FileHelper.appendToFile(text, new File("temp/test_outservice.txt"), Charset.defaultCharset());

				text = "";
			}
		}

	}
}

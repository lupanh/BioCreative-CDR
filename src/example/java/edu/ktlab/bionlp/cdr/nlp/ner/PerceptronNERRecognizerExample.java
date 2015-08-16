package edu.ktlab.bionlp.cdr.nlp.ner;

import java.io.File;
import java.nio.charset.Charset;

import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class PerceptronNERRecognizerExample {
	public static void main(String[] args) throws Exception {
		MaxentNERRecognizer nerFinder = new MaxentNERRecognizer("models/ner/cdr_full.perc.model",
				MaxentNERFactoryExample.createFeatureGenerator());
		
		String[] lines = FileHelper.readFileAsLines("temp/test_webservice.txt");
		String text = "";
		for (int i = 0; i < lines.length; i++) {
			text += lines[i] + "\n";
			if (i % 2 == 1) {
				Document doc = CollectionFactory.loadDocumentFromString(text, false);				
				for (Sentence sent : doc.getSentences()) {
					String tagged = nerFinder.recognize(doc, sent);
					text += tagged;
				}
				
				FileHelper.appendToFile(text, new File("temp/test_outservice.txt"), Charset.defaultCharset());
				
				text = "";
			}
		}

	}
}

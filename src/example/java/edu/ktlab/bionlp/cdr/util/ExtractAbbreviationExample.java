package edu.ktlab.bionlp.cdr.util;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;

public class ExtractAbbreviationExample {

	public static void main(String[] args) {
		Collection col = CollectionFactory.loadFile("data/cdr/cdr_dev/cdr_dev.txt", false);
		for (Document doc : col.getDocuments())
			System.out.println(doc.getPmid() + " " + doc.getAbbreviations());
	}

}

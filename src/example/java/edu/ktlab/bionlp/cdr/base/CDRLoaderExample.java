package edu.ktlab.bionlp.cdr.base;

import edu.ktlab.bionlp.cdr.base.Collection;
import edu.ktlab.bionlp.cdr.base.CollectionFactory;

public class CDRLoaderExample {

	public static void main(String[] args) throws Exception {
		Collection col = CollectionFactory.loadFile("data/cdr/cdr_train/cdr_train.txt", true);
		CollectionFactory.saveJsonFile("data/cdr/cdr_train/cdr_train.gzip", col);

		col = CollectionFactory.loadFile("data/cdr/cdr_dev/cdr_dev.txt", true);
		CollectionFactory.saveJsonFile("data/cdr/cdr_dev/cdr_dev.gzip", col);

		col = CollectionFactory.loadFile("data/cdr/cdr_full/cdr_full.txt", true);
		CollectionFactory.saveJsonFile("data/cdr/cdr_full/cdr_full.gzip", col);

		// Collection col = CollectionFactory.loadFile("data/cdr/cdr_test.txt");
		col = CollectionFactory.loadJsonFile("data/cdr/cdr_train/cdr_train.gzip");
		System.out.println(col.documents.size());
	}

}

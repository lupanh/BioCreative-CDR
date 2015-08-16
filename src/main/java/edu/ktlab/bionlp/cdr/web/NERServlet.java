package edu.ktlab.bionlp.cdr.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ktlab.bionlp.cdr.base.CollectionFactory;
import edu.ktlab.bionlp.cdr.base.Document;
import edu.ktlab.bionlp.cdr.base.Sentence;
import edu.ktlab.bionlp.cdr.nlp.nen.MentionNormalization;
import edu.ktlab.bionlp.cdr.nlp.ner.MaxentNERFactoryExample;
import edu.ktlab.bionlp.cdr.nlp.ner.CDRNERRecognizer;
import edu.ktlab.bionlp.cdr.util.FileHelper;

public class NERServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	CDRNERRecognizer nerFinder;
	File temp = new File("temp/data_services.txt");
	MentionNormalization normalizer;

	public NERServlet() {
		try {
			nerFinder = new CDRNERRecognizer("models/ner/cdr_full.perc.model",
					MaxentNERFactoryExample.createFeatureGenerator());
			normalizer = new MentionNormalization("models/nen/cdr_full.txt", "models/nen/mesh2015.gzip");
			if (temp.exists())
				temp.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "All services require POST.");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// test parameters
		String format = request.getParameter("format");
		if (format == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter format ");
			return;
		}

		int run = 1;
		String setString = request.getParameter("run");
		if (setString != null) {
			try {
				run = Integer.parseInt(setString);
			} catch (NumberFormatException e) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot parse parameter set: " + setString);
				return;
			}
		}
		if (!(1 <= run && run <= 3)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Run has to be one of 1, 2, 3: " + run);
			return;
		}

		// read data
		Optional<String> optional = readData(request);
		if (!optional.isPresent()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No data is found in the POST.");
			return;
		}

		// test format
		String data = optional.get();
		if (!checkFormat(data, format)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					String.format("The input text is not in %s format", format));
			return;
		}

		try {
			String result = annotate(data, run);
			PrintWriter out = response.getWriter();
			out.print(result);
			out.flush();
			out.close();
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	/**
	 * Read data from the http post
	 *
	 * @param request
	 *            the http post
	 * @return data from the http post
	 */
	private Optional<String> readData(HttpServletRequest request) {
		try {
			StringBuilder sb = new StringBuilder();
			String line = null;
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
			if (sb.length() == 0) {
				return Optional.empty();
			} else {
				return Optional.of(sb.toString());
			}
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private boolean checkFormat(String data, String format) {
		if (format.equals("pubtator")) {
			// test pubtator format
			return true;
		} else if (format.equals("bioc")) {
			// test bioc format
			return true;
		} else {
			return false;
		}
	}

	private String annotate(String data, int run) throws Exception {
		Document doc = CollectionFactory.loadDocumentFromString(data, false);

		for (Sentence sent : doc.getSentences()) {
			String tagged = nerFinder.recognize(doc, sent, normalizer);
			data += tagged;
		}
		FileHelper.appendToFile(data + "\n", temp, Charset.forName("UTF-8"));
		return data;
	}
}

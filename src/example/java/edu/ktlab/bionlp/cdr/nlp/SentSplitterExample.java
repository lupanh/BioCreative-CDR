package edu.ktlab.bionlp.cdr.nlp;

import edu.ktlab.bionlp.cdr.nlp.splitter.SentSplitterMESingleton;

public class SentSplitterExample {

	public static void main(String[] args) {
		String text = "BACKGROUND: Irritable bowel syndrome is a common cause of abdominal pain and discomfort and may be related to disordered gastrointestinal motility. Our aim was to assess the effects of long-term treatment with a prokinetic agent, cisapride, on postprandial jejunal motility and symptoms in the irritable bowel syndrome (IBS). METHODS: Thirty-eight patients with IBS (constipation-predominant, n = 17; diarrhoea-predominant, n = 21) underwent 24-h ambulatory jejunal manometry before and after 12 week's treatment [cisapride, 5 mg three times daily (n = 19) or placebo (n = 19)]. RESULTS: In diarrhoea-predominant patients significant differences in contraction characteristics were observed between the cisapride and placebo groups. In cisapride-treated diarrhoea-predominant patients the mean contraction amplitude was higher (29.3 +/- 3.2 versus 24.9 +/- 2.6 mm Hg, cisapride versus placebo (P < 0.001); pretreatment, 25.7 +/- 6.0 mm Hg), the mean contraction duration longer (3.4 +/- 0.2 versus 3.0 +/- 0.2 sec, cisapride versus placebo (P < 0.001); pretreatment, 3.1 +/- 0.5 sec), and the mean contraction frequency lower (2.0 +/- 0.2 versus 2.5 +/- 0.4 cont./min, cisapride versus placebo (P < 0.001); pretreatment, 2.5 +/- 1.1 cont./min] than patients treated with placebo. No significant differences in jejunal motility were found in the constipation-predominant IBS group. Symptoms were assessed by using a visual analogue scale before and after treatment. Symptom scores relating to the severity of constipation were lower in cisapride-treated constipation-predominant IBS patients [score, 54 +/- 5 versus 67 +/- 14 mm, cisapride versus placebo (P < 0.05); pretreatment, 62 +/- 19 mm]. Diarrhoea-predominant IBS patients had a higher pain score after cisapride therapy [score, 55 +/- 15 versus 34 +/- 12 mm, cisapride versus placebo (P < 0.05); pretreatment, 67 +/- 19 mm]. CONCLUSION: Cisapride affects jejunal contraction characteristics and some symptoms in IBS.";
		String[] sents = SentSplitterMESingleton.getInstance().split(text);
		for (String sent : sents)
			System.out.println(sent);
	}

}

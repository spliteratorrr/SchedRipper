package me.josie.ripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SchedRipper {

	private ArrayList<SectionBloc> sections;
	private int schedCount;

	public SchedRipper(String path) throws IOException {
		// Load the document and body
		Document doc = Jsoup.parse(new File(path), "utf-8");

		System.out.println("Loaded HTML document: " + doc.location());
		System.out.println("Ripping schedules from the document...");

		Element body = doc.body();
		Elements tables = body.select("table");
		String subject = null;
		this.sections = new ArrayList<>();

		for (int i = 0; i < tables.size(); i++) {
			if (i < 1)
				continue;
			Element table = tables.get(i);

			// If table is a subject header...
			if (!table.hasAttr("align")) {
				subject = skimSubject(table);
			} else {
				skim(subject, table);
			}
		}

		Collections.sort(sections);

		System.out.printf("Ripped %d schedule entries for %d block section(s).\n", schedCount, sections.size());

		System.out.println("Searchable block sections: " + sections.toString());
	}

	public void skim(String subject, Element table) {
		Elements tds = skimTds(table);

		if (tds.size() == 7) {
			// Get raw fields
			String rawSection = tds.get(0).text();
			String rawDays = tds.get(1).text();
			String rawTime = tds.get(2).text();
			String rawRoom = tds.get(3).text();

			if (rawSection.contains("/")) {
				String[] sections = rawSection.split("/");
				for (String s : sections) {
					String sName = s.trim();
					SchedEntry en = new SchedEntry(subject, sName, rawDays, rawTime, rawRoom);
					SectionBloc bloc = getSection(sName);
					bloc.addEntry(en);
					schedCount += 1;
				}
				return;
			}

			SchedEntry en = new SchedEntry(subject, rawSection, rawDays, rawTime, rawRoom);
			SectionBloc bloc = getSection(rawSection);
			bloc.addEntry(en);
			schedCount += 1;
		}
	}

	public String skimSubject(Element table) {
		Elements tds = skimTds(table);
		return tds.get(0).text();
	}

	public Elements skimTds(Element table) {
		Element tbody = table.selectFirst("tbody");
		Element trow = tbody.selectFirst("tr");
		Elements tds = trow.select("td");
		return tds;
	}

	public void addSection(String name) {
		sections.add(new SectionBloc(name));
	}

	public SectionBloc getSection(String name) {
		for (SectionBloc s : sections) {
			if (s.name.equalsIgnoreCase(name))
				return s;
		}
		SectionBloc s = new SectionBloc(name);
		sections.add(s);
		return s;
	}

	public ArrayList<SectionBloc> getSections() {
		return sections;
	}

	public boolean hasSection(String name) {
		return getSection(name) != null;
	}
}

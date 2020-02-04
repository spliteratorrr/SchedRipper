package me.josie.ripper;

import java.util.ArrayList;

public class SectionBloc implements Comparable<SectionBloc>{

	public ArrayList<SchedEntry> entries;
	public String name;

	public SectionBloc(String name) {
		this.entries = new ArrayList<>();
		this.name = name;
	}

	public void addEntry(SchedEntry e) {
		entries.add(e);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(SectionBloc b) {
		return name.compareTo(b.name);
	}
}

package me.josie.ripper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;

public class SchedEntry implements Comparable<SchedEntry> {

	public LinkedHashSet<DayType> dayTypes;

	private SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");

	private int fromD, toD;

	public enum DayType {
		MONDAY("m", "mon"), TUESDAY("t", "tue"), WEDNESDAY("w", "wed"), THURSDAY("th", "thu"), FRIDAY("f",
				"fri"), SATURDAY("s", "sat");

		String[] literals;

		DayType(String... literals) {
			this.literals = literals;
		}

		public String[] getLiterals() {
			return literals;
		}

		public String toString() {
			return this.name();
		}
	}

	public String subject, subjectCode, section, days, time, room;

	public SchedEntry(String rawSubject, String rawSection, String rawDays, String rawTime, String rawRoom) {
		this.dayTypes = new LinkedHashSet<>();

		String[] sub = rawSubject.split("-");

		this.subjectCode = sub[0].trim();

		String subjectFat = sub[1].trim();
		String[] subs = subjectFat.split(":");
		this.subject = subs[0].trim();

		this.section = rawSection;
		this.days = rawDays;
		this.time = rawTime;
		this.room = rawRoom;

		// Generate day types
		processDays(rawDays);

		// Generate time sorting indices
		try {
			processTime(rawTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void processDays(String rawDays) {
		// There is possibility to be a single day.
		if (rawDays.length() == 3) {
			// Compare string to every longhand of a day
			for (DayType t : DayType.values()) {
				String longHand = t.literals[1];
				if (rawDays.equalsIgnoreCase(longHand)) {
					dayTypes.add(t);
					return;
				}
			}
		}

		// Compute series of characters...
		char[] daysChars = rawDays.toCharArray();

		for (int i = 0; i < daysChars.length; i++) {
			char d = daysChars[i];
			if (d == 'M') {
				dayTypes.add(DayType.MONDAY);
			} else if (d == 'W') {
				dayTypes.add(DayType.WEDNESDAY);
			} else if (d == 'F') {
				dayTypes.add(DayType.FRIDAY);
			} else if (d == 'T') {
				if (i + 1 < daysChars.length)
					if (daysChars[i + 1] == 'H') {
						dayTypes.add(DayType.THURSDAY);
						continue;
					}
				dayTypes.add(DayType.TUESDAY);
			}
		}
	}

	private void processTime(String rawTime) throws ParseException {
		String[] time = rawTime.split("-");
		String from = time[0].trim();
		String to = time[1].trim();
		String fromD = displayFormat.format(parseFormat.parse(from));
		String toD = displayFormat.format(parseFormat.parse(to));

		String[] fromDComp = fromD.split(":");
		String[] toDComp = toD.split(":");
		this.fromD = (Integer.parseInt(fromDComp[0]) * 60) + Integer.parseInt(fromDComp[1]);
		this.toD = (Integer.parseInt(toDComp[0]) * 60) + Integer.parseInt(toDComp[1]);
	}

	public boolean hasDay(DayType t) {
		return dayTypes.contains(t);
	}

	public String toString() {
		return "" + subject + " (" + subjectCode + "), " + days + " " + time + " @ Rm. " + room;
	}

	public String pretty() {
		return String.format("%-50s %-15s %-6s %4s %13s", subject, subjectCode, days, time, room);
	}

	@Override
	public int compareTo(SchedEntry e) {
		return fromD > e.fromD ? 1 : -1;
	}

	public int getFromD() {
		return fromD;
	}

	public int getToD() {
		return toD;
	}

}

package me.josie.ripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Core {

	private static String path = "D:/Users/Gab/Desktop/Online Inquiry.html";

	public static void main(String[] args) throws IOException {
		SchedRipper ripper = new SchedRipper(path);

		Scanner scan = new Scanner(System.in);
		System.out.println("\nGet from section: ");

		// Retrieves inputs
		ArrayList<SchedEntry> entries = new ArrayList<>();
		boolean exit = false;
		String targetSection = null;

		while (!exit) {

			targetSection = scan.nextLine();

			if (targetSection.equalsIgnoreCase("exit")) {
				System.out.println("Goodbye. UwU");
				exit = true;
			}
			entries.clear();

			for (SectionBloc b : ripper.getSections()) {
				if (b.name.equalsIgnoreCase(targetSection)) {
					for (SchedEntry e : b.entries) {
						entries.add(e);
					}

					System.out.printf("-- %s SCHEDULE --\n", targetSection);
					System.out.println(
							String.format("%-50s %-15s %-6s %10s %18s", "Subject", "Code", "Days", "Time", "Room"));

					System.out.println(
							String.format("%-50s %-15s %-6s %4s %13s", "---------------------------------------------",
									"----------", "-----", "-----------------", "--------"));

					Collections.sort(entries);
					for (SchedEntry e : entries) {
						System.out.println(e.pretty());
					}
					System.out.println("\nGet from section: ");
				}
			}
		}

		scan.close();

	}
}

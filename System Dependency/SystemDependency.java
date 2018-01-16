package mutation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class finds system dependency and install and remove the packages as required.
 * Created by Prince on 01/06/2018.
 */
public class SystemDependency {

	//For example:- DEPEND TELNET TCPIP NETCARD It will make a entry as follows TELNET -> TCPIP,NETCARD
	HashMap<String, HashSet<String>> dependsOnHashMap = new HashMap<String, HashSet<String>>();

	//For example:- DEPEND TELNET TCPIP NETCARD This will create two entry as 
	//follows:- TCPIP ->TELNET NETCARD -> TELNET
	HashMap<String, HashSet<String>> dependantHashMap = new HashMap<String, HashSet<String>>();

	
	//Set of all currently installed packages
	HashSet<String> installedPackages = new HashSet<String>();

	public static void main(String[] args) {

		SystemDependency dependencyClass = new SystemDependency();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String input = null;
			String[] values = null;
			boolean flag = true;
			while ((input = br.readLine()) != null && flag) {
				System.out.println("\n" + input);
				values = input.split(" +");

				String value = values[0].trim();
				switch (value) {
				case "DEPEND":
					try {
						dependencyClass.dependencyCreation(values);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				case "INSTALL":
					dependencyClass.install(values[1], true);
					break;
				case "REMOVE":
					dependencyClass.remove(values[1]);
					break;
				case "LIST":
					dependencyClass.list();
					break;
				case "END":
					flag = false;
					break;
				default:
					System.out.println("Command not recognized");
					break;

				}
			}

		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	/*
	 * This method creates dependency and finds if there is any cyclic entry
	 * 
	 * @values this is input values
	 */
	public void dependencyCreation(String[] values) {
		try {

			if (values.length <= 1) {
				System.out.println("\tInvalid input in DEPEND");
			}
			String key = values[1];
			HashSet<String> current_list;
			if (!dependsOnHashMap.containsKey(key)) {
				current_list = new HashSet<String>();
				dependsOnHashMap.put(key, current_list);
			} else {
				current_list = dependsOnHashMap.get(key);
			}
			for (int i = 2; i < values.length; i++) {

				if (dependantHashMap.containsKey(key)) {
					if (dependantHashMap.get(key).contains(values[i])) {
						System.out.println("\t" + values[i] + " depends on " + key + ". Ignoring command");
					}
				} else {
					// Adding to depends List
					current_list.add(values[i]);

					HashSet<String> list;

					// Adding to dependent list, the key
					if (!dependantHashMap.containsKey(values[i])) {
						list = new HashSet<String>();
						list.add(key);
						dependantHashMap.put(values[i], list);
					} else {
						dependantHashMap.get(values[i]).add(key);
					}
				}
			}
			return;
		} catch (Exception ex) {
			throw ex;
		}

	}

	/*
	 * This method runs install command and put it in installed package
	 * 
	 * @value This contain the package to be installed
	 */
	public void install(String value, boolean print) {

		if (installedPackages.contains(value)) {
			if (print)
				System.out.println("\t" + value + " is already installed");
			return;
		}
		if (dependsOnHashMap.containsKey(value)) {
			HashSet<String> set = dependsOnHashMap.get(value);
			for (String s : set) {
				install(s, false);
			}
		}
		System.out.println("\tInstalling " + value);
		installedPackages.add(value);

		return;
	}

	/*
	 * This method removes the package and finds all the dependent to be removed
	 * 
	 * @value
	 */
	public void remove(String value) {
		if (!installedPackages.contains(value)) {
			System.out.println("\t" + value + " is not installed");
			return;
		}

		//If present in dependantHashMap and there is a package dependent on this, then return
		if (dependantHashMap.containsKey(value)) {
			HashSet<String> set = dependantHashMap.get(value);
			for (String s : set) {
				if (installedPackages.contains(s) && !s.equals(value)) {
					System.out.println("\t" + value + " is still needed");
					return;
				}
			}
		}
		System.out.println("\tRemoving " + value);
		installedPackages.remove(value);

		// Recursively call remove for all components it depends on
		if (dependsOnHashMap.containsKey(value)) {
			for (String s : dependsOnHashMap.get(value)) {
				removeDepenencies(s);
			}
		}

		return;
	}

	/*
	 * This method removes dependency on package to be removed
	 * 
	 * @value
	 */
	public void removeDepenencies(String value) {
		if (!installedPackages.contains(value)) {
			return;
		}
		if (dependantHashMap.containsKey(value)) {
			HashSet<String> set = dependantHashMap.get(value);
			for (String s : set) {
				if (installedPackages.contains(s)) {
					return;
				}
			}
		}
		System.out.println("Removing " + value);
		installedPackages.remove(value);
		if (dependsOnHashMap.containsKey(value)) {
			for (String s : dependsOnHashMap.get(value)) {
				removeDepenencies(s);
			}
		}
		return;
	}

	/*
	 * This method lists all the installed packages
	 * 
	 */
	public void list() {
		for (String s : installedPackages) {
			System.out.println("\t" + s);
		}
	}

}

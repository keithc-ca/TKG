/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/
package org.testKitGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class TestInfo {

	private Element testEle;
	private boolean valid;
	private String testCaseName;
	private String command;
	private String platformRequirements;
	private List<Variation> vars;
	private Map<String, String> capabilities;
	private String aotOptions;
	private int iterations;
	private String levelStr;
	private List<String> levels;
	private List<String> groups;
	private List<String> types;
	private String[] disabledReasons;

	public TestInfo(Element testEle) {
		this.testEle = testEle;
		this.testCaseName = null;
		this.command = null;
		this.platformRequirements = null;
		this.vars = new ArrayList<>();
		this.aotOptions = "";
		this.iterations = Integer.parseInt(Options.getIterations());
		this.capabilities = new HashMap<>();
		this.levelStr = "";
		this.levels = new ArrayList<>();
		this.groups = new ArrayList<>();
		this.types = new ArrayList<>();
		this.disabledReasons = null;
	}

	public void parseInfo() {
		this.testCaseName = testEle.getElementsByTagName("testCaseName").item(0).getTextContent().trim();
		validate();
		if (isValid()) {
			command = testEle.getElementsByTagName("command").item(0).getTextContent().trim();

			NodeList preqNodes = testEle.getElementsByTagName("platformRequirements");
			if (preqNodes.getLength() > 0) {
				platformRequirements = preqNodes.item(0).getTextContent().trim();
			}

			NodeList variationsNodes = testEle.getElementsByTagName("variation");
			for (int i = 0; i < variationsNodes.getLength(); i++) {
				String subTestName = testCaseName + "_" + i;
				vars.add(new Variation(subTestName, variationsNodes.item(i).getTextContent(), platformRequirements));
			}
			if (vars.size() == 0) {
				String subTestName = testCaseName + "_0";
				vars.add(new Variation(subTestName, "NoOptions", platformRequirements));
			}

			NodeList capabilitiesNodes = testEle.getElementsByTagName("capabilities");
			if (capabilitiesNodes.getLength() > 0) {
				String[] capabilityReqs_Arr = capabilitiesNodes.item(0).getTextContent().split(",");
				for (String capabilityReq : capabilityReqs_Arr) {
					String[] colonSplit = capabilityReq.trim().split(":");
					capabilities.put(colonSplit[0], colonSplit[1]);
				}
			}

			NodeList aotNodes = testEle.getElementsByTagName("aot");
			if (Options.getTestFlag().contains("aot")) {
				// aot defaults to "applicable" when testFlag contains AOT
				if (aotNodes.getLength() == 0 || aotNodes.item(0).getTextContent().trim().equals("applicable")) {
					aotOptions = "$(AOT_OPTIONS) ";
				} else if (aotNodes.item(0).getTextContent().trim().equals("explicit")) {
					// When test tagged with AOT explicit, its test command has AOT options and runs
					// multiple times explicitly
					iterations = 1;
				}
			}

			NodeList disabledNodes = testEle.getElementsByTagName("disabled");
			if (disabledNodes.getLength() > 0) {
				disabledReasons = disabledNodes.item(0).getTextContent().split("[\t\n]");
			}

			getElements(levels, "level", Constants.ALLLEVELS);
			// level defaults to "extended"
			if (levels.size() == 0) {
				levels.add("extended");
			}
			for (int i = 0; i < levels.size(); i++) {
				if (!levelStr.isEmpty()) {
					levelStr += ",";
				}
				levelStr = levelStr + "level." + levels.get(i);
			}

			getElements(groups, "group", Constants.ALLGROUPS);
			// group defaults to "extended"
			if (groups.size() == 0) {
				groups.add("functional");
			}

			getElements(types, "type", Constants.ALLTYPES);
			// type defaults to "regular"
			if (types.size() == 0) {
				types.add("regular");
			}
		}
	}

	private void getElements(List<String> list, String tag, List<String> all) {
		NodeList nodes = testEle.getElementsByTagName(tag);
		for (int i = 0; i < nodes.getLength(); i++) {
			String value = nodes.item(i).getTextContent().trim();
			if (!all.contains(value)) {
				System.err.println("The " + tag + ": " + value + " for test " + testCaseName
						+ " is not valid, the valid " + tag + " strings are " + joinStrList(all) + ".");
				System.exit(1);
			}
			list.add(nodes.item(i).getTextContent().trim());
		}
	}

	public boolean isValid() {
		return valid;
	}

	private static String joinStrList(List<String> list) {
		return list.stream().collect(Collectors.joining(" "));
	}

	private void validate() {
		valid = true;
		// Do not generate make taget if impl doesn't match the exported impl
		NodeList implNodes = testEle.getElementsByTagName("impl");

		boolean isValidImpl = implNodes.getLength() == 0;
		if (!isValidImpl) {
			List<String> impls = new ArrayList<String>();
			getElements(impls, "impl", Constants.ALLIMPLS);
			isValidImpl = impls.contains(Options.getImpl());
		}

		valid &= isValidImpl;

		// Do not generate make target if subset doesn't match the exported jdk_version
		NodeList subsets = testEle.getElementsByTagName("subset");

		boolean isValidSubset = subsets.getLength() == 0;

		for (int j = 0; j < subsets.getLength(); j++) {
			String subset = subsets.item(j).getTextContent().trim();

			if (subset.equalsIgnoreCase(Options.getJdkVersion())) {
				isValidSubset = true;
				break;
			} else {
				try {
					Pattern pattern = Pattern.compile("^(.*)\\+$");
					Matcher matcher = pattern.matcher(subset);
					if (matcher.matches()) {
						if (Integer.parseInt(matcher.group(1)) <= Integer.parseInt(Options.getJdkVersion())) {
							isValidSubset = true;
							break;
						}
					}
				} catch (NumberFormatException e) {
					// Nothing to do
				}
			}
		}

		valid &= isValidSubset;

		// Do not generate make taget if the test is AOT not applicable when test flag
		// is set to AOT
		NodeList aot = testEle.getElementsByTagName("aot");

		boolean isValidAot = true;

		if (aot.getLength() > 0 && Options.getTestFlag().contains("aot")) {
			isValidAot = !aot.item(0).getTextContent().trim().equals("nonapplicable");
		}

		valid &= isValidAot;
	}

	public String getTestCaseName() {
		return this.testCaseName;
	}

	public List<Variation> getVars() {
		return this.vars;
	}

	public String getCommand() {
		return this.command;
	}

	public String getPlatformRequirements() {
		return this.platformRequirements;
	}

	public Map<String, String> getCapabilities() {
		return this.capabilities;
	}

	public String getAotOptions() {
		return this.aotOptions;
	}

	public int getIterations() {
		return this.iterations;
	}

	public String getLevelStr() {
		return this.levelStr;
	}

	public List<String> getLevels() {
		return this.levels;
	}

	public List<String> getGroups() {
		return this.groups;
	}

	public List<String> getTypes() {
		return this.types;
	}

	public String[] getDisabledReasons() {
		return this.disabledReasons;
	}

}

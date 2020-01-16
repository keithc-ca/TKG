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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Counter {

	private static Map<String, Integer> count = new HashMap<>();

	private static String countmk = Options.getProjectRootDir() + "/TKG/" + Constants.COUNTMK;

	private Counter() {
		super();
	}

	public static void generateFile() {
		String newline = System.lineSeparator();

		try (Writer file = UtilsGen.openWithHeader(countmk)) {
			List<String> targetCountKeys = new ArrayList<>(count.keySet());
			Collections.sort(targetCountKeys);

			file.write("_GROUPTARGET := $(firstword $(MAKECMDGOALS))");
			file.write(newline);

			file.write("GROUPTARGET := $(patsubst _%,%,$(_GROUPTARGET))");
			file.write(newline);

			for (String key : targetCountKeys) {
				file.write("ifeq ($(GROUPTARGET),");
				file.write(key);
				file.write(")");
				file.write(newline);

				file.write("  TOTALCOUNT := ");
				file.write(count.get(key));
				file.write(newline);

				file.write("endif");
				file.write(newline);
			}

			file.write(newline);

			System.out.println();
			System.out.println("Generated " + countmk);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void add(String key, int value) {
		count.put(key, count.getOrDefault(key, 0) + value);
	}

}

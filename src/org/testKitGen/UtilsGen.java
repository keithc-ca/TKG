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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.function.Function;

public class UtilsGen {

	private static String utilsmk = Options.getProjectRootDir() + "/TKG/" + Constants.UTILSMK;

	private static String dependmk = Options.getProjectRootDir() + "/TKG/" + Constants.DEPENDMK;

	private UtilsGen() {
		super();
	}

	public static void start() {
		try {
			genDependMk();
			genUtilsMk();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void genDependMk() throws IOException {
		try (Writer file = openWithHeader(dependmk)) {
			String[] heads = new String[] { "", "disabled.", "echo.disabled." };

			for (String head : heads) {
				for (String level : Constants.ALLLEVELS) {
					for (String group : Constants.ALLGROUPS) {
						String hlgKey = head + level + '.' + group;

						genDepend(file, hlgKey, Constants.ALLTYPES, type -> hlgKey + '.' + type);
					}
				}

				for (String group : Constants.ALLGROUPS) {
					for (String type : Constants.ALLTYPES) {
						String hgtKey = head + group + '.' + type;

						genDepend(file, hgtKey, Constants.ALLLEVELS, level -> head + level + '.' + group + '.' + type);
					}
				}

				for (String type : Constants.ALLTYPES) {
					for (String level : Constants.ALLLEVELS) {
						String hltKey = head + level + '.' + type;

						genDepend(file, hltKey, Constants.ALLGROUPS, group -> head + level + '.' + group + '.' + type);
					}
				}

				for (String level : Constants.ALLLEVELS) {
					String hlKey = head + level;

					genDepend(file, hlKey, Constants.ALLGROUPS, group -> hlKey + '.' + group);
				}

				for (String group : Constants.ALLGROUPS) {
					String hgKey = head + group;

					genDepend(file, hgKey, Constants.ALLLEVELS, level -> head + level + '.' + group);
				}

				for (String type : Constants.ALLTYPES) {
					String htKey = head + type;

					genDepend(file, htKey, Constants.ALLLEVELS, level -> head + level + '.' + type);
				}

				genDepend(file, head + "all", Constants.ALLLEVELS, level -> head + level);
			}
		}

		System.out.println();
		System.out.println("Generated " + dependmk);
	}

	private static void genDepend(Writer file, String target, List<String> keys, Function<String, String> mapper)
			throws IOException {
		String newline = System.lineSeparator();

		file.write(newline);

		file.write(".PHONY : ");
		file.write(target);
		file.write(newline);

		file.write(target);
		file.write(" :");

		for (String key : keys) {
			file.write(" \\");
			file.write(newline);

			file.write("\t");
			file.write(mapper.apply(key));
		}

		file.write(newline);
	}

	public static Writer openWithHeader(String fileName) throws IOException {
		Writer file = new FileWriter(fileName);
		String newline = System.lineSeparator();

		file.write("########################################################");
		file.write(newline);
		file.write("# This is an auto generated file. Please do NOT modify!");
		file.write(newline);
		file.write("########################################################");
		file.write(newline);

		return file;
	}

	private static void genUtilsMk() throws IOException {
		String newline = System.lineSeparator();

		try (Writer file = openWithHeader(utilsmk)) {
			String spec = Options.getSpec();
			String plat = ModesDictionary.getPlat(spec);

			if (plat.isEmpty()) {
				file.write("PLATFORM :=");
				file.write(newline);
			} else {
				file.write("ifeq" + " ($(SPEC)," + spec + ")");
				file.write(newline);

				file.write("  PLATFORM := " + plat);
				file.write(newline);

				file.write("else");
				file.write(newline);

				file.write("  PLATFORM :=");
				file.write(newline);

				file.write("endif");
				file.write(newline);
			}

			file.write(newline);
		}

		System.out.println();
		System.out.println("Generated " + utilsmk);
	}

}

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
package org.openj9.envInfo;

import java.io.IOException;
import java.io.Writer;

import org.testKitGen.UtilsGen;

public class EnvDetector {

	public static void main(String[] args) {
		parseArgs(args);
	}

	private static void parseArgs(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String option = args[i].toLowerCase();

			if (option.equals("machineinfo")) {
				getMachineInfo();
			} else if (option.equals("javainfo")) {
				getJavaInfo();
			}
		}
	}

	/*
	 * getJavaInfo() is used for AUTO_DETECT
	 */
	private static void getJavaInfo() {
		JavaInfo envDetection = new JavaInfo();
		String SPECInfo = envDetection.getSPEC();
		int javaVersionInfo = envDetection.getJDKVersion();
		String javaImplInfo = envDetection.getJDKImpl();

		if (SPECInfo == null || javaVersionInfo == -1 || javaImplInfo == null) {
			System.exit(1);
		}

		/**
		 * autoGenEnv.mk file will be created to store auto detected java info.
		 */
		try (Writer output = UtilsGen.openWithHeader("autoGenEnv.mk")) {
			String newline = System.lineSeparator();

			output.write("DETECTED_SPEC := ");
			output.write(SPECInfo);
			output.write(newline);

			output.write("DETECTED_JDK_VERSION := ");
			output.write(Integer.toString(javaVersionInfo));
			output.write(newline);

			output.write("DETECTED_JDK_IMPL := ");
			output.write(javaImplInfo);
			output.write(newline);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static void getMachineInfo() {
		MachineInfo machineInfo = new MachineInfo();

		machineInfo.getMachineInfo(MachineInfo.UNAME_CMD);
		machineInfo.getMachineInfo(MachineInfo.SYS_ARCH_CMD);
		machineInfo.getMachineInfo(MachineInfo.PROC_ARCH_CMD);
		machineInfo.getMachineInfo(MachineInfo.SYS_OS_CMD);
		machineInfo.getMachineInfo(MachineInfo.CPU_CORES_CMD);
		machineInfo.getMachineInfo(MachineInfo.ULIMIT_CMD);

		machineInfo.getRuntimeInfo();
		machineInfo.getSpaceInfo("");

		System.out.println(machineInfo);
	}
}

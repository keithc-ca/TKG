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

public class JavaInfo {

	private static String getAndPrintProperty(String name) {
		String value = System.getProperty(name);

		System.out.println("getAndPrintProperty('" + name + "')=" + value);

		return value;
	}

	public String getSPEC() {
		String osName = getAndPrintProperty("os.name").toLowerCase();
		String osArch = getAndPrintProperty("os.arch").toLowerCase();
		String fullversion = getAndPrintProperty("java.fullversion");
		String spec;

		if (osName.contains("linux")) {
			spec = "linux";
		} else if (osName.contains("win")) {
			spec = "win";
		} else if (osName.contains("mac")) {
			spec = "osx";
		} else if (osName.contains("aix") || osName.contains("nix") || osName.contains("nux")) {
			spec = "aix";
		} else if (osName.contains("z/os")) {
			spec = "zos";
		} else if (osName.contains("sunos")) {
			spec = "sunos";
		} else if (osName.contains("bsd")) {
			spec = "bsd";
		} else {
			System.out.println("Cannot determine System.getProperty('os.name')=" + osName);
			return null;
		}

		if (osArch.contains("amd64") || osArch.contains("x86")) {
			spec += "_x86";
		} else if (osArch.contains("ppc") || osArch.contains("powerpc")) {
			spec += "_ppc";
		} else if (osArch.contains("s390x")) {
			spec += "_390";
		} else if (osArch.contains("aarch")) {
			spec += "_arm";
		} else if (osArch.contains("sparcv9")) {
			spec += "_sparcv9";
		} else {
			System.out.println("Cannot determine System.getProperty('os.arch')=" + osArch);
			return null;
		}

		String model = getAndPrintProperty("sun.arch.data.model");

		if (model.trim().equals("64")) {
			spec += "-64";
			spec = spec.replace("arm-64", "aarch64");
		}

		if (fullversion != null && fullversion.contains("Compressed References")) {
			spec += "_cmprssptrs";
		}

		if (spec.contains("ppc") && osArch.contains("le")) {
			spec += "_le";
		}

		return spec;
	}

	public int getJDKVersion() {
		String javaVersion = System.getProperty("java.version");
		if (javaVersion.startsWith("1.")) {
			javaVersion = javaVersion.substring(2);
		}
		int dotIndex = javaVersion.indexOf('.');
		int dashIndex = javaVersion.indexOf('-');
		try {
			return Integer.parseInt(javaVersion.substring(0, dotIndex > -1 ? dotIndex : dashIndex > -1 ? dashIndex : javaVersion.length()));
		} catch (NumberFormatException e) {
			System.out.println("Cannot determine System.getProperty('java.version')=" + javaVersion);
			return -1;
		}
	}

	public String getJDKImpl() {
		String impl = getAndPrintProperty("java.vm.name").toLowerCase();

		if (impl.contains("ibm")) {
			return "ibm";
		} else if (impl.contains("openj9")) {
			return "openj9";
		} else if (impl.contains("oracle") || impl.contains("hotspot") || impl.contains("openjdk")) {
			return "hotspot";
		} else {
			System.out.println("Cannot determine System.getProperty('java.vm.name')=" + impl);
			return null;
		}
	}

}

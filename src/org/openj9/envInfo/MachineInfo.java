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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MachineInfo {

	public static final String UNAME_CMD = "uname -a";
	public static final String SYS_ARCH_CMD = "uname -m";
	public static final String PROC_ARCH_CMD = "uname -p";
	public static final String ULIMIT_CMD = "ulimit -a";

	public static final String INSTALLED_MEM_CMD = "grep MemTotal /proc/meminfo | awk '{print $2}";
	public static final String FREE_MEM_CMD = "grep MemFree /proc/meminfo | awk '{print $2}";
	public static final String CPU_CORES_CMD = "cat /proc/cpuinfo | grep processor | wc -l";

	public static final String NUMA_CMD = "numactl --show | grep 'No NUMA support available on this system";
	public static final String SYS_VIRT_CMD = "";

	// Software
	public static final String SYS_OS_CMD = "uname -s";
	public static final String KERNEL_VERSION_CMD = "uname -r";
	public static final String GCC_VERSION_CMD = "gcc -dumpversion";

	public static final String XLC_VERSION_CMD = "xlC -qversion | grep 'Version' ";
	public static final String GDB_VERSION_CMD = "gdb --version | head -1"; // debugger on Linux
	public static final String LLDB_VERSION_CMD = "lldb --version"; // debugger on Darwin/Mac
	public static final String GCLIBC_VERSION_CMD = "ldd --version | head -1";

	// Console
	public static final String NUM_JAVA_PROCESSES_CMD = "ps -ef | grep -i [j]ava | wc -l";
	public static final String ACTIVE_JAVA_PROCESSES_CMD = "ps -ef | grep -i [j]ava";

	String uname = "";
	String ulimit = "";
	String installedMem = "";
	String freeMem = "";
	String cpuCores = "";
	String cpuModel = "";
	String cpuSpeed = "";
	String sysArch = "";
	String sysOS = "";
	String procArch = "";
	String numa = "Unknown";
	String sysVirt = "Unknown";
	long totalMemory = -1;
	long freeMemory = -1;

	String vmVendor = "";
	String vmVersion = "";
	String specVendor = "";
	String specVersion = "";
	String javaVersion = "";

	public String toString() {
		String newline = System.lineSeparator();

		return newline //
				+ "uname: " + uname + newline //
				+ "cpuCores: " + cpuCores + newline //
				+ "sysArch: " + sysArch + newline //
				+ "procArch: " + procArch + newline //
				+ "sysOS: " + sysOS + newline //
				+ "ulimit: " + ulimit + newline //
				+ newline //
				+ "vmVendor: " + vmVendor + newline // 
				+ "vmVersion: " + vmVersion + newline //
				+ "specVendor: " + specVendor + newline //
				+ "specVersion: " + specVersion + newline //
				+ "javaVersion: " + javaVersion + newline //
				+ newline //
				+ "Total memory (bytes): " + totalMemory + newline //
				+ "Free memory (bytes): " + freeMemory + newline //
		;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getUname() {
		return uname;
	}

	public String getUlimit() {
		return ulimit;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public void setUlimit(String ulimit) {
		this.ulimit = ulimit;
	}

	public String getVmVendor() {
		return vmVendor;
	}

	public void setVmVendor(String vmVendor) {
		this.vmVendor = vmVendor;
	}

	public String getVmVersion() {
		return vmVersion;
	}

	public void setVmVersion(String vmVersion) {
		this.vmVersion = vmVersion;
	}

	public String getSpecVendor() {
		return specVendor;
	}

	public void setSpecVendor(String specVendor) {
		this.specVendor = specVendor;
	}

	public String getSpecVersion() {
		return specVersion;
	}

	public void setSpecVersion(String specVersion) {
		this.specVersion = specVersion;
	}

	public String getJavaVersion() {
		return javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getInstalledMem() {
		return installedMem;
	}

	public void setInstalledMem(String installedMem) {
		this.installedMem = installedMem;
	}

	public String getFreeMem() {
		return freeMem;
	}

	public void setFreeMem(String freeMem) {
		this.freeMem = freeMem;
	}

	public String getCpuCores() {
		return cpuCores;
	}

	public void setCpuCores(String cpuCores) {
		this.cpuCores = cpuCores;
	}

	public String getCpuModel() {
		return cpuModel;
	}

	public void setCpuModel(String cpuModel) {
		this.cpuModel = cpuModel;
	}

	public String getCpuSpeed() {
		return cpuSpeed;
	}

	public void setCpuSpeed(String cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
	}

	public String getSysArch() {
		return sysArch;
	}

	public void setSysArch(String sysArch) {
		this.sysArch = sysArch;
	}

	public String getSysOS() {
		return sysOS;
	}

	public void setSysOS(String sysOS) {
		this.sysOS = sysOS;
	}

	public String getProcArch() {
		return procArch;
	}

	public void setProcArch(String procArch) {
		this.procArch = procArch;
	}

	public String getNuma() {
		return numa;
	}

	public void setNuma(String numa) {
		this.numa = numa;
	}

	public String getSysVirt() {
		return sysVirt;
	}

	public void setSysVirt(String sysVirt) {
		this.sysVirt = sysVirt;
	}

	public void getRuntimeInfo() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

		setVmVendor(runtimeMXBean.getVmVendor());
		setVmVersion(runtimeMXBean.getVmVersion());
		setSpecVendor(runtimeMXBean.getSpecVendor());
		setSpecVersion(runtimeMXBean.getSpecVersion());
		setJavaVersion(System.getProperty("java.version"));

		setFreeMemory(Runtime.getRuntime().freeMemory());
		setTotalMemory(Runtime.getRuntime().totalMemory());
	}

	public void getMachineInfo(String command) {
		try {
			Process proc;
			// ulimit needs to be invoked via shell with -c option for it to work on all platforms
			if (command.equals(MachineInfo.ULIMIT_CMD)) {
				proc = Runtime.getRuntime().exec(new String[] { "bash", "-c", MachineInfo.ULIMIT_CMD });
			} else {
				proc = Runtime.getRuntime().exec(command);
			}

			BufferedReader sout = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = sout.readLine();

			if (command.equals(MachineInfo.UNAME_CMD)) {
				setUname(line);
			} else if (command.equals(MachineInfo.ULIMIT_CMD)) {
				String rest = "";
				while (true) {
					rest = sout.readLine();
					if (rest == null) {
						break;
					}
					line = line + "\n" + rest;
				}
				setUlimit(line);
			} else if (command.equals(MachineInfo.SYS_ARCH_CMD)) {
				setSysArch(line);
			} else if (command.equals(MachineInfo.PROC_ARCH_CMD)) {
				setProcArch(line);
			} else if (command.equals(MachineInfo.SYS_OS_CMD)) {
				setSysOS(line);
			} else if (command.equals(MachineInfo.CPU_CORES_CMD)) {
				setCpuCores(line);
				if (getCpuCores() == null) {
					setCpuCores("" + Runtime.getRuntime().availableProcessors());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getSpaceInfo(String path) {
		Path userPath = Paths.get(path);
		File file = userPath.toAbsolutePath().toFile();

		System.out.println("File path: " + file.getAbsolutePath());
		System.out.println("Total space (bytes): " + file.getTotalSpace());
		System.out.println("Free space (bytes): " + file.getFreeSpace());
		System.out.println("Usable space (bytes): " + file.getUsableSpace());
	}

}

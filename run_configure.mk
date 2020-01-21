##############################################################################
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##############################################################################

# If AUTO_DETECT is enabled, compile and execute EnvDetector in TestKitGen.jar,
# otherwise, just call makeGen.mk.

ifndef TEST_JDK_HOME
$(error Please provide TEST_JDK_HOME value.)
endif
export TEST_JDK_HOME := $(subst \,/,$(TEST_JDK_HOME))

.PHONY : testconfig
testconfig :
	ant -f scripts/build_tools.xml -DTEST_JDK_HOME=$(TEST_JDK_HOME)
ifneq ($(AUTO_DETECT), false)
	@echo "AUTO_DETECT is enabled"
	${TEST_JDK_HOME}/bin/java -cp bin/TestKitGen.jar org.openj9.envInfo.EnvDetector JavaInfo
else
	@echo "AUTO_DETECT is disabled"
endif
	$(MAKE) -f makeGen.mk AUTO_DETECT=$(AUTO_DETECT)

.PHONY : clean
clean :
	ant -f scripts/build_tools.xml clean

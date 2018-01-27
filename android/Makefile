
archs=arm64 armv7 x86
TOPDIR=$(shell pwd)

.PHONY: all
.NOTPARALLEL: all generate-apk install release

all: generate-apk

build: $(addsuffix -build, $(archs))

clean: java-clean

install: install-apk run-linphone

install-test:
	$(MAKE) -C liblinphone_tester copy-libs
	$(MAKE) -C liblinphone_tester copy-files
	./gradlew -b liblinphone_tester/build.gradle assembleDebug
	./gradlew -b liblinphone_tester/build.gradle installDebug

java-clean:
	./gradlew clean

$(TOPDIR)/res/raw/rootca.pem:
	cp liblinphone-sdk/android-arm64/share/linphone/rootca.pem $@

copy-libs:
	rm -rf libs-debug/armeabi
	rm -rf libs/armeabi
	if test -d "liblinphone-sdk/android-arm"; then \
		mkdir -p libs-debug/armeabi && \
		cp -f liblinphone-sdk/android-arm/lib/lib*.so libs-debug/armeabi && \
		cp -f liblinphone-sdk/android-arm/lib/mediastreamer/plugins/*.so libs-debug/armeabi && \
		mkdir -p libs/armeabi && \
		cp -f liblinphone-sdk/android-arm/lib/lib*.so libs/armeabi && \
		cp -f liblinphone-sdk/android-arm/lib/mediastreamer/plugins/*.so libs/armeabi && \
		sh WORK/android-arm/strip.sh libs/armeabi/*.so; \
	fi
	if test -f "liblinphone-sdk/android-arm/bin/gdbserver"; then \
		cp -f liblinphone-sdk/android-arm/bin/gdbserver libs-debug/armeabi && \
		cp -f liblinphone-sdk/android-arm/bin/gdb.setup libs-debug/armeabi && \
		cp -f liblinphone-sdk/android-arm/bin/gdbserver libs/armeabi && \
		cp -f liblinphone-sdk/android-arm/bin/gdb.setup libs/armeabi; \
	fi
	rm -rf libs-debug/armeabi-v7a
	rm -rf libs/armeabi-v7a
	if test -d "liblinphone-sdk/android-armv7"; then \
		mkdir -p libs-debug/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/lib/lib*.so libs-debug/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/lib/mediastreamer/plugins/*.so libs-debug/armeabi-v7a && \
		mkdir -p libs/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/lib/lib*.so libs/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/lib/mediastreamer/plugins/*.so libs/armeabi-v7a && \
		sh WORK/android-armv7/strip.sh libs/armeabi-v7a/*.so; \
	fi
	if test -f "liblinphone-sdk/android-armv7/bin/gdbserver"; then \
		cp -f liblinphone-sdk/android-armv7/bin/gdbserver libs-debug/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/bin/gdb.setup libs-debug/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/bin/gdbserver libs/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/bin/gdb.setup libs/armeabi-v7a; \
	fi
	rm -rf libs-debug/arm64-v8a
	rm -rf libs/arm64-v8a
	if test -d "liblinphone-sdk/android-arm64"; then \
		mkdir -p libs-debug/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/lib/lib*.so libs-debug/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/lib/mediastreamer/plugins/*.so libs-debug/arm64-v8a && \
		mkdir -p libs/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/lib/lib*.so libs/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/lib/mediastreamer/plugins/*.so libs/arm64-v8a && \
		sh WORK/android-arm64/strip.sh libs/arm64-v8a/*.so; \
	fi
	if test -f "liblinphone-sdk/android-arm64/bin/gdbserver"; then \
		cp -f liblinphone-sdk/android-arm64/bin/gdbserver libs-debug/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/bin/gdb.setup libs-debug/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/bin/gdbserver libs/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/bin/gdb.setup libs/arm64-v8a; \
	fi
	rm -rf libs-debug/x86
	rm -rf libs/x86
	if test -d "liblinphone-sdk/android-x86"; then \
		mkdir -p libs-debug/x86 && \
		cp -f liblinphone-sdk/android-x86/lib/lib*.so libs-debug/x86 && \
		cp -f liblinphone-sdk/android-x86/lib/mediastreamer/plugins/*.so libs-debug/x86 && \
		mkdir -p libs/x86 && \
		cp -f liblinphone-sdk/android-x86/lib/lib*.so libs/x86 && \
		cp -f liblinphone-sdk/android-x86/lib/mediastreamer/plugins/*.so libs/x86 && \
		sh WORK/android-x86/strip.sh libs/x86/*.so; \
	fi
	if test -f "liblinphone-sdk/android-x86/bin/gdbserver"; then \
		cp -f liblinphone-sdk/android-x86/bin/gdbserver libs-debug/x86 && \
		cp -f liblinphone-sdk/android-x86/bin/gdb.setup libs-debug/x86 && \
		cp -f liblinphone-sdk/android-x86/bin/gdbserver libs/x86 && \
		cp -f liblinphone-sdk/android-x86/bin/gdb.setup libs/x86; \
	fi

copy-libs-mediastreamer:
	rm -rf submodules/mediastreamer2/java/libs/armeabi
	if test -d "liblinphone-sdk/android-arm"; then \
		mkdir -p submodules/mediastreamer2/java/libs/armeabi && \
		cp -f liblinphone-sdk/android-arm/lib/*mediastreamer*.so submodules/mediastreamer2/java/libs/armeabi && \
		sh WORK/android-arm/strip.sh submodules/mediastreamer2/java/libs/armeabi/*.so; \
	fi
	rm -rf submodules/mediastreamer2/java/libs/armeabi-v7a
	if test -d "liblinphone-sdk/android-armv7"; then \
		mkdir -p submodules/mediastreamer2/java/libs/armeabi-v7a && \
		cp -f liblinphone-sdk/android-armv7/lib/*mediastreamer*.so submodules/mediastreamer2/java/libs/armeabi-v7a && \
		sh WORK/android-armv7/strip.sh submodules/mediastreamer2/java/libs/armeabi-v7a/*.so; \
	fi
	rm -rf submodules/mediastreamer2/java/libs/arm64-v8a
	if test -d "liblinphone-sdk/android-arm64"; then \
		mkdir -p submodules/mediastreamer2/java/libs/arm64-v8a && \
		cp -f liblinphone-sdk/android-arm64/lib/*mediastreamer*.so submodules/mediastreamer2/java/libs/arm64-v8a && \
		sh WORK/android-arm64/strip.sh submodules/mediastreamer2/java/libs/arm64-v8a/*.so; \
	fi
	rm -rf submodules/mediastreamer2/java/libs/x86
	if test -d "liblinphone-sdk/android-x86"; then \
		mkdir -p submodules/mediastreamer2/java/libs/x86 && \
		cp -f liblinphone-sdk/android-x86/lib/*mediastreamer*.so submodules/mediastreamer2/java/libs/x86 && \
		sh WORK/android-x86/strip.sh submodules/mediastreamer2/java/libs/x86/*.so; \
	fi

generate-apk: java-clean build copy-libs $(TOPDIR)/res/raw/rootca.pem
	./gradlew assembleDebug

quick: clean install-apk run-linphone

install-apk:
	./gradlew installDebug

uninstall:
	./gradlew uninstallAll

release: java-clean build copy-libs
	./gradlew assembleRelease

unsigned: java-clean build copy-libs
	./gradlew assemblePackaged

generate-sdk: liblinphone-android-sdk

generate-javadoc:
	./gradlew -b libLinphoneAndroidSdk.gradle androidJavadocsJar
	./gradlew -b libLinphoneAndroidSdk.gradle sourcesJar

liblinphone-android-sdk: java-clean build copy-libs $(TOPDIR)/res/raw/rootca.pem
	./gradlew -b libLinphoneAndroidSdk.gradle androidJavadocsJar
	./gradlew -b libLinphoneAndroidSdk.gradle sourcesJar
	./gradlew -b libLinphoneAndroidSdk.gradle assembleRelease
	@mv $(TOPDIR)/bin/outputs/aar/*.aar $(TOPDIR)/bin/outputs/aar/liblinphone-sdk.aar
	./gradlew -b libLinphoneAndroidSdk.gradle sdkZip

linphone-android-sdk: java-clean build copy-libs $(TOPDIR)/res/raw/rootca.pem
	./gradlew -b linphoneAndroidSdk.gradle androidJavadocsJar
	./gradlew -b linphoneAndroidSdk.gradle sourcesJar
	./gradlew -b linphoneAndroidSdk.gradle assembleRelease
	./gradlew -b linphoneAndroidSdk.gradle sdkZip

mediastreamer2-sdk: build copy-libs-mediastreamer
	@cd $(TOPDIR)/submodules/mediastreamer2/java && \
	./gradlew -b mediastreamerSdk.gradle assembleRelease
	@cd $(TOPDIR)/submodules/mediastreamer2/java && \
	./gradlew -b mediastreamerSdk.gradle sdkZip

liblinphone_tester:
	$(MAKE) -C liblinphone_tester

run-linphone:
	./gradlew runApplication

run-liblinphone-tests:
	@cd liblinphone_tester && \
	make run-all-tests

run-all-tests: clean install
	patch -p1 < test.patch
	./gradlew grantDebugPermissions
	./gradlew connectedAndroidTest
	patch -Rp1 < test.patch

pull-transifex:
	tx pull -af

push-transifex:
	tx push -s -f --no-interactive


arm64: arm64-build

arm64-build:
	$(MAKE) -C WORK/android-arm64/cmake
	@echo "Done"

armv7: armv7-build

armv7-build:
	$(MAKE) -C WORK/android-armv7/cmake
	@echo "Done"

x86: x86-build

x86-build:
	$(MAKE) -C WORK/android-x86/cmake
	@echo "Done"


help-prepare-options:
	@echo "prepare.py was previously executed with the following options:"
	@echo "   ./prepare.py"

help: help-prepare-options
	@echo ""
	@echo "(please read the README.md file first)"
	@echo ""
	@echo "Available architectures: arm64 armv7 x86"
	@echo ""
	@echo "Available targets:"
	@echo ""
	@echo "   * all or generate-apk: builds all architectures and creates the linphone application APK"
	@echo "   * generate-sdk: builds all architectures and creates the liblinphone SDK"
	@echo "   * install: install the linphone application APK (run this only after generate-apk)"
	@echo "   * uninstall: uninstall the linphone application"
	@echo ""

PREFIX=/usr/local
DEBUG=0
COREMAKE=./corec/tools/coremake/coremake

bits_64=$(shell uname -a | grep x86_64)
os_linux=$(shell uname -a | grep Linux)
os_mac=$(shell uname -a |grep Darwin)
os_mingw32=$(shell uname -a |grep MINGW32)

ifneq ($(os_linux),)
os=linux
endif

ifneq ($(os_mac),)
os=osx
endif

ifneq ($(os_mingw32),)
os=win32
endif

ifneq ($(bits_64),)
bits=_x64
endif

ifeq ($(DEBUG), 1)
debug=_debug
endif

flavor=gcc_$(os)$(bits)$(debug)

libs=release/$(flavor)/libebml2.a release/$(flavor)/libmatroska2.a

all: GNUmakefile
	$(MAKE) -f GNUmakefile

$(COREMAKE):
	cd ./corec/tools/coremake/ && make

GNUmakefile: $(COREMAKE)
	$(COREMAKE) $(flavor)



install:
	mkdir -p $(DESTDIR)/$(PREFIX)
	cp -rf ./libmatroska2/matroska $(DESTDIR)/$(PREFIX)/include/.
	cp -rf ./corec/corec $(DESTDIR)/$(PREFIX)/include/.
	cp -rf ./libebml2/ebml $(DESTDIR)/$(PREFIX)/include/.
	cp -f  ./config.h $(DESTDIR)/$(PREFIX)/include/corec/.
	cp -f  ./corec/tools/coremake/config_helper.h $(DESTDIR)/$(PREFIX)/include/corec/.
	cp -rf $(libs) $(DESTDIR)/$(PREFIX)/lib/.

BINS = step0_repl step1_read_print

STEPS = $(BINS)

STEP1_DEPS = eggs/lib.util.o eggs/lib.types.o \
             eggs/lib.reader.o eggs/lib.printer.o

STEP1_USES = lib.util,lib.types,lib.reader,lib.printer

EGG_DEPS = eggs/miscmacros.o eggs/comparse.o

# CHICKEN = CHICKEN_INSTALL_REPOSITORY=$(CURDIR)/eggs \
#           CHICKEN_REPOSITORY=$(CURDIR)/eggs csc -O3 -static
CHICKEN = csc -O3 -static
CHICKENLIB = $(CHICKEN) -cJ -static

SCM = $(CHICKEN)
SCMLIB = $(CHICKENLIB)

MKDIR = mkdir -p
SYMLINK = ln -sfr
RM = rm -f
RMR = rm -rf

all: $(STEPS)

.PHONY: clean
.PRECIOUS: lib/%.scm eggs/lib.%.scm

eggs/miscmacros.o:
#	chicken-install -init eggs
# CHICKEN_INSTALL_REPOSITORY=$(CURDIR)/eggs \
# CHICKEN_REPOSITORY_PATH=$(CURDIR)/eggs chicken-install miscmacros
	CHICKEN_INSTALL_REPOSITORY=$(CURDIR)/eggs chicken-install miscmacros
eggs/comparse.o:
	chicken-install comparse

eggs/lib.%.scm: lib/%.scm $(EGG_DEPS)
	$(SYMLINK) $< $@

eggs/lib.%.o: eggs/lib.%.scm
	$(SCMLIB) -unit lib.$* $<

%: %.scm $(STEP1_DEPS)
	$(SCM) -uses $(STEP1_USES) $< $(STEP1_DEPS)

step0_repl: $(EGG_DEPS)
step1_read_print.scm step2_eval.scm: $(STEP1_DEPS)

clean:
	$(RM) eggs/*
	$(RMR) out

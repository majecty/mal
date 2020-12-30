BINS = step0_repl

STEPS = $(BINS)

STEP1_DEPS = eggs/lib.util.so eggs/lib.types.so \
             eggs/lib.reqder.so eggs/lib.printer.so

CHICKEN = CHICKEN_REPOSITORY=$(CURDIR)/eggs csc -O3
CHICKENLIB = $(CHICKEN) -sJ

SCM = $(CHICKEN)
SCMLIB = $(CHICKENLIB)

MKDIR = mkdir -p
SYMLINK = ln -sfr
RM = rm -f
RMR = rm -rf

all: $(STEPS)

.PHONY: clean
.PRECIOUS: lib/%.scm eggs/lib.%.scm

eggs/miscmacros.so:
#	chicken-install -init eggs
	CHICKEN_INSTALL_REPOSITORY=$(CURDIR)/eggs \
	CHICKEN_REPOSITORY_PATH=$(CURDIR)/eggs chicken-install miscmacros

eggs/lib.%.scm: lib/%.sld
	$(SYMLINK) $< $@

eggs/lib.%.so: eggs/lib.%.scm
	$(SCMLIB) $<

%: %.scm
	$(SCM) $<

step0_repl: eggs/miscmacros.so
# step1_read_print.scm step2_eval.scm: $(STEP1_DEPS)

clean:
	$(RM) eggs/*
	$(RMR) out

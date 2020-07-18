MAKEFLAGS += --warn-undefined-variables
SHELL = /bin/bash
.SHELLFLAGS := -eu -o pipefail -c
.DEFAULT_GOAL := all
.DELETE_ON_ERROR:
.SUFFIXES:

TIMESTAMPED_VERSION := $(shell /bin/date "+%Y.%m.%d-%H%M%S")
CURRENT_DATE := $(shell /bin/date "+%Y-%m-%d")
USERNAME := $(shell id -u -n)
USERID := $(shell id -u)
GREEN  := $(shell tput -Txterm setaf 2)
WHITE  := $(shell tput -Txterm setaf 7)
YELLOW := $(shell tput -Txterm setaf 3)
RESET  := $(shell tput -Txterm sgr0)

HELP_FUN = \
    %help; \
    while(<>) { push @{$$help{$$2 // 'targets'}}, [$$1, $$3] if /^([a-zA-Z0-9\-]+)\s*:.*\#\#(?:@([a-zA-Z\-]+))?\s(.*)$$/ }; \
    print "usage: make [target]\n\n"; \
    for (sort keys %help) { \
    print "${WHITE}$$_:${RESET}\n"; \
    for (@{$$help{$$_}}) { \
    $$sep = " " x (32 - length $$_->[0]); \
    print "  ${YELLOW}$$_->[0]${RESET}$$sep${GREEN}$$_->[1]${RESET}\n"; \
    }; \
    print "\n"; }

.PHONY: all
all: help

help: ##@other Show this help
	@perl -e '$(HELP_FUN)' $(MAKEFILE_LIST)

.PHONY: build
build: ##@hacking Build everything
	mvn verify

.PHONY: native-image
native-image: ##@hacking Create a native image using GraalVM
	mvn verify -Dskip.graal=false

.PHONY: clean
clean: ##@hacking Clean build artifacts
	mvn clean

.PHONY: example-redis-java11
example-redis-java11: ##@example Example shell using compose w/ redis & java 11
	ilo @examples/compose/redis-with-java11/build-env

.PHONY: sign-waiver
sign-waiver: ##@contributing Sign the WAIVER
	gpg2 --no-version --armor --sign AUTHORS/WAIVER
	mv AUTHORS/WAIVER.asc AUTHORS/WAIVER-signed-by-$(USERNAME)-$(CURRENT_DATE).asc

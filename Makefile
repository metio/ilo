# SPDX-FileCopyrightText: The ilo Authors
# SPDX-License-Identifier: 0BSD

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

.PHONY: help
help: ##@other Show this help
	@perl -e '$(HELP_FUN)' $(MAKEFILE_LIST)

.PHONY: build
build: ##@hacking Build everything
	mvn verify

.PHONY: native-image
native-image: ##@hacking Create a native image using GraalVM
	mvn verify --define skipNativeBuild=false

.PHONY: clean
clean: ##@hacking Clean build artifacts
	mvn clean

.PHONY: ilo-build
ilo-build: ##@hacking Build everything with ilo
	ilo @dev/build

.PHONY: ilo-native
ilo-native: ##@hacking Create a native image using GraalVM with ilo
	ilo @dev/native

.PHONY: ilo-env
ilo-env: ##@hacking Open a new development environment with ilo
	ilo @dev/env

.PHONY: ilo-website
ilo-website: ##@hacking Build the website with ilo
	ilo @dev/website

.PHONY: ilo-serve
ilo-serve: ##@hacking Serve the website locally with ilo
	ilo @dev/serve

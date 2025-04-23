SHELL=/bin/bash
.EXPORT_ALL_VARIABLES:
.SHELLFLAGS = -uec -o pipefail

## ENV options are in environments folder. Example: ENV=mock-az-storage-uw-sb make plan
ENV := $(if $(ENV),$(ENV),$(error missing ENV. Example: ENV=mock-az-storage-uw-sb make plan))
ENV_PREFIX = environments/$(ENV)

# Standard Commands
RM = rm -rf
TF = terraform

# Standard terraform options
TF_BACKEND_CONFIG = environments/$(ENV).backend.tfvars
TF_OPTIONS        = -input=false
TF_OUTPUT_INIT    = output/$(ENV)_init.txt
TF_OUTPUT_PLAN    = output/$(ENV).tf-plan
TF_PULLED_STATE   = output/${ENV}_pulled.tfstate

default: help

init:
	mkdir -p $$(dirname ${TF_OUTPUT_INIT})
	${RM} .terraform
	${RM} .terraform.lock.hcl
	${RM} ${ENV}.settings.auto.tfvars
	terraform --version
	${TF} init -backend-config="${TF_BACKEND_CONFIG}" 2>&1 | tee ${TF_OUTPUT_INIT}

plan: init ## run terraform plan
	cp environments/${ENV}.settings.tfvars ${ENV}.settings.auto.tfvars
	${TF} plan ${TF_OPTIONS} 2>&1 | tee ${TF_OUTPUT_PLAN}

migrate-state: ## Pull remote state from storage and Push to env0
	terraform init -migrate-state -force-copy

pull-remote-state: ## Pull remote state from storage
	${TF} state pull > ${TF_PULLED_STATE}
	echo "ran 'terraform state pull' remote state downloaded to ${TF_PULLED_STATE}."

push-remote-state: ## Push local pulled state to remote storage
	${TF} state push -lock=true ${TF_PULLED_STATE}
	echo "ran 'terraform state push' remote state ${TF_PULLED_STATE} uploaded."

.PHONY: help
help: ## Show make targets
	@grep -hE '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: clean
clean: ## Removes all files in the .gitignore
	git clean -fdX

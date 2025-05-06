SHELL=/bin/bash
.EXPORT_ALL_VARIABLES:
.SHELLFLAGS = -uec -o pipefail

## ENV options are in environments folder. Example: ENV=remote-state-uw-sb make plan
ENV := $(if $(ENV),$(ENV),poc-pin-reset-uw-d)
ENV_PREFIX = environments/$(ENV)

# Standard Commands
RM = rm -rf
TF = TF_WORKSPACE=$(ENV) terraform

# Standard terraform options
TF_BACKEND_CONFIG = environments/$(ENV).backend.tfvars
TF_VARS           = environments/$(ENV).settings.tfvars
TF_OPTIONS        = -var-file="${TF_BACKEND_CONFIG}" -input=false
TF_OUTPUT_APPLY   = output/$(ENV)_apply.txt
TF_OUTPUT_DESTROY = output/$(ENV)_destroy.txt
TF_OUTPUT_INIT    = output/$(ENV)_init.txt
TF_OUTPUT_JSON    = output/$(ENV)_output.json
TF_OUTPUT_PLAN    = output/$(ENV)_plan.txt
TF_PULLED_STATE   = output/${ENV}_pulled.tfstate
TF_VERSION        = 1.3.1

default: help

init:
	mkdir -p $$(dirname ${TF_OUTPUT_INIT})
	${RM} .terraform
	terraform --version
	${TF} init -backend-config="${TF_BACKEND_CONFIG}" 2>&1 | tee ${TF_OUTPUT_INIT}

plan: init ## run terraform plan
	${TF} plan -var-file="${TF_VARS}" ${TF_OPTIONS} 2>&1 | tee ${TF_OUTPUT_PLAN}

apply: init ## run terraform apply
	${TF} apply -var-file="${TF_VARS}" ${TF_OPTIONS} --auto-approve 2>&1 | tee ${TF_OUTPUT_APPLY}
	${TF} output -json > ${TF_OUTPUT_JSON}

destroy: init ## run terraform destroy
	${TF} destroy -var-file="${TF_VARS}" ${TF_OPTIONS} --auto-approve 2>&1 | tee ${TF_OUTPUT_DESTROY}

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

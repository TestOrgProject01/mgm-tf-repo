data "azurerm_client_config" "default" {}

module "globals" {
  source  = "api.env0.com/39137cc6-6aa7-48c6-b52d-f7a5e7748e87/mgm-af-globals/terraform"
  version = "~> v1"

  ### REQUIRED INPUT VARIABLES ###
  department  = "it"
  environment = var.environment
  project     = "${var.project_prefix}-${var.project_suffix}"

  ### OVERRIDE INPUT VARIABLES ###
  # azure_subscription = data.azurerm_client_config.default.subscription_id This can be set after module list is updated
  azure_subscription = "Information Technology Gaming & Hospitality Development"
  azure_tenant_id    = data.azurerm_client_config.default.tenant_id
  environment_suffix = "d"
  location           = var.location

  ### OVERRIDE TAG VARIABLES ###
  tags_business_cost_center = "516-800-80182"
  tags_business_sponsor     = "Digital Engineering"
  tags_compliance           = "None"
  tags_created_by           = "Unknown"
  tags_expired_by           = "9999-01-01T00:00:00.001Z"
  tags_group_email          = "Unknown"
  tags_product_name         = "Unknown"
  tags_project_lead         = "Marc San Pedro"
  tags_service_name         = "Unknown"
  tags_uptime               = "Unknown"

  ### CUSTOM TAGS MAP ###
  tags_custom = {}
}

module "resource_group" {
  source = "api.env0.com/39137cc6-6aa7-48c6-b52d-f7a5e7748e87/mgm-af-azure-resource-group/azurerm"
  #source = "git@github.com:MGMResorts/mgm-af-azure-resource-group.git?ref=v1" # Use if not env0, requires github access.
  version  = "~> 1.0" # Comment out if using github source, pin to snapshot tag if tagged from develop
  name     = "${var.project_prefix}-rg-${var.project_suffix}"
  location = var.location
  tags     = module.globals.tags
}

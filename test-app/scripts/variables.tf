variable "tags" {}
variable "environment" {
  type        = string
  description = "Name of environment tier that infrastructure will be deployed to. Options are 'production', 'performance', 'test', and 'development'."
  validation {
    condition = contains(
      ["production", "performance", "test", "development"],
      var.environment
    )
    error_message = "Err: not a valid environment. Use production, performance, test, or development."
  }
}
variable "location" {
  type        = string
  description = "Region that infrastructure will be deployed to. (e.g. 'West US')."
  default     = "West US"
}

variable "project_prefix" {}
variable "project_suffix" {}

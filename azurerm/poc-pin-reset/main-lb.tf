resource "azurerm_public_ip" "default" {
  name                = "${var.project_prefix}-pip-${var.project_suffix}"
  location            = var.location
  resource_group_name = module.resource_group.name
  allocation_method   = "Static"
  sku                 = "Standard"

  lifecycle {
    ignore_changes = [
      tags,
    ]
  }

  depends_on = [module.resource_group]
}

# resource "azurerm_application_gateway" "default" {
#   name                = "${var.project_prefix}-ag-${var.project_suffix}"
#   location            = var.location
#   resource_group_name = module.resource_group.name

#   enable_http2 = true

#   sku {
#     name     = "Standard_v2"
#     tier     = "Standard_v2"
#     capacity = 2
#   }

#   gateway_ip_configuration {
#     name      = data.azurerm_subnet.ace-pin-reset-uw-sn-d.name
#     subnet_id = data.azurerm_subnet.ace-pin-reset-uw-sn-d.id
#   }

#   frontend_ip_configuration {
#     name                 = "public_ip"
#     public_ip_address_id = azurerm_public_ip.default.id
#   }

#   #
#   # Non-SSL
#   #

#   frontend_port {
#     name = "port_80"
#     port = 80
#   }

#   http_listener {
#     name                           = "listener_http"
#     frontend_ip_configuration_name = "public_ip"
#     frontend_port_name             = "port_80"
#     protocol                       = "Http"
#   }

#   request_routing_rule {
#     name                        = "http_redirect_https"
#     http_listener_name          = "listener_http"
#     redirect_configuration_name = "http_redirect_https"
#     rule_type                   = "Basic"
#     priority                    = 1
#   }

#   # Request Configuration for Https
#   redirect_configuration {
#     name                 = "http_redirect_https"
#     target_listener_name = "listener_https"
#     redirect_type        = "Permanent"
#     include_path         = true
#     include_query_string = true
#   }

#   #
#   # SSL
#   #

#   frontend_port {
#     name = "port_443"
#     port = 443
#   }

#   http_listener {
#     name                           = "listener_https"
#     frontend_ip_configuration_name = "public_ip"
#     frontend_port_name             = "port_443"
#     protocol                       = "Https"
#     ssl_certificate_name           = "default"
#   }

#   request_routing_rule {
#     name                       = "default"
#     rule_type                  = "Basic"
#     http_listener_name         = "listener_https"
#     backend_address_pool_name  = "default"
#     backend_http_settings_name = "default"
#     priority                    = 2
#   }

#   backend_http_settings {
#     name                  = "default"
#     cookie_based_affinity = "Disabled"
#     path                  = "/mgmresorts/home"
#     port                  = 443
#     protocol              = "Https"
#     request_timeout       = 60
#     probe_name            = "default"

#     pick_host_name_from_backend_address = true
#   }

#   probe {
#     name                = "default"
#     protocol            = "Https"
#     path                = "/mgmresorts/home"
#     interval            = 1
#     timeout             = 10
#     unhealthy_threshold = 3

#     pick_host_name_from_backend_http_settings = true
#   }

#   #
#   # Backend
#   #

#   backend_address_pool {
#     name         = "default"
#     ip_addresses = [azurerm_linux_virtual_machine.default.private_ip_address]
#   }

#   ssl_certificate {
#     name     = "default"
#     password = "default"
#     data     = filebase64("secrets/default.pfx")
#   }

#   lifecycle {
#     # Ignore all changes because AKS manages this resource after it's provisioned
#     # ignore_changes = all not working with recent upgrade of TF 1.3.1
#     # Being more explicit works though
#     ignore_changes = [
#       frontend_ip_configuration,
#       gateway_ip_configuration,
#       redirect_configuration,
#       backend_http_settings,
#       request_routing_rule,
#       frontend_port,
#       http_listener,
#       url_path_map,
#       probe,
#       tags,
#       sku
#     ]
#   }

#   depends_on = [module.resource_group]
# }

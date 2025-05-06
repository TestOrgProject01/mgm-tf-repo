data "azurerm_virtual_network" "default" {
  name                = "gaminghospitality-uw-vnet-d"
  resource_group_name = "gaminghospitalityvnets-uw-rg-d"
}

data "azurerm_subnet" "ace-pin-reset-uw-sn-d" {
  name                 = "ace-pin-reset-uw-sn-d"
  virtual_network_name = "gaminghospitality-uw-vnet-d"
  resource_group_name  = "gaminghospitalityvnets-uw-rg-d"
}

resource "tls_private_key" "default" {
  algorithm = "RSA"
  rsa_bits  = 2048
}

module "resource_group" {
  source   = "api.env0.com/39137cc6-6aa7-48c6-b52d-f7a5e7748e87/mgm-af-azure-resource-group/azurerm"
  version  = "~> v0"

  name     = "${var.project_prefix}-rg-${var.project_suffix}"
  location = var.location
  tags     = {}
}

resource "local_sensitive_file" "ssh" {
  content         = tls_private_key.default.private_key_openssh
  filename        = "secrets/${var.project_prefix}-vm-${var.project_suffix}"
  file_permission = "0600"
}

resource "azurerm_network_interface" "default" {
  name                = "${var.project_prefix}-nic-${var.project_suffix}"
  location            = var.location
  resource_group_name = module.resource_group.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = data.azurerm_subnet.ace-pin-reset-uw-sn-d.id
    private_ip_address_allocation = "Dynamic"
  }

  depends_on = [module.resource_group]
}

resource "azurerm_linux_virtual_machine" "default" {
  name                = "${var.project_prefix}-vm-${var.project_suffix}"
  location            = var.location
  resource_group_name = module.resource_group.name
  size                = "Standard_B2s"
  admin_username      = "mgmadmin"
  network_interface_ids = [
    azurerm_network_interface.ubuntu_20_04.id,
  ]

  admin_ssh_key {
    username   = "mgmadmin"
    public_key = tls_private_key.default.public_key_openssh
  }

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-minimal-jammy"
    sku       = "minimal-22_04-lts"
    version   = "latest"
  }

  depends_on = [module.resource_group]
}

resource "azurerm_network_interface" "ubuntu_20_04" {
  name                = "${var.project_prefix}-nic-20-04-${var.project_suffix}"
  location            = var.location
  resource_group_name = module.resource_group.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = data.azurerm_subnet.ace-pin-reset-uw-sn-d.id
    private_ip_address_allocation = "Dynamic"
  }

  depends_on = [module.resource_group]
}

resource "azurerm_linux_virtual_machine" "ubuntu_20_04" {
  name                = "${var.project_prefix}-vm-20-04-${var.project_suffix}"
  location            = var.location
  resource_group_name = module.resource_group.name
  size                = "Standard_D8s_v3"
  admin_username      = "mgmadmin"
  network_interface_ids = [
    azurerm_network_interface.default.id,
  ]

  admin_ssh_key {
    username   = "mgmadmin"
    public_key = tls_private_key.default.public_key_openssh
  }

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "0001-com-ubuntu-minimal-focal"
    sku       = "minimal-20_04-lts"
    version   = "latest"
  }

  depends_on = [module.resource_group]
}

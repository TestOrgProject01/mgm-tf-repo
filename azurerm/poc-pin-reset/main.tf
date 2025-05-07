data "azurerm_virtual_network" "default" {
  name                = "gaminghospitality-uw-vnet-d"
  resource_group_name = "gaminghospitalityvnets-uw-rg-d"
}

data "azurerm_subnet" "ace-pin-reset-uw-sn-d" {
  name                 = "ace-pin-reset-uw-sn-d"
  virtual_network_name = "gaminghospitality-uw-vnet-d"
  resource_group_name  = "gaminghospitalityvnets-uw-rg-d"
}

resource "azurerm_storage_account" "insecure_storage" {
  name                     = "insecurestorageacct"
  resource_group_name      = "myResourceGroup"
  location                 = "East US"
  account_tier             = "Standard"
  account_replication_type = "LRS"

  # Insecure: Allow public access
  allow_blob_public_access = true
}

resource "azurerm_key_vault" "insecure_kv" {
  name                        = "insecurekeyvault"
  location                    = "East US"
  resource_group_name         = "myResourceGroup"
  tenant_id                   = "00000000-0000-0000-0000-000000000000"
  sku_name                    = "standard"
  purge_protection_enabled    = false  # Insecure: should be true
  soft_delete_retention_days  = 7
  soft_delete_enabled         = true   # okay, but with purge off it's vulnerable

  access_policy {
    tenant_id = "00000000-0000-0000-0000-000000000000"
    object_id = "11111111-1111-1111-1111-111111111111"

    key_permissions = [
      "get",
      "list",
      "delete",  # Excessive permission
    ]

    secret_permissions = [
      "get",
      "list",
      "delete",
    ]
  }
}

resource "tls_private_key" "default" {
  algorithm = "RSA"
  rsa_bits  = 1024
}

resource "azurerm_network_interface" "default" {
  name                = "vulnerable-nic"
  location            = "eastus"
  resource_group_name = "vulnerable-rg"

  ip_configuration {
    name                          = "internal"
    subnet_id                     = "/subscriptions/xxx/resourceGroups/yyy/providers/Microsoft.Network/virtualNetworks/zzz/subnets/default"
    private_ip_address_allocation = "Dynamic"
  }
}

resource "local_sensitive_file" "ssh" {
  content = <<EOF
-----BEGIN RSA PRIVATE KEY-----
MIIEpAIBAAKCAQEA4...
...bad-key-example...
-----END RSA PRIVATE KEY-----
EOF
  filename        = "secrets/insecure-key"
  file_permission = "0600"
}

resource "azurerm_resource_group" "vulnerable_rg" {
  name     = "vulnerable-rg"
  location = "eastus"
  # ⚠️ No tags provided
}

resource "azurerm_resource_group" "insecure_rg" {
  name     = "insecure-vm-rg"
  location = "East US"
}

resource "azurerm_virtual_network" "insecure_vnet" {
  name                = "insecure-vnet"
  address_space       = ["10.0.0.0/16"]
  location            = azurerm_resource_group.insecure_rg.location
  resource_group_name = azurerm_resource_group.insecure_rg.name
}

resource "azurerm_subnet" "insecure_subnet" {
  name                 = "insecure-subnet"
  resource_group_name  = azurerm_resource_group.insecure_rg.name
  virtual_network_name = azurerm_virtual_network.insecure_vnet.name
  address_prefixes     = ["10.0.1.0/24"]
}

resource "azurerm_network_security_group" "open_nsg" {
  name                = "allow-all-nsg"
  location            = azurerm_resource_group.insecure_rg.location
  resource_group_name = azurerm_resource_group.insecure_rg.name

  security_rule {
    name                       = "AllowAllInbound"
    priority                   = 100
    direction                  = "Inbound"
    access                     = "Allow"
    protocol                   = "*"
    source_port_range          = "*"
    destination_port_range     = "*"
    source_address_prefix      = "*"
    destination_address_prefix = "*"
  }
}

resource "azurerm_public_ip" "insecure_public_ip" {
  name                = "insecure-public-ip"
  location            = azurerm_resource_group.insecure_rg.location
  resource_group_name = azurerm_resource_group.insecure_rg.name
  allocation_method   = "Static"
  sku                 = "Basic"
}

resource "azurerm_network_interface" "insecure_nic" {
  name                = "insecure-nic"
  location            = azurerm_resource_group.insecure_rg.location
  resource_group_name = azurerm_resource_group.insecure_rg.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = azurerm_subnet.insecure_subnet.id
    private_ip_address_allocation = "Dynamic"
    public_ip_address_id          = azurerm_public_ip.insecure_public_ip.id
  }

  network_security_group_id = azurerm_network_security_group.open_nsg.id
}

resource "azurerm_linux_virtual_machine" "insecure_vm" {
  name                = "insecure-vm"
  location            = azurerm_resource_group.insecure_rg.location
  resource_group_name = azurerm_resource_group.insecure_rg.name
  size                = "Standard_B1s"
  admin_username      = "azureuser"
  network_interface_ids = [
    azurerm_network_interface.insecure_nic.id,
  ]

  admin_password = "Password1234!"
  disable_password_authentication = false

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "UbuntuServer"
    sku       = "18.04-LTS"
    version   = "latest"
  }
}

resource "azurerm_linux_virtual_machine" "default" {
  name                = "vulnerable-vm"
  location            = "eastus"
  resource_group_name = "vulnerable-rg"
  size                = "Standard_D2s_v3"
  admin_username      = "admin"  # ⚠️ Generic username

  network_interface_ids = [
    azurerm_network_interface.default.id,
  ]

  admin_ssh_key {
    username   = "admin"
    public_key = tls_private_key.default.public_key_openssh
  }

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  source_image_reference {
    publisher = "Canonical"
    offer     = "UbuntuServer"
    sku       = "16.04-LTS"  # ⚠️ Deprecated image
    version   = "latest"
  }
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

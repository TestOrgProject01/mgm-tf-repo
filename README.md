# Event Grid Proxy

## Overview

Event Grid Proxy (EGP) is an Azure Function service to securely process Azure Event Grid events to a destination WebHook using MGM Resorts international Okta auth provider.

The service receives an Azure Event Grid event, then based on the configuration finds a destination Web Hook and forwards the event with Okta JWT from the identity service.

To add your service as a destination service:

1. Add your service into the appsettings JSON configuration file for a specific environment.
2. Make sure that identity is configured for the EGP service to requested your scope (reach to the identity team/Create ISD ticket in Jira).

# Branches overview

| Branch     | Protected? | Base Branch | Description                                                                                                                                                                                                |
| :--------- | :--------- | :---------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `main`     | YES        | `preprod`   | This branch is dedicated for the production releases. All changes that merged to this branch will be deployed to the production. The code base must be **stable**.                                         |
| `preprod`  | YES        | `develop`   | This branch is dedicated for the preproduction non-prod environment for testing before releases. The code base is primary stable but may have some issues that should be fixed during release preparation. |
| `develop`  | YES        | N/A         | This branch is dedicated for the development non-prod environment and contains the latest features changes (**unstable**).                                                                                 |
| `hotfix-*` | NO         | `main`      | These are bug fixes against production.<br/>This is used because develop might have moved on from the last published state.<br/>Remember to merge this back into develop and any release branches.         |

## Feature branches

You should create a local feature branch from the `develop` branch before start your work. Your feature branch should be named in the following format: _your_name/service_name/feature_name_. For example, _andrew/egp/swagger_. This will allow to group branches between developers.

## Commits

Whenever you work on a feature, you should have a Jira ticket for that. When you commit to your feature branch, your commit summary should start with the Jira ticket number. For example, _SRE-0: swagger added to the project_. In this case, your commit will be linked to your Jira ticket.

## Pull Requests

When you are ready to push your changes to the development, you can create a Pull Request (PR) to the `develop` branch. The requirement for creating a PR:

1. Your code should compile with no errors.
2. Code that you created should have Unit tests.
3. Recommended to merge you feature branch with the `develop` branch before creating PR to avoid merge conflicts.
4. Pull request name should be started with Jira ticket number. For example, SRE-0: KTTS - Swagger page added.
5. To merge your PR it should be reviewed by, at least, two developers from your team.

Please notify your teammates that they assigned to the PR separately.

## Secrets

Secrets committing to the repository is prohibited. Please use secrets storage, like Azure Key Vault, for storing your secrets.

# Configuration providers

KVMS has several configuration providers (listed in order of priority - from lower to highest):

1. Environmental variables. Provides information about environment, running instance name, and information for a Key Vault connection.
2. "appsettings.Environment.json"(Development/PreProduction/Production) as main configuration storage.
3. Azure Key Vault for storing secrets.
4. "local.settings.json" for saving configuration parameters to run project locally.

# Local configuration file

To run the project locally you will need the "local.settings.json" configuration file. This file ignored by Git and has the highest priority in configuration providers. You can use this configuration file to override any configuration parameter from "appsettings.Development.json", environmental variables, or Azure Key Vault secrets. It's OK to save secrets here since this file is not going to be committed to Git.

####appsettings.local.json example

    {
      "IsEncrypted": false,
      "Values": {
        "Environment": "Development",
        "AzureWebJobsStorage": "UseDevelopmentStorage=true",
        "FUNCTIONS_WORKER_RUNTIME": "dotnet-isolated",
        "KeyVaultUrl": "https://sreprv-uw-kv-d.vault.azure.net/",
        "KeyVaultAadAppTenantId": "{obtain_from_SEDS_owners}",
        "KeyVaultAadAppClientId": "{obtain_from_SEDS_owners}",
        "KeyVaultAadAppSecret": "{obtain_from_SEDS_owners}"
      }
    }

# Run Project

When you have "local.settings.json" in place, you are ready to run the project.

1. Make sure that you specified right Key Vault AAD app client ID and secret in "local.settings.json"
2. Run the project.

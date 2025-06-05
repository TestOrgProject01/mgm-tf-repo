// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ConfigurationKeys.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>The configuration key names constants class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Application.Constants
{
    /// <summary>
    /// The configuration keys.
    /// </summary>
    internal static class ConfigurationKeys
    {
        /// <summary>The application settings environment key.</summary>
        internal const string Environment = "Environment";

        /// <summary>The application settings Azure Key Vault URL key.</summary>
        internal const string KeyVaultUrl = "KeyVaultUrl";
        
        /// <summary>The application settings Azure Key Vault AAD app tenant ID.</summary>
        internal const string KeyVaultAadAppTenantId = "KeyVaultAadAppTenantId";

        /// <summary>The application settings Azure Key Vault AAD app client ID key.</summary>
        internal const string KeyVaultAadAppClientId = "KeyVaultAadAppClientId";

        /// <summary>The application settings Azure Key Vault AAD app Secret key.</summary>
        internal const string KeyVaultAadAppSecret = "KeyVaultAadAppSecret";

        /// <summary>The authorization configuration section.</summary>
        internal const string AuthorizationSection = "Authorization";
    }
}
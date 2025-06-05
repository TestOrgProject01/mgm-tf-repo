// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Settings.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>The static settings class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Application.Constants
{
    /// <summary>
    /// The settings.
    /// </summary>
    internal static class Settings
    {
        /// <summary>The application name.</summary>
        internal const string ApplicationName = "EventGridProxy";

        /// <summary>The application short name.</summary>
        internal const string ApplicationShortName = "EGP";

        /// <summary>The JWT bearer authentication scheme.</summary>
        internal const string JwtBearerAuthenticationScheme = "Bearer";

        /// <summary>The correlation ID header name.</summary>
        internal const string CorrelationIdHeaderName = "X-Correlation-ID";

        /// <summary>The external heath check.</summary>
        internal const string ExternalHeathCheckTag = "external";

        /// <summary>The HTTP transient error retry count.</summary>
        internal const int HttpRetryCount = 3;

        /// <summary>The stored in Key Vault configuration placeholder.</summary>
        internal const string StoredInKeyVaultPlaceholder = "[stored in KeyVault]";

        /// <summary>The health check connection timeout (in milliseconds).</summary>
        internal const int HealthCheckConnectionTimeoutInMilliseconds = 3000;
    }
}

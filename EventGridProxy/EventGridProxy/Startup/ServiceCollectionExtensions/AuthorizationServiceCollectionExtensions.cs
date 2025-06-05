// --------------------------------------------------------------------------------------------------------------------
// <copyright file="AuthorizationServiceCollectionExtensions.cs" company="MGM Resorts International">
//   Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Service Collection extension for adding authorization services.</summary>
// --------------------------------------------------------------------------------------------------------------------

// ReSharper disable CheckNamespace

// ReSharper disable CheckNamespace
namespace Microsoft.Extensions.DependencyInjection
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using Mgm.Framework.AzureFunction.Authentication;
    using Mgm.Sre.Services.EventGridProxy.Models.Configuration;
    using Mgm.Sre.Services.EventGridProxy.Models.Configuration.Authorization;
    using Microsoft.Extensions.Configuration;

    /// <summary>
    /// The startup extension.
    /// </summary>
    [ExcludeFromCodeCoverage]
    public static class AuthorizationServiceCollectionExtensions
    {
        /// <summary>
        /// Adds authorization service to the service collection.
        /// </summary>
        /// <param name="services"> The services. </param>
        /// <param name="configuration"> The configuration. </param>
        /// <returns> The <see cref="IServiceCollection"/>. </returns>
        public static IServiceCollection AddAuthorizationServices(this IServiceCollection services, IConfiguration configuration)
        {
            if (services == null)
            {
                throw new ArgumentNullException(nameof(services));
            }

            if (configuration == null)
            {
                throw new ArgumentNullException(nameof(configuration));
            }

            // Read configuration
            var authorizationConfiguration = configuration.GetSection(AuthorizationConfiguration.SectionPath).Get<AuthorizationConfiguration>()?.Validate();
            if (authorizationConfiguration == null)
            {
                throw new ArgumentNullException(nameof(services));
            }

            var proxyConfiguration = configuration.Get<Proxy>()?.Validate();
            if (proxyConfiguration == null)
            {
                throw new ArgumentNullException(nameof(proxyConfiguration));
            }

            var requestScopes = new HashSet<string>();
            foreach (var proxyRoute in proxyConfiguration.ProxyRoutes)
            {
                foreach (string requestScope in proxyRoute.RequestScopes)
                {
                    requestScopes.Add(requestScope);
                }
            }

            services.AddIdentityAuthorization(
                options =>
                {
                    options.IdentityServiceBaseUri = authorizationConfiguration.IdentityAuthorizationServiceUrl;
                    options.ClientId = authorizationConfiguration.OktaClientId;
                    options.ClientSecret = authorizationConfiguration.OktaClientSecret;
                    options.RequestScopes = requestScopes;
                },
                _ =>
                {
                });
            return services;
        }
    }
}
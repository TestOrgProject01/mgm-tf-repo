// --------------------------------------------------------------------------------------------------------------------
// <copyright file="HealthChecksServiceCollectionExtensions.cs" company="MGM Resorts International">
// Copyright (c) 2022 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Health Check Collection extension for adding helth check endpoint.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Startup.ServiceCollectionExtensions
{
    using System;
    using System.Net.Http;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Mgm.Sre.Services.EventGridProxy.Models.Configuration.Authorization;
    using Microsoft.Extensions.Configuration;
    using Microsoft.Extensions.DependencyInjection;
    using Microsoft.Extensions.Diagnostics.HealthChecks;

    /// <summary>
    /// The HealthChecks Service Collection Extensions class.
    /// </summary>
    public static class HealthChecksServiceCollectionExtensions
    {
        /// <summary>
        /// Adds health checks service to the service collection.
        /// </summary>
        /// <param name="services"> The services. </param>
        /// <param name="configuration"> The application configuration. </param>
        /// <returns>The <see cref="IServiceCollection"/>.</returns>
        public static IServiceCollection AddHealthChecksServices(this IServiceCollection services, IConfiguration configuration)
        {
            if (services == null)
            {
                throw new ArgumentNullException(nameof(services));
            }

            if (configuration == null)
            {
                throw new ArgumentNullException(nameof(configuration));
            }

            var identityConfiguration = configuration.GetSection(ConfigurationKeys.AuthorizationSection).Get<AuthorizationConfiguration>();
            services.AddHealthChecks()
                    .AddUrlGroup(
                            new Uri(identityConfiguration.PingUrl),
                            HttpMethod.Get,
                            "Identity Service",
                            HealthStatus.Unhealthy,
                            timeout: TimeSpan.FromMilliseconds(Settings.HealthCheckConnectionTimeoutInMilliseconds),
                            tags: [Settings.ExternalHeathCheckTag]);

            return services;
        }
    }
}
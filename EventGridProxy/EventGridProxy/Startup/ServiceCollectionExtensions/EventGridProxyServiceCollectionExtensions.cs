// --------------------------------------------------------------------------------------------------------------------
// <copyright file="EventGridProxyServiceCollectionExtensions.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements Event Grid proxy service collection extensions class.</summary>
// --------------------------------------------------------------------------------------------------------------------

// ReSharper disable CheckNamespace
namespace Microsoft.Extensions.DependencyInjection
{
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Net.Http.Headers;
    using Mgm.Framework.Authentication.Handlers;
    using Mgm.Sre.Services.EventGridProxy.Application.Extensions;
    using Mgm.Sre.Services.EventGridProxy.Models.Configuration;
    using Mgm.Sre.Services.EventGridProxy.Services.EventGridProxy;
    using Microsoft.Extensions.Configuration;

    /// <summary>
    /// The Event Grid proxy service collection extensions.
    /// </summary>
    [ExcludeFromCodeCoverage]
    public static class EventGridProxyServiceCollectionExtensions
    {
        /// <summary>
        /// Adds Event Grid proxy services ton the service collection.
        /// </summary>
        /// <param name="services">The service collection.</param>
        /// <param name="configuration">The application configuration.</param>
        /// <returns>The <see cref="IServiceCollection"/>.</returns>
        public static IServiceCollection AddEventGridProxy(this IServiceCollection services, IConfiguration configuration)
        {
            // Read configuration
            var proxyConfiguration = configuration.Get<Proxy>()?.Validate();
            if (proxyConfiguration == null)
            {
                throw new ArgumentNullException(nameof(proxyConfiguration));
            }

            services.AddRestClients(proxyConfiguration.ProxyRoutes);
            services.AddOptions<Proxy>().Bind(configuration);
            services.AddScoped<IEventGridProxyService, EventGridProxyService>();
            return services;
        }

        /// <summary>
        /// Adds HTTP clients for Event Grid proxy service.
        /// </summary>
        /// <param name="services">The service collection.</param>
        /// <param name="proxyRoutes">Proxy routes configuration.</param>
        private static void AddRestClients(this IServiceCollection services, IEnumerable<ProxyRoute> proxyRoutes)
        {
            foreach (var proxyRoute in proxyRoutes)
            {
                services.AddHttpClient(
                        proxyRoute.RouteName,
                        client =>
                        {
                            client.BaseAddress = proxyRoute.DestinationEndpoint;
                            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                        })
                    .AddRetryPolicy()
                    .AddHttpMessageHandler<ServiceTokenAuthorizationHandler>();
            }
        }
    }
}
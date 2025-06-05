// --------------------------------------------------------------------------------------------------------------------
// <copyright file="EventGridProxyService.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements Event Grid proxy service class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Services.EventGridProxy
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Net.Http;
    using System.Text;
    using System.Threading;
    using System.Threading.Tasks;
    using Azure.Messaging.EventGrid;
    using Mgm.Framework.Common.Extensions;
    using Mgm.Sre.Services.EventGridProxy.Application.Extensions;
    using Mgm.Sre.Services.EventGridProxy.Mappers;
    using Mgm.Sre.Services.EventGridProxy.Models.Configuration;
    using Microsoft.Extensions.Logging;
    using Microsoft.Extensions.Options;
    using Newtonsoft.Json;

    /// <summary>
    /// The Event Grid proxy service.
    /// </summary>
    public class EventGridProxyService : IEventGridProxyService
    {
        #region Private members

        /// <summary>The HTTP client factory.</summary>
        private readonly IHttpClientFactory httpClientFactory;

        /// <summary>The proxy configuration.</summary>
        private readonly Proxy proxyConfiguration;

        /// <summary>The logger.</summary>
        private readonly ILogger<EventGridProxyService> logger;

        #endregion

        #region Constructors

        /// <summary>
        /// Initializes a new instance of the <see cref="EventGridProxyService"/> class.
        /// </summary>
        /// <param name="httpClientFactory">The HTTP client factory.</param>
        /// <param name="proxyOptions">The proxy options.</param>
        /// <param name="logger">The logger.</param>
        /// <exception cref="ArgumentNullException">Argument not supplied.</exception>
        public EventGridProxyService(IHttpClientFactory httpClientFactory, IOptions<Proxy> proxyOptions, ILogger<EventGridProxyService> logger)
        {
            this.httpClientFactory = httpClientFactory ?? throw new ArgumentNullException(nameof(httpClientFactory));
            this.proxyConfiguration = proxyOptions?.Value?.Validate() ?? throw new ArgumentNullException(nameof(proxyOptions));
            this.logger = logger ?? throw new ArgumentNullException(nameof(logger));
        }

        #endregion

        /// <summary>
        /// Proxies Event Grid events to destination endpoints based on the configuration.
        /// </summary>
        /// <param name="eventGridEvents">Azure Event Grid events.</param>
        /// <param name="cancellationToken">Cancellation token.</param>
        /// <returns>The task object representing the asynchronous operation.</returns>
        /// <exception cref="KeyNotFoundException">A named HTTP client is not found.</exception>
        /// <exception cref="HttpRequestException">Proxy HTTP request failed.</exception>
        /// <exception cref="InvalidOperationException">Operation failed.</exception>
        public async Task ProxyEventsAsync(IEnumerable<EventGridEvent> eventGridEvents, CancellationToken cancellationToken = default)
        {
            string source = this.GetSourceName();
            try
            {
                foreach (var eventGridEventsGroup in eventGridEvents.GroupBy(evt => evt.EventType))
                {
                    var routesConfiguration = this.proxyConfiguration.ProxyRoutes.Where(route => string.Equals(route.EventGridEventType, eventGridEventsGroup.Key));
                    if (!routesConfiguration.Any())
                    {
                        this.logger.LogWarning($"{source}. No configuration found for Event Grid event type '{eventGridEventsGroup.Key}'. The events ignored.");
                    }

                    foreach (var routeConfiguration in routesConfiguration)
                    {
                        var httpClient = this.httpClientFactory.CreateClient(routeConfiguration.RouteName);
                        if (httpClient == null)
                        {
                            throw new KeyNotFoundException(
                                $"The named HTTP client '{routeConfiguration.RouteName}' is not found. The Event Grid Event event type '{routeConfiguration.EventGridEventType}' cannot be proxied.");
                        }

                        string data = JsonConvert.SerializeObject(eventGridEventsGroup);
                        using var requestContent = new StringContent(data, Encoding.UTF8, "application/json");
                        using var response = await httpClient.PostAsync(string.Empty, requestContent, cancellationToken);

                        if (!response.IsSuccessStatusCode)
                        {
                            string responseContent = await response.Content.ReadAsStringAsync(cancellationToken);
                            throw new HttpRequestException(
                                $"Sending Event Grid Event proxy request failed with status code '{response.StatusCode}'. {responseContent}",
                                null,
                                response.StatusCode);
                        }

                        //// TODO: save status to storage table

                        if (cancellationToken.IsCancellationRequested)
                        {
                            break;
                        }
                    }

                    if (cancellationToken.IsCancellationRequested)
                    {
                        break;
                    }
                }
            }
            catch (Exception ex)
            {
                this.logger.LogError($"{source}. {Application.Constants.Messages.MethodFailed}{ex.GetErrorMessage()}");
                //// TODO: save status to storage table
                throw new InvalidOperationException($"Proxy Event Grid events failed: {ex.Message}", ex);
            }
        }

        /// <summary>
        /// Proxies Event Grid event to a destination endpoints based on the configuration.
        /// </summary>
        /// <param name="eventGridEvent">The <see cref="EventGridEvent"/> to be proxied.</param>
        /// <param name="cancellationToken">Cancellation token.</param>
        /// <returns>The task object representing the asynchronous operation.</returns>
        /// <exception cref="KeyNotFoundException">A named HTTP client is not found.</exception>
        /// <exception cref="HttpRequestException">Proxy HTTP request failed.</exception>
        /// <exception cref="InvalidOperationException">Operation failed.</exception>
        public async Task ProxyEventAsync(EventGridEvent eventGridEvent, CancellationToken cancellationToken = default)
        {
            string source = this.GetSourceName();

            try
            {
                this.logger.LogInformation($"{source}. Proxying  Event Grid event. Details - {nameof(eventGridEvent.EventType)}: {eventGridEvent.EventType}.");

                var routesConfiguration = this.proxyConfiguration.ProxyRoutes.Where(route => string.Equals(route.EventGridEventType, eventGridEvent.EventType)).ToList();
                if (!routesConfiguration.Any())
                {
                    this.logger.LogWarning($"{source}. No configuration found for Event Grid event type '{eventGridEvent.EventType}'. The events ignored.");
                    return;
                }

                foreach (var routeConfiguration in routesConfiguration)
                {
                    var httpClient = this.httpClientFactory.CreateClient(routeConfiguration.RouteName);
                    if (httpClient == null)
                    {
                        throw new KeyNotFoundException(
                            $"The named HTTP client '{routeConfiguration.RouteName}' is not found. The Event Grid Event event type '{routeConfiguration.EventGridEventType}' cannot be proxied.");
                    }

                    string data = JsonConvert.SerializeObject(new[] { eventGridEvent.ToDto() });
                    using var requestContent = new StringContent(data, Encoding.UTF8, "application/json");
                    using var response = await httpClient.PostAsync(string.Empty, requestContent, cancellationToken);

                    if (!response.IsSuccessStatusCode)
                    {
                        string responseContent = await response.Content.ReadAsStringAsync(cancellationToken);
                        throw new HttpRequestException(
                            $"Sending Event Grid Event proxy request failed with status code '{response.StatusCode}'. {responseContent}",
                            null,
                            response.StatusCode);
                    }

                    //// TODO: save status to storage table
                }
            }
            catch (Exception ex)
            {
                this.logger.LogError($"{source}. {Application.Constants.Messages.MethodFailed}{ex.GetErrorMessage()}");
                //// TODO: save status to storage table
                throw new InvalidOperationException($"Proxy Event Grid events failed: {ex.Message}", ex);
            }
        }
    }
}
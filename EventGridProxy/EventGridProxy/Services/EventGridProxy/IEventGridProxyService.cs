// --------------------------------------------------------------------------------------------------------------------
// <copyright file="IEventGridProxyService.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Declares Event Grid proxy service interface.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Services.EventGridProxy
{
    using Azure.Messaging.EventGrid;
    using System;
    using System.Collections.Generic;
    using System.Net.Http;
    using System.Threading;
    using System.Threading.Tasks;

    /// <summary>
    /// Event Grid proxy service interface.
    /// </summary>
    public interface IEventGridProxyService
    {
        /// <summary>
        /// Proxies Event Grid events to destination endpoints based on the configuration.
        /// </summary>
        /// <param name="eventGridEvents">Azure Event Grid events.</param>
        /// <param name="cancellationToken">Cancellation token.</param>
        /// <returns>The task object representing the asynchronous operation.</returns>
        /// <exception cref="KeyNotFoundException">A named HTTP client is not found.</exception>
        /// <exception cref="HttpRequestException">Proxy HTTP request failed.</exception>
        /// <exception cref="InvalidOperationException">Operation failed.</exception>
        Task ProxyEventsAsync(IEnumerable<EventGridEvent> eventGridEvents, CancellationToken cancellationToken = default);

        /// <summary>
        /// Proxies Event Grid event to a destination endpoints based on the configuration.
        /// </summary>
        /// <param name="eventGridEvent">The <see cref="EventGridEvent"/> to be proxied.</param>
        /// <param name="cancellationToken">Cancellation token.</param>
        /// <returns>The task object representing the asynchronous operation.</returns>
        /// <exception cref="KeyNotFoundException">A named HTTP client is not found.</exception>
        /// <exception cref="HttpRequestException">Proxy HTTP request failed.</exception>
        /// <exception cref="InvalidOperationException">Operation failed.</exception>
        Task ProxyEventAsync(EventGridEvent eventGridEvent, CancellationToken cancellationToken = default);
    }
}

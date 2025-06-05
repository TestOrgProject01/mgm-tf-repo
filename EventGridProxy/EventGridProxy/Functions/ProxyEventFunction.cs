// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ProxyEventFunction.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>
// Azure function for listen events from Azure Event Grid and forward them to a destination based on configuration.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Functions
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.IO;
    using System.Net;
    using System.Threading;
    using System.Threading.Tasks;
    using Azure.Messaging.EventGrid;
    using Azure.Messaging.EventGrid.SystemEvents;
    using Mgm.Framework.Common.Extensions;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Mgm.Sre.Services.EventGridProxy.Application.Extensions;
    using Mgm.Sre.Services.EventGridProxy.Services.EventGridProxy;
    using Microsoft.Azure.Functions.Worker;
    using Microsoft.Azure.Functions.Worker.Http;
    using Microsoft.Extensions.Logging;

    /// <summary>
    /// The Event Grid proxy event function.
    /// </summary>
    public class ProxyEventFunction
    {
        #region Private members

        /// <summary>The Event Grid proxy service.</summary>
        private readonly IEventGridProxyService eventGridProxy;

        /// <summary>The logger.</summary>
        private readonly ILogger<ProxyEventFunction> logger;

        #endregion

        #region Constructor

        /// <summary>
        /// Initializes a new instance of the <see cref="ProxyEventFunction"/> class.
        /// </summary>
        /// <param name="eventGridProxy">The Event Grid proxy service.</param>
        /// <param name="logger">The logger injection.</param>
        /// <exception cref="ArgumentNullException">Argument is not supplied.</exception>
        public ProxyEventFunction(IEventGridProxyService eventGridProxy, ILogger<ProxyEventFunction> logger)
        {
            this.eventGridProxy = eventGridProxy ?? throw new ArgumentNullException(nameof(eventGridProxy));
            this.logger = logger ?? throw new ArgumentNullException(nameof(logger));
        }

        #endregion

        #region Functions

        /// <summary>
        /// The ProxyEvent function run method.
        /// </summary>
        /// <param name="request"><see cref="HttpRequestData"/>.</param>
        /// <returns>The operation result.</returns>
        [Function("ProxyEvent")]
        public async Task<HttpResponseData> RunAsync(
            [HttpTrigger(AuthorizationLevel.Anonymous, "post", Route = null)] HttpRequestData request)
        {
            string source = this.GetSourceName();

            try
            {
                this.logger.LogInformation($"{source}. {Messages.RequestReceived}");

                var eventGridEvents = await ParseRequestAsync(request);
                if (eventGridEvents == null)
                {
                    this.logger.LogWarning($"{source}. Request data invalid.");
                    return request.CreateResponse(HttpStatusCode.BadRequest);
                }

                var result = await this.ProcessEventsAsync(eventGridEvents);

                var response = request.CreateResponse(HttpStatusCode.OK);
                if (result != null)
                {
                    await response.WriteJsonAsync(result);
                }

                this.logger.LogInformation($"{source}. {Messages.RequestCompleted}");
                return response;
            }
            catch (Exception ex)
            {
                this.logger.LogError($"{source}. {Messages.RequestFailed}{ex.GetErrorMessage()}");
                return await request.CreateErrorResponse(ex);
            }
        }

        #endregion

        /// <summary>
        /// Parses HTTP request into collection of <see cref="EventGridEvent"/>.
        /// </summary>
        /// <param name="request">The request data.</param>
        /// <returns>Parsed Event Grid events.</returns>
        private static async Task<EventGridEvent[]> ParseRequestAsync(HttpRequestData request)
        {
            // Read the request body as a stream and parse it into EventGridEvent array
            using var stream = new MemoryStream();
            await request.Body.CopyToAsync(stream);
            stream.Position = 0; // Reset stream position for reading
            var events = EventGridEvent.ParseMany(BinaryData.FromStream(stream)); // Updated parsing method
            return events.ToArray(); // Convert to array for compatibility
        }

        /// <summary>
        /// Process Event Grid events.
        /// </summary>
        /// <param name="eventGridEvents">Event Grid events to be processed.</param>
        /// <param name="cancellationToken">Cancellation token.</param>
        /// <returns>
        /// <see cref="SubscriptionValidationResponse"/> handshake response if events contains <see cref="SubscriptionValidationEventData"/>.
        ///  Otherwise null.
        /// </returns>
        [return: MaybeNull]
        private async Task<SubscriptionValidationResponse> ProcessEventsAsync(EventGridEvent[] eventGridEvents, CancellationToken cancellationToken = default)
        {
            string source = this.GetSourceName();
            this.logger.LogInformation($"{source}. Processing event grid events. Details - eventCount: {eventGridEvents.Length}.");

            foreach (var eventGridEvent in eventGridEvents)
            {
                if (eventGridEvent == null)
                {
                    throw new NullReferenceException($"{nameof(eventGridEvent)} object reference not set to an instance of an object in the {nameof(eventGridEvents)} collection.");
                }

                if (eventGridEvent.TryGetSystemEventData(out object eventData))
                {
                    if (eventData is SubscriptionValidationEventData subscriptionValidationEventData)
                    {
                        this.logger.LogInformation($"{source}. Handshake received. Details - Validation code: {subscriptionValidationEventData.ValidationCode}; Topic: {eventGridEvent.Topic}.");
                        return new SubscriptionValidationResponse { ValidationResponse = subscriptionValidationEventData.ValidationCode };
                    }
                }

                await this.eventGridProxy.ProxyEventAsync(eventGridEvent, cancellationToken);
            }

            return null;
        }
    }
}
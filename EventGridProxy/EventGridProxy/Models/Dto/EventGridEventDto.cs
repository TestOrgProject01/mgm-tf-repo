// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Proxy.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements the proxy routes configuration model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Dto
{
    using Newtonsoft.Json;

    /// <summary>
    /// The Event Grid Event Dto.
    /// </summary>
    public class EventGridEventDto
    {
        /// <summary>
        /// Gets or sets Id.
        /// </summary>
        [JsonProperty("id")]
        public string Id { get; set; }

        /// <summary>
        /// Gets or sets EventType.
        /// </summary>
        [JsonProperty("eventType")]
        public string EventType { get; set; }

        /// <summary>
        /// Gets or sets Subject.
        /// </summary>
        [JsonProperty("subject")]
        public string Subject { get; set; }

        /// <summary>
        /// Gets or sets EventTime.
        /// </summary>
        [JsonProperty("eventTime")]
        public DateTimeOffset EventTime { get; set; }

        /// <summary>
        /// Gets or sets Data.
        /// </summary>
        [JsonProperty("data")]
        public object Data { get; set; }

        /// <summary>
        /// Gets or Data Version.
        /// </summary>
        [JsonProperty("dataVersion")]
        public string DataVersion { get; set; }

        /// <summary>
        /// Gets or sets Topic.
        /// </summary>
        [JsonProperty("topic")]
        public string Topic { get; set; }
    }
}
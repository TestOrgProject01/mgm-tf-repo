// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ErrorResponseData.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements error response data model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Error
{
    using System.Diagnostics.CodeAnalysis;
    using Newtonsoft.Json;

    /// <summary>
    /// Error response data.
    /// </summary>
    [ExcludeFromCodeCoverage]
    public class ErrorResponseData
    {
        /// <summary>
        /// Gets or sets error HTTP status.
        /// </summary>
        [JsonProperty("status", NullValueHandling = NullValueHandling.Ignore)]
        public string Status { get; set; }

        /// <summary>
        /// Gets or sets error message.
        /// </summary>
        [JsonProperty("message", NullValueHandling = NullValueHandling.Ignore)]
        public string Message { get; set; }

        /// <summary>
        /// Gets or sets error code.
        /// </summary>
        [JsonProperty("code", NullValueHandling = NullValueHandling.Ignore)]
        public string Code { get; set; }

        /// <summary>
        /// Gets or sets error description.
        /// </summary>
        [JsonProperty("description", NullValueHandling = NullValueHandling.Ignore)]
        public string Description { get; set; }
    }
}

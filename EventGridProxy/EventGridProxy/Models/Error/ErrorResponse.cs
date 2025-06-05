// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ErrorResponse.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements error response model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Error
{
    using System.Diagnostics.CodeAnalysis;
    using Newtonsoft.Json;

    /// <summary>
    /// The error response.
    /// </summary>
    [ExcludeFromCodeCoverage]
    public class ErrorResponse
    {
        /// <summary>
        /// Gets or sets error.
        /// </summary>
        [JsonProperty("error")]
        public ErrorResponseData Error { get; set; } = new();
    }
}

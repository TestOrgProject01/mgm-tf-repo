// --------------------------------------------------------------------------------------------------------------------
// <copyright file="HttpClientExtensions.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements HTTP client extensions class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Application.Extensions
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using System.Net.Http;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Microsoft.Extensions.DependencyInjection;
    using Microsoft.Extensions.Http;
    using Polly;
    using Polly.Extensions.Http;

    /// <summary>
    /// The HTTP client extensions.
    /// </summary>
    [ExcludeFromCodeCoverage]
    public static class HttpClientExtensions
    {
        /// <summary>
        /// Adds a <see cref="PolicyHttpMessageHandler"/> which will surround request execution with retry policy.
        /// </summary>
        /// <param name="builder">The <see cref="IHttpClientBuilder"/>.</param>
        /// <returns>An <see cref="IHttpClientBuilder"/> that can be used to configure the client.</returns>
        /// <remarks>
        /// <para>
        /// See the remarks on <see cref="PolicyHttpMessageHandler"/> for guidance on configuring policies.
        /// </para>
        /// </remarks>
        public static IHttpClientBuilder AddRetryPolicy(this IHttpClientBuilder builder)
        {
            return builder.AddPolicyHandler(GetRetryPolicy());
        }

        /// <summary>
        /// Gets the retry policy.
        /// </summary>
        /// <returns>The retry policy.</returns>
        private static IAsyncPolicy<HttpResponseMessage> GetRetryPolicy()
        {
            var jitterer = new Random();
            return HttpPolicyExtensions.HandleTransientHttpError()
                .WaitAndRetryAsync(Settings.HttpRetryCount, retryAttempt => TimeSpan.FromSeconds(Math.Pow(2, retryAttempt)) + TimeSpan.FromMilliseconds(jitterer.Next(0, 100)));
        }
    }
}

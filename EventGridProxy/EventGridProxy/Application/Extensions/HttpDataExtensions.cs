// --------------------------------------------------------------------------------------------------------------------
// <copyright file="HttpDataExtensions.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements HTTP data extensions class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Application.Extensions
{
    using System;
    using System.Net;
    using System.Threading.Tasks;
    using Mgm.Sre.Services.EventGridProxy.Models.Error;
    using Microsoft.Azure.Functions.Worker.Http;
    using Newtonsoft.Json;

    /// <summary>
    /// HTTP request data extensions.
    /// </summary>
    public static class HttpDataExtensions
    {
        /// <summary>
        /// Creates error response from the <see cref="HttpRequestData"/>.
        /// </summary>
        /// <param name="request">Request data.</param>
        /// <param name="exception">The error.</param>
        /// <param name="statusCode">Http error status code.</param>
        /// <returns>The error <see cref="HttpResponseData"/>.</returns>
        /// <exception cref="ArgumentNullException">Argument not supplied.</exception>
        public static async Task<HttpResponseData> CreateErrorResponse(this HttpRequestData request, Exception exception, HttpStatusCode statusCode = HttpStatusCode.InternalServerError)
        {
            if (request == null)
            {
                throw new ArgumentNullException(nameof(request));
            }

            var errorResponse = new ErrorResponse { Error = { Status = ((int)statusCode).ToString(), Message = exception?.Message } };
            var response = request.CreateResponse(statusCode);
            await response.WriteJsonAsync(errorResponse);

            return response;
        }

        /// <summary>
        /// Write the specified value as JSON to the response body using the <see cref="Newtonsoft.Json"/> serializer.
        /// </summary>
        /// <typeparam name="T">The type of object to write.</typeparam>
        /// <param name="response">The response to write JSON to.</param>
        /// <param name="instance">The instance to serialize and write as JSON.</param>
        /// <param name="statusCode">The status code to set on the response.</param>
        /// <returns>A <see cref="Task"/> that represents the asynchronous operation.</returns>
        public static Task WriteJsonAsync<T>(this HttpResponseData response, T instance, HttpStatusCode? statusCode = null)
        {
            if (response is null)
            {
                throw new ArgumentNullException(nameof(response));
            }

            if (instance is null)
            {
                throw new ArgumentNullException(nameof(instance));
            }

            if (statusCode != null)
            {
                response.StatusCode = statusCode.Value;
            }

            response.Headers.Add("Content-Type", "application/json");
            return response.WriteStringAsync(JsonConvert.SerializeObject(instance));
        }
    }
}

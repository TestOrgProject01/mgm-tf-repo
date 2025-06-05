// --------------------------------------------------------------------------------------------------------------------
// <copyright file="HealthCheckFunction.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>
// Azure function for receiving service health status.
// </summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Functions
{
    using System;
    using System.Threading.Tasks;
    using HealthChecks.UI.Core;
    using Mgm.Framework.Utilities.Helpers;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Microsoft.AspNetCore.Http;
    using Microsoft.AspNetCore.Mvc;
    using Microsoft.Azure.Functions.Worker;
    using Microsoft.Azure.Functions.Worker.Http;
    using Microsoft.Extensions.Diagnostics.HealthChecks;

    /// <summary>
    /// The Health Check function.
    /// </summary>
    public class HealthCheckFunction
    {
        #region Private member

        /// <summary>The health check service.</summary>
        private readonly HealthCheckService healthCheckService;

        #endregion

        #region Constructors

        /// <summary>
        /// Initializes a new instance of the <see cref="HealthCheckFunction"/> class.
        /// </summary>
        /// <param name="healthCheckService">Health check service.</param>
        public HealthCheckFunction(HealthCheckService healthCheckService)
        {
            this.healthCheckService = healthCheckService ?? throw new ArgumentNullException(nameof(healthCheckService));
        }

        #endregion

        #region Function

        /// <summary>
        /// Runs the internal health check request.
        /// </summary>
        /// <param name="req">The request.</param>
        /// <returns>The health checks report.</returns>
        /// <exception cref="ArgumentNullException">Argument is not supplied.</exception>
        [Function("HealthInternal")]
        public async Task<IActionResult> RunPing([HttpTrigger(AuthorizationLevel.Anonymous, "get", Route = "health/internal")] HttpRequest req)
        {
            if (req == null)
            {
                throw new ArgumentNullException(nameof(req));
            }

            try
            {
                var healthCheckReport = await this.healthCheckService.CheckHealthAsync((status) => !status.Tags.Contains(Settings.ExternalHeathCheckTag)).ConfigureAwait(false);
                var uiHealthReport = UIHealthReport.CreateFrom(healthCheckReport);
                switch (uiHealthReport.Status)
                {
                    case UIHealthStatus.Unhealthy:
                        return HttpHelper.StatusCode(StatusCodes.Status503ServiceUnavailable, uiHealthReport);
                    default:
                        return new OkObjectResult(uiHealthReport);
                }
            }
            catch (Exception ex)
            {
                var uiHealthReport = UIHealthReport.CreateFrom(ex);
                return HttpHelper.StatusCode(StatusCodes.Status503ServiceUnavailable, uiHealthReport);
            }
        }

        /// <summary>
        /// Runs the health check request.
        /// </summary>
        /// <param name="request"><see cref="HttpRequestData"/>.</param>
        /// <returns>Health status report.</returns>
        [Function("Health")]
        public async Task<IActionResult> Run([HttpTrigger(AuthorizationLevel.Anonymous, "get", Route = "health")] HttpRequest req)
        {
            if (req == null)
            {
                throw new ArgumentNullException(nameof(req));
            }

            try
            {
                var healthCheckReport = await this.healthCheckService.CheckHealthAsync().ConfigureAwait(false);
                var uiHealthReport = UIHealthReport.CreateFrom(healthCheckReport);
                switch (uiHealthReport.Status)
                {
                    case UIHealthStatus.Unhealthy:
                        return HttpHelper.StatusCode(StatusCodes.Status503ServiceUnavailable, uiHealthReport);
                    default:
                        return new OkObjectResult(uiHealthReport);
                }
            }
            catch (Exception ex)
            {
                var uiHealthReport = UIHealthReport.CreateFrom(ex);
                return HttpHelper.StatusCode(StatusCodes.Status503ServiceUnavailable, uiHealthReport);
            }
        }

        #endregion
    }
}

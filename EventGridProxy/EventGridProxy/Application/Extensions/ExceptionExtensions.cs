// <copyright file="ExceptionExtensions.cs" company="MGM Resorts International">
// Copyright (c) 2022 MGM Resorts International. All rights reserved.
// </copyright>

namespace Mgm.Sre.Services.EventGridProxy.Application.Extensions
{
    /// <summary>
    /// Exception Extensions class.
    /// </summary>
    public static class ExceptionExtensions
    {
        /// <summary>
        /// Return a formatted error message
        /// </summary>
        /// <param name="ex">Exception.</param>
        /// <returns>formatted error message.</returns>
        public static string GetErrorMessage(this Exception ex)
        {
            var message = ex != null && !string.IsNullOrEmpty(ex.Message) ? $" Message: {ex.Message}" : string.Empty;
            var innerMessage = ex != null && ex.InnerException != null && !string.IsNullOrEmpty(ex.InnerException.Message) ? $" InnerException Message: {ex.InnerException.Message}" : string.Empty;

            return $"{message}{innerMessage}";
        }
    }
}
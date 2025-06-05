// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ConfigurationExtensions.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements application configuration extensions class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Application.Extensions
{
    using System;
    using System.Collections.Generic;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Microsoft.Extensions.Configuration;

    /// <summary>
    /// The configuration extensions.
    /// </summary>
    public static class ConfigurationExtensions
    {
        /// <summary>
        /// Gets the application environment from configuration.
        /// </summary>
        /// <param name="configuration">Application configuration.</param>
        /// <returns>The application environment.</returns>
        /// <exception cref="ArgumentNullException">Argument is not supplied.</exception>
        /// <exception cref="KeyNotFoundException">Configuration key is not found.</exception>
        public static string GetEnvironment(this IConfiguration configuration)
        {
            if (configuration == null)
            {
                throw new ArgumentNullException(nameof(configuration));
            }

            string value = configuration.GetValue<string>(ConfigurationKeys.Environment)?.Trim();
            return string.IsNullOrWhiteSpace(value) ? throw new KeyNotFoundException($"'{ConfigurationKeys.Environment}' {Messages.ConfigurationParameterNotSetSuffix}.") : value;
        }

        /// <summary>
        /// Gets a string value from the configuration.
        /// </summary>
        /// <param name="configuration">The application configuration.</param>
        /// <param name="key">The configuration key.</param>
        /// <returns>The configuration string value.</returns>
        /// <exception cref="ArgumentNullException">Argument is not supplied.</exception>
        /// <exception cref="KeyNotFoundException">The configuration key is not found.</exception>
        public static string GetStringValue(this IConfiguration configuration, string key)
        {
            if (configuration == null)
            {
                throw new ArgumentNullException(nameof(configuration));
            }

            if (string.IsNullOrWhiteSpace(key))
            {
                throw new ArgumentNullException(nameof(key));
            }

            string value = configuration.GetValue<string>(key);
            if (string.IsNullOrWhiteSpace(value))
            {
                throw new KeyNotFoundException($"The configuration key '{key}' is not found or has empty value.");
            }

            return value;
        }

        /// <summary>
        /// Gets the boolean property from configuration by the configuration key.
        /// </summary>
        /// <param name="configuration">Application configuration.</param>
        /// <param name="configurationKey">The configuration key name.</param>
        /// <returns>The configuration boolean value.</returns>
        /// <exception cref="ArgumentNullException">When the argument is null.</exception>
        /// <exception cref="KeyNotFoundException">When configuration parameter not found.</exception>
        public static bool GetBooleanValue(this IConfiguration configuration, string configurationKey)
        {
            if (configuration == null)
            {
                throw new ArgumentNullException(nameof(configuration));
            }

            if (string.IsNullOrWhiteSpace(configurationKey))
            {
                throw new ArgumentNullException(nameof(configurationKey));
            }

            string configurationStringValue = configuration.GetStringValue(configurationKey);
            if (!bool.TryParse(configurationStringValue, out bool configurationValue))
            {
                throw new InvalidCastException($"{configurationKey} has invalid format.");
            }

            return configurationValue;
        }

        /// <summary>
        /// Gets the integer property from configuration by the configuration key.
        /// </summary>
        /// <param name="configuration">Application configuration.</param>
        /// <param name="configurationKey">The configuration key name.</param>
        /// <returns>The configuration integer value.</returns>
        /// <exception cref="ArgumentNullException">When the argument is null.</exception>
        /// <exception cref="KeyNotFoundException">When configuration parameter not found.</exception>
        public static int GetIntegerValue(this IConfiguration configuration, string configurationKey)
        {
            if (configuration == null)
            {
                throw new ArgumentNullException(nameof(configuration));
            }

            if (string.IsNullOrWhiteSpace(configurationKey))
            {
                throw new ArgumentNullException(nameof(configurationKey));
            }

            string configurationStringValue = configuration.GetStringValue(configurationKey);
            if (!int.TryParse(configurationStringValue, out int configurationValue))
            {
                throw new InvalidCastException($"{configurationKey} has invalid format.");
            }

            return configurationValue;
        }
    }
}

// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Messages.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>The class with constant messages.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Application.Constants
{
    /// <summary>
    /// The constants class with messages.
    /// </summary>
    internal static class Messages
    {
        /// <summary> Cannot be null message. </summary>
        internal const string CannotBeNull = "cannot be null";

        /// <summary> Cannot be null or empty or whitespace message. </summary>
        internal const string CannotBeNullOrEmptyOrWhitespace = "cannot be null or empty or whitespace";

        /// <summary> Cannot be null message. </summary>
        internal const string IsNull = "is null";

        /// <summary> Cannot be null or empty or whitespace message. </summary>
        internal const string IsNullOrEmptyOrWhitespace = "is null or empty or whitespace";

        /// <summary>The application setting is not set placeholder.</summary>
        internal const string AppSettingIsNotSet = "application setting is not set.";

        /// <summary>The request received message.</summary>
        internal const string RequestReceived = "Request received.";

        /// <summary>The request parsed message.</summary>
        internal const string RequestParsed = "Request parsed.";

        /// <summary>The request completed message.</summary>
        internal const string RequestCompleted = "Request completed.";

        /// <summary>The request failed message.</summary>
        internal const string RequestFailed = "Request failed.";

        /// <summary>The request received message.</summary>
        internal const string MethodStarted = "Started.";

        /// <summary>The request completed message.</summary>
        internal const string MethodCompleted = "Completed.";

        /// <summary>The request failed message.</summary>
        internal const string MethodFailed = "Failed.";

        /// <summary>The configuration parameter is not set message.</summary>
        internal const string ConfigurationParameterNotSetSuffix = "configuration parameter is not set.";
    }
}

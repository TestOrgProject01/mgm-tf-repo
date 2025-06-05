// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ProxyRoute.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements the proxy route configuration model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Configuration
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using FluentValidation;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Mgm.Sre.Services.EventGridProxy.Models.Validation;

    /// <summary>
    /// The proxy route configuration.
    /// </summary>
    public class ProxyRoute :
        ModelValidator<ProxyRoute>
    {
        /// <summary>The section path.</summary>
        public const string SectionPath = "ProxyRoutes";

        #region Properties

        /// <summary>
        /// Gets or sets Event Grid Event type to proxy.
        /// </summary>
        public string EventGridEventType { get; set; }

        /// <summary>
        /// Gets or sets route name.
        /// <remarks>This name must be unique.</remarks>
        /// </summary>
        public string RouteName { get; set; }

        /// <summary>
        /// Gets or sets proxy destination endpoint.
        /// </summary>
        public Uri DestinationEndpoint { get; set; }

        /// <summary>
        /// Gets or sets JWT scopes that should be requested from the identity service to proxy event.
        /// </summary>
        public string[] RequestScopes { get; set; }

        #endregion

        #region Overridden methods

        /// <inheritdoc />
        public override ProxyRoute Validate()
        {
            var validator = new ProxyRouteValidator();
            validator.ValidateAndThrow(this);
            return this;
        }

        /// <inheritdoc />
        public override bool TryValidate(out string validationMessage)
        {
            var validator = new ProxyRouteValidator();
            var validationResult = validator.Validate(this);
            validationMessage = validationResult.ToString();
            return validationResult.IsValid;
        }

        #endregion
    }

    /// <summary>
    /// The <see cref="ProxyRoute"/> model validator.
    /// </summary>
    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1402:File may only contain a single type", Justification = "To keep model and its validator in the same file.")]
    public class ProxyRouteValidator : AbstractValidator<ProxyRoute>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ProxyRouteValidator"/> class.
        /// </summary>
        public ProxyRouteValidator()
        {
            this.RuleFor(p => p.EventGridEventType).NotEmpty().WithMessage(Messages.ConfigurationParameterNotSetSuffix);
            this.RuleFor(p => p.RouteName).NotEmpty().WithMessage(Messages.ConfigurationParameterNotSetSuffix);
            this.RuleFor(p => p.DestinationEndpoint).NotEmpty().WithMessage(Messages.ConfigurationParameterNotSetSuffix);
        }
    }
}

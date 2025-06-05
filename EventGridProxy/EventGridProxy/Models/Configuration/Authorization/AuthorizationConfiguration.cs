// --------------------------------------------------------------------------------------------------------------------
// <copyright file="AuthorizationConfiguration.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements Authorization configuration section model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Configuration.Authorization
{
    using System;
    using System.Diagnostics.CodeAnalysis;
    using FluentValidation;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Mgm.Sre.Services.EventGridProxy.Models.Validation;

    /// <summary>
    /// The authorization configuration.
    /// </summary>
    public class AuthorizationConfiguration : ModelValidator<AuthorizationConfiguration>
    {
        /// <summary>The section path.</summary>
        public const string SectionPath = "Authorization";

        #region Properties

        /// <summary>
        /// Gets or sets the ping URL.
        /// </summary>
        public string PingUrl { get; set; }

        /// <summary>
        /// Gets or sets the Okta configuration provider client identifier.
        /// </summary>
        public string OktaClientId { get; set; }

        /// <summary>
        /// Gets or sets the Okta configuration provider client secret.
        /// </summary>
        public string OktaClientSecret { get; set; }

        /// <summary>
        /// Gets or sets the identity authorization service base URL.
        /// </summary>
        public Uri IdentityAuthorizationServiceUrl { get; set; }

        #endregion

        #region Overridden methods

        /// <inheritdoc />
        public override AuthorizationConfiguration Validate()
        {
            var validator = new AuthorizationConfigurationValidator();
            validator.ValidateAndThrow(this);
            return this;
        }

        /// <inheritdoc />
        public override bool TryValidate(out string validationMessage)
        {
            var validator = new AuthorizationConfigurationValidator();
            var validationResult = validator.Validate(this);
            validationMessage = validationResult.ToString();
            return validationResult.IsValid;
        }

        #endregion
    }

    /// <summary>
    /// The authorization configuration validator.
    /// </summary>
    [SuppressMessage("StyleCop.CSharp.MaintainabilityRules", "SA1402:File may only contain a single type", Justification = "To keep model and its validator in the same file.")]
    public class AuthorizationConfigurationValidator : AbstractValidator<AuthorizationConfiguration>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="AuthorizationConfigurationValidator"/> class.
        /// </summary>
        public AuthorizationConfigurationValidator()
        {
            this.RuleFor(p => p.OktaClientId).NotEmpty().WithMessage(Messages.ConfigurationParameterNotSetSuffix);
            this.RuleFor(p => p.OktaClientSecret)
                .NotEmpty()
                .NotEqual(Settings.StoredInKeyVaultPlaceholder)
                .WithMessage(Messages.ConfigurationParameterNotSetSuffix);
            this.RuleFor(p => p.IdentityAuthorizationServiceUrl)
                .NotEmpty()
                .WithMessage(Messages.ConfigurationParameterNotSetSuffix);
        }
    }
}

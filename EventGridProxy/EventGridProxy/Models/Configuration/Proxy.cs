// --------------------------------------------------------------------------------------------------------------------
// <copyright file="Proxy.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements the proxy routes configuration model class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Configuration
{
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using FluentValidation;
    using Mgm.Sre.Services.EventGridProxy.Application.Constants;
    using Mgm.Sre.Services.EventGridProxy.Models.Validation;

    /// <summary>
    /// The Event Grid Proxy configuration.
    /// </summary>
    public class Proxy :
        ModelValidator<Proxy>
    {
        /// <summary>
        /// Gets or sets the proxy routes.
        /// </summary>
        public ProxyRoute[] ProxyRoutes { get; set; }

        #region Overridden methods

        /// <inheritdoc />
        public override Proxy Validate()
        {
            var validator = new ProxyValidator();
            validator.ValidateAndThrow(this);
            return this;
        }

        /// <inheritdoc />
        public override bool TryValidate(out string validationMessage)
        {
            var validator = new ProxyValidator();
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
    public class ProxyValidator : AbstractValidator<Proxy>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ProxyValidator"/> class.
        /// </summary>
        public ProxyValidator()
        {
            this.RuleFor(p => p.ProxyRoutes).NotEmpty().WithMessage(Messages.ConfigurationParameterNotSetSuffix);
            this.RuleForEach(p => p.ProxyRoutes).NotEmpty();
            this.RuleForEach(p => p.ProxyRoutes).SetValidator(new ProxyRouteValidator());
            this.RuleFor(p => p.ProxyRoutes)
                .Must((proxy, _) => proxy.ProxyRoutes.GroupBy(p => p.RouteName).Count() > 1)
                .WithMessage($"The {nameof(ProxyRoute.RouteName)} configuration value isn't unique.");
        }
    }
}

// --------------------------------------------------------------------------------------------------------------------
// <copyright file="ModelValidator.cs" company="MGM Resorts International">
// Copyright (c) 2021 MGM Resorts International. All rights reserved.
// </copyright>
// <author>MGM Resorts International</author>
// <summary>Implements the model validator abstract class.</summary>
// --------------------------------------------------------------------------------------------------------------------

namespace Mgm.Sre.Services.EventGridProxy.Models.Validation
{
    using FluentValidation;

    /// <summary>
    /// The model validator class.
    /// </summary>
    /// <typeparam name="T">Model class.</typeparam>
    public abstract class ModelValidator<T>
    {
        /// <summary>
        /// Validates the model instance.
        /// </summary>
        /// <returns>The validated model.</returns>
        /// <exception cref="ValidationException">When object validation failed.</exception>
        public abstract T Validate();

        /// <summary>
        /// Validates the model instance.
        /// </summary>
        /// <param name="validationMessage">Model validation message.</param>
        /// <returns>True if model is valid, otherwise false.</returns>
        public abstract bool TryValidate(out string validationMessage);
    }
}

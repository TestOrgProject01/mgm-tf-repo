// <copyright file="ExceptionExtensionsTests.cs" company="MGM Resorts International">
// Copyright (c) 2022 MGM Resorts International. All rights reserved.
// </copyright>

namespace Mgm.Sre.Services.EventGridProxy.Tests.Application.Extension
{
    using Mgm.Sre.Services.EventGridProxy.Application.Extensions;
    using NUnit.Framework;

    /// <summary>
    /// The string extensions tests.
    /// </summary>
    [TestFixture]
    public class ExceptionExtensionsTests
    {
        #region Set Up

        /// <summary>
        /// Sets up tests.
        /// </summary>
        [SetUp]
        public void SetUp()
        {
            // Do nothing
        }

        /// <summary>
        /// Runs after each test.
        /// </summary>
        [TearDown]
        public void Cleanup()
        {
            // Do nothing
        }

        #endregion

        #region Extension Methods Tests

        /// <summary>
        /// Return input string with upper cased first letter.
        /// </summary>
        /// <param name="input">input string.</param>
        /// <param name="expectedOutput">expected output string.</param>
        [TestCase("", "", "")]
        [TestCase("error.", "", " Message: error.")]
        [TestCase("error.", "inner error.", " Message: error. InnerException Message: inner error.")]
        [TestCase("", "inner error.", " InnerException Message: inner error.")]
        public void ToUpperFirstLetter(string ex, string innerEx, string expectedOutput)
        {
            // setup
            var obj = string.IsNullOrWhiteSpace(innerEx)
                ? new Exception(ex)
                : new Exception(ex, new Exception(innerEx));

            // act
            var result = obj.GetErrorMessage();

            // asert
            Assert.That(result, Is.EqualTo(expectedOutput));
        }

        #endregion
    }
}
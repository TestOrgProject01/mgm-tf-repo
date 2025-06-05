using Azure.Identity;
using Mgm.Framework.Utilities.KeyVault;
using Mgm.Sre.Services.EventGridProxy.Application.Constants;
using Mgm.Sre.Services.EventGridProxy.Startup.ServiceCollectionExtensions;
using Microsoft.Azure.Functions.Worker;
using Microsoft.Azure.Functions.Worker.Builder;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

var host = new HostBuilder()
    .ConfigureFunctionsWebApplication()
    .ConfigureAppConfiguration((context, configurationBuilder) =>
    {
        var settings = configurationBuilder.Build();
        var environment = settings.GetValue<string>(ConfigurationKeys.Environment);

        //Add environment-based appsetting file
        configurationBuilder.AddJsonFile($"appsettings.{environment}.json");

        //Add values from Key Vault
        configurationBuilder.AddAzureKeyVault(
            new Uri(settings.GetValue<string>(ConfigurationKeys.KeyVaultUrl)),
            new ClientSecretCredential(settings.GetValue<string>(
            ConfigurationKeys.KeyVaultAadAppTenantId),
            settings.GetValue<string>(ConfigurationKeys.KeyVaultAadAppClientId),
            settings.GetValue<string>(ConfigurationKeys.KeyVaultAadAppSecret)),
            new PrefixKeyVaultSecretManager(Settings.ApplicationShortName));
    })
    .ConfigureServices((hostContext, services) =>
    {
        services.AddAuthorizationServices(hostContext.Configuration);
        services.AddEventGridProxy(hostContext.Configuration);
        services.AddHealthChecksServices(hostContext.Configuration);
        services.AddApplicationInsightsTelemetryWorkerService();
        services.ConfigureFunctionsApplicationInsights();
    })
    .ConfigureLogging(logging =>
    {
        logging.Services.Configure<LoggerFilterOptions>(options =>
        {
            var defaultRule = options.Rules.FirstOrDefault(rule => rule.ProviderName == "Microsoft.Extensions.Logging.ApplicationInsights.ApplicationInsightsLoggerProvider");
            if (defaultRule != null)
            {
                //removing default configuration will allow logs with the level type of "Information" to be logged in Application Insights
                options.Rules.Remove(defaultRule);
            }
        });
    });

host.Build().Run();
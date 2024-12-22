var blobContainerName = 'tw-sheets'

param storageName string
param storageLocation string

param applicationInsightsName string
param appInsightsLocation string

param appServicePlanName string
param appServicePlanLocation string

param functionAppName string
param functionAppLocation string

resource storageAccount 'Microsoft.Storage/storageAccounts@2024-01-01' = {
    name: storageName
    location: storageLocation
    sku: {
        name: 'Standard_LRS'
        tier: 'Standard'
    }
    kind: 'StorageV2'
    properties: {
        accessTier: 'Hot'
    }
}

resource blobService 'Microsoft.Storage/storageAccounts/blobServices@2023-05-01' = {
    name: 'default'
    parent: storageAccount
}

resource blobContainer 'Microsoft.Storage/storageAccounts/blobServices/containers@2023-05-01' = {
    parent: blobService
    name: blobContainerName
}

resource applicationInsights 'Microsoft.Insights/components@2020-02-02' = {
    name: applicationInsightsName
    location: appInsightsLocation
    kind: 'web'
    properties: {
        Application_Type: 'web'
        Request_Source: 'rest'
    }
}

resource appServicePlan 'Microsoft.Web/serverfarms@2024-04-01' = {
  name: appServicePlanName
  location: appServicePlanLocation
  kind: 'functionapp'
  sku: {
    name: 'Y1'
    tier: 'Dynamic'
    size: 'Y1'
    family: 'Y'
    capacity: 0
  }
   properties: {
      perSiteScaling: false
      elasticScaleEnabled: false
      maximumElasticWorkerCount: 1
      isSpot: false
      reserved: false
      isXenon: false
      hyperV: false
      targetWorkerCount: 0
      targetWorkerSizeId: 0
      zoneRedundant: false
  }
}

resource functionApp 'Microsoft.Web/sites@2024-04-01' = {
    name: functionAppName
    location: functionAppLocation
    kind: 'functionapp'
    properties: {
        reserved: false
        serverFarmId: appServicePlan.id
        siteConfig: {
            javaVersion: '21'
            javaContainer: 'JAVA'
        }
    }
}

resource appsettings 'Microsoft.Web/sites/config@2024-04-01' = {
  parent: functionApp
  name: 'appsettings'
  properties: {
    SPRING_CLOUD_AZURE_STORAGE_CONNECTION_STRING: 'DefaultEndpointsProtocol=https;AccountName=${storageAccount.name};AccountKey=${storageAccount.listKeys().keys[0].value}'
    AzureWebJobsStorage: 'DefaultEndpointsProtocol=https;AccountName=${storageAccount.name};AccountKey=${storageAccount.listKeys().keys[0].value}'
    APPINSIGHTS_INSTRUMENTATIONKEY: applicationInsights.properties.InstrumentationKey
    APPLICATIONINSIGHTS_SAMPLINGSETTINGS_ISENABLED: 'false'
    FUNCTIONS_EXTENSION_VERSION: '~4'
    FUNCTIONS_WORKER_RUNTIME: 'java'
  }
}
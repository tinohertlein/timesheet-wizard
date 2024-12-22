targetScope='subscription'

param name string
param functionAppName string
param location string

resource newRG 'Microsoft.Resources/resourceGroups@2024-11-01' = {
  name: name
  location: location
}

module resources 'resources.bicep' = {
  name: 'resourceModule'
  scope: newRG
  params: {
    storageLocation: location
    storageName: name
    appServicePlanName: name
    appServicePlanLocation: location
    appInsightsLocation: location
    applicationInsightsName: name
    functionAppLocation: location
    functionAppName: functionAppName
  }
}
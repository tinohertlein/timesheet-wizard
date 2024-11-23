targetScope='subscription'

var name = 'timesheetwizard2'
var location = 'northeurope'

resource newRG 'Microsoft.Resources/resourceGroups@2024-08-01' = {
  name: '${name}rg'
  location: location
}

module resources 'resources.bicep' = {
  name: 'resourceModule'
  scope: newRG
  params: {
    storageLocation: location
    storageName: '${name}sa'
    appServicePlanName: '${name}asp'
    appServicePlanLocation: location
    appInsightsLocation: location
    applicationInsightsName: '${name}ai'
    functionAppLocation: location
    functionAppName: '${name}fa'
  }
}
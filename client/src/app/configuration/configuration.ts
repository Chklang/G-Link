import {ConfigurationController} from "./configuration.controller";

export const configuration: angular.IComponentOptions = {
  template: require('./configuration.html'),
  controller: ConfigurationController,
  controllerAs: 'ctrl'
};

import {MainController} from "./main.controller";

export const main: angular.IComponentOptions = {
  template: require('./main.html'),
  controller: MainController,
  controllerAs: 'ctrl'
};

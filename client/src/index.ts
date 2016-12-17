/// <reference path="../typings/index.d.ts" />

import * as angular from 'angular';

import 'angular-ui-router';
import routesConfig from './routes';

import {main} from './app/main/main';
import {configuration} from './app/configuration/configuration';
import {ConfigurationService} from './services/configuration';
import {LinkService} from './services/link';

import './index.css';

angular
  .module('app', ['ui.router'])
  .config(routesConfig)
  .component('main', main)
  .component('configuration', configuration)
  .service('Configuration', ConfigurationService)
  .service('Link', LinkService);

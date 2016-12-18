export interface IHotKey {
  hotkey_ctrl: boolean;
  hotkey_alt: boolean;
  hotkey_shift: boolean;
  hotkey: string;
}

export interface IConfiguration {
  nbSquaresX: number;
  nbSquaresY: number;
  hotkey: IHotKey;
}

export class ConfigurationService {
  public constructor(
    private $http: ng.IHttpService,
    private $q: ng.IQService
  ) {

  }

  public getConfiguration(): ng.IPromise<IConfiguration> {
    var lDefer: ng.IDeferred<IConfiguration> = this.$q.defer();
    this.$http.get('/rest/config').then((pResponse: ng.IHttpPromiseCallbackArg<IConfiguration>) => {
      lDefer.resolve(pResponse.data);
    }, lDefer.reject);
    return lDefer.promise;
  }

  public saveConfiguration(pConfiguration: IConfiguration): ng.IPromise<void> {
    var lDefer: ng.IDeferred<any> = this.$q.defer();
    this.$http.post('/rest/config', pConfiguration).then(() => lDefer.resolve(), lDefer.reject);
    return lDefer.promise;
  }
}

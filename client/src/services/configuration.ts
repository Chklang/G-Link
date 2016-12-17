interface ILigneConfiguration {
  key: string;
  value: string;
}

export interface IConfiguration {
  nbSquaresX: number;
  nbSquaresY: number;
}

export class ConfigurationService {
  public constructor(
    private $http: ng.IHttpService,
    private $q: ng.IQService
  ) {

  }

  public getConfiguration(): ng.IPromise<IConfiguration> {
    var lDefer: ng.IDeferred<IConfiguration> = this.$q.defer();
    this.$http.get('/rest/config').then((pResponse: ng.IHttpPromiseCallbackArg<ILigneConfiguration[]>) => {
      var lResult: IConfiguration = <any>{};
      var lConfigList: ILigneConfiguration[] = pResponse.data;
      lConfigList.forEach((pConfig: ILigneConfiguration) => {
        switch (pConfig.key) {
          case 'nbSquaresX':
            lResult.nbSquaresX = Number(pConfig.value);
            break;
          case 'nbSquaresY':
            lResult.nbSquaresY = Number(pConfig.value);
            break;
        }
      });
      lDefer.resolve(lResult);
    }, lDefer.reject);
    return lDefer.promise;
  }
}

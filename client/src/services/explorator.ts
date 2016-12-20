export enum ExploratorType {
  FILE, FOLDER
}

export interface IExploratorObject {
  name: string;
  path: string;
  type: ExploratorType;
}

interface IExploratorDTO {
  name: string;
  path: string;
  isFolder: boolean;
}

export class ExploratorService {

      /* @ngInject */
      public constructor(
        private $http: ng.IHttpService,
        private $q: ng.IQService
      ) {

      }

      public get(pPath: String): ng.IPromise<IExploratorObject[]> {
        var lDefer: ng.IDeferred<IExploratorObject[]> = this.$q.defer();
        this.$http.get('/rest/explore', {
          params: {'parent': pPath}
        }).then((pResponse: ng.IHttpPromiseCallbackArg<IExploratorDTO[]>) => {
          var pElements: IExploratorDTO[] = pResponse.data;
          var lResults: IExploratorObject[] = [];
          pElements.forEach((pElement: IExploratorDTO) => {
            var lResult: IExploratorObject = {
              name: pElement.name,
              path: pElement.path,
              type: pElement.isFolder?ExploratorType.FOLDER:ExploratorType.FILE
            }
            lResults.push(lResult);
          });
          lDefer.resolve(lResults);
        }, lDefer.reject);
        return lDefer.promise;
      }
}

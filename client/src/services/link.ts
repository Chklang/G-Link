export interface ILink {
  name: string;
  description: string;
  command: string;
  parameters: string;

  squareX: number;
  squareY: number;
}

export class LinkService {

    /* @ngInject */
    public constructor(
      private $http: ng.IHttpService,
      private $q: ng.IQService
    ) {

    }

    public getAllLinks(): ng.IPromise<ILink[]> {
      var lDefer: ng.IDeferred<ILink[]> = this.$q.defer();
      this.$http.get('/rest/links').then((pResponse: ng.IHttpPromiseCallbackArg<ILink[]>) => {
        lDefer.resolve(pResponse.data);
      }, lDefer.reject);
      return lDefer.promise;
    }

    public setLinks(pLinks: ILink[]): ng.IPromise<void> {
      var lDefer: ng.IDeferred<any> = this.$q.defer();
      this.$http.post('/rest/links', pLinks).then((pResponse: ng.IHttpPromiseCallbackArg<any>) => {
        lDefer.resolve();
      }, lDefer.reject);
      return lDefer.promise;
    }

    public addLink(pLink: ILink): ng.IPromise<void> {
      var lDefer: ng.IDeferred<any> = this.$q.defer();
      this.$http.put('/rest/links/'+pLink.name, pLink).then((pResponse: ng.IHttpPromiseCallbackArg<any>) => {
        lDefer.resolve();
      }, lDefer.reject);
      return lDefer.promise;
    }

    public deleteLink(pLink: ILink): ng.IPromise<void> {
      var lDefer: ng.IDeferred<any> = this.$q.defer();
      this.$http.delete('/rest/links/'+pLink.name).then((pResponse: ng.IHttpPromiseCallbackArg<any>) => {
        lDefer.resolve();
      }, lDefer.reject);
      return lDefer.promise;
    }

    public start(pLink: ILink): void {
      this.$http.get('/rest/start/' + pLink.name);
    }
}

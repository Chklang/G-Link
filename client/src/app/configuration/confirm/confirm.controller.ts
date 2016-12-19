export interface IConfirmContent {
  title: string;
  message: string;
}

export class ConfirmController {

  /* @ngInject */
  constructor(
    private $uibModalInstance: ng.ui.bootstrap.IModalServiceInstance,
    public content : IConfirmContent
  ) {
console.log(content);
  }

  public ok() {
    this.$uibModalInstance.close(true);
  }

  public cancel() {
    this.$uibModalInstance.dismiss('cancel');
  }
}

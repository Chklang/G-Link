export interface IEditLinkContent {
  name: string;
  description: string;
  command: string;
  parameters: string;
}

export class EditLinkController {

  /* @ngInject */
  public constructor(
    private $uibModalInstance: ng.ui.bootstrap.IModalServiceInstance,
    public content: IEditLinkContent
  ) {

  }

  public save() {
    this.$uibModalInstance.close(this.content);
  }

  public cancel() {
    this.$uibModalInstance.dismiss('cancel');
  }
}

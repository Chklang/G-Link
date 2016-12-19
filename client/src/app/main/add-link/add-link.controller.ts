import {ILink} from '../../../services/link';

export class AddLinkController {

  public name: string;
  public command: string;
  public parameters: string;
  public description: string;

  /* @ngInject */
  public constructor(
    private $uibModalInstance: ng.ui.bootstrap.IModalServiceInstance
  ) {

  }

  public save() {
    this.$uibModalInstance.close(<ILink>{
      name: this.name,
      description: this.description,
      parameters: this.parameters,
      command: this.command,
      squareX: null,
      squareY: null
    });
  }

  public cancel() {
    this.$uibModalInstance.dismiss('cancel');
  }
}

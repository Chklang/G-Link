import {ILink} from '../../../services/link';
import {ExploratorController} from '../explorator/explorator.controller';

export class AddLinkController {

  public name: string;
  public command: string;
  public parameters: string;
  public description: string;

  /* @ngInject */
  public constructor(
    private $uibModal: ng.ui.bootstrap.IModalService,
    private $uibModalInstance: ng.ui.bootstrap.IModalServiceInstance
  ) {
  }

  public browse() {
      var options: ng.ui.bootstrap.IModalSettings = {
        template: require('../explorator/explorator.html'),
        controller: ExploratorController,
        controllerAs: 'ctrl',
        resolve: {
        }
      };
      this.$uibModal.open(options).result.then((pPath: string) => {
        this.command = pPath;
      });
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

import {ExploratorService, IExploratorObject, ExploratorType} from '../../../services/explorator';

export class ExploratorController {

  /* @ngInject */
  public constructor(
    private $uibModalInstance: ng.ui.bootstrap.IModalServiceInstance
  ) {
    //this.open(this.tree);
  }

  public fileSelected = (pPath: string): void => {
    this.$uibModalInstance.close(pPath);
  };
}
